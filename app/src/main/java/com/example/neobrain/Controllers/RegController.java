package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegController extends Controller {

    private static final String PASSWORD_PATTERN = "((?=.*d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
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
        if (name.equals("")) {
            Toast.makeText(getApplicationContext(), "Не введёно Имя", Toast.LENGTH_SHORT).show();
            error = true;
        } else
            Toast.makeText(getApplicationContext(), "С именем всё ок", Toast.LENGTH_SHORT).show();
        if (surname.equals("")) {
            Toast.makeText(getApplicationContext(), "Не введёно Фамилия", Toast.LENGTH_SHORT).show();
            error = true;
        } else
            Toast.makeText(getApplicationContext(), "С фамилией всё ок", Toast.LENGTH_SHORT).show();
        if (isNicknameFree(nickname)) {
            Toast.makeText(getApplicationContext(), "С никнеймом всё ок", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Никнейм занят", Toast.LENGTH_SHORT).show();
            error = true;
        }
        if (passwordValidate(password)) {
            Toast.makeText(getApplicationContext(), "С паролем всё ок", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Пароль плохой", Toast.LENGTH_SHORT).show();
            error = true;
        }
        if (isPasswordSame(password, passwordRepeat)) {
            Toast.makeText(getApplicationContext(), "Пароли схожи", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Пароли не похожи", Toast.LENGTH_SHORT).show();
            error = true;
        }
        if (isPhonenumberValid(number)) {
            Toast.makeText(getApplicationContext(), "С телефоном всё ок", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "С никнеймом всё ок", Toast.LENGTH_SHORT).show();
            error = true;
        }
        /*
        if(!error){
            Call<UserModel> call = DataManager.getInstance().register();
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    int code = response.code();
                    if (code == 200){
                        Toast.makeText(getApplicationContext(), "Удачно", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
                }
            });
            */
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean passwordValidate(String password) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isPasswordSame(String password1, String password2) {
        return password1.equals(password2);
    }

    /*
        Проверка номера телефона
        Метод должен проверять, является ли аргумент валидным номером телефона.
        Критерии валидности:
        1) если номер начинается с '+', то он содержит 12 цифр
        2) если номер начинается с цифры или открывающей скобки, то он содержит 10 цифр
        3) может содержать 0-2 знаков '-', которые не могут идти подряд
        4) может содержать 1 пару скобок '(' и ')' , причем если она есть, то она расположена левее знаков '-'
        5) скобки внутри содержат четко 3 цифры
        6) номер не содержит букв
        7) номер заканчивается на цифру
    */
    public boolean isPhonenumberValid(String phone) {
        if (phone != null) {
            String reg = "(\\+?\\d+\\(?\\d{3}\\)?\\d{2}\\-?\\d{2}\\-?\\d{2,3})";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(phone);
            return m.matches();
        } else {
            return false;
        }
    }

    public boolean isNicknameFree(String nick) {
        return true;
    }
}
