package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PeopleModel {

    @SerializedName("user_author_id")
    @Expose
    private Integer userAuthorId;
    @SerializedName("user_subscribe_id")
    @Expose
    private Integer userSubscribeId;

    public PeopleModel(Integer userAuthorId, Integer userSubscribeId) {
        this.userAuthorId = userAuthorId;
        this.userSubscribeId = userSubscribeId;
    }

    public Integer getUserAuthorId() {
        return userAuthorId;
    }

    public void setUserAuthorId(Integer userAuthorId) {
        this.userAuthorId = userAuthorId;
    }

    public Integer getUserSubscribeId() {
        return userSubscribeId;
    }

    public void setUserSubscribeId(Integer userSubscribeId) {
        this.userSubscribeId = userSubscribeId;
    }

}