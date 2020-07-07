package com.itschool.neobrain.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.itschool.neobrain.API.models.App;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.SearchAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Контроллер для поиска по приложениям */
public class SearchAppsController extends Controller {
    @BindView(R.id.appsRecycler)
    public RecyclerView appsRecycler;
    private SearchAdapter appsAdapter;
    private ArrayList<App> mApps = new ArrayList<>();

    private Router mainRouter;
    private boolean found;

    // Несколько конструкторов для первого создания и последующего обновления контроллера
    public SearchAppsController() {
        found = true;
    }

    public SearchAppsController(ArrayList<App> mApps, Router router, boolean found) {
        this.mApps = mApps;
        this.mainRouter = router;
        this.found = found;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_apps_controller, container, false);
        ButterKnife.bind(this, view);

        // Получаем приложения и выводим на экран
        getApps();
        return view;
    }

    /* Метод для получения и отображения приложений при поиске */
    private void getApps() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        appsRecycler.setLayoutManager(mLayoutManager);
        appsRecycler.setItemAnimator(new DefaultItemAnimator());

        // Присваиваем нужный адаптер
        appsAdapter = new SearchAdapter(mainRouter, found);
        appsAdapter.setAppsList(mApps);
        appsAdapter.notifyDataSetChanged();
        appsRecycler.setAdapter(appsAdapter);
    }
}
