package com.jw.wyden.binance.config;

import com.jw.wyden.binance.socket.BookTickerSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final BookTickerSocketHandler bookTickerSocketHandler;

    @Autowired
    public WebSocketConfig(BookTickerSocketHandler bookTickerSocketHandler) {
        this.bookTickerSocketHandler = bookTickerSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(bookTickerSocketHandler, "/tickers");
    }
}