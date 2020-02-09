package com.example.neobrain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

public class AuthController extends Controller implements View.OnClickListener{
    private boolean isAuth = false;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.auth_controller, container, false);
        Button loginButton = view.findViewById(R.id.authButton);
        Button regButton = view.findViewById(R.id.regButton);
        loginButton.setOnClickListener(this);
        regButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.authButton:
                Toast.makeText(getApplicationContext(), "AUTH", Toast.LENGTH_LONG).show();
                break;
            case R.id.regButton:
                getRouter().pushController(RouterTransaction.with(new RegController())
                        .popChangeHandler(new FadeChangeHandler())
                        .pushChangeHandler(new FadeChangeHandler()));
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isAuth", isAuth);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isAuth = savedInstanceState.getBoolean("isAuth");
    }
}
