package com.example.neobrain.API;

// Импортируем нужные библиотеки

import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.API.model.Achievements;
import com.example.neobrain.API.model.Apps;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.ChatUsers;
import com.example.neobrain.API.model.Chats;
import com.example.neobrain.API.model.Corona;
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

    @GET("users/{user_id}")
    Call<UserModel> getUser(
            @Path("user_id") Integer user_id);

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

    @PUT("users/{user_id}")
    Call<Status> editUser(
            @Path("user_id") Integer user_id,
            @Body User user
    );

    @DELETE("users/{user_id}")
    Call<Status> deleteUser(
            @Path("user_id") Integer user_id
    );

    @GET("users/search/{user_name_surname}")
    Call<Users> searchUser(
            @Path("user_name_surname") String user_name_surname
    );

    @GET("chats/users_id/{user_id}")
    Call<Users> getUsersChats(
            @Path("user_id") Integer user_id
    );

    @GET("chats/two_users/{user_id1}/{user_id2}")
    Call<ChatModel> getUsersChat(
            @Path("user_id1") Integer user_id1,
            @Path("user_id2") Integer user_id2
    );

    @GET("people/{user_id}")
    Call<People> getPeople(
            @Path("user_id") Integer user_id
    );

    @POST("people")
    Call<Status> createPeople(
            @Body PeopleModel people
    );

    @DELETE("people/{user_id1}/{user_id2}")
    Call<Status> deletePeople(
            @Path("user_id1") Integer user_id1,
            @Path("user_id2") Integer user_id2
    );

    @GET("chats/{chat_id}")
    Call<ChatModel> getChat(
            @Path("chat_id") Integer chat_id
    );

    @GET("chats/users/{user_id}")
    Call<Chats> getChats(
            @Path("user_id") Integer user_id
    );

    @PUT("chats/{chat_id}")
    Call<Status> editChat(
            @Path("chat_id") Integer chat_id,
            @Body Chat chat
    );

    @DELETE("chats/{chat_id}")
    Call<Status> deleteChat(
            @Path("chat_id") Integer chat_id
    );

    @GET("chats/search/{chat_id}")
    Call<ChatUsers> searchUsersInChat(
            @Path("chat_id") Integer chat_id
    );

    @POST("chats")
    Call<Status> createChat(
            @Body Chat chat
    );

    @GET("photos/{photo_id}")
    Call<Photo> getPhoto(
            @Path("photo_id") Integer photo_id
    );

    @DELETE("photos/{photo_id}")
    Call<Status> deletePhoto(
            @Path("photo_id") Integer photo_id
    );

    @GET("posts/users/{author_id}/{user_id}")
    Call<PostList> getPosts(
            @Path("author_id") Integer author_id,
            @Path("user_id") Integer user_id
    );

    @GET("lenta/{user_id}")
    Call<PostList> getLenta(
            @Path("user_id") Integer user_id
    );

    @GET("messages/{message_id}")
    Call<Message> getMessage(
            @Path("message_id") Integer message_id
    );

    @GET("chats/{chat_id}/messages")
    Observable<Messages> getMessages(
            @Path("chat_id") Integer chat_id
    );

    @GET("posts/{post_id}")
    Call<PostModel> getPost(
            @Path("post_id") Integer post_id
    );

    @POST("posts")
    Call<Status> createPost(
            @Body Post post
    );

    @PUT("posts/{post_id}")
    Call<Status> editPost(
            @Path("post_id") Integer post_id,
            @Body Post post
    );

    @DELETE("posts/{post_id}")
    Call<Status> deletePost(
            @Path("post_id") Integer post_id
    );

    @POST("messages")
    Call<Status> createMessage(
            @Body Message message
    );

    @PUT("messages/{message_id}")
    Call<Status> editMessage(
            @Path("message_id") Integer message_id,
            @Body Message message
    );

    @GET("apps/{user_id}")
    Call<Apps> getMyApps(
            @Path("user_id") Integer user_id
    );

    @DELETE("apps/{user_id}/{app_id}")
    Call<Status> deleteApp(
            @Path("user_id") Integer user_id,
            @Path("app_id") Integer app_id
    );

    @POST("apps")
    Call<Status> addApp(
            @Body UserApp userApp
    );

    @GET("apps")
    Call<Apps> getOtherApps();

    @GET("achievements/user/{user_id}")
    Call<Achievements> getAchievements(
            @Path("user_id") Integer user_id
    );

    @PUT("achievements/user/{user_id}")
    Call<Status> editAchievements(
            @Path("user_id") Integer user_id,
            @Body Achievement achievement
    );

    @GET("countries")
    Call<Coronas> getAllCoronaCountry();

    @GET("countries/{country_id}")
    Call<Corona> getOneCoronaCountry(
            @Path("country_id") Integer country_id
    );

}
