package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/* Ресурс для работы с постами */
public class PostList {
    @SerializedName("posts")
    @Expose
    private List<Post> posts = null;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
