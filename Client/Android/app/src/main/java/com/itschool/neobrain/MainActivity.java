package com.itschool.neobrain;

// Импортируем нужные библиотеки

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.controllers.AuthController;
import com.itschool.neobrain.controllers.HomeController;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Создаём единственную активность на всё приложение */
public class MainActivity extends AppCompatActivity {
    /* Создаём необходимые поля */
    @BindView(R.id.controller_container)
    ViewGroup container;

    private Router router;

    public static final String MY_SETTINGS = "my_settings";
    public static Context contextOfApplication;
    SharedPreferences sp;
    Integer userIdSP;

    public static final int PERMISSION_REQUEST_CODE = 1001;

    /* Метод, вызываемый при первом создании активности */
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
        contextOfApplication = getApplicationContext();

        requestMultiplePermissions();

        // Проводим тур для пользователя если он не видел его
        if (!sp.getBoolean("watchedIntro", false)) {
            Intent intent = new Intent(MainActivity.getContextOfApplication(), NeoIntro.class);
            startActivity(intent);
        }
        // Инициализируем роутер
        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            // Если пользователь уже авторизовался, то запускаем сразу HomeController, иначе AuthController
            boolean hasVisited = sp.getBoolean("hasAuthed", false);
            if (hasVisited) {
                router.setRoot(RouterTransaction.with(new HomeController()));
            } else {
                router.setRoot(RouterTransaction.with(new AuthController()));
            }
        }
    }

    /* Обработчик нажатия кнопки назад на устройстве, непозволяющий выйти из приложения */
    @Override
    public void onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }

    /* Запрашиваем доступ к необходим ресурсам устройства */
    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.VIBRATE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                },
                PERMISSION_REQUEST_CODE);
    }

    /* Метод, ставящий на активном пользователе метку "в сети" */
    private void setOnline() {
        userIdSP = sp.getInt("userId", -1);
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

    /* Метод, ставящий на пользователе метку "не в сети", если пользователь вышёл из приложения */
    private void setOffline() {
        userIdSP = sp.getInt("userId", -1);
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

    /* Вызывается перед тем, как пользователю станет видна активность */
    @Override
    protected void onStart() {
        super.onStart();
        boolean hasVisited = sp.getBoolean("hasAuthed", false);
        if (hasVisited) {
            setOnline();
        }
    }

    /* Вызывается когда Activity становится не видно пользователю */
    @Override
    protected void onStop() {
        super.onStop();
        boolean hasVisited = sp.getBoolean("hasAuthed", false);
        if (hasVisited) {
            setOffline();
        }
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
