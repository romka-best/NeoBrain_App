package com.itschool.neobrain.controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.changehandler.FlipChangeHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Контроллер регистрации */
public class RegController extends Controller {
    private String authlog;
    private String authpass;

    // Много полей для ввода и их инициализация
    @BindView(R.id.name_text)
    public TextView textName;
    @BindView(R.id.surname_text)
    public TextView textSurname;
    @BindView(R.id.nickname_text)
    public TextView textNickname;
    @BindView(R.id.password_text)
    public TextView textPassword;
    @BindView(R.id.passwordRepeat_text)
    public TextView textPasswordRepeat;
    @BindView(R.id.email_text)
    public TextView textEmail;
    @BindView(R.id.passwordRepeat)
    public TextInputLayout passRepEdit;
    @BindView(R.id.password)
    public TextInputLayout passEdit;
    @BindView(R.id.email)
    public TextInputLayout emailEdit;
    @BindView(R.id.name)
    public TextInputLayout nameEdit;
    @BindView(R.id.surname)
    public TextInputLayout surnameEdit;
    @BindView(R.id.nickname)
    public TextInputLayout nicknameEdit;
    @BindView(R.id.regButton)
    public MaterialButton regButton;

    // Заранее инициализируем нужные переменные
    private String name;
    private String surname;
    private String nickname;
    private String password;
    private String passwordRepeat;
    private String email;
    private List<String> errors = new ArrayList<>();

    private SharedPreferences sp;

    // Несколько конструкторов, для передачи при необходимости введнного ранее логина и пароля
    public RegController() {
    }

    public RegController(String log, String pass) {
        authlog = log;
        authpass = pass;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.reg_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        return view;
    }

    /* Метод срабатывает при нажатии на кнопку регистрации. Проверяет корректность данных, а затем
       регистрирует нового пользователя и входит под новым именем в само приложение */
    @SuppressLint("ResourceAsColor")
    @OnClick({R.id.regButton})
    void launchReg() {
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(regButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        // Берём данные из полей ввода
        name = textName.getText().toString().trim();
        surname = textSurname.getText().toString().trim();
        nickname = textNickname.getText().toString().trim();
        password = textPassword.getText().toString().trim();
        passwordRepeat = textPasswordRepeat.getText().toString().trim();
        email = textEmail.getText().toString().trim();

        // Если не обнаружена некорректность, приводим к стандартному виду поля воода и отправляем
        // письмо для подтверждения почты, в ином случае уведомляем о том, где именно была
        // совершенна ошибка ввода
        if (!(!isPasswordSame(password, passwordRepeat) | !passwordValidate(password)
                | !isEmailValid(email) | name.equals("") | surname.equals("") | nickname.equals("")
                | name.equals("") | nickname.equals("") | surname.equals("")
                | !isNicknameValid(nickname) | !isNameSurnameValid(name) | !isNameSurnameValid(surname))) {
            emailEdit.setError(null);
            assert getView() != null;
            getView().findViewById(R.id.email_text).getBackground().clearColorFilter();
            passEdit.setError(null);
            getView().findViewById(R.id.password_text).getBackground().clearColorFilter();
            passRepEdit.setError(null);
            getView().findViewById(R.id.passwordRepeat_text).getBackground().clearColorFilter();
            nameEdit.setError(null);
            getView().findViewById(R.id.name_text).getBackground().clearColorFilter();
            surnameEdit.setError(null);
            getView().findViewById(R.id.surname_text).getBackground().clearColorFilter();
            nicknameEdit.setError(null);
            getView().findViewById(R.id.nickname_text).getBackground().clearColorFilter();

            User userEmail = new User();
            userEmail.setEmail(email);
            // Отправляем письмо, выводим диалоговое окно для подтверждения
            Call<Status> acceptCall = DataManager.getInstance().sendEmail(userEmail);
            acceptCall.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                    Status post = response.body();
                    assert post != null;
                    if (response.isSuccessful() && post.getStatus() == 200) {
                        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_layout, null);
                        final TextInputEditText valueKey = view.findViewById(R.id.code_text);
                        new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()), R.style.AlertDialogCustomDarkBlue)
                                .setMessage(R.string.check_code)
                                .setView(view)
                                .setPositiveButton(R.string.accept, (dialog, which) -> {
                                    // Если всё в порядке, создаём пользователя, если нет - сообщаем об этом
                                    if (Objects.requireNonNull(valueKey.getText()).toString().equals(post.getText())) {
                                        User user = new User();
                                        user.setName(name);
                                        user.setSurname(surname);
                                        user.setNickname(nickname);
                                        user.setEmail(email);
                                        user.setHashedPassword(password);
                                        // Входим в приложение только что созданным пользователем
                                        Call<Status> call1 = DataManager.getInstance().createUser(user);
                                        call1.enqueue(new Callback<Status>() {
                                            @Override
                                            public void onResponse(@NotNull Call<Status> call1, @NotNull Response<Status> response1) {
                                                Status post1 = response1.body();
                                                assert post1 != null;
                                                if (response1.isSuccessful() && post1.getStatus() == 201) {
                                                    SharedPreferences.Editor e = sp.edit();
                                                    e.putBoolean("hasAuthed", true);
                                                    e.putInt("userId", Integer.parseInt(post1.getText().substring(5, post1.getText().length() - 8)));
                                                    e.putString("login", email);
                                                    e.putString("password", password);
                                                    e.apply();
                                                    for (RouterTransaction routerTransaction : getRouter().getBackstack()) {
                                                        routerTransaction.controller().getRouter().popCurrentController();
                                                    }
                                                    getRouter().setRoot(RouterTransaction.with(new HomeController())
                                                            .popChangeHandler(new FlipChangeHandler())
                                                            .pushChangeHandler(new FlipChangeHandler()));
                                                } else {
                                                    assert getView() != null;
                                                    switch (post1.getText()) {
                                                        case "Nickname already exists":
                                                            nicknameEdit.setError(Objects.requireNonNull(getActivity()).getResources().getString(R.string.nickname_already_exists));
                                                            break;
                                                        case "Phone number already exists":
                                                            break;
                                                        case "Email already exists":
                                                            emailEdit.setError(Objects.requireNonNull(getActivity()).getResources().getString(R.string.email_already_exists));
                                                            break;
                                                        default:
                                                            Snackbar.make(getView(), R.string.error, Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NotNull Call<Status> call1, @NotNull Throwable t) {
                                                assert getView() != null;
                                                Snackbar.make(getView(), R.string.errors_with_connection, Snackbar.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        assert getView() != null;
                                        Snackbar.make(getView(), R.string.invalid_code, Snackbar.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                }
            });
        } else {
            assert getView() != null;
            if (errors != null) errors.clear();
            if (name.equals("")) {
                assert errors != null;
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.empty_name));
                nameEdit.setError(getResources().getString(R.string.empty_name));
                getView().findViewById(R.id.name_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else if (!isNameSurnameValid(name)) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.incorrectName));
                nameEdit.setError(getResources().getString(R.string.incorrectName));
                getView().findViewById(R.id.name_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else {
                nameEdit.setError(null);
                getView().findViewById(R.id.name_text).getBackground().clearColorFilter();
            }
            if (surname.equals("")) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.empty_surname));
                surnameEdit.setError(getResources().getString(R.string.empty_surname));
                getView().findViewById(R.id.surname_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else if (!isNameSurnameValid(surname)) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.incorrectSurname));
                surnameEdit.setError(getResources().getString(R.string.incorrectSurname));
                getView().findViewById(R.id.surname_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else {
                surnameEdit.setError(null);
                getView().findViewById(R.id.surname_text).getBackground().clearColorFilter();
            }
            if (nickname.equals("")) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.empty_nickname));
                nicknameEdit.setError(getResources().getString(R.string.empty_nickname));
                getView().findViewById(R.id.nickname_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else if (!isNicknameValid(nickname)) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.incorrectNicknameError));
                nicknameEdit.setError(getResources().getString(R.string.incorrectNickname));
                getView().findViewById(R.id.nickname_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else {
                nicknameEdit.setError(null);
                getView().findViewById(R.id.nickname_text).getBackground().clearColorFilter();
            }
            if (!isPasswordSame(password, passwordRepeat)) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.pass_not_same));
                passRepEdit.setError(getResources().getString(R.string.pass_not_same));
                getView().findViewById(R.id.passwordRepeat_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
                passEdit.setError(getResources().getString(R.string.pass_not_same));
                getView().findViewById(R.id.password_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else {
                passRepEdit.setError(null);
                getView().findViewById(R.id.passwordRepeat_text).getBackground().clearColorFilter();
                passEdit.setError(null);
                getView().findViewById(R.id.password_text).getBackground().clearColorFilter();
            }
            if (!passwordValidate(password)) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.pass_not_corr));
                passEdit.setError(getResources().getString(R.string.pass_not_corr));
                getView().findViewById(R.id.password_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
                passRepEdit.setError(getResources().getString(R.string.pass_not_corr));
                getView().findViewById(R.id.passwordRepeat_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else {
                passEdit.setError(null);
                getView().findViewById(R.id.password_text).getBackground().clearColorFilter();
                passRepEdit.setError(null);
                getView().findViewById(R.id.passwordRepeat_text).getBackground().clearColorFilter();
            }
            if (!isEmailValid(email)) {
                errors.add(Objects.requireNonNull(getResources()).getString(R.string.email_not_correct));
                emailEdit.setError(getResources().getString(R.string.email_not_correct));
                getView().findViewById(R.id.email_text).getBackground().
                        setColorFilter(R.color.colorError, PorterDuff.Mode.SRC_OUT);
            } else {
                emailEdit.setError(null);
                getView().findViewById(R.id.email_text).getBackground().clearColorFilter();
            }
            StringBuilder message = new StringBuilder();
            for (String er : errors) {
                if (!er.equals(errors.get(errors.size() - 1))) {
                    message.append(er).append("\n");
                } else {
                    message.append(er);
                }
            }

            // Вывод сообщения об ошибках
            Snackbar snackbar = Snackbar.make(getView(), message.toString(), Snackbar.LENGTH_INDEFINITE);
            Button button = snackbar.getView().findViewById(R.id.snackbar_action);
            button.setTextColor(Color.GREEN);
            TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);
            textView.setMaxLines(6);
            snackbar.setAction(R.string.undo, v -> snackbar.dismiss());
            snackbar.show();
        }
    }

    /* Срабатывает при нажатии на кнопку перехода на авторизацию
     или по нажатию на экран авторизации слева */
    @OnClick({R.id.authButton, R.id.square_s})
    void launchAuth() {
        // Переходим на авторизацию, передаём значения ранее введённые пользователем в авторизации
        getRouter().pushController(RouterTransaction.with(new AuthController(authlog, authpass))
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler()));
    }

    /* Сохранение данных при повороте экрана */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name);
        outState.putString("surname", surname);
        outState.putString("nickname", nickname);
        outState.putString("password", password);
        outState.putString("passwordRepeat", passwordRepeat);
        outState.putString("email", email);
    }

    /* Восстановление данных */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textName.setText(savedInstanceState.getString("name"));
        textSurname.setText(savedInstanceState.getString("surname"));
        textNickname.setText(savedInstanceState.getString("nickname"));
        textPassword.setText(savedInstanceState.getString("password"));
        textPasswordRepeat.setText(savedInstanceState.getString("passwordRepeat"));
        textEmail.setText(savedInstanceState.getString("email"));
    }

    /* Метод, проверяющий фамилию и имя на валидность */
    private boolean isNameSurnameValid(String nameOrSurname) {
        final String regex1 = "(.*)(\\d{1,})(.*)";
        final String regex2 = "(.*)(\\s{1,})(.*)";
        final String regex3 = ".{2,}";
        return !Pattern.matches(regex1, nameOrSurname) &&
                !Pattern.matches(regex2, nameOrSurname) &&
                Pattern.matches(regex3, nameOrSurname);
    }

    /* Метод, проверяющий никнейм на валидность */
    private boolean isNicknameValid(String nickname) {
        final String regex1 = "^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)(?!\\d+$)[a-zA-Z0-9.]{5,15}$";
        return Pattern.matches(regex1, nickname);
    }

    /* Метод, проверяющий почту на валидность */
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /* Метод, проверяющий пароль на валидность */
    private boolean passwordValidate(String password) {
        // Пароль должен содержать латинксие буквы (оба регистра),
        // цифры, и содержать в себе как минимум 8 символов
        final String regex1 = "(.*)(\\d{1,})(.*)";
        final String regex2 = "(.*)([a-z]{1,})(.*)";
        final String regex3 = "(.*)([A-Z]{1,})(.*)";
        final String regex4 = ".{8,}";

        return Pattern.matches(regex1, password) &
                Pattern.matches(regex2, password) &
                Pattern.matches(regex3, password) &
                Pattern.matches(regex4, password);
    }

    /* Метод, проверяющий пароли на совпадение */
    private boolean isPasswordSame(String password1, String password2) {
        return password1.equals(password2);
    }

    /* Метод, проверяющий номер телефона на валидность */
    private boolean isPhoneNumberValid(String phone) {
        if (phone == null) return false;
        if (phone.isEmpty()) return false;
        int digits = phone.replaceAll("\\D", "").length();
        if (digits == 11) {
            return phone.matches("(\\+\\d+)?\\d*(\\(\\d{3}\\))?\\d+(-?\\d+){0,2}");
        } else return false;
    }
}
