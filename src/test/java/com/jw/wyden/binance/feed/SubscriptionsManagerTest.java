package com.jw.wyden.binance.feed;

import com.jw.wyden.binance.feed.binfut.SubscriptionsManager;
import com.jw.wyden.binance.feed.binfut.model.UserDataRequest;
import com.jw.wyden.binance.feed.binfut.model.SubscribeEventsResponse;
import com.jw.wyden.binance.websocket.WebsocketConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubscriptionsManagerTest {

    private WebsocketConnector connector;
    private Set<String> tickerToSubscribe;

    @BeforeEach
    public void setUp() {
        connector = Mockito.mock(WebsocketConnector.class);
        tickerToSubscribe = new HashSet<>();
        tickerToSubscribe.add("btcusdt");
    }

    @Test
    public void subscribeTickerTest() {
        SubscriptionsManager manager = new SubscriptionsManager(connector, tickerToSubscribe);
        String ticker = "ethusdt";
        manager.subscribeTicker(ticker);
        verify(connector, times(0)).sendMessage(any());
        manager.onConnected();
        manager.onSubscribeResponse(new SubscribeEventsResponse("", 1));
        assertTrue(manager.isTickerSubscriber(ticker));
    }

    @Test
    public void onSubscribeResponseTest() {
        SubscriptionsManager manager = new SubscriptionsManager(connector, tickerToSubscribe);
        SubscribeEventsResponse response = new SubscribeEventsResponse("", 1);
        response.setId(1);
        UserDataRequest request = new UserDataRequest();
        request.setSymbols(new HashSet<>(tickerToSubscribe));
        manager.getSentRequests().put(response.getId(), request);

        manager.onSubscribeResponse(response);
        assertFalse(manager.getTickerToSubscribe().contains("btcusdt"));
        assertTrue(manager.getSubscribedTickers().contains("btcusdt"));
    }

    @Test
    public void onDisconnectedTest() {
        SubscriptionsManager manager = new SubscriptionsManager(connector, tickerToSubscribe);
        manager.onDisconnected();
        assertTrue(manager.getTickerToSubscribe().contains("btcusdt"));
        assertFalse(manager.getSubscribedTickers().contains("btcusdt"));
    }

    @Test
    public void onConnectedTest() {
        SubscriptionsManager manager = new SubscriptionsManager(connector, tickerToSubscribe);
        manager.onConnected();
        verify(connector, times(1)).sendMessage(any());
        SubscribeEventsResponse response = new SubscribeEventsResponse("", 1);
        manager.onSubscribeResponse(response);
        assertTrue(manager.getSubscribedTickers().contains("btcusdt"));
        assertFalse(manager.getTickerToSubscribe().contains("btcusdt"));
    }

    @Test
    public void subscribeTest() {
        SubscriptionsManager manager = new SubscriptionsManager(connector, tickerToSubscribe);
        manager.onConnected();
        String ticker = "bnbeth";
        manager.subscribeTicker(ticker);
        verify(connector, times(2)).sendMessage(any());
        assertTrue(manager.getTickerToSubscribe().contains(ticker));
    }
}
