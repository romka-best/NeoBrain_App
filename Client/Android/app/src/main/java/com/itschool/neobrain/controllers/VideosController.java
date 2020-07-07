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
import com.itschool.neobrain.adapters.VideosAdapter;
import com.itschool.neobrain.utils.BundleBuilder;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

// Контроллер видео
public class VideosController extends Controller {
    @BindView(R.id.videosRecycler)
    public RecyclerView videosRecycler;
    private VideosAdapter videosAdapter;

    private SharedPreferences sp;
    private boolean bottomIsGone = false;

    public VideosController() {

    }

    public VideosController(boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }

    public VideosController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.videos_controller, container, false);
        ButterKnife.bind(this, view);

        getVideo();
        return view;
    }

    private void getVideo() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        videosRecycler.setLayoutManager(mLayoutManager);
        videosRecycler.setItemAnimator(new DefaultItemAnimator());
        videosAdapter = new VideosAdapter(new ArrayList<>());
        videosRecycler.setAdapter(videosAdapter);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        try {
            if (bottomIsGone) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }
}
