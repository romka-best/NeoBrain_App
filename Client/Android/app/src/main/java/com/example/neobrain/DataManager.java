package com.example.neobrain;

import com.example.neobrain.API.APIConfig;
import com.example.neobrain.API.APIService;
import com.example.neobrain.API.ServiceConstructor;
import com.example.neobrain.API.model.StatusResponse;
import com.example.neobrain.API.model.UserModel;

import java.util.List;

import retrofit2.Call;

public class DataManager {
    private static DataManager instance;
    private APIService mAPIService;

    private DataManager() {
        mAPIService = ServiceConstructor.CreateService(APIService.class);
    }

    public static DataManager getInstance() {
        if (instance != null)
            instance = new DataManager();
        return instance;
    }

    public Call<UserModel> getUser(Integer id) {
        return mAPIService.getUser(
                id
        );
    }

    public Call<UserModel> login(String number, String password) {
        return mAPIService.login(
                number,
                password
        );
    }

    public Call<UserModel> createUser(String name, String surname, String nickname, String number, String password) {
        return mAPIService.createUser(
                name,
                surname,
                nickname,
                number,
                password
        );
    }
}
