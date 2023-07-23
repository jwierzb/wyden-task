package com.jw.wyden.binance.websocket;

public interface EventsListener<T> {

    void onEvent(T t, long eventTime);

}
