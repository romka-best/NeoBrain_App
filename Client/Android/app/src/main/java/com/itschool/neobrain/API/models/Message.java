package com.itschool.neobrain.API.models;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/* Ресурс для работы с людьми (поля) */
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

    private Boolean showDate = true;

    public Message(String text, String createdDate, Integer authorId) {
        this.text = text;
        this.createdDate = createdDate;
        this.authorId = authorId;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Message(String text, String createdDate, Integer authorId, Boolean show) {
        this.showDate = show;
        this.text = text;
        this.createdDate = createdDate;
        this.authorId = authorId;
    }

    public Message(String text, Integer authorId, Integer chatId) {
        this.text = text;
        this.authorId = authorId;
        this.chatId = chatId;
    }

    public Message() {

    }

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

    public static final Comparator<Message> COMPARE_BY_TIME = new Comparator<Message>() {
        @Override
        public int compare(Message message1, Message message2) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = format.parse(message1.getCreatedDate());
                date2 = format.parse(message2.getCreatedDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date1 != null;
            assert date2 != null;
            Long timestamp1 = date1.getTime();
            Long timestamp2 = date2.getTime();
            return (int) (timestamp1 - timestamp2);
        }
    };

}
