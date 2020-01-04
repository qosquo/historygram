package com.historygram.api.models;

public class Token {
    private String token;
    private Integer id;

    public Token(String token) {
        this.token = token;
    }

    public Token(String token, Integer id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }
}
