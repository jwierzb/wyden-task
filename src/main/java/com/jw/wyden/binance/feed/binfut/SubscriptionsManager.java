package com.jw.wyden.binance.feed.binfut;

import com.jw.wyden.binance.feed.binfut.model.UserDataRequest;
import com.jw.wyden.binance.feed.binfut.model.SubscribeEventsResponse;
import com.jw.wyden.binance.websocket.WebsocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class SubscriptionsManager {
    private final Logger logger = LoggerFactory.getLogger(SubscriptionsManager.class);

    private final WebsocketConnector connector;
    private final MessageSerde serde = new MessageSerde();

    private final CopyOnWriteArraySet<String> tickerToSubscribe = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<String> subscribedTickers = new CopyOnWriteArraySet<>();

    private final Map<Integer, UserDataRequest> sentRequests = new ConcurrentHashMap<>();

    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicInteger subscribeRequestCounter = new AtomicInteger(1);


    public SubscriptionsManager(WebsocketConnector connector, Set<String> tickerToSubscribe) {
        this.connector = connector;
        this.tickerToSubscribe.addAll(tickerToSubscribe);
    }

    //lack of failed subscription handling
    public void subscribeTicker(String ticker) {
        logger.info("Subscribing to {}", ticker);
        tickerToSubscribe.add(ticker);
        subscribe(ticker);
    }

    public void unsubscribeTicker(String ticker) {
        logger.info("Unsubscribing from {}", ticker);
        if (isConnected.get()) {
            if(subscribedTickers.contains(ticker)) {
                subscribedTickers.remove(ticker);
                var subscribeMsg = new UserDataRequest("UNSUBSCRIBE", new String[]{ticker.toLowerCase() + "@bookTicker"}, subscribeRequestCounter.getAndIncrement(), Collections.singleton(ticker));
                sentRequests.put(subscribeMsg.getId(), subscribeMsg);
                String serialized = serde.serialize(subscribeMsg);
                logger.debug("Sending unsubscribe message: {}", serialized);
                connector.sendMessage(serialized);
            } else {
                tickerToSubscribe.remove(ticker);
            }
        } else {
            subscribedTickers.remove(ticker);
            tickerToSubscribe.remove(ticker);
        }
    }

    public void onSubscribeResponse(SubscribeEventsResponse subscribeEventsResponse) {
        var subscribeRequest = sentRequests.remove(subscribeEventsResponse.getId());
        if(subscribeRequest != null) {
            subscribedTickers.addAll(subscribeRequest.getSymbols());
            tickerToSubscribe.removeAll(subscribeRequest.getSymbols());
        }
    }

    public boolean isTickerSubscriber(String ticker) {
        return subscribedTickers.contains(ticker);
    }

    public void onDisconnected() {
        logger.info("Disconnected");
        tickerToSubscribe.addAll(subscribedTickers);
        subscribedTickers.clear();
    }

    public void onConnected() {
        logger.info("Connected");
        isConnected.set(true);
        subscribeMany(tickerToSubscribe);
    }

    private void subscribe(String ticker) {
        if (isConnected.get()) {
            var subscribeMsg = new UserDataRequest("SUBSCRIBE", new String[]{ticker.toLowerCase() + "@bookTicker"}, subscribeRequestCounter.getAndIncrement(), Collections.singleton(ticker));
            sentRequests.put(subscribeMsg.getId(), subscribeMsg);
            String serialized = serde.serialize(subscribeMsg);
            logger.debug("Sending subscribe message: {}", serialized);
            connector.sendMessage(serialized);
        }
    }

    private void subscribeMany(Collection<String> tickers) {
        if (isConnected.get()) {
            UserDataRequest subscribe = new UserDataRequest("SUBSCRIBE", tickers.stream().map(t -> t.toLowerCase() + "@bookTicker").toArray(String[]::new), subscribeRequestCounter.getAndIncrement(), new HashSet<>(tickers));
            sentRequests.put(subscribe.getId(), subscribe);
            String serialized = serde.serialize(subscribe);
            logger.debug("Sending subscribe message: {}", serialized);
            connector.sendMessage(serialized);
        }
    }

    public Map<Integer, UserDataRequest> getSentRequests() {
        return sentRequests;
    }

    public CopyOnWriteArraySet<String> getTickerToSubscribe() {
        return tickerToSubscribe;
    }

    public CopyOnWriteArraySet<String> getSubscribedTickers() {
        return subscribedTickers;
    }

}
