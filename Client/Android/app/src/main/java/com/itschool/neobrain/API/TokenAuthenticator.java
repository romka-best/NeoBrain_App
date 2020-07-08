package com.itschool.neobrain.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

public class TokenAuthenticator implements Authenticator {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    SharedPreferences sp;

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
        sp = Objects.requireNonNull(MainActivity.getContextOfApplication()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        updateToken();
        return response.request().newBuilder()
                .header(HEADER_AUTHORIZATION, sp.getString("token", ""))
                .build();
    }

    private void updateToken() {
        try {
            Status authTokenResponse = DataManager.getInstance().auth(Credentials.basic(sp.getString("login", ""), sp.getString("password", ""))).execute().body();
            assert authTokenResponse != null;
            String newToken = "Bearer " + authTokenResponse.getToken();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("token", newToken);
            editor.apply();
        } catch (IOException ignored) {
        }

    }
}