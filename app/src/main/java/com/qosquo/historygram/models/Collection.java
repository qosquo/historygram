package com.qosquo.historygram.models;

import java.util.Set;

public class Collection {
    private int id;
    private String name;
    private int owner;
    private String lastMediaUrl;
    private Set<Integer> items;

    public Collection(int id, String name, int owner, String lastMedia, Set<Integer> items) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.lastMediaUrl = lastMedia;
        this.items = items;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", lastMediaUrl=" + lastMediaUrl +
                ", items=" + items +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOwner() {
        return owner;
    }

    public String getLastMediaUrl() {
        return lastMediaUrl;
    }

    public Set<Integer> getItems() {
        return items;
    }
}
