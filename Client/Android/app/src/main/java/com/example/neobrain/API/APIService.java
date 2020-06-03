package com.example.neobrain.API;

// Импортируем нужные библиотеки

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
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

// Интерфейс для работы с API NeoBrain
public interface APIService {

    @GET("users/{userId}")
    Call<UserModel> getUser(
            @Path("userId") Integer userId);

    @POST("users")
    Call<Status> createUser(
            @Body User user
    );

    @POST("users/login")
    Call<Status> login(
            @Body User user
    );

    @POST("send_email")
    Call<Status> sendEmail(
            @Body User user
    );

    @PUT("users/{userId}")
    Call<Status> editUser(
            @Path("userId") Integer userId,
            @Body User user
    );

    @DELETE("users/{userId}")
    Call<Status> deleteUser(
            @Path("userId") Integer userId
    );

    @GET("users/search/{userNameSurname}")
    Call<Users> searchUser(
            @Path("userNameSurname") String userNameSurname
    );

    @GET("chats/users_id/{userId}")
    Call<Users> getUsersChats(
            @Path("userId") Integer userId
    );

    @GET("chats/two_users/{userId1}/{userId2}")
    Call<ChatModel> getUsersChat(
            @Path("userId1") Integer userId1,
            @Path("userId2") Integer userId2
    );

    @GET("people/{userId}")
    Call<People> getPeople(
            @Path("userId") Integer userId
    );

    @POST("people")
    Call<Status> createPeople(
            @Body PeopleModel people
    );

    @DELETE("people/{userId1}/{userId2}")
    Call<Status> deletePeople(
            @Path("userId1") Integer userId1,
            @Path("userId2") Integer userId2
    );

    @GET("chats/{chatId}")
    Call<ChatModel> getChat(
            @Path("chatId") Integer chatId
    );

    @GET("chats/users/{userId}")
    Call<Chats> getChats(
            @Path("userId") Integer userId
    );

    @PUT("chats/{chatId}")
    Call<Status> editChat(
            @Path("chatId") Integer chatId,
            @Body Chat chat
    );

    @DELETE("chats/{chatId}")
    Call<Status> deleteChat(
            @Path("chatId") Integer chatId
    );

    @GET("chats/search/{chatId}")
    Call<ChatUsers> searchUsersInChat(
            @Path("chatId") Integer chatId
    );

    @POST("chats")
    Call<Status> createChat(
            @Body Chat chat
    );

    @GET("photos/{photoId}")
    Call<Photo> getPhoto(
            @Path("photoId") Integer photoId
    );

    @DELETE("photos/{photoId}")
    Call<Status> deletePhoto(
            @Path("photoId") Integer photoId
    );

    @GET("posts/users/{authorId}/{userId}")
    Call<PostList> getPosts(
            @Path("authorId") Integer authorId,
            @Path("userId") Integer userId
    );

    @GET("lenta/{userId}")
    Call<PostList> getLenta(
            @Path("userId") Integer userId
    );

    @GET("messages/{messageId}")
    Call<Message> getMessage(
            @Path("messageId") Integer messageId
    );

    @GET("chats/{chatId}/messages")
    Observable<Messages> getMessages(
            @Path("chatId") Integer chatId
    );

    @GET("posts/{postId}")
    Call<PostModel> getPost(
            @Path("postId") Integer postId
    );

    @POST("posts")
    Call<Status> createPost(
            @Body Post post
    );

    @PUT("posts/{postId}")
    Call<Status> editPost(
            @Path("postId") Integer postId,
            @Body Post post
    );

    @DELETE("posts/{postId}")
    Call<Status> deletePost(
            @Path("postId") Integer postId
    );

    @POST("messages")
    Call<Status> createMessage(
            @Body Message message
    );

    @PUT("messages/{messageId}")
    Call<Status> editMessage(
            @Path("messageId") Integer messageId,
            @Body Message message
    );

    @GET("apps/{userId}")
    Call<Apps> getMyApps(
            @Path("userId") Integer userId
    );

    @DELETE("apps/{userId}/{appId}")
    Call<Status> deleteApp(
            @Path("userId") Integer userId,
            @Path("appId") Integer appId
    );

    @POST("apps")
    Call<Status> addApp(
            @Body UserApp userApp
    );

    @GET("apps")
    Call<Apps> getOtherApps();

    @GET("apps/search/{appName}")
    Call<Apps> searchApp(
            @Path("appName") String appName
    );

    @GET("achievements/user/{userId}")
    Call<Achievements> getAchievements(
            @Path("userId") Integer userId
    );

    @PUT("achievements/user/{userId}")
    Call<Status> editAchievements(
            @Path("userId") Integer userId,
            @Body Achievement achievement
    );

    @GET("countries")
    Call<Coronas> getAllCoronaCountry();

    @GET("countries/{country_id}")
    Call<CoronaModel> getOneCoronaCountry(
            @Path("country_id") Integer country_id
    );

}
