package com.itschool.neobrain.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Corona {
    @SerializedName("country_name")
    @Expose
    private String name;
    @SerializedName("cases_today")
    @Expose
    private int new_admitted;
    @SerializedName("deaths_count")
    @Expose
    private int all_deaths;
    @SerializedName("cases")
    @Expose
    private int all_admitted;
    @SerializedName("flag")
    @Expose
    private String uri;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Corona(Integer id) {
        this.id = id;
    }

    public Corona(String name, int new_admitted, int all_deaths, int all_admitted, String uri) {
        this.name = name;
        this.new_admitted = new_admitted;
        this.all_deaths = all_deaths;
        this.all_admitted = all_admitted;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNew_admitted() {
        return new_admitted;
    }

    public void setNew_admitted(int new_admitted) {
        this.new_admitted = new_admitted;
    }

    public int getAll_deaths() {
        return all_deaths;
    }

    public void setAll_deaths(int all_deaths) {
        this.all_deaths = all_deaths;
    }

    public int getAll_admitted() {
        return all_admitted;
    }

    public void setAll_admitted(int all_admitted) {
        this.all_admitted = all_admitted;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
