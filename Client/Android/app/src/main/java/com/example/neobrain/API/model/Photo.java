package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_avatar")
    @Expose
    private Boolean isAvatar;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getAvatar() {
        return isAvatar;
    }

    public void setAvatar(Boolean avatar) {
        isAvatar = avatar;
    }
}