package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/* Ресурс для работы с приложениями */
// Необходимо для Retrofit
public class AppModel {
    @SerializedName("app")
    @Expose
    private App app;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
