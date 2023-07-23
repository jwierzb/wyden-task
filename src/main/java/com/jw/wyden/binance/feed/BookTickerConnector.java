package com.jw.wyden.binance.feed;

import com.jw.wyden.binance.feed.binfut.model.SymbolBookTickerEvent;
import com.jw.wyden.binance.websocket.EventsListener;

public interface BookTickerConnector {

    void connect();

    void subscribeTicker(String ticker);

    void unsubscribeTicker(String ticker);

    void addEventsListener(String ticker, EventsListener<SymbolBookTickerEvent> bookTickerEventEventsListener);

    String getExchangeName();
}
