package com.itschool.neobrain.API;

// Импортируем нужные библиотеки

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

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/* Интерфейс для работы с API NeoBrain */
public interface APIService {

    @GET("users/{userId}")
    Call<UserModel> getUser(
            @Header("Authorization") String token,
            @Path("userId") Integer userId);

    @POST("users")
    Call<Status> createUser(
            @Body User user
    );

    @POST("users/login")
    Call<Status> login(
            @Body User user
    );

//    @FormUrlEncoded
//    @POST("auth")
//    Call<Status> getAuthenticationToken(@FieldMap HashMap<String, String> map);

    @POST("auth")
    Call<Status> auth(
            @Header("Authorization") String authorization
    );

    @POST("logout")
    Call<Status> logout(
            @Header("Authorization") String authorization
    );

    @POST("send_email")
    Call<Status> sendEmail(
            @Body User user
    );

    @PUT("users/{userId}")
    Call<Status> editUser(
            @Header("Authorization") String token,
            @Path("userId") Integer userId,
            @Body User user
    );

    @DELETE("users/{userId}")
    Call<Status> deleteUser(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @GET("users/search/{userNameSurname}")
    Call<Users> searchUser(
            @Header("Authorization") String token,
            @Path("userNameSurname") String userNameSurname
    );

    @GET("chats/users_id/{userId}")
    Call<Users> getUsersChats(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @GET("chats/two_users/{userId1}/{userId2}")
    Call<ChatModel> getUsersChat(
            @Header("Authorization") String token,
            @Path("userId1") Integer userId1,
            @Path("userId2") Integer userId2
    );

    @GET("people/{userId}")
    Call<People> getPeople(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @POST("people")
    Call<Status> createPeople(
            @Header("Authorization") String token,
            @Body PeopleModel people
    );

    @DELETE("people/{userId1}/{userId2}")
    Call<Status> deletePeople(
            @Header("Authorization") String token,
            @Path("userId1") Integer userId1,
            @Path("userId2") Integer userId2
    );

    @GET("chats/{chatId}")
    Call<ChatModel> getChat(
            @Header("Authorization") String token,
            @Path("chatId") Integer chatId
    );

    @GET("chats/users/{userId}")
    Call<Chats> getChats(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @PUT("chats/{chatId}")
    Call<Status> editChat(
            @Header("Authorization") String token,
            @Path("chatId") Integer chatId,
            @Body Chat chat
    );

    @DELETE("chats/{chatId}")
    Call<Status> deleteChat(
            @Header("Authorization") String token,
            @Path("chatId") Integer chatId
    );

    @GET("chats/search/{chatId}")
    Call<ChatUsers> searchUsersInChat(
            @Header("Authorization") String token,
            @Path("chatId") Integer chatId
    );

    @POST("chats")
    Call<Status> createChat(
            @Header("Authorization") String token,
            @Body Chat chat
    );

    @GET("photos/{photoId}")
    Call<Photo> getPhoto(
            @Header("Authorization") String token,
            @Path("photoId") Integer photoId
    );

    @DELETE("photos/{photoId}")
    Call<Status> deletePhoto(
            @Header("Authorization") String token,
            @Path("photoId") Integer photoId
    );

    @GET("posts/users/{authorId}/{userId}")
    Call<PostList> getPosts(
            @Header("Authorization") String token,
            @Path("authorId") Integer authorId,
            @Path("userId") Integer userId
    );

    @GET("lenta/{userId}")
    Call<PostList> getLenta(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @GET("messages/{messageId}")
    Call<Message> getMessage(
            @Header("Authorization") String token,
            @Path("messageId") Integer messageId
    );

    @GET("chats/{chatId}/messages")
    Observable<Messages> getMessages(
            @Header("Authorization") String token,
            @Path("chatId") Integer chatId
    );

    @GET("posts/{postId}")
    Call<PostModel> getPost(
            @Header("Authorization") String token,
            @Path("postId") Integer postId
    );

    @POST("posts")
    Call<Status> createPost(
            @Header("Authorization") String token,
            @Body Post post
    );

    @PUT("posts/{postId}")
    Call<Status> editPost(
            @Header("Authorization") String token,
            @Path("postId") Integer postId,
            @Body Post post
    );

    @DELETE("posts/{postId}")
    Call<Status> deletePost(
            @Header("Authorization") String token,
            @Path("postId") Integer postId
    );

    @POST("messages")
    Call<Status> createMessage(
            @Header("Authorization") String token,
            @Body Message message
    );

    @PUT("messages/{messageId}")
    Call<Status> editMessage(
            @Header("Authorization") String token,
            @Path("messageId") Integer messageId,
            @Body Message message
    );

    @GET("apps/{userId}")
    Call<Apps> getMyApps(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @DELETE("apps/{userId}/{appId}")
    Call<Status> deleteApp(
            @Header("Authorization") String token,
            @Path("userId") Integer userId,
            @Path("appId") Integer appId
    );

    @POST("apps")
    Call<Status> addApp(
            @Header("Authorization") String token,
            @Body UserApp userApp
    );

    @GET("apps")
    Call<Apps> getOtherApps();

    @GET("apps/search/{appName}")
    Call<Apps> searchApp(
            @Header("Authorization") String token,
            @Path("appName") String appName
    );

    @GET("achievements/user/{userId}")
    Call<Achievements> getAchievements(
            @Header("Authorization") String token,
            @Path("userId") Integer userId
    );

    @PUT("achievements/user/{userId}")
    Call<Status> editAchievements(
            @Header("Authorization") String token,
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
