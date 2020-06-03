package com.example.neobrain;

// Импортируем нужные библиотеки

import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.API.model.Achievements;
import com.example.neobrain.API.model.Apps;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.ChatUsers;
import com.example.neobrain.API.model.Chats;
import com.example.neobrain.API.model.CoronaModel;
import com.example.neobrain.API.model.Coronas;
import com.example.neobrain.API.model.Message;
import com.example.neobrain.API.model.Messages;
import com.example.neobrain.API.model.People;
import com.example.neobrain.API.model.PeopleModel;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostList;
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

    public Call<UserModel> getUser(Integer userId) {
        return mAPIService.getUser(
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
                userId,
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

    public Call<Users> searchUser(String userNameSurname) {
        return mAPIService.searchUser(
                userNameSurname
        );
    }

    public Call<Photo> getPhoto(Integer photoId) {
        return mAPIService.getPhoto(
                photoId
        );
    }

    public Call<Status> deletePhoto(Integer photoId) {
        return mAPIService.deletePhoto(
                photoId
        );
    }

    public Call<ChatModel> getChat(Integer chatId) {
        return mAPIService.getChat(
                chatId
        );
    }

    public Call<Chats> getChats(Integer userId) {
        return mAPIService.getChats(
                userId
        );
    }

    public Call<Status> createChat(Chat chat) {
        return mAPIService.createChat(
                chat
        );
    }

    public Call<Status> editChat(Integer chatId, Chat chat) {
        return mAPIService.editChat(
                chatId,
                chat
        );
    }

    public Call<ChatUsers> searchUsersInChat(Integer chatId) {
        return mAPIService.searchUsersInChat(
                chatId
        );
    }

    public Call<People> getPeople(Integer userId) {
        return mAPIService.getPeople(
                userId
        );
    }

    public Call<Status> deletePeople(Integer userId1, Integer userId2) {
        return mAPIService.deletePeople(
                userId1,
                userId2
        );
    }

    public Call<Status> createPeople(PeopleModel people) {
        return mAPIService.createPeople(people);
    }

    public Call<PostModel> getPost(Integer postId) {
        return mAPIService.getPost(postId);
    }

    public Call<PostList> getPosts(Integer authorId, Integer userId) {
        return mAPIService.getPosts(
                authorId,
                userId
        );
    }

    public Call<Status> createPost(Post post) {
        return mAPIService.createPost(
                post
        );
    }

    public Call<Status> editPost(Integer postId, Post post) {
        return mAPIService.editPost(postId, post);
    }

    public Call<Status> deletePost(Integer postId) {
        return mAPIService.deletePost(postId);
    }

    public Call<PostList> getLenta(Integer userId) {
        return mAPIService.getLenta(
                userId
        );
    }

    public Call<Users> getUsersChats(Integer userId) {
        return mAPIService.getUsersChats(
                userId
        );
    }

    public Call<ChatModel> getUsersChat(Integer userId1, Integer userId2) {
        return mAPIService.getUsersChat(
                userId1,
                userId2
        );
    }

    public Call<Status> createMessage(Message message) {
        return mAPIService.createMessage(
                message
        );
    }

    public Call<Message> getMessage(Integer messageId) {
        return mAPIService.getMessage(messageId);
    }

    public Call<Status> editMessage(Integer messageId, Message message) {
        return mAPIService.editMessage(
                messageId,
                message
        );
    }

    public Call<Achievements> getAchievements(Integer userId) {
        return mAPIService.getAchievements(userId);
    }

    public Call<Status> editAchievements(Integer userId, Achievement achievement) {
        return mAPIService.editAchievements(userId, achievement);
    }

    public Observable<Messages> getMessages(Integer chatId) {
        return mAPIService.getMessages(chatId);
    }

    public Call<Apps> getMyApps(Integer userId) {
        return mAPIService.getMyApps(userId);
    }

    public Call<Status> deleteApp(Integer userId, Integer appId) {
        return mAPIService.deleteApp(userId, appId);
    }

    public Call<Status> addApp(UserApp userApp) {
        return mAPIService.addApp(userApp);
    }

    public Call<Apps> getOtherApps() {
        return mAPIService.getOtherApps();
    }

    public Call<Apps> searchApp(String appName) {
        return mAPIService.searchApp(appName);
    }

    public Call<CoronaModel> getOneCoronaCountry(Integer country_id) {
        return mAPIService.getOneCoronaCountry(country_id);
    }

    public Call<Coronas> getAllCoronaCountry() {
        return mAPIService.getAllCoronaCountry();
    }
}
