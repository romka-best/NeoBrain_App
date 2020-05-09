package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.FlipChangeHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

public class SettingsController extends Controller {
    @BindView(R.id.exitButton)
    public MaterialButton exitButton;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.settings_controller, container, false);
        ButterKnife.bind(this, view);

        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        return view;
    }

    @OnClick({R.id.exitButton})
    void exit() {
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("hasAuthed", false);
        e.apply();
        for (RouterTransaction routerTransaction : getRouter().getBackstack()) {
            routerTransaction.controller().getRouter().popCurrentController();
        }
        getRouter().setRoot(RouterTransaction.with(new AuthController())
                .popChangeHandler(new FlipChangeHandler())
                .pushChangeHandler(new FlipChangeHandler()));
    }

    @Override
    public boolean handleBack() {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }
}
