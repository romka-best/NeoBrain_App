package com.itschool.neobrain.controllers;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.changehandler.FlipChangeHandler;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;


/* Контроллер авторизации */
public class AuthController extends Controller {
    @BindView(R.id.login_text)
    TextInputEditText textLogin;
    @BindView(R.id.password_text)
    TextInputEditText textPassword;
    @BindView(R.id.authButton)
    MaterialButton authButton;
    @BindView(R.id.regButton)
    MaterialButton regButton;
    @BindView(R.id.forgot_button)
    MaterialButton forgotButton;

    private String login = "";
    private String pass = "";

    private SharedPreferences sp;

    // Несколько конструкторов, для передачи при необходимости введнного ранее логина и пароля
    public AuthController() {
    }
    public AuthController(String log, String pass) {
        this.login = log;
        this.pass = pass;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.auth_controller, container, false);
        ButterKnife.bind(this, view);

        // Восстанавливаем значения в полях, если они были введены ранее
        if (!this.login.equals("")) textLogin.setText(this.login);
        if (!this.pass.equals("")) textPassword.setText(this.login);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        return view;
    }

    /* Запускаем контроллер регистрации при нажатии на кнопку или фон */
    @OnClick({R.id.regButton, R.id.square_s})
    void launchReg() {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(regButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (getRouter().getBackstackSize() > 1) {
            Objects.requireNonNull(getActivity()).onBackPressed();
        } else {
            getRouter().pushController(RouterTransaction.with(new RegController(
                    Objects.requireNonNull(textLogin.getText()).toString(), Objects.requireNonNull(textPassword.getText()).toString()))
                    .popChangeHandler(new FadeChangeHandler())
                    .pushChangeHandler(new FadeChangeHandler()));
        }
    }

    /* Вызывается при нажатии пользователем на кнопку авторизации */
    @OnClick(R.id.authButton)
    void launchAuth() {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(authButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        boolean findError = false;
        if (Objects.requireNonNull(textLogin.getText()).toString().equals("")) {
            textLogin.setError(Objects.requireNonNull(getActivity()).getResources().getString(R.string.empty_field));
            findError = true;
        }
        if (Objects.requireNonNull(textPassword.getText()).toString().equals("")) {
            textPassword.setError(Objects.requireNonNull(getActivity()).getResources().getString(R.string.empty_field));
            findError = true;
        }
        if (findError) return;
        String login = Objects.requireNonNull(textLogin.getText()).toString().trim();
        String password = Objects.requireNonNull(textPassword.getText()).toString().trim();
        if (isPasswordValid(password)) {
            textLogin.setError(null);
            textPassword.setError(null);
            User user = new User();
            if (isEmailValid(login)) {
                user.setEmail(login);
            } else {
                user.setNickname(login);
            }
            user.setHashedPassword(password);
            // Отправляем запрос на авторизацию пользователя на сервер и парсим ответ
            Call<Status> call = DataManager.getInstance().login(user);
            call.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                    Status post = response.body();
                    assert post != null;
                    if (response.isSuccessful() && post.getStatus() == 200) {
                        SharedPreferences.Editor e = sp.edit();
                        e.putBoolean("hasAuthed", true);
                        e.putInt("userId", Integer.parseInt(post.getText().substring(6, post.getText().length() - 8)));
                        e.putString("login", login);
                        e.putString("password", password);
                        e.apply();
                        for (RouterTransaction routerTransaction : getRouter().getBackstack()) {
                            routerTransaction.controller().getRouter().popCurrentController();
                        }
                        User newUser = new User();
                        newUser.setStatus(1);
                        Call<Status> editCall = DataManager.getInstance().editUser(Integer.parseInt(post.getText().substring(6, post.getText().length() - 8)), newUser);
                        editCall.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                if (response.isSuccessful()) {
                                    getRouter().setRoot(RouterTransaction.with(new HomeController())
                                            .popChangeHandler(new FlipChangeHandler())
                                            .pushChangeHandler(new FlipChangeHandler()));
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                            }
                        });
                    } else {
                        assert getView() != null;
                        try {
                            switch (post.getStatus()) {
                                case 404:
                                    Snackbar.make(getView(), R.string.invalid_password_or_username, Snackbar.LENGTH_LONG).show();
                                    break;
                                case 449:
                                    textPassword.setError(Objects.requireNonNull(getActivity()).getResources().getString(R.string.invalid_password));
                                    break;
                                default:
                                    Snackbar.make(getView(), R.string.error, Snackbar.LENGTH_LONG).show();
                            }
                        } catch (NullPointerException npe) {
                            Snackbar.make(getView(), R.string.error, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                    assert getView() != null;
                    Snackbar.make(getView(), R.string.errors_with_connection, Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            textPassword.setError(Objects.requireNonNull(getActivity()).getResources().getString(R.string.error_with_password));
        }
    }

    @OnClick(R.id.forgot_button)
    void launchForgot() {
        // TODO
    }


    /* Проверка валидности пароля */
    private boolean isPasswordValid(String text) {
        // Пароль должен содержать латинксие буквы (оба регистра),
        // цифры, и быть от 8 символов
        final String regex1 = "(.*)(\\d{1,})(.*)";
        final String regex2 = "(.*)([a-z]{1,})(.*)";
        final String regex3 = "(.*)([A-Z]{1,})(.*)";
        final String regex4 = ".{8,}";

        return Pattern.matches(regex1, text) &
                Pattern.matches(regex2, text) &
                Pattern.matches(regex3, text) &
                Pattern.matches(regex4, text);
    }

    /* Проверка валидности почты */
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /* Сохранение состояния контрллера */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("login", Objects.requireNonNull(textLogin.getText()).toString());
        outState.putString("password", Objects.requireNonNull(textPassword.getText()).toString());
    }

    /* Восстановление состояния контрллера */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textLogin.setText(savedInstanceState.getString("login"));
        textPassword.setText(savedInstanceState.getString("password"));
    }

}