package com.example.neobrain;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.neobrain.Controllers.AuthController;
import com.example.neobrain.Controllers.HomeController;
import com.example.neobrain.Controllers.ProfileController;
import com.example.neobrain.changehandler.FlipChangeHandler;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.controller_container)
    ViewGroup container;

    private Router router;

    public static final String MY_SETTINGS = "my_settings";
    SharedPreferences sp; // TODO Сделать так, чтобы везде было доступно


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
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
