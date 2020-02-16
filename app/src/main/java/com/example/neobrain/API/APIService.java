package com.example.neobrain.API;

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

    @GET("/{nickname}{format}")
    Call<List<UserModel>> getUser(
            @Path("nickname") String nickname,
            @Path("format") String format);

    @FormUrlEncoded
    @POST("/login")
    Call<UserModel> login(
            @Field("nickname") String nickname,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/register")
    Call<UserModel> register(
            @Body UserModel user
    );
}
