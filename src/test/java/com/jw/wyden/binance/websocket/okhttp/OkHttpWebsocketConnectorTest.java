package com.jw.wyden.binance.websocket.okhttp;

import com.jw.wyden.binance.websocket.ConnectionListener;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OkHttpWebsocketConnectorTest {

    private OkHttpWebsocketConnector connector;
    private OkHttpClient okHttpClientMock;
    private ConnectionListener connectionListenerMock;

    @BeforeEach
    public void setUp() {
        okHttpClientMock = mock(OkHttpClient.class);
        connectionListenerMock = mock(ConnectionListener.class);

        connector = new OkHttpWebsocketConnector(okHttpClientMock);
    }

    @Test
    public void testConnect() {
        String url = "wss://test.com";
        connector.connect(url, connectionListenerMock);

    }

    // Tutaj możemy dodać więcej testów dla innych metod...
}