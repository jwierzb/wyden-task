package com.jw.wyden.binance.feed.binfut;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jw.wyden.binance.feed.BookTickerConnector;
import com.jw.wyden.binance.feed.binfut.model.SymbolBookTickerEvent;
import com.jw.wyden.binance.websocket.*;
import com.jw.wyden.binance.websocket.okhttp.OkHttpWebsocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class BinanceFuturesBookTickerConnector implements BookTickerConnector {

    private static final String EXCHANGE_NAME = "binance";

    private final Logger logger = LoggerFactory.getLogger(BinanceFuturesBookTickerConnector.class);
    private final MessageSerde messageSerde = new MessageSerde();
    private final WebsocketConnector connector;

    //as for now hardcoded, config management out of scope
    private final String wssUrl = "wss://fstream.binance.com/ws/";

    private final ConnectionWatcher connectionWatcher;
    private final SubscriptionsManager subscriptionsManager;
    private final Map<String, Collection<EventsListener<SymbolBookTickerEvent>>> listeners = new ConcurrentHashMap<>();

    private final ConnectionListener connectionListener;


    @Autowired
    public BinanceFuturesBookTickerConnector(@Value("${initialTickers:}#{T(java.util.Collections).emptySet()}") Set<String> initialTickers) {
        this.connectionListener = createConnectionListener();
        this.connectionWatcher = new ConnectionWatcher(connectionListener);
        this.connector = new OkHttpWebsocketConnector();
        connector.setMessageListener(createMessageListener());
        this.subscriptionsManager = new SubscriptionsManager(connector, initialTickers);
        connect();
    }

    //hard to test without using more beans
    public BinanceFuturesBookTickerConnector(WebsocketConnector connector,
                                             ConnectionWatcher connectionWatcher,
                                             SubscriptionsManager subscriptionsManager) {
        this.connectionListener = createConnectionListener();
        this.connector = connector;
        connector.setMessageListener(createMessageListener());
        this.connectionWatcher = connectionWatcher;
        this.subscriptionsManager = subscriptionsManager;
        connect();
    }


    @Override
    public void connect() {
        logger.info("Connecting to {}", wssUrl);
        connector.connect(wssUrl, connectionListener);
    }

    @Override
    public void subscribeTicker(String ticker) {
        logger.info("Subscribing to {}", ticker);
        subscriptionsManager.subscribeTicker(ticker);
    }

    @Override
    public void unsubscribeTicker(String ticker) {
        logger.info("Unsubscribing from {}", ticker);
        subscriptionsManager.unsubscribeTicker(ticker);
        listeners.remove(ticker);
    }

    @Override
    public void addEventsListener(String ticker, EventsListener<SymbolBookTickerEvent> bookTickerEventEventsListener) {
        if(subscriptionsManager.isTickerSubscriber(ticker)) {
            listeners.compute(ticker, (t, listeners) -> {
                if (listeners == null) {
                    List<EventsListener<SymbolBookTickerEvent>> result = new ArrayList<>();
                    result.add(bookTickerEventEventsListener);
                    return result;
                } else {
                    listeners.add(bookTickerEventEventsListener);
                    return listeners;
                }
            });
        } else {
            throw new IllegalArgumentException("Ticker " + ticker + " is not subscribed");
        }
    }

    @Override
    public String getExchangeName() {
        return EXCHANGE_NAME;
    }

    private MessageListener createMessageListener() {
        return new MessageListener() {
            @Override
            public void onMessage(String message) {
                logger.trace("Message received: {}", message);
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                if(jsonObject.has("result") || jsonObject.has("id")) {
                    var subscribeResponse = messageSerde.deserializeSubscribeEventsResponse(message);
                    subscriptionsManager.onSubscribeResponse(subscribeResponse);
                } else if(jsonObject.has("e") && jsonObject.get("e").getAsString().equals("bookTicker")) {
                    var bookTickerEvent = messageSerde.deserializeSymbolBookTickerEvent(message);
                    //to lower case because of the case sensitivity of the symbol, but normally it would be mapped to some internal representation
                    var symbolListeners = listeners.get(bookTickerEvent.getSymbol().toLowerCase());
                    logger.debug("Received book ticker event: {}", bookTickerEvent);
                    final long now = System.currentTimeMillis();
                    if(symbolListeners!=null) symbolListeners.forEach(listener -> listener.onEvent(bookTickerEvent, now));
                } else if (jsonObject.has("code")) {
                    //lack of failed subscription handling
                    logger.error("Error message received: {}", message);
                } else {
                    logger.error("Unknown message received: {}", message);
                }
            }
        };
    }

    public ConnectionListener createConnectionListener() {
        return new ConnectionListener() {
            @Override
            public void onConnected(WebsocketConnector websocketConnector) {
                logger.info("Websocket connected");
                connectionWatcher.onConnected(websocketConnector);
                subscriptionsManager.onConnected();
            }

            @Override
            public void onConnectionFailure(WebsocketConnector websocketConnector, Throwable throwable) {
                connectionWatcher.onConnectionFailure(websocketConnector);
                subscriptionsManager.onDisconnected();
            }

            @Override
            public void onConnectionClosedNormally(WebsocketConnector websocketConnector) {
                connectionWatcher.onConnectionClosed(websocketConnector);
                subscriptionsManager.onDisconnected();
            }
        };
    }

}
