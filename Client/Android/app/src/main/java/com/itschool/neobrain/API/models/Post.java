package com.itschool.neobrain.API.models;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Post {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("like_emoji_count")
    @Expose
    private Integer likeEmojiCount;
    @SerializedName("laughter_emoji_count")
    @Expose
    private Integer laughterEmojiCount;
    @SerializedName("heart_emoji_count")
    @Expose
    private Integer heartEmojiCount;
    @SerializedName("disappointed_emoji_count")
    @Expose
    private Integer disappointedEmojiCount;
    @SerializedName("smile_emoji_count")
    @Expose
    private Integer smileEmojiCount;
    @SerializedName("angry_emoji_count")
    @Expose
    private Integer angryEmojiCount;
    @SerializedName("smile_with_heart_eyes_count")
    @Expose
    private Integer smileWithHeartEyesCount;
    @SerializedName("screaming_emoji_count")
    @Expose
    private Integer screamingEmojiCount;

    @SerializedName("like_emoji")
    @Expose
    private Boolean likeEmoji;
    @SerializedName("laughter_emoji")
    @Expose
    private Boolean laughterEmoji;
    @SerializedName("heart_emoji")
    @Expose
    private Boolean heartEmoji;
    @SerializedName("disappointed_emoji")
    @Expose
    private Boolean disappointedEmoji;
    @SerializedName("smile_emoji")
    @Expose
    private Boolean smileEmoji;
    @SerializedName("angry_emoji")
    @Expose
    private Boolean angryEmoji;
    @SerializedName("smile_with_heart_eyes")
    @Expose
    private Boolean smileWithHeartEyes;
    @SerializedName("screaming_emoji")
    @Expose
    private Boolean screamingEmoji;

    public Post(Integer id, String title, String text, Integer photoId, String createdDate,
                Integer userId, Boolean isAuthor) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.photoId = photoId;
        this.createdDate = createdDate;
        this.userId = userId;
        this.isAuthor = isAuthor;
    }

    @SerializedName("user_id")
    @Expose
    private Integer userId;

    @SerializedName("is_author")
    @Expose
    private Boolean isAuthor;

    @SerializedName("user_nickname")
    @Expose
    private String userNickname;

    public Post() {

    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
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

    public Integer getLikeEmojiCount() {
        return likeEmojiCount;
    }

    public void setLikeEmojiCount(Integer likeEmojiCount) {
        this.likeEmojiCount = likeEmojiCount;
    }

    public Integer getLaughterEmojiCount() {
        return laughterEmojiCount;
    }

    public void setLaughterEmojiCount(Integer laughterEmojiCount) {
        this.laughterEmojiCount = laughterEmojiCount;
    }

    public Integer getHeartEmojiCount() {
        return heartEmojiCount;
    }

    public void setHeartEmojiCount(Integer heartEmojiCount) {
        this.heartEmojiCount = heartEmojiCount;
    }

    public Integer getDisappointedEmojiCount() {
        return disappointedEmojiCount;
    }

    public void setDisappointedEmojiCount(Integer disappointedEmojiCount) {
        this.disappointedEmojiCount = disappointedEmojiCount;
    }

    public Integer getSmileEmojiCount() {
        return smileEmojiCount;
    }

    public void setSmileEmojiCount(Integer smileEmojiCount) {
        this.smileEmojiCount = smileEmojiCount;
    }

    public Integer getAngryEmojiCount() {
        return angryEmojiCount;
    }

    public void setAngryEmojiCount(Integer angryEmojiCount) {
        this.angryEmojiCount = angryEmojiCount;
    }

    public Integer getSmileWithHeartEyesCount() {
        return smileWithHeartEyesCount;
    }

    public void setSmileWithHeartEyesCount(Integer smileWithHeartEyesCount) {
        this.smileWithHeartEyesCount = smileWithHeartEyesCount;
    }

    public Integer getScreamingEmojiCount() {
        return screamingEmojiCount;
    }

    public void setScreamingEmojiCount(Integer screamingEmojiCount) {
        this.screamingEmojiCount = screamingEmojiCount;
    }

    public Boolean getLikeEmoji() {
        return likeEmoji;
    }

    public void setLikeEmoji(Boolean likeEmoji) {
        this.likeEmoji = likeEmoji;
    }

    public Boolean getLaughterEmoji() {
        return laughterEmoji;
    }

    public void setLaughterEmoji(Boolean laughterEmoji) {
        this.laughterEmoji = laughterEmoji;
    }

    public Boolean getHeartEmoji() {
        return heartEmoji;
    }

    public void setHeartEmoji(Boolean heartEmoji) {
        this.heartEmoji = heartEmoji;
    }

    public Boolean getDisappointedEmoji() {
        return disappointedEmoji;
    }

    public void setDisappointedEmoji(Boolean disappointedEmoji) {
        this.disappointedEmoji = disappointedEmoji;
    }

    public Boolean getSmileEmoji() {
        return smileEmoji;
    }

    public void setSmileEmoji(Boolean smileEmoji) {
        this.smileEmoji = smileEmoji;
    }

    public Boolean getAngryEmoji() {
        return angryEmoji;
    }

    public void setAngryEmoji(Boolean angryEmoji) {
        this.angryEmoji = angryEmoji;
    }

    public Boolean getSmileWithHeartEyes() {
        return smileWithHeartEyes;
    }

    public void setSmileWithHeartEyes(Boolean smileWithHeartEyes) {
        this.smileWithHeartEyes = smileWithHeartEyes;
    }

    public Boolean getScreamingEmoji() {
        return screamingEmoji;
    }

    public void setScreamingEmoji(Boolean screamingEmoji) {
        this.screamingEmoji = screamingEmoji;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getAuthor() {
        return isAuthor;
    }

    public void setAuthor(Boolean author) {
        isAuthor = author;
    }

    public static final Comparator<Post> COMPARE_BY_TIME = new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = format.parse(post1.getCreatedDate());
                date2 = format.parse(post2.getCreatedDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date1 != null;
            assert date2 != null;
            Long timestamp1 = date1.getTime();
            Long timestamp2 = date2.getTime();
            return (int) (timestamp2 - timestamp1);
        }
    };
}