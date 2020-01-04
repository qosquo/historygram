package com.qosquo.historygram.models;

public class Article {
    private int id;
    private String name;
    private String body;
    private User owner;
    private int views;

    public Article(String name, String body) {
        this.name = name;
        this.body = body;
    }

    public Article(int id, String name, String body, User owner, int views) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.owner = owner;
        this.views = views;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", body='" + body + '\'' +
                ", owner=" + owner +
                ", views=" + views +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public User getOwner() {
        return owner;
    }

    public int getViews() {
        return views;
    }
}
