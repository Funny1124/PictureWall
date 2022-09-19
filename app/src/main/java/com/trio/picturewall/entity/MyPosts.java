package com.trio.picturewall.entity;

import java.util.Arrays;

public class MyPosts {
    private int id;
    private String pUserId;
    private String imageCode;
    private String title;
    private String content;
    private String createTime;
    private String[] imageUrlList;
    private int likeId;
    private int likeNum;
    private boolean hasLike;
    private int collectId;
    private int collectNum;
    private boolean hasCollect;
    private boolean hasFocus;
    private String username;

    public MyPosts() {
    }

    public MyPosts(int id, String pUserId, String imageCode,
                   String title, String content, String createTime,
                   String[] imageUrlList, int likeId, int likeNum,
                   boolean hasLike, int collectId, int collectNum,
                   boolean hasCollect, boolean hasFocus, String username) {
        this.id = id;
        this.pUserId = pUserId;
        this.imageCode = imageCode;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.imageUrlList = imageUrlList;
        this.likeId = likeId;
        this.likeNum = likeNum;
        this.hasLike = hasLike;
        this.collectId = collectId;
        this.collectNum = collectNum;
        this.hasCollect = hasCollect;
        this.hasFocus = hasFocus;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getpUserId() {
        return pUserId;
    }

    public void setpUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String[] getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(String[] imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public boolean getHasLike() {
        return hasLike;
    }

    public void setHasLike(boolean hasLike) {
        this.hasLike = hasLike;
    }

    public int getCollectId() {
        return collectId;
    }

    public void setCollectId(int collectId) {
        this.collectId = collectId;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    public boolean getHasCollect() {
        return hasCollect;
    }

    public void setHasCollect(boolean hasCollect) {
        this.hasCollect = hasCollect;
    }

    public boolean getHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "MyPosts{" +
                "id=" + id +
                ", pUserId=" + pUserId +
                ", imageCode='" + imageCode + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", imageUrlList=" + Arrays.toString(imageUrlList) +
                ", likeId=" + likeId +
                ", likeNum=" + likeNum +
                ", hasLike=" + hasLike +
                ", collectId=" + collectId +
                ", collectNum=" + collectNum +
                ", hasCollect=" + hasCollect +
                ", hasFocus=" + hasFocus +
                ", username='" + username + '\'' +
                '}';
    }
}
