package com.example.neobrain.API.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatUsers {

    @SerializedName("users")
    @Expose
    private List<Integer> users = null;

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

}
