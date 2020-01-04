package com.historygram.api.models;

import com.qosquo.historygram.models.User;

import java.util.List;

public class SearchResponse {
    private Hashtag[] hashtags;
    private String users;
    private String status;

    public SearchResponse(Hashtag[] hashtags, String users, String status) {
        this.hashtags = hashtags;
        this.users = users;
        this.status = status;
    }

    public Hashtag[] getHashtags() {
        return hashtags;
    }

    public String getUsers() {
        return users;
    }

    public String getStatus() {
        return status;
    }
}
