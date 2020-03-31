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
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

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

        Router childRouterStart = getChildRouter(childContainers[0]).setPopsLastView(false);
        childRouterStart.setRoot(RouterTransaction.with(new LentaController()));

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_lenta);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    Router childRouter = getChildRouter(childContainers[0]).setPopsLastView(false);
                    switch (item.getItemId()) {
                        case R.id.action_lenta:
                            getChildRouter(childContainers[0]).popCurrentController();
                            childRouter.setRoot(RouterTransaction.with(new LentaController()));
                            return true;
                        case R.id.action_navigation:
                            getChildRouter(childContainers[0]).popCurrentController();
                            childRouter.setRoot(RouterTransaction.with(new NavigationController()));
                            return true;
                        case R.id.action_messages:
                            getChildRouter(childContainers[0]).popCurrentController();
                            childRouter.setRoot(RouterTransaction.with(new ChatController()));
                            return true;
                        case R.id.action_apps:
                            getChildRouter(childContainers[0]).popCurrentController();
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
                                        String photo = response.body().getPhoto();
                                        assert user != null;
                                        String name = user.getName();
                                        String surname = user.getSurname();
                                        String nickname = user.getNickname();
                                        byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        childRouter.setRoot(RouterTransaction.with(new ProfileController(name, surname, nickname, decodedString)));
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                                    // TODO Корректно обработать ошибку и изменить на Snackbar
                                    Log.e("ERROR", t.toString());
                                    Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                            return true;
                    }
                    return false;
                });
        return view;
    }
}
