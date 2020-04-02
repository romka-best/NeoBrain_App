package com.example.neobrain.API.model;

import com.google.gson.annotations.SerializedName;

public class Chat {
    @SerializedName("text")
    private String text;
    @SerializedName("time")
    private String time;
    @SerializedName("imageUrl")
    private String imageURL;
    @SerializedName("name")
    private String name;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chat(String text, String time, String imageURL, String name) {
        this.text = text;
        this.time = time;
        this.imageURL = imageURL;
        this.name = name;
    }
}
