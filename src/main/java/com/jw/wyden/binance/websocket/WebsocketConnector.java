package com.jw.wyden.binance.websocket;

public abstract class WebsocketConnector {

    protected volatile MessageListener messageListener;

    public WebsocketConnector() {
    }

    public abstract void connect(String websocketUrl, ConnectionListener connectionListener);


    /**
     * Reconnects the websocket connection with the same url as before
     */
    public abstract void reconnect(ConnectionListener connectionListener);

    public abstract void close();

    public abstract boolean sendMessage(String message);

    public final void setMessageListener(final MessageListener messageListener) {
        if(this.messageListener != null) {
            throw new IllegalStateException("MessageListener already set");
        }
        this.messageListener = messageListener;
    }


}
