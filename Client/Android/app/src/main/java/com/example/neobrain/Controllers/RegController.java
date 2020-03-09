package com.example.neobrain.Controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.StatusResponse;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegController extends Controller {

    private Pattern pattern;
    private Matcher matcher;
    private TextView textName;
    private TextView textSurname;
    private TextView textNickname;
    private TextView textPassword;
    private TextView textPasswordRepeat;
    private TextView textNumber;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.reg_controller, container, false);
        ButterKnife.bind(this, view);
        textName = view.findViewById(R.id.name);
        textSurname = view.findViewById(R.id.surname);
        textNickname = view.findViewById(R.id.nickname);
        textPassword = view.findViewById(R.id.password);
        textPasswordRepeat = view.findViewById(R.id.passwordRepeat);
        textNumber = view.findViewById(R.id.number);
        return view;
    }

    @OnClick({R.id.regButton})
    void launchReg() {
        String name = textName.getText().toString();
        String surname = textSurname.getText().toString();
        String nickname = textNickname.getText().toString();
        String password = textPassword.getText().toString();
        String passwordRepeat = textPasswordRepeat.getText().toString();
        String number = textNumber.getText().toString();
        boolean error = false;
        if (!isPasswordSame(password, passwordRepeat)) error = true;
        if (!error) {
            String TAG = "RRR";
            DataManager.getInstance().createUser(name, surname, nickname, number, password).enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    if (response.isSuccessful()) {
                        System.out.println(response.body());
                    }
                    else{
                        System.out.println(response.errorBody());
                    }

                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick({R.id.authButton})
    void launchAuth() {
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

        if (Pattern.matches(regex1, password) &
                Pattern.matches(regex2, password) &
                Pattern.matches(regex3, password) &
                Pattern.matches(regex4, password)) {
            return true;
        }
        return false;
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

    private boolean isNicknameFree(String nick) {
        return true;
    }
}
