package com.example.werayouapp.model;

import android.util.Log;

public class Post implements Comparable<Post>{
    String description;
    String id_post;
    String id_user;
    String image;
    String createdDate;
    Long serverTime;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Post() {
    }

    public Post(String description, String id_post, String id_user, String image, String createdDate, Long serverTime) {
        this.description = description;
        this.id_post = id_post;
        this.id_user = id_user;
        this.image = image;
        this.createdDate = createdDate;
        this.serverTime = serverTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public int compareTo(Post o) {
        int result = o.getServerTime().intValue();
        Log.i("ResultPost",result+"");
        return result-this.serverTime.intValue();
    }
}
