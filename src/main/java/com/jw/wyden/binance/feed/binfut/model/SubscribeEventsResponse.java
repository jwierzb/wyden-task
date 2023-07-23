package com.jw.wyden.binance.feed.binfut.model;

public class SubscribeEventsResponse {

    String result;
    Integer id;

    public SubscribeEventsResponse(String result, Integer id) {
        this.result = result;
        this.id = id;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
