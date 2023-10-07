package com.imooc.pojo;

import javax.persistence.*;

@Table(name = "users_like_videos")
public class UsersLikeVideos {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "video_id")
    private String videoId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}