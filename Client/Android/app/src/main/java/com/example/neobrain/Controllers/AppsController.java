package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.App;
import com.example.neobrain.API.model.Apps;
import com.example.neobrain.Adapters.AppsAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер приложений
public class AppsController extends Controller implements AppsAdapter.CallbackInterface {
    @BindView(R.id.appsRecycler)
    public RecyclerView myAppsRecycler;
    @BindView(R.id.appsRecycler2)
    public RecyclerView otherAppsRecycler;
    private AppsAdapter myAppsAdapter;
    private AppsAdapter otherAppsAdapter;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.apps_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        getMyApps();
        getOtherApps();
        return view;
    }

    private void getMyApps() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        myAppsRecycler.setLayoutManager(mLayoutManager);
        myAppsRecycler.setItemAnimator(new DefaultItemAnimator());

        Integer userIdSP = sp.getInt("userId", -1);
        Call<Apps> appsCall = DataManager.getInstance().getMyApps(userIdSP);
        appsCall.enqueue(new Callback<Apps>() {
            @Override
            public void onResponse(@NotNull Call<Apps> call, @NotNull Response<Apps> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<App> apps = response.body().getApps();
                    ArrayList<App> mApps = new ArrayList<>();
                    for (App app : apps) {
                        mApps.add(new App(app.getId(), app.getTitle(), app.getSecondaryText(), app.getDescription(), app.getLinkAndroid(), app.getPhotoId(), true));
                    }
                    myAppsAdapter = new AppsAdapter(mApps, getRouter());
                    myAppsAdapter.setCallback(AppsController.this::onEmptyViewRetryClick);
                    myAppsRecycler.setAdapter(myAppsAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Apps> call, @NotNull Throwable t) {

            }
        });
    }

    private void getOtherApps() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        otherAppsRecycler.setLayoutManager(mLayoutManager);
        otherAppsRecycler.setItemAnimator(new DefaultItemAnimator());

        Call<Apps> appsCall = DataManager.getInstance().getOtherApps();
        appsCall.enqueue(new Callback<Apps>() {
            @Override
            public void onResponse(@NotNull Call<Apps> call, @NotNull Response<Apps> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<App> apps = response.body().getApps();
                    ArrayList<App> mApps = new ArrayList<>();
                    for (App app : apps) {
                        mApps.add(new App(app.getId(), app.getTitle(), app.getSecondaryText(), app.getDescription(), app.getLinkAndroid(), app.getPhotoId(), false));
                    }
                    otherAppsAdapter = new AppsAdapter(mApps, getRouter());
                    otherAppsRecycler.setAdapter(otherAppsAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Apps> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onEmptyViewRetryClick() {
        getMyApps();
    }
}
