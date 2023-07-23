package com.jw.wyden.binance.feed.binfut;

import com.jw.wyden.binance.websocket.ConnectionListener;
import com.jw.wyden.binance.websocket.ConnectionWatcher;
import com.jw.wyden.binance.websocket.WebsocketConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BinanceFuturesBookTickerConnectorTest {

    private WebsocketConnector websocketConnectorMock;
    private SubscriptionsManager subscriptionsManagerMock;
    private ConnectionWatcher connectionWatcherMock;

    @BeforeEach
    public void setUp() {
        websocketConnectorMock = mock(WebsocketConnector.class);
        subscriptionsManagerMock = mock(SubscriptionsManager.class);
        connectionWatcherMock = mock(ConnectionWatcher.class);
    }

    @Test
    public void testSubscribeTicker() {
        BinanceFuturesBookTickerConnector connector = new BinanceFuturesBookTickerConnector(websocketConnectorMock, connectionWatcherMock, subscriptionsManagerMock);
        String ticker = "testTicker";
        connector.subscribeTicker(ticker);
        verify(subscriptionsManagerMock).subscribeTicker(ticker);
    }

    @Test
    public void testUnsubscribeTicker() {
        BinanceFuturesBookTickerConnector connector = new BinanceFuturesBookTickerConnector(websocketConnectorMock, connectionWatcherMock, subscriptionsManagerMock);
        String ticker = "testTicker";
        connector.unsubscribeTicker(ticker);
        verify(subscriptionsManagerMock).unsubscribeTicker(ticker);
    }

}