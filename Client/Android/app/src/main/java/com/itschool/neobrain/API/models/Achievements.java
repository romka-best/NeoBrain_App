package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/* Ресурс для работы со списком приложений */
public class Achievements {
    @SerializedName("achievements")
    @Expose
    private List<Achievement> achievements = null;

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }
}
