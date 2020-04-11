package com.example.neobrain.API;

// Импортируем нужные библиотеки
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

// Интерфейс для работы с API NeoBrain
public interface APIService {

    @GET("users/{nickname}")
    Call<UserModel> getUser(
            @Path("nickname") String nickname);

    @POST("users")
    Call<Status> createUser(
            @Body User user
    );

    @POST("users/login")
    Call<Status> login(
            @Body User user
    );

    @POST("users/logout")
    Call<Status> logout(
            @Body User user
    );

    @PUT("users/{nickname}")
    Call<Status> editUser(
            @Path("nickname") String nickname,
            @Body User user
    );

    @DELETE("users/{nickname}")
    Call<Status> deleteUser(
            @Path("nickname") String nickname
    );

    @GET("chats/{chat_id}")
    Call<Chat> getChat(
            @Path("chat_id") Integer chat_id
    );

    @GET("chats/{nickname}")
    Call<ChatModel> getChats(
            @Path("nickname") String nickname
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

    @GET("posts/{nickname}")
    Call<PostModel> getPosts(
            @Path("nickname") String nickname
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
