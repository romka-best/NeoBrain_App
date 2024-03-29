package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/* Ресурс для работы с постами */
// Необходимо для Retrofit
public class PostModel {
    @SerializedName("post")
    @Expose
    private Post post;
    @SerializedName("users")
    @Expose
    private List<PostModel> users = null;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<PostModel> getUsers() {
        return users;
    }

    public void setUsers(List<PostModel> users) {
        this.users = users;
    }
}
