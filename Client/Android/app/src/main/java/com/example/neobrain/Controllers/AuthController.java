package com.example.neobrain.Controllers;

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
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.FlipChangeHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;


// Контроллер авторизации
public class AuthController extends Controller {
    @BindView(R.id.login_text)
    public TextInputEditText textLogin;
    @BindView(R.id.password_text)
    public TextInputEditText textPassword;
    @BindView(R.id.authButton)
    public MaterialButton authButton;
    @BindView(R.id.regButton)
    public MaterialButton regButton;
    private String login = "";
    private String pass = "";

    private SharedPreferences sp;

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

        if (!this.login.equals("")) textLogin.setText(this.login);
        if (!this.pass.equals("")) textPassword.setText(this.login);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        return view;
    }

    // Запускаем контроллер регистрации
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

    // Пользователь заходит
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
        String login = Objects.requireNonNull(textLogin.getText()).toString();
        String password = Objects.requireNonNull(textPassword.getText()).toString();
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
                        e.apply();
                        for (RouterTransaction routerTransaction : getRouter().getBackstack()) {
                            routerTransaction.controller().getRouter().popCurrentController();
                        }
                        getRouter().setRoot(RouterTransaction.with(new HomeController())
                                .popChangeHandler(new FlipChangeHandler())
                                .pushChangeHandler(new FlipChangeHandler()));
                    } else {
                        assert getView() != null;
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


    private boolean isPasswordValid(String text) {
        // Пароль должен содержать латинксие буквы (оба регистра),
        // знаки {}@#$%^&+=*()!?,.~_, цифры, и быть от 8
        final String regex1 = "(.*)(\\d{1,})(.*)";
        final String regex2 = "(.*)([a-z]{1,})(.*)";
        final String regex3 = "(.*)([A-Z]{1,})(.*)";
        final String regex4 = ".{8,}";

        return Pattern.matches(regex1, text) &
                Pattern.matches(regex2, text) &
                Pattern.matches(regex3, text) &
                Pattern.matches(regex4, text);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("login", Objects.requireNonNull(textLogin.getText()).toString());
        outState.putString("password", Objects.requireNonNull(textPassword.getText()).toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textLogin.setText(savedInstanceState.getString("login"));
        textPassword.setText(savedInstanceState.getString("password"));
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}