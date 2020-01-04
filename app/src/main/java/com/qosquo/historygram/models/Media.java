package com.qosquo.historygram.models;

import java.io.Serializable;
import java.util.Set;

public class Media implements Serializable {
    private Integer id;
    private String caption;
    private Set<Integer> children;
    private Long timestamp;
    private Integer article;
    private String mediaType;
    private String mediaUrl;
    private User owner;

    private int likesCount;
    private int commentsCount;

    private boolean userLiked;
    private boolean userSaved;

    public Media(Integer id, String caption, Set<Integer> children, Long timestamp, Integer article, String mediaUrl, User owner, int likesCount, int commentsCount, boolean userLiked, boolean userSaved) {
        this.id = id;
        this.caption = caption;
        this.children = children;
        this.timestamp = timestamp;
        this.article = article;
        this.userSaved = userSaved;
        this.mediaType = "IMAGE";
        this.mediaUrl = mediaUrl;
        this.owner = owner;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.userLiked = userLiked;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", caption='" + caption + '\'' +
                ", children=" + children +
                ", timestamp=" + timestamp +
                ", mediaType='" + mediaType + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", owner=" + owner +
                ", likesCount=" + likesCount +
                ", commentsCount=" + commentsCount +
                ", userLiked=" + userLiked +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public Set<Integer> getChildren() {
        return children;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public User getOwner() {
        return owner;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public boolean isUserLiked() {
        return userLiked;
    }

    public void setUserLiked(boolean userLiked) {
        this.userLiked = userLiked;
    }

    public Integer getArticle() {
        return article;
    }

    public boolean isUserSaved() {
        return userSaved;
    }

    public void setUserSaved(boolean userSaved) {
        this.userSaved = userSaved;
    }
}
