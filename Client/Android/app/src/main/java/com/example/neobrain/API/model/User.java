package com.example.neobrain.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("hashed_password")
    @Expose
    private String hashedPassword;
    @SerializedName("is_closed")
    @Expose
    private Boolean isClosed;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("can_see_audio")
    @Expose
    private Boolean canSeeAudio;
    @SerializedName("can_see_groups")
    @Expose
    private Boolean canSeeGroups;
    @SerializedName("can_see_videos")
    @Expose
    private Boolean canSeeVideos;
    @SerializedName("can_write_message")
    @Expose
    private Boolean canWriteMessage;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("followers_count")
    @Expose
    private Integer followersCount;
    @SerializedName("subscriptions_count")
    @Expose
    private Integer subscriptionsCount;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getCanSeeAudio() {
        return canSeeAudio;
    }

    public void setCanSeeAudio(Boolean canSeeAudio) {
        this.canSeeAudio = canSeeAudio;
    }

    public Boolean getCanSeeGroups() {
        return canSeeGroups;
    }

    public void setCanSeeGroups(Boolean canSeeGroups) {
        this.canSeeGroups = canSeeGroups;
    }

    public Boolean getCanSeeVideos() {
        return canSeeVideos;
    }

    public void setCanSeeVideos(Boolean canSeeVideos) {
        this.canSeeVideos = canSeeVideos;
    }

    public Boolean getCanWriteMessage() {
        return canWriteMessage;
    }

    public void setCanWriteMessage(Boolean canWriteMessage) {
        this.canWriteMessage = canWriteMessage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getSubscriptionsCount() {
        return subscriptionsCount;
    }

    public void setSubscriptionsCount(Integer subscriptionsCount) {
        this.subscriptionsCount = subscriptionsCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }
}