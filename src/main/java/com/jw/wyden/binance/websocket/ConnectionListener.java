package com.jw.wyden.binance.websocket;

public interface ConnectionListener {

    void onConnected(WebsocketConnector websocketConnector);

    void onConnectionFailure(WebsocketConnector websocketConnector, Throwable throwable);

    void onConnectionClosedNormally(WebsocketConnector websocketConnector);

}
