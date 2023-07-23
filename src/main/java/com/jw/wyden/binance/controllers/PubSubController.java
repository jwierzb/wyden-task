package com.jw.wyden.binance.controllers;

import com.jw.wyden.binance.services.BookTickerConnectorPubSubService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickers")
public class PubSubController {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(PubSubController.class);

    private final BookTickerConnectorPubSubService bookTickerConnectorPubSubService;

    @Autowired
    public PubSubController(BookTickerConnectorPubSubService bookTickerConnectorPubSubService) {
        this.bookTickerConnectorPubSubService = bookTickerConnectorPubSubService;
    }

    @PostMapping("/{exchange}/subscribe/{ticker}")
    public void subscribeTicker(@PathVariable String exchange, @PathVariable String ticker) {
        bookTickerConnectorPubSubService.subscribeTicker(exchange, ticker);
    }

    @PostMapping("/{exchange}/unsubscribe/{ticker}")
    public void unsubscribeTicker(@PathVariable String exchange, @PathVariable String ticker) {
        bookTickerConnectorPubSubService.unsubscribeTicker(exchange, ticker);
    }


}
