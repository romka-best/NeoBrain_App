package com.example.neobrain;

// Импортируем нужные библиотеки

import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.Apps;
import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.API.model.Achievements;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.ChatUsers;
import com.example.neobrain.API.model.Chats;
import com.example.neobrain.API.model.Message;
import com.example.neobrain.API.model.Messages;
import com.example.neobrain.API.model.People;
import com.example.neobrain.API.model.PeopleModel;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserApp;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.API.model.Users;

import io.reactivex.Observable;
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

    public Call<Status> createUser(User user) {
        return mAPIService.createUser(
                user
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

    public Call<Status> sendEmail(User user) {
        return mAPIService.sendEmail(user);
    }

    public Call<Users> searchUser(String user_name_surname) {
        return mAPIService.searchUser(
                user_name_surname
        );
    }

    public Call<Photo> getPhoto(Integer photo_id) {
        return mAPIService.getPhoto(
                photo_id
        );
    }

    public Call<Status> deletePhoto(Integer photo_id) {
        return mAPIService.deletePhoto(
                photo_id
        );
    }

    public Call<ChatModel> getChat(Integer chat_id) {
        return mAPIService.getChat(
                chat_id
        );
    }

    public Call<Chats> getChats(Integer user_id) {
        return mAPIService.getChats(
                user_id
        );
    }

    public Call<Status> createChat(Chat chat) {
        return mAPIService.createChat(
                chat
        );
    }

    public Call<Status> editChat(Integer chat_id, Chat chat) {
        return mAPIService.editChat(
                chat_id,
                chat
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

    public Call<Status> deletePeople(Integer user_id1, Integer user_id2) {
        return mAPIService.deletePeople(
                user_id1,
                user_id2
        );
    }

    public Call<Status> createPeople(PeopleModel people) {
        return mAPIService.createPeople(people);
    }

    public Call<PostModel> getPosts(Integer user_id) {
        return mAPIService.getPosts(
                user_id
        );
    }

    public Call<Status> createPost(Post post) {
        return mAPIService.createPost(
                post
        );
    }

    public Call<Status> deletePost(Integer post_id) {
        return mAPIService.deletePost(post_id);
    }

    public Call<PostModel> getLenta(Integer user_id) {
        return mAPIService.getLenta(
                user_id
        );
    }

    public Call<Users> getUsersChats(Integer user_id) {
        return mAPIService.getUsersChats(
                user_id
        );
    }

    public Call<ChatModel> getUsersChat(Integer user_id1, Integer user_id2) {
        return mAPIService.getUsersChat(
                user_id1,
                user_id2
        );
    }

    public Call<Status> createMessage(Message message) {
        return mAPIService.createMessage(
                message
        );
    }

    public Call<Message> getMessage(Integer message_id) {
        return mAPIService.getMessage(message_id);
    }

    public Call<Status> editMessage(Integer message_id, Message message) {
        return mAPIService.editMessage(
                message_id,
                message
        );
    }

    public Call<Achievements> getAchievements(Integer user_id) {
        return mAPIService.getAchievements(user_id);
    }

    public Call<Status> editAchievements(Integer user_id, Achievement achievement) {
        return mAPIService.editAchievements(user_id, achievement);
    }

    public Observable<Messages> getMessages(Integer chat_id) {
        return mAPIService.getMessages(chat_id);
    }

    public Call<Apps> getMyApps(Integer user_id) {
        return mAPIService.getMyApps(user_id);
    }

    public Call<Status> deleteApp(Integer user_id, Integer app_id) {
        return mAPIService.deleteApp(user_id, app_id);
    }

    public Call<Status> addApp(UserApp userApp) {
        return mAPIService.addApp(userApp);
    }

    public Call<Apps> getOtherApps() {
        return mAPIService.getOtherApps();
    }
}
