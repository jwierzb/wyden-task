package com.jw.wyden.binance.feed.binfut.model;

public class SubscribeEventsErrorResponse {

    Integer code;
    String msg;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
