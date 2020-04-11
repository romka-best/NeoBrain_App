package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class App {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("secondary_text")
    @Expose
    private String secondaryText;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("button_text1")
    @Expose
    private String buttonText1;
    @SerializedName("button_text2")
    @Expose
    private String buttonText2;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getButtonText1() {
        return buttonText1;
    }

    public void setButtonText1(String buttonText1) {
        this.buttonText1 = buttonText1;
    }

    public String getButtonText2() {
        return buttonText2;
    }

    public void setButtonText2(String buttonText2) {
        this.buttonText2 = buttonText2;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

}
