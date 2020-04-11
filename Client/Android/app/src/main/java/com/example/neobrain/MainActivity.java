package com.example.neobrain;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.neobrain.Controllers.AuthController;
import com.example.neobrain.Controllers.HomeController;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        // Инициализируем роутер
        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            // Если пользователь уже авторизовался, то запускаем сразу HomeController, иначе AuthController
            sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                    Context.MODE_PRIVATE);
            boolean hasVisited = sp.getBoolean("hasAuthed", false);
            if (hasVisited) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO Изменить статус с online на offline через минуту
    }
}
