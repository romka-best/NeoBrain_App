package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class App {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("secondary_text")
    @Expose
    private String secondaryText;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("link_android")
    @Expose
    private String linkAndroid;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("is_added")
    @Expose
    private Boolean isAdded;

    public App(Integer id, String title, String secondaryText, String description, String linkAndroid, Integer photoId, boolean isAdded) {
        this.id = id;
        this.title = title;
        this.secondaryText = secondaryText;
        this.description = description;
        this.linkAndroid = linkAndroid;
        this.photoId = photoId;
        this.isAdded = isAdded;
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

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Boolean getAdded() {
        return isAdded;
    }

    public void setAdded(Boolean added) {
        isAdded = added;
    }

    public String getLinkAndroid() {
        return linkAndroid;
    }

    public void setLinkAndroid(String linkAndroid) {
        this.linkAndroid = linkAndroid;
    }
}
