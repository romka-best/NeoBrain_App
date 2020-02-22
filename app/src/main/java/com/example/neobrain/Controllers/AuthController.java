package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

public class AuthController extends Controller {
    private boolean isAuth = false;
    private TextView textLogin;
    private TextView textPassword;



    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.auth_controller, container, false);
        ButterKnife.bind(this, view);
        textLogin = view.findViewById(R.id.login);
        textPassword = view.findViewById(R.id.password);
        return view;
    }


    @OnClick({R.id.regButton}) void launchReg() {
        getRouter().pushController(RouterTransaction.with(new RegController())
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler()));
    }

    @OnClick(R.id.authButton) void launchAuth() {
        String login = textLogin.getText().toString();
        String password = textPassword.getText().toString();
        if(isPasswordValid(password)){
        Call<UserModel> call = DataManager.getInstance().login(login, password);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                int code = response.code();
                if (code == 200){
                    Toast.makeText(getApplicationContext(), "Удачно", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
            }
        });}
    }


    private boolean isPasswordValid(String text) {
        return text != null && text.length() >= 8;
    }



//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("isAuth", isAuth);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        isAuth = savedInstanceState.getBoolean("isAuth");
//    }
}
