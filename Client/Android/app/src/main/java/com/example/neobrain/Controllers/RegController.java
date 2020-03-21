package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

public class RegController extends Controller {

    private Pattern pattern;
    private Matcher matcher;
    private TextView textName;
    private TextView textSurname;
    private TextView textNickname;
    private TextView textPassword;
    private TextView textPasswordRepeat;
    private TextView textNumber;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.reg_controller, container, false);
        ButterKnife.bind(this, view);
        textName = view.findViewById(R.id.name_text);
        textSurname = view.findViewById(R.id.surname_text);
        textNickname = view.findViewById(R.id.nickname_text);
        textPassword = view.findViewById(R.id.password_text);
        textPasswordRepeat = view.findViewById(R.id.passwordRepeat_text);
        textNumber = view.findViewById(R.id.number_text);

        View squareAuth = view.findViewById(R.id.square_s);
        squareAuth.setOnClickListener(v -> launchAuth());

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        return view;
    }

    @OnClick({R.id.regButton})
    void launchReg() {
        // TODO Сделать корректную обработку регистрации
        String name = textName.getText().toString();
        String surname = textSurname.getText().toString();
        String nickname = textNickname.getText().toString();
        String password = textPassword.getText().toString();
        String passwordRepeat = textPasswordRepeat.getText().toString();
        String number = textNumber.getText().toString();
        boolean error = false;
        if (!isPasswordSame(password, passwordRepeat)) error = true;
        if (!error) {
            User user = new User();
            user.setName(name);
            user.setSurname(surname);
            user.setNickname(nickname);
            user.setNumber(number);
            user.setHashedPassword(password);
            Call<Status> call = DataManager.getInstance().createUser(user);
            call.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                    if (response.isSuccessful()) {
                        Status post = response.body();
                        assert post != null;
                        if (post.getStatus() != 201) {
                            // TODO В зависимости от текста, вывести пользователю ошибку в Error message(outlinedTextField.setError("Error message"))
                            Toast.makeText(getApplicationContext(), "Status code: " + post.getStatus() + "\n" + post.getText(), Toast.LENGTH_LONG).show();
                        } else if (post.getStatus() == 201) {
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean("hasAuthed", true);
                            e.putString("nickname", nickname);
                            e.apply();
                            getRouter().pushController(RouterTransaction.with(new HomeController())
                                    .popChangeHandler(new FlipChangeHandler())
                                    .pushChangeHandler(new FlipChangeHandler()));
                            getRouter().popController(RegController.this);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                    // TODO Корректно обработать ошибку
                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // TODO Корректно обработать ошибку
            Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick({R.id.authButton})
    void launchAuth() {
        getRouter().pushController(RouterTransaction.with(new AuthController())
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler()));
        getRouter().popController(this);
    }


    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean passwordValidate(String password) {
        // Пароль должен содержать латинксие буквы,
        // знаки @#_&, цифры, и быть от 6 до 20 символов длинной
        final String regex1 = "(.*)(\\d{1,})(.*)";
        final String regex2 = "(.*)([a-zA-Z]{1,})(.*)";
        final String regex3 = "(.*)([@#_&]{1,})(.*)";
        final String regex4 = ".{6,20}";

        return Pattern.matches(regex1, password) &
                Pattern.matches(regex2, password) &
                Pattern.matches(regex3, password) &
                Pattern.matches(regex4, password);
    }

    private boolean isPasswordSame(String password1, String password2) {
        return password1.equals(password2);
    }

    private boolean isPhoneNumberValid(String phone) {
        if (phone == null) return false;
        if (phone.isEmpty()) return false;
        int digits = phone.replaceAll("\\D", "").length();
        if (digits == 11) {
            return phone.matches("(\\+\\d+)?\\d*(\\(\\d{3}\\))?\\d+(-?\\d+){0,2}");
        } else return false;
    }
}
