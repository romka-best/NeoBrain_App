package com.example.neobrain;

// Импортируем нужные библиотеки

import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;

import retrofit2.Call;

// Класс для работы с API NeoBrain сервера
public class DataManager {
    private static DataManager instance;
    private APIService mAPIService;

    private DataManager() {
        mAPIService = ServiceConstructor.CreateService(APIService.class);
    }

    public static DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public Call<UserModel> getUser(String nickname) {
        return mAPIService.getUser(
                nickname
        );
    }

    public Call<Photo> getPhoto(Integer photo_id) {
        return mAPIService.getPhoto(
                photo_id
        );
    }

    public Call<Chat> getChat(Integer chat_id) {
        return mAPIService.getChat(
                chat_id
        );
    }


    public Call<ChatModel> getChats(String nickname) {
        return mAPIService.getChats(
                nickname
        );
    }

    public Call<PostModel> getPosts(String nickname) {
        return mAPIService.getPosts(
                nickname
        );
    }

    public Call<Status> editUser(String nickname, User user) {
        return mAPIService.editUser(
                nickname,
                user
        );
    }

    public Call<Status> login(User user) {
        return mAPIService.login(
                user
        );
    }

    public Call<Status> createUser(User user) {
        return mAPIService.createUser(
                user
        );
    }

    public Call<UserModel> searchUser(String user_name_surname) {
        return mAPIService.searchUser(
                user_name_surname
        );
    }

    public Call<Status> createChat(Chat chat) {
        return mAPIService.createChat(
                chat
        );
    }

    public Call<Status> deletePhoto(Integer photo_id) {
        return mAPIService.deletePhoto(
                photo_id
        );
    }

    public Call<Status> createPost(Post post) {
        return mAPIService.createPost(
                post
        );
    }
}
