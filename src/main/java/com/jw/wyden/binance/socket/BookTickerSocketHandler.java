package com.jw.wyden.binance.socket;

import com.google.gson.Gson;
import com.jw.wyden.binance.feed.BookTickerConnector;
import com.jw.wyden.binance.feed.binfut.model.SymbolBookTickerEvent;
import com.jw.wyden.binance.websocket.EventsListener;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookTickerSocketHandler extends TextWebSocketHandler {

    private final Map<String, BookTickerConnector> bookTickerConnectors = new HashMap<>();
    private final Gson gson = new Gson();

    @Autowired
    public BookTickerSocketHandler(ListableBeanFactory beanFactory) {
        Collection<BookTickerConnector> connectors = beanFactory.getBeansOfType(BookTickerConnector.class).values();
        for (BookTickerConnector connector : connectors) {
            bookTickerConnectors.put(connector.getExchangeName(), connector);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        SubscribeRequest subscribeRequest = gson.fromJson(message.getPayload(), SubscribeRequest.class);
        if(bookTickerConnectors.containsKey(subscribeRequest.getExchange())) {
            bookTickerConnectors.get(subscribeRequest.getExchange()).addEventsListener(subscribeRequest.getTicker(), createEventsListener(session, subscribeRequest));
        } else {
            throw new IllegalArgumentException("Unknown exchange: " + subscribeRequest.getExchange());
        }
    }

    //should map to different domain object than the one used for serialization
    private EventsListener<SymbolBookTickerEvent> createEventsListener(WebSocketSession session, SubscribeRequest subscribeRequest) {
        return new EventsListener<SymbolBookTickerEvent>() {

            private long lastEventTime = 0;
            private long delayMs = subscribeRequest.delayMs!=null?subscribeRequest.delayMs:0;

            @Override
            public void onEvent(SymbolBookTickerEvent event, long eventTime) {
                if(eventTime - lastEventTime > delayMs) {
                    try {
                        session.sendMessage(new TextMessage(gson.toJson(event)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastEventTime = eventTime;
                }
            }
        };
    }
}