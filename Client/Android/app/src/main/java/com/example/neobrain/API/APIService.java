package com.example.neobrain.API;

import com.example.neobrain.API.model.StatusResponse;
import com.example.neobrain.API.model.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("/users/{id}")
    Call<UserModel> getUser(
            @Path("id") Integer id);

    @FormUrlEncoded
    @POST("/users/create")
    Call<UserModel> createUser(
            @Field("name") String name,
            @Field("surname") String surname,
            @Field("nickname") String nickname,
            @Field("number") String number,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/users/login")
    Call<UserModel> login(
            @Field("number") String number,
            @Field("password") String password
    );
}
