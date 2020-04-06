package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chat {

    @SerializedName("count_messages")
    @Expose
    private Integer countMessages;
    @SerializedName("count_new_messages")
    @Expose
    private Integer countNewMessages;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("last_message")
    @Expose
    private String lastMessage;
    @SerializedName("last_time_message")
    @Expose
    private String lastTimeMessage;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("type_of_chat")
    @Expose
    private Integer typeOfChat;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    public Chat(String lastMessage, String lastTimeMessage, String name, Integer photoId) {
        this.lastMessage = lastMessage;
        this.lastTimeMessage = lastTimeMessage;
        this.name = name;
        this.photoId = photoId;
    }

    public Integer getCountMessages() {
        return countMessages;
    }

    public void setCountMessages(Integer countMessages) {
        this.countMessages = countMessages;
    }

    public Integer getCountNewMessages() {
        return countNewMessages;
    }

    public void setCountNewMessages(Integer countNewMessages) {
        this.countNewMessages = countNewMessages;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTimeMessage() {
        return lastTimeMessage;
    }

    public void setLastTimeMessage(String lastTimeMessage) {
        this.lastTimeMessage = lastTimeMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTypeOfChat() {
        return typeOfChat;
    }

    public void setTypeOfChat(Integer typeOfChat) {
        this.typeOfChat = typeOfChat;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}