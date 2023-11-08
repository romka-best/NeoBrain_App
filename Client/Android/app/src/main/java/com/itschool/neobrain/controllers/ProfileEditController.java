package com.itschool.neobrain.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.ProfileInfoAdapter;
import com.itschool.neobrain.utils.BundleBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Контроллер для работы с изменениями в профиле */
public class ProfileEditController extends Controller {
    @BindView(R.id.infoRecycler)
    public RecyclerView profileInfoRecycler;
    private ProfileInfoAdapter profileInfoAdapter;
    @BindView(R.id.saveButton)
    public MaterialButton saveButton;

    private boolean bottomIsGone = false;
    private SharedPreferences sp;

    // Несколько конструкторов для передачи необходимых значений в разных ситуациях

    public ProfileEditController() {

    }

    public ProfileEditController(boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }

    public ProfileEditController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
    }


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.profile_edit_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        // Получаем текущую информацию о профиле пользователя
        getProfileInfo();
        return view;
    }

    /* Метод, получающий информацию о профиле */
    private void getProfileInfo() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        profileInfoRecycler.setLayoutManager(mLayoutManager);
        profileInfoRecycler.setItemAnimator(new DefaultItemAnimator());
        // Запрос на наш сервер для получения нужной информации
        Call<UserModel> userModelCall = DataManager.getInstance().getUser(sp.getInt("userId", -1));
        userModelCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    User user = response.body().getUser();
                    profileInfoAdapter = new ProfileInfoAdapter(user, getRouter(), true);
                    profileInfoRecycler.setAdapter(profileInfoAdapter);
                    profileInfoRecycler.getRecycledViewPool().clear();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {

            }
        });
    }

    /* Обработчик клика на кнопку сохранить */
    @OnClick(R.id.saveButton)
    void save() {
        User user = profileInfoAdapter.getInfo();
        // Если есть некоректность введения пола
        if (user.getGender() != null && user.getGender() == -2) {
            Snackbar.make(getView(), R.string.not_correct_gender, Snackbar.LENGTH_LONG).show();
            return;
        }
        // Сохраняем новые данные, уведомляем сервер
        Call<Status> call = DataManager.getInstance().editUser(sp.getInt("userId", -1), user);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                assert getView() != null;
                if (response.isSuccessful()) {
                    Snackbar.make(getView(), R.string.save_successful, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), R.string.problems_with_server, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

            }
        });
    }

    /* Метод, определяющий поведение при нажатии на кнопку "назад" на устройстве */
    @Override
    public boolean handleBack() {
        // Показываем BottomNavigationView
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }

    /* Вызывается, когда контроллер связывается с активностью */
    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        // Пробуем скрыть BottomNavigationView, если уже скрыта, ставим заглушку
        try {
            if (bottomIsGone) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }
}
