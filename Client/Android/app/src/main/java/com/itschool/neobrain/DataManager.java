package com.itschool.neobrain;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;

import com.itschool.neobrain.API.APIService;
import com.itschool.neobrain.API.ServiceConstructor;
import com.itschool.neobrain.API.models.Achievement;
import com.itschool.neobrain.API.models.Achievements;
import com.itschool.neobrain.API.models.Apps;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.API.models.ChatModel;
import com.itschool.neobrain.API.models.ChatUsers;
import com.itschool.neobrain.API.models.Chats;
import com.itschool.neobrain.API.models.CoronaModel;
import com.itschool.neobrain.API.models.Coronas;
import com.itschool.neobrain.API.models.Message;
import com.itschool.neobrain.API.models.Messages;
import com.itschool.neobrain.API.models.People;
import com.itschool.neobrain.API.models.PeopleModel;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.Post;
import com.itschool.neobrain.API.models.PostList;
import com.itschool.neobrain.API.models.PostModel;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserApp;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.API.models.Users;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.Observable;
import retrofit2.Call;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Класс для работы с API NeoBrain сервера */
public class DataManager {
    /* Создаём необходимые поля и методы, позволяющие запрашивать необходимые данные с сервера */
    private static DataManager instance;
    private APIService mAPIService;
    private SharedPreferences sp;

    private DataManager() {
        mAPIService = ServiceConstructor.CreateService(APIService.class);
        sp = Objects.requireNonNull(MainActivity.getContextOfApplication()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
    }

    public static DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public Call<UserModel> getUser(Integer userId) {
        return mAPIService.getUser(
                sp.getString("token", ""),
                userId
        );
    }

    public Call<Status> createUser(User user) {
        return mAPIService.createUser(
                user
        );
    }

    public Call<Status> editUser(Integer userId, User user) {
        return mAPIService.editUser(
                sp.getString("token", ""),
                userId,
                user
        );
    }

    public Call<Status> login(User user) {
        return mAPIService.login(
                user
        );
    }

    public Call<Status> auth(String authorization) {
        return mAPIService.auth(authorization);
    }

    public Call<Status> logout(String authorization) {
        return mAPIService.logout(
                authorization
        );
    }

    public Call<Status> sendEmail(User user) {
        return mAPIService.sendEmail(user);
    }

    public Call<Users> searchUser(String userNameSurname) {
        return mAPIService.searchUser(
                sp.getString("token", ""),
                userNameSurname
        );
    }

    public Call<Photo> getPhoto(Integer photoId) {
        return mAPIService.getPhoto(
                sp.getString("token", ""),
                photoId
        );
    }

    public Call<Status> deletePhoto(Integer photoId) {
        return mAPIService.deletePhoto(
                sp.getString("token", ""),
                photoId
        );
    }

    public Call<ChatModel> getChat(Integer chatId) {
        return mAPIService.getChat(
                sp.getString("token", ""),
                chatId
        );
    }

    public Call<Chats> getChats(Integer userId) {
        return mAPIService.getChats(
                sp.getString("token", ""),
                userId
        );
    }

    public Call<Status> createChat(Chat chat) {
        return mAPIService.createChat(
                sp.getString("token", ""),
                chat
        );
    }

    public Call<Status> editChat(Integer chatId, Chat chat) {
        return mAPIService.editChat(
                sp.getString("token", ""),
                chatId,
                chat
        );
    }

    public Call<ChatUsers> searchUsersInChat(Integer chatId) {
        return mAPIService.searchUsersInChat(
                sp.getString("token", ""),
                chatId
        );
    }

    public Call<People> getPeople(Integer userId) {
        return mAPIService.getPeople(
                sp.getString("token", ""),
                userId
        );
    }

    public Call<Status> deletePeople(Integer userId1, Integer userId2) {
        return mAPIService.deletePeople(
                sp.getString("token", ""),
                userId1,
                userId2
        );
    }

    public Call<Status> createPeople(PeopleModel people) {
        return mAPIService.createPeople(
                sp.getString("token", ""),
                people);
    }

    public Call<PostModel> getPost(Integer postId) {
        return mAPIService.getPost(
                sp.getString("token", ""),
                postId);
    }

    public Call<PostList> getPosts(Integer authorId, Integer userId) {
        return mAPIService.getPosts(
                sp.getString("token", ""),
                authorId,
                userId
        );
    }

    public Call<Status> createPost(Post post) {
        return mAPIService.createPost(
                sp.getString("token", ""),
                post
        );
    }

    public Call<Status> editPost(Integer postId, Post post) {
        return mAPIService.editPost(sp.getString("token", ""),
                postId,
                post);
    }

    public Call<Status> deletePost(Integer postId) {
        return mAPIService.deletePost(
                sp.getString("token", ""),
                postId);
    }

    public Call<PostList> getLenta(Integer userId) {
        return mAPIService.getLenta(
                sp.getString("token", ""),
                userId
        );
    }

    public Call<Users> getUsersChats(Integer userId) {
        return mAPIService.getUsersChats(
                sp.getString("token", ""),
                userId
        );
    }

    public Call<ChatModel> getUsersChat(Integer userId1, Integer userId2) {
        return mAPIService.getUsersChat(
                sp.getString("token", ""),
                userId1,
                userId2
        );
    }

    public Call<Status> createMessage(Message message) {
        return mAPIService.createMessage(
                sp.getString("token", ""),
                message
        );
    }

    public Call<Message> getMessage(Integer messageId) {
        return mAPIService.getMessage(
                sp.getString("token", ""),
                messageId);
    }

    public Call<Status> editMessage(Integer messageId, Message message) {
        return mAPIService.editMessage(
                sp.getString("token", ""),
                messageId,
                message
        );
    }

    public Call<Achievements> getAchievements(Integer userId) {
        return mAPIService.getAchievements(
                sp.getString("token", ""),
                userId);
    }

    public Call<Status> editAchievements(Integer userId, Achievement achievement) {
        return mAPIService.editAchievements(
                sp.getString("token", ""),
                userId,
                achievement);
    }

    public Observable<Messages> getMessages(Integer chatId) {
        return mAPIService.getMessages(
                sp.getString("token", ""),
                chatId);
    }

    public Call<Apps> getMyApps(Integer userId) {
        return mAPIService.getMyApps(
                sp.getString("token", ""),
                userId);
    }

    public Call<Status> deleteApp(Integer userId, Integer appId) {
        return mAPIService.deleteApp(sp.getString("token", ""),
                userId,
                appId);
    }

    public Call<Status> addApp(UserApp userApp) {
        return mAPIService.addApp(
                sp.getString("token", ""),
                userApp);
    }

    public Call<Apps> getOtherApps() {
        return mAPIService.getOtherApps();
    }

    public Call<Apps> searchApp(String appName) {
        return mAPIService.searchApp(
                sp.getString("token", ""),
                appName);
    }

    public Call<CoronaModel> getOneCoronaCountry(Integer country_id) {
        return mAPIService.getOneCoronaCountry(country_id);
    }

    public Call<Coronas> getAllCoronaCountry() {
        return mAPIService.getAllCoronaCountry();
    }
}
