package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.FlipChangeHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

public class AuthController extends Controller {
    private TextInputEditText textLogin;
    private TextInputEditText textPassword;

    private SharedPreferences sp;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.auth_controller, container, false);
        ButterKnife.bind(this, view);
        textLogin = view.findViewById(R.id.login_text);
        textPassword = view.findViewById(R.id.password_text);

        View squareReg = view.findViewById(R.id.square_s);
        squareReg.setOnClickListener(v -> launchReg());

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        return view;
    }


    @OnClick({R.id.regButton})
    void launchReg() {
        // TODO Исправить баг: При первом переходе, отсутствует анимация
        getRouter().pushController(RouterTransaction.with(new RegController())
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler()));
        getRouter().popController(this);
    }

    @OnClick(R.id.authButton)
    void launchAuth() {
        // TODO Корректно обработать вход: Если поля пустые или неправильные, сделать Error message к textLogin и textPassword
        String number = Objects.requireNonNull(textLogin.getText()).toString();
        String password = Objects.requireNonNull(textPassword.getText()).toString();
        if (isPasswordValid(password)) {
            User user = new User();
            // TODO Проверка на логин, в зависимости от логина, отправляем разные значения
            user.setNumber(number);
            user.setHashedPassword(password);
            Call<Status> call = DataManager.getInstance().login(user);
            call.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                    if (response.isSuccessful()) {
                        Status post = response.body();
                        assert post != null;
                        if (post.getStatus() != 200) {
                            if (post.getStatus() == 404) {
                                Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_LONG).show(); // TODO Изменить текст на R.string.{}
                            } else if (post.getStatus() == 449) {
                                Toast.makeText(getApplicationContext(), "Неверный пароль", Toast.LENGTH_LONG).show(); // TODO Изменить текст на R.string.{}
                            } else {
                                Toast.makeText(getApplicationContext(), post.getText(), Toast.LENGTH_LONG).show();
                            }
                        } else if (post.getStatus() == 200) {
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean("hasAuthed", true);
                            e.putString("nickname", post.getText().substring(6, post.getText().length() - 8));
                            e.apply();
                            getRouter().pushController(RouterTransaction.with(new HomeController())
                                    .popChangeHandler(new FlipChangeHandler())
                                    .pushChangeHandler(new FlipChangeHandler()));
                            getRouter().popController(AuthController.this);
                        }
                    } else {
                        // TODO Изменить на Snackbar
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                    // TODO Изменить везде Toast на Snackbar и в зависимости от ошибки, показать пользователю ошибку
                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // TODO Добавить Error message
        }
    }


    private boolean isPasswordValid(String text) {
        return text != null && text.length() >= 1;
    }


//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("isAuth", isAuth);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        isAuth = savedInstanceState.getBoolean("isAuth");
//    }
}
