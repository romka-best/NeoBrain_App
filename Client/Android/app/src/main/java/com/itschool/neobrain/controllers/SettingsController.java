package com.itschool.neobrain.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.changehandler.FlipChangeHandler;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

public class SettingsController extends Controller {
    @BindView(R.id.exitButton)
    public MaterialButton exitButton;
    private BottomNavigationView bottomNavigationView;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.settings_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        return view;
    }

    @OnClick({R.id.exitButton})
    void exit() {
        Integer userIdSP = sp.getInt("userId", -1);
        User user = new User();
        user.setStatus(0);
        user.setExit(true);
        Call<Status> call = DataManager.getInstance().editUser(userIdSP, user);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                if (response.isSuccessful()) {
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
            }

            @Override
            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
            }
        });
    }

    @Override
    public boolean handleBack() {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        try {
            bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
        } catch (NullPointerException ignored) {
        }
    }
}
