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
import com.example.neobrain.Adapters.NavigationAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// Контроллер с навигацией
public class NavigationController extends Controller {
    @BindView(R.id.navigationRecycler)
    public RecyclerView navigationRecycler;
    private NavigationAdapter navigationAdapter;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.navigation_controller, container, false);
        ButterKnife.bind(this, view);

        getNavigation();
        return view;
    }

    private void getNavigation() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        navigationRecycler.setLayoutManager(mLayoutManager);
        navigationRecycler.setItemAnimator(new DefaultItemAnimator());
        navigationAdapter = new NavigationAdapter(new ArrayList<>());
        navigationRecycler.setAdapter(navigationAdapter);
    }
}
