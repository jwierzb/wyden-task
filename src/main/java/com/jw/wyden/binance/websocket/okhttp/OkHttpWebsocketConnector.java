package com.jw.wyden.binance.websocket.okhttp;

import com.jw.wyden.binance.websocket.ConnectionListener;
import com.jw.wyden.binance.websocket.WebsocketConnector;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class OkHttpWebsocketConnector extends WebsocketConnector {

    private final Logger logger = LoggerFactory.getLogger(OkHttpWebsocketConnector.class);

    private final OkHttpClient okHttpClient;
    private volatile WebSocket webSocket = null;
    private final ReentrantLock websocketLock = new ReentrantLock();

    public OkHttpWebsocketConnector(final OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpWebsocketConnector() {
        this(createDefaultHttpClient());
    }

    @Override
    public void connect(String websocketUrl, ConnectionListener connectionListener) {
        try {
            logger.info("Connecting to {}", websocketUrl);
            websocketLock.lock();
            Request request = new Request.Builder()
                    .url(websocketUrl)
                    .build();
            webSocket = okHttpClient.newWebSocket(request, createWebsocketListener(connectionListener));
            okHttpClient.dispatcher().executorService().shutdown();
        } finally {
            websocketLock.unlock();
        }
    }

    @Override
    public void reconnect(ConnectionListener connectionListener) {
        try {
            websocketLock.lock();
            close();
            webSocket = okHttpClient.newWebSocket(webSocket.request(), createWebsocketListener(connectionListener));
        } finally {
            websocketLock.unlock();
        }
    }

    @Override
    public void close() {
        try {
            websocketLock.lock();
            if (webSocket != null) {
                webSocket.close(1000, "");
            }
        } finally {
            websocketLock.unlock();
        }
    }

    @Override
    public boolean sendMessage(String message) {
        try{
            websocketLock.lock();
            if(webSocket != null) {
                return webSocket.send(message);
            } else {
                throw new IllegalStateException("Websocket is not connected, cannot send message");
            }
        } finally {
            websocketLock.unlock();
        }
    }


    private WebSocketListener createWebsocketListener(ConnectionListener connectionListener) {
        return new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                logger.warn("Wss connection closed");
                connectionListener.onConnectionClosedNormally(OkHttpWebsocketConnector.this);
            }

            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                logger.warn("Wss connection closing");
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                logger.warn("Websocket connection failed", t);
                connectionListener.onConnectionFailure(OkHttpWebsocketConnector.this, t);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                if(messageListener != null) {
                    messageListener.onMessage(text);
                }
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                if(messageListener != null) {
                    messageListener.onMessage(bytes.utf8());
                }
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                logger.info("Websocket connection opened");
                connectionListener.onConnected(OkHttpWebsocketConnector.this);
            }
        };
    }

    private static OkHttpClient createDefaultHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(10_000,  TimeUnit.MILLISECONDS)
                .writeTimeout(10_000,  TimeUnit.MILLISECONDS)
                .callTimeout(10_000,  TimeUnit.MILLISECONDS)
                .connectTimeout(10_000,  TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public ReentrantLock getWebsocketLock() {
        return websocketLock;
    }
}
