package com.example.neobrain.API;

import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.UserModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("users/{id}")
    Call<UserModel> getUser(
            @Path("id") Integer id);

    @POST("users/create")
    Call<Status> createUser(
            @Body UserModel userModel
    );

    @POST("users/login")
    Call<Status> login(
            @Body UserModel userModel
    );
}
