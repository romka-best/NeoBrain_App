package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.FlipChangeHandler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private TextView textEmail;

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
        textEmail = view.findViewById(R.id.email_text);

        View squareAuth = view.findViewById(R.id.square_s);
        squareAuth.setOnClickListener(v -> launchAuth());

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @OnClick({R.id.regButton})
    void launchReg() {
        // TODO Сделать корректную обработку регистрации
        String name = textName.getText().toString();
        String surname = textSurname.getText().toString();
        String nickname = textNickname.getText().toString();
        String password = textPassword.getText().toString();
        String passwordRepeat = textPasswordRepeat.getText().toString();
        String email = textEmail.getText().toString();
        boolean error = false;
        if (!isPasswordSame(password, passwordRepeat) | !passwordValidate(password)
        | !isEmailValid(email) | name.equals("") | surname.equals("") | nickname.equals("")) error = true;
        if (!error) {
            TextInputLayout ema = (TextInputLayout) getView().findViewById(R.id.email);
            ema.setError(null);
            getView().findViewById(R.id.email_text).getBackground().clearColorFilter();
            TextInputLayout pass = (TextInputLayout) getView().findViewById(R.id.password);
            pass.setError(null);
            getView().findViewById(R.id.password_text).getBackground().clearColorFilter();
            TextInputLayout passRep = (TextInputLayout) getView().findViewById(R.id.passwordRepeat);
            passRep.setError(null);
            getView().findViewById(R.id.passwordRepeat_text).getBackground().clearColorFilter();

            User user = new User();
            user.setName(name);
            user.setSurname(surname);
            user.setNickname(nickname);
            user.setEmail(email);
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
                            Snackbar.make(getView(), "Status code: " + post.getStatus() + "\n" + post.getText(), Snackbar.LENGTH_LONG).show();
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
                        Snackbar.make(getView(), response.message(), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                    // TODO Корректно обработать ошибку
                    Snackbar.make(getView(), t.toString(), Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            //TODO обработать ошибку нулевого ввода в нике, имени и фамилии
            if (!isPasswordSame(password, passwordRepeat)) {
                Snackbar.make(getView(), getResources().getString(R.string.pass_not_same), Snackbar.LENGTH_LONG).show();
                TextInputLayout passRep = (TextInputLayout) getView().findViewById(R.id.passwordRepeat);
                passRep.setError(getResources().getString(R.string.pass_not_same));
                getView().findViewById(R.id.passwordRepeat_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            }   else {
                TextInputLayout passRep = (TextInputLayout) getView().findViewById(R.id.passwordRepeat);
                passRep.setError(null);
                getView().findViewById(R.id.passwordRepeat_text).getBackground().clearColorFilter();
            }
            if (!passwordValidate(password)) {
                Snackbar.make(getView(), getResources().getString(R.string.pass_not_corr), Snackbar.LENGTH_LONG).show();
                TextInputLayout pass = (TextInputLayout) getView().findViewById(R.id.password);
                pass.setError(getResources().getString(R.string.not_correct));
                getView().findViewById(R.id.password_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
                TextInputLayout passRep = (TextInputLayout) getView().findViewById(R.id.passwordRepeat);
                passRep.setError(null);
                getView().findViewById(R.id.passwordRepeat_text).getBackground().clearColorFilter();

            } else {
                TextInputLayout pass = (TextInputLayout) getView().findViewById(R.id.password);
                pass.setError(null);
                getView().findViewById(R.id.password_text).getBackground().clearColorFilter();
            }
            if (!isEmailValid(email)) {
                TextInputLayout ema = (TextInputLayout) getView().findViewById(R.id.email);
                ema.setError(getResources().getString(R.string.email_not_correct));
                getView().findViewById(R.id.email_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
                Snackbar.make(getView(), getResources().getString(R.string.email_not_correct),
                        Snackbar.LENGTH_LONG).show();
            } else {
                TextInputLayout ema = (TextInputLayout) getView().findViewById(R.id.email);
                ema.setError(null);
                getView().findViewById(R.id.email_text).getBackground().clearColorFilter();
            }
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
