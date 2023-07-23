package com.jw.wyden.binance.feed.binfut.model;

import java.util.Set;

public class UserDataRequest {

    private String method;
    private String[] params;
    private Integer id;

    private transient Set<String> symbols;

    public UserDataRequest(String method, String[] params, Integer id, Set<String> symbols) {
        this.method = method;
        this.params = params;
        this.id = id;
        this.symbols = symbols;
    }

    public UserDataRequest() {
    }

    public String getMethod() {
        return method;
    }

    public String[] getParams() {
        return params;
    }

    public Integer getId() {
        return id;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }
}
