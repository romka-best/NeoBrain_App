package com.example.neobrain.API.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Post {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    public Post(Integer id, String title, String text, Integer photoId, String createdDate) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.photoId = photoId;
        this.createdDate = createdDate;
    }

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("user_nickname")
    @Expose
    private String userNickname;

    public Post() {

    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public static final Comparator<Post> COMPARE_BY_TIME = new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = format.parse(post1.getCreatedDate());
                date2 = format.parse(post2.getCreatedDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date1 != null;
            assert date2 != null;
            Long timestamp1 = date1.getTime();
            Long timestamp2 = date2.getTime();
            return (int) (timestamp2 - timestamp1);
        }
    };
}