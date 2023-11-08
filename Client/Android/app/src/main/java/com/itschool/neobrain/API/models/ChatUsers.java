package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/* Ресурс для работы с пользователями чата */
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
