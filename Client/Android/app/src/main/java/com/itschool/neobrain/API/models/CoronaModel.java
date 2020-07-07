package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoronaModel {
    @SerializedName("country")
    @Expose
    private Corona corona;

    public Corona getCorona() {
        return corona;
    }

    public void setCorona(Corona corona) {
        this.corona = corona;
    }
}
