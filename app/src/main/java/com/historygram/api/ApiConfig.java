package com.historygram.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

public class ApiConfig {
    private ApiOkHttpProvider okHttpProvider = new ApiOkHttpProvider.DefaultProvider();
    private Long defaultTimeoutMs = TimeUnit.SECONDS.toMillis(10);
    private Long postRequestTimeoutMs = TimeUnit.SECONDS.toMillis(5);
    private String accessToken;
    private Integer userId;

    public ApiConfig() {}

    public ApiOkHttpProvider getOkHttpProvider() {
        return okHttpProvider;
    }

    public Long getDefaultTimeoutMs() {
        return defaultTimeoutMs;
    }

    public Long getPostRequestTimeoutMs() {
        return postRequestTimeoutMs;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getHttpApiHost() {
        return "https://historygram.herokuapp.com";
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
