package com.trio.picturewall.entity;

public class User {

    private String id;
    private String appKey;
    private String username;
    private String password;
    private String sex;
    private String introduce;
    private String avatar;
    private String createTime;
    private String lastUpdateTime;

    public User() {
    }

    //用于修改信息
    public User(String id, String username, String sex, String introduce, String avatar) {
        this.id = id;
        this.username = username;
        this.sex = sex;
        this.introduce = introduce;
        this.avatar = avatar;
    }

    public User(String id, String appKey, String username,
                String password, String sex, String introduce,
                String avatar, String createTime, String lastUpdateTime) {
        this.id = id;
        this.appKey = appKey;
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.introduce = introduce;
        this.avatar = avatar;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", appKey='" + appKey + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", introduce='" + introduce + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createTime='" + createTime + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                '}';
    }
}
