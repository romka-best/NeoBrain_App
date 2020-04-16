package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Music {

    @SerializedName("music")
    @Expose
    private String music;

    public String getMusic() {
        return music;
    }

    public void setMusic(String photo) {
        this.music = music;
    }
}
