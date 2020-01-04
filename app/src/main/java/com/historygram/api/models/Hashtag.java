package com.historygram.api.models;

import java.io.Serializable;

public class Hashtag implements Serializable {
    private Integer id;
    private String name;

    public Hashtag(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
