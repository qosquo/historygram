package com.qosquo.historygram.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {
    private Integer id;
    private String username;
    private String name;
    private int role;
    private String profilePicture;
    private String biography;

    private Integer mediaCount;
    private Integer followersCount;
    private Integer followsCount;

    private Set<Integer> collections;

    private String login;
    private String password;

    private boolean userFollowed;

    private User() {}

    public User(Integer id, String username, String profilePicture) {
        this.id = id;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public User(Integer id, String username, String name, int role, String profilePicture, String biography, Integer mediaCount, Integer followersCount, Integer followsCount, Set<Integer> collections, boolean userFollowed) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.profilePicture = profilePicture;
        this.biography = biography;
        this.mediaCount = mediaCount;
        this.followersCount = followersCount;
        this.followsCount = followsCount;
        this.collections = collections;
        this.userFollowed = userFollowed;
    }

    public User(Integer id, String username, String name, int role, String profilePicture, String biography, Integer mediaCount, Integer followersCount, Integer followsCount, Set<Integer> collections, String login, String password, boolean userFollowed) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.profilePicture = profilePicture;
        this.biography = biography;
        this.mediaCount = mediaCount;
        this.followersCount = followersCount;
        this.followsCount = followsCount;
        this.collections = collections;
        this.login = login;
        this.password = password;
        this.userFollowed = userFollowed;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", biography='" + biography + '\'' +
                ", role=" + role +
                ", mediaCount=" + mediaCount +
                ", followersCount=" + followersCount +
                ", followsCount=" + followsCount +
                ", login=" + login +
                ", password=" + password +
                ", userFollowed=" + userFollowed +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getBiography() {
        return biography;
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public Integer getFollowsCount() {
        return followsCount;
    }

    public boolean isUserFollowed() {
        return userFollowed;
    }

    public int getRole() {
        return role;
    }

    public Set<Integer> getCollections() {
        return collections;
    }
}
