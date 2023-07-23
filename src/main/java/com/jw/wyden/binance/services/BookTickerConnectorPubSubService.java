package com.jw.wyden.binance.services;

import com.jw.wyden.binance.feed.BookTickerConnector;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookTickerConnectorPubSubService {

    private final Map<String, BookTickerConnector> bookTickerConnectors = new HashMap<>();

    @Autowired
    public BookTickerConnectorPubSubService(ListableBeanFactory beanFactory) {
        Collection<BookTickerConnector> connectors = beanFactory.getBeansOfType(BookTickerConnector.class).values();
        for (BookTickerConnector connector : connectors) {
            bookTickerConnectors.put(connector.getExchangeName(), connector);
        }

    }

    public void subscribeTicker(String exchange, String ticker) {
        if(bookTickerConnectors.containsKey(exchange)) {
            bookTickerConnectors.get(exchange).subscribeTicker(ticker);

        } else {
            throw new IllegalArgumentException("Unknown exchange: " + exchange);
        }
    }

    public void unsubscribeTicker(String exchange, String ticker) {
        if(bookTickerConnectors.containsKey(exchange)) {
            bookTickerConnectors.get(exchange).unsubscribeTicker(ticker);
        } else {
            throw new IllegalArgumentException("Unknown exchange: " + exchange);
        }
    }

}
