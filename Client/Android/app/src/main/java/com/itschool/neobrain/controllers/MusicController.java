package com.itschool.neobrain.controllers;

// Импортируем нужные библиотеки

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.MusicAdapter;
import com.itschool.neobrain.utils.BundleBuilder;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

/* Контроллер с музыкой */
public class MusicController extends Controller {
    @BindView(R.id.musicRecycler)
    public RecyclerView musicRecycler;
    private MusicAdapter musicAdapter;

    private boolean bottomIsGone = false;

    private SharedPreferences sp;

    // Несколько конструкторов для передачи необходимых значений в разных ситуациях
    public MusicController() {

    }
    public MusicController(boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }
    public MusicController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
    }


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.music_controller, container, false);
        ButterKnife.bind(this, view);

        // Получаем музыку
        getMusic();
        return view;
    }

    /* Метод, получающий нужную музыку с сервера */
    private void getMusic() {
        // Настраиваем RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        musicRecycler.setLayoutManager(mLayoutManager);
        musicRecycler.setItemAnimator(new DefaultItemAnimator());
        // Присваиваем адаптер
        musicAdapter = new MusicAdapter(new ArrayList<>());
        musicRecycler.setAdapter(musicAdapter);
    }

    /* Вызывается, когда контроллер связывается с активностью */
    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        // Пробуем скрыть BottomNavigationView, если уже скрыта, ставим заглушку
        try {
            if (bottomIsGone) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }
}
