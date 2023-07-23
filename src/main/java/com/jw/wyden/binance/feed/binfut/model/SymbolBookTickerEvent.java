package com.jw.wyden.binance.feed.binfut.model;


import com.google.gson.annotations.SerializedName;

public class SymbolBookTickerEvent {

    @SerializedName("u")
    private Long orderBookUpdateId;

    @SerializedName("s")
    private String symbol;

    @SerializedName("E")
    private Long eventTime;

    @SerializedName("b")
    private String bestBidPrice;

    @SerializedName("B")
    private String bestBidQty;

    @SerializedName("a")
    private String bestAskPrice;

    @SerializedName("A")
    private String bestAskQty;

    public Long getOrderBookUpdateId() {
        return orderBookUpdateId;
    }

    public void setOrderBookUpdateId(Long orderBookUpdateId) {
        this.orderBookUpdateId = orderBookUpdateId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBestBidPrice() {
        return bestBidPrice;
    }

    public void setBestBidPrice(String bestBidPrice) {
        this.bestBidPrice = bestBidPrice;
    }

    public String getBestBidQty() {
        return bestBidQty;
    }

    public void setBestBidQty(String bestBidQty) {
        this.bestBidQty = bestBidQty;
    }

    public String getBestAskPrice() {
        return bestAskPrice;
    }

    public void setBestAskPrice(String bestAskPrice) {
        this.bestAskPrice = bestAskPrice;
    }

    public String getBestAskQty() {
        return bestAskQty;
    }

    public void setBestAskQty(String bestAskQty) {
        this.bestAskQty = bestAskQty;
    }


    @Override
    public String toString() {
        return "SymbolBookTickerEvent{" +
                "symbol='" + symbol + '\'' +
                "bestBidPrice='" + bestBidPrice + '\'' +
                ", bestBidQty='" + bestBidQty + '\'' +
                ", bestAskPrice='" + bestAskPrice + '\'' +
                ", bestAskQty='" + bestAskQty + '\'' +
                '}';
    }

    public Long getEventTime() {
        return eventTime;
    }
}
