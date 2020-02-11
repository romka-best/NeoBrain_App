package com.example.neobrain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegController extends Controller implements View.OnClickListener{

    private static final String PASSWORD_PATTERN = "((?=.*d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private Pattern pattern;
    private Matcher matcher;
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.reg_controller, container, false);
        Button regButton = view.findViewById(R.id.regButton);
        return view;
    }

    @Override
    public void onClick(View v) {
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean validate(String password) {
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isPasswordSame(String password1, String password2){
        if (password1.equals(password2)) {
            return false;
        }
        return true;
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
    public boolean isPhonenumberValid(String phone){
        if(phone!=null){
            String reg ="(\\+?\\d+\\(?\\d{3}\\)?\\d{2}\\-?\\d{2}\\-?\\d{2,3})";
            Pattern p =  Pattern.compile(reg);
            Matcher m = p.matcher(phone);
            return m.matches();
        }else {
            return false;
        }
    }

    public boolean isNicknameFree(String nick){
        return true;
    }

}
