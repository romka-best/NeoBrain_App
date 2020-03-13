package com.example.neobrain.API;

import androidx.room.Delete;

import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("users/{id}")
    Call<UserModel> getUser(
            @Path("id") Integer id);

    @POST("users")
    Call<Status> createUser(
            @Body User user
    );

    @POST("users/login")
    Call<Status> login(
            @Body User user
    );

    @PUT("users/{id}")
    Call<Status> editUser(
            @Path("id") Integer id,
            @Body User user
    );

    @DELETE("users/{id}")
    Call<Status> deleteUser(
            @Path("id") Integer id
    );
}
