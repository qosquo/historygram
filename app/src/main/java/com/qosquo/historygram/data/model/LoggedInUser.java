package com.qosquo.historygram.data.model;

import androidx.annotation.Nullable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    private String token;
    @Nullable
    private Integer id;

    private String login;
    private String password;

    public LoggedInUser(String token, Integer id, String login, String password) {
        this.token = token;
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
