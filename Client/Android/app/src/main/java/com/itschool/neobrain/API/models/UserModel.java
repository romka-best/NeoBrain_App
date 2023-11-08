package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/* Ресурс для работы с пользователями */
// Необходимо для Retrofit
public class UserModel {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("photos")
    @Expose
    private List<PhotoModel> photos = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoModel> photos) {
        this.photos = photos;
    }
}