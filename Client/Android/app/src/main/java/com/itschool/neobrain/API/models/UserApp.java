package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserApp {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("app_id")
    @Expose
    private Integer appId;

    public UserApp(Integer userId, Integer appId) {
        this.userId = userId;
        this.appId = appId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

}
