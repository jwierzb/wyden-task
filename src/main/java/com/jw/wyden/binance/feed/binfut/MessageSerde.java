package com.jw.wyden.binance.feed.binfut;

import com.google.gson.Gson;
import com.jw.wyden.binance.feed.binfut.model.UserDataRequest;
import com.jw.wyden.binance.feed.binfut.model.SubscribeEventsResponse;
import com.jw.wyden.binance.feed.binfut.model.SymbolBookTickerEvent;

public class MessageSerde {

    private final Gson gson = new Gson();


    public String serialize(UserDataRequest message) {
        return gson.toJson(message);
    }

    //response is a json string in a form:
    // {
    //    "result": [
    //      "btcusdt@aggTrade"
    //    ],
    //    "id": 3
    //  }
    public SubscribeEventsResponse deserializeSubscribeEventsResponse(String message) {
        return gson.fromJson(message, SubscribeEventsResponse.class);
    }

    public SymbolBookTickerEvent deserializeSymbolBookTickerEvent(String message) {
        return gson.fromJson(message, SymbolBookTickerEvent.class);
    }

}
