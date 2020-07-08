package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/* Ресурс для работы с фотографиями (геттер и сеттер) */
// Необходимо для Retrofit
public class PhotoModel {
    @SerializedName("photo")
    @Expose
    private Photo photo;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
