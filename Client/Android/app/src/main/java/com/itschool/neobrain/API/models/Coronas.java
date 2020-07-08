package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/* Ресурс для работы со списком информации о COVID-19 в странах */
public class Coronas {
    @SerializedName("countries")
    @Expose
    private List<Corona> countries = null;

    public List<Corona> getCountries() {
        return countries;
    }

    public void setCountries(List<Corona> countries) {
        this.countries = countries;
    }
}
