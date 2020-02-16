package com.example.neobrain;

import com.example.neobrain.API.APIConfig;
import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.UserModel;

import java.util.List;

import retrofit2.Call;

public class DataManager {
    private static DataManager instance;
    private APIService mAPIService;

    private DataManager(){
        mAPIService = ServiceConstructor.CreateService(APIService.class);
    }

    public static DataManager getInstance() {
        if(instance != null)
            instance = new DataManager();
        return instance;
    }

    public Call<List<UserModel>> getUser(String nickname){
        return mAPIService.getUser(
                nickname,
                APIConfig.FORMAT
        );
    }

    public Call<UserModel> login(String nickname, String password){
        return mAPIService.login(
                nickname,
                password
        );
    }

    public Call<UserModel> register(UserModel user){
        return mAPIService.register(
                user
        );
    }
}
