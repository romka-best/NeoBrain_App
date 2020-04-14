package com.example.neobrain;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.Controllers.AuthController;
import com.example.neobrain.Controllers.HomeController;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Создаём единственную активность на всё приложение
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.controller_container)
    ViewGroup container;

    private Router router;

    public static final String MY_SETTINGS = "my_settings";
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Перед запуском приложения мы загружаем SplashTheme, для красивой загрузки.
        // Здесь же мы запускаем тему самого приложения
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        // Инициализируем роутер
        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            // Если пользователь уже авторизовался, то запускаем сразу HomeController, иначе AuthController
            boolean hasVisited = sp.getBoolean("hasAuthed", false);
            if (hasVisited) {
                setOnline();
                router.setRoot(RouterTransaction.with(new HomeController()));
            } else {
                router.setRoot(RouterTransaction.with(new AuthController()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }

    private void setOnline() {
        Integer userIdSP = sp.getInt("userId", -1);
        User user = new User();
        user.setStatus(1);
        Call<Status> call = DataManager.getInstance().editUser(userIdSP, user);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {

            }

            @Override
            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

            }
        });
    }

    private void setOffline() {
        Integer userIdSP = sp.getInt("userId", -1);
        User user = new User();
        user.setStatus(0);
        Call<Status> call = DataManager.getInstance().editUser(userIdSP, user);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {

            }

            @Override
            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean hasVisited = sp.getBoolean("hasAuthed", false);
        if (hasVisited) {
            setOnline();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean hasVisited = sp.getBoolean("hasAuthed", false);
        if (hasVisited) {
            setOffline();
        }
    }
}
