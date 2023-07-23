package com.jw.wyden.binance.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ConnectionWatcher {

    private final Logger logger = LoggerFactory.getLogger(ConnectionWatcher.class);

    private static final Duration RECONNECT_INTERVAL = Duration.ofSeconds(10);

    private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> reconnectTask;
    private final ConnectionListener connectionListener;

    public ConnectionWatcher(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void onConnected(WebsocketConnector websocketConnector) {
        synchronized (this) {
            logger.info("Websocket connected");
            cancelPreviousCancelTask();
        }
    }

    public void onConnectionClosed(WebsocketConnector websocketConnector) {
        synchronized (this) {
            logger.info("Websocket connection closed reconnecting in {} seconds", RECONNECT_INTERVAL.toSeconds());
            reconnectTask = exec.schedule(() -> {
                logger.info("Reconnecting websocket");
                websocketConnector.reconnect(connectionListener);
            }, RECONNECT_INTERVAL.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    public void onConnectionFailure(WebsocketConnector websocketConnector) {
        synchronized (this) {
            logger.info("Websocket connection failed reconnecting in {} seconds", RECONNECT_INTERVAL.toSeconds());
            cancelPreviousCancelTask();
            reconnectTask = exec.schedule(() -> {
                logger.info("Reconnecting websocket");
                websocketConnector.reconnect(connectionListener);
            }, RECONNECT_INTERVAL.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    private void cancelPreviousCancelTask() {
        if(reconnectTask != null) {
            reconnectTask.cancel(true);
            reconnectTask = null;
        }
    }




}
