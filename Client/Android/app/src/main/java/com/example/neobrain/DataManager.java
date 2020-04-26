package com.example.neobrain;

// Импортируем нужные библиотеки

import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.ChatUsers;
import com.example.neobrain.API.model.Message;
import com.example.neobrain.API.model.Messages;
import com.example.neobrain.API.model.People;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.API.model.Users;

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

    public Call<UserModel> getUser(Integer user_id) {
        return mAPIService.getUser(
                user_id
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

    public Call<ChatUsers> searchUsersInChat(Integer chat_id) {
        return mAPIService.searchUsersInChat(
                chat_id
        );
    }

    public Call<People> getPeople(Integer user_id) {
        return mAPIService.getPeople(
                user_id
        );
    }


    public Call<ChatModel> getChats(Integer user_id) {
        return mAPIService.getChats(
                user_id
        );
    }

    public Call<PostModel> getPosts(Integer user_id) {
        return mAPIService.getPosts(
                user_id
        );
    }

    public Call<Status> editUser(Integer user_id, User user) {
        return mAPIService.editUser(
                user_id,
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

    public Call<Users> searchUser(String user_name_surname) {
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

    public Call<Message> getMessage(Integer message_id) {
        return mAPIService.getMessage(message_id);
    }

    public Call<Messages> getMessages(Integer chat_id) {
        return mAPIService.getMessages(chat_id);
    }
}
