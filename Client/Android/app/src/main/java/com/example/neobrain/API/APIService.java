package com.example.neobrain.API;

// Импортируем нужные библиотеки

import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
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

    @GET("people/{user_id}")
    Call<People> getPeople(
            @Path("user_id") Integer user_id
    );

    @GET("chats/{chat_id}")
    Call<Chat> getChat(
            @Path("chat_id") Integer chat_id
    );

    @GET("chats/users/{user_id}")
    Call<ChatModel> getChats(
            @Path("user_id") Integer user_id
    );

    @PUT("chats/{chat_id}")
    Call<Status> editChat(
            @Path("chat_id") Integer chat_id,
            @Body Chat chat
    );

    @DELETE("chats/{chat_id}")
    Call<Status> deleteChat(
            @Path("chat_id") Integer chat_id,
            @Body Chat chat
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

    @GET("posts/users/{user_id}")
    Call<PostModel> getPosts(
            @Path("user_id") Integer user_id
    );

    @GET("messages/{message_id}")
    Call<Message> getMessage(
            @Path("message_id") Integer message_id
    );

    @GET("chats/{chat_id}/messages")
    Call<Messages> getMessages(
            @Path("chat_id") Integer chat_id
    );

    @GET("posts/{post_id}")
    Call<Post> getPost(
            @Path("post_id") Integer post_id
    );

    @POST("posts")
    Call<Status> createPost(
            @Body Post post
    );
}
