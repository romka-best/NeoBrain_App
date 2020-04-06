package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

@SuppressLint("ValidController")
public class HomeController extends Controller {

    @BindViews({R.id.container})
    ViewGroup[] childContainers; // TODO Уверен, что здесь можно изменить на не массив(Ведь у нас всего один контейнер)
    SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.start_controller, container, false);
        ButterKnife.bind(this, view);

        ProgressBar progressBar = view.findViewById(R.id.progress_circular);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        Router childRouterStart = getChildRouter(childContainers[0]).setPopsLastView(true);
        childRouterStart.setRoot(RouterTransaction.with(new LentaController()));

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_lenta);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    Router childRouter = getChildRouter(childContainers[0]).setPopsLastView(true);
                    switch (item.getItemId()) {
                        case R.id.action_lenta:
                            if (!childRouter.hasRootController()) {
                                try {
                                    getChildRouter(childContainers[0]).popCurrentController();
                                } catch (java.lang.IllegalStateException e) {
                                    return false;
                                }
                            }
                            childRouter.setRoot(RouterTransaction.with(new LentaController()));
                            return true;
                        case R.id.action_navigation:
                            if (!childRouter.hasRootController()) {
                                try {
                                    getChildRouter(childContainers[0]).popCurrentController();
                                } catch (java.lang.IllegalStateException e) {
                                    return false;
                                }
                            }
                            Log.d("HomeController", "!hasRoot");
                            childRouter.setRoot(RouterTransaction.with(new NavigationController()));
                            return true;
                        case R.id.action_messages:
                            if (!childRouter.hasRootController()) {
                                try {
                                    getChildRouter(childContainers[0]).popCurrentController();
                                } catch (java.lang.IllegalStateException e) {
                                    return false;
                                }
                            }
                            childRouter.setRoot(RouterTransaction.with(new ChatController()));
                            return true;
                        case R.id.action_apps:
                            if (!childRouter.hasRootController()) {
                                try {
                                    getChildRouter(childContainers[0]).popCurrentController();
                                } catch (java.lang.IllegalStateException e) {
                                    return false;
                                }
                            }
                            childRouter.setRoot(RouterTransaction.with(new AppsController()));
                            return true;
                        case R.id.action_menu:
                            getChildRouter(childContainers[0]).popCurrentController();
                            progressBar.setVisibility(View.VISIBLE);
                            String nicknameSP = sp.getString("nickname", "");

                            Call<UserModel> call = DataManager.getInstance().getUser(nicknameSP);
                            call.enqueue(new Callback<UserModel>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        User user = response.body().getUser();
                                        assert user != null;
                                        String name = user.getName();
                                        String surname = user.getSurname();
                                        String nickname = user.getNickname();
                                        Call<Photo> photoCall = DataManager.getInstance().getPhoto(user.getPhotoId());
                                        photoCall.enqueue(new Callback<Photo>() {
                                            @Override
                                            public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                                                assert response.body() != null;
                                                String photo = response.body().getPhoto();
                                                byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                childRouter.setRoot(RouterTransaction.with(new ProfileController(name, surname, nickname, decodedString)));
                                            }

                                            @Override
                                            public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                                    // TODO Корректно обработать ошибку
                                    if (t.toString().startsWith("java.net.SocketTimeoutException")) {
                                        Log.d("HomeController", "Catch!");
                                    }
                                }
                            });
                            return true;
                    }
                    return false;
                });
        return view;
    }
}
