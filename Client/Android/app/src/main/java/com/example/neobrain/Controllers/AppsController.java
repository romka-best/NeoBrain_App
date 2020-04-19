package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.Adapters.AppsAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// Контроллер приложений
public class AppsController extends Controller {
    @BindView(R.id.appsRecycler)
    public RecyclerView appsRecycler;
    private AppsAdapter appsAdapter;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.apps_controller, container, false);
        ButterKnife.bind(this, view);

        getMyApps();
        return view;
    }

    private void getMyApps() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        appsRecycler.setLayoutManager(mLayoutManager);
        appsRecycler.setItemAnimator(new DefaultItemAnimator());
        appsAdapter = new AppsAdapter(new ArrayList<>());
        appsRecycler.setAdapter(appsAdapter);
    }
}
