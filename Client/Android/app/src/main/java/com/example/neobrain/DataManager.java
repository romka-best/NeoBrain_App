package com.example.neobrain;

import com.example.neobrain.API.APIConfig;
import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.UserModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataManager {
    private static DataManager instance;
    private APIService mAPIService;

    private DataManager() {
        mAPIService = ServiceConstructor.CreateService(APIService.class);
    }

    public static DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    public Call<UserModel> getUser(Integer id) {
        return mAPIService.getUser(
                id
        );
    }

    public Call<Status> login(UserModel userModel) {
        return mAPIService.login(
                userModel
        );
    }

    public Call<Status> createUser(UserModel userModel) {
        return mAPIService.createUser(
                userModel
        );
    }
}
