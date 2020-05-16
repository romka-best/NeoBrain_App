package com.example.neobrain.API.model;

public class Achievement {
    private Boolean got;
    private String title;
    private String description;
    private Integer id;
    private Integer photo_id;

    public Boolean getGot() {
        return got;
    }

    public void setGot(Boolean got) {
        this.got = got;
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

    public Achievement(Integer id, String description, Boolean got, Integer photo_id, String title) {
        this.got = got;
        this.title = title;
        this.description = description;
        this.id = id;
        this.photo_id = photo_id;
    }
}
