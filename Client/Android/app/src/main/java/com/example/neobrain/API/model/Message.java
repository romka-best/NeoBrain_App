package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("with_attachments")
    @Expose
    private Boolean withAttachments;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("author_id")
    @Expose
    private Integer authorId;
    @SerializedName("chat_id")
    @Expose
    private Integer chatId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getWithAttachments() {
        return withAttachments;
    }

    public void setWithAttachments(Boolean withAttachments) {
        this.withAttachments = withAttachments;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

}
