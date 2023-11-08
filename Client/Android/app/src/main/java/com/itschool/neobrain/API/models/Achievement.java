package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

/* Ресурс для работы с конкретным достижением */
public class Achievement {
    @SerializedName("is_got")
    @Expose
    private Boolean isGot;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("photo_id")
    @Expose
    private Integer photo_id;

    public Boolean getGot() {
        return isGot;
    }

    public void setGot(Boolean isGot) {
        this.isGot = isGot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(Integer photo_id) {
        this.photo_id = photo_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Achievement(Integer id, String description, Boolean isGot, Integer photo_id, String title) {
        this.isGot = isGot;
        this.title = title;
        this.description = description;
        this.id = id;
        this.photo_id = photo_id;
    }

    public static final Comparator<Achievement> COMPARE_BY_ID = new Comparator<Achievement>() {
        @Override
        public int compare(Achievement o1, Achievement o2) {
            return o1.getId() - o2.getId();
        }
    };
}
