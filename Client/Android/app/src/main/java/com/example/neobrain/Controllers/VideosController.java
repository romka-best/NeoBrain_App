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
import com.example.neobrain.Adapters.VideosAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// Контроллер видео
public class VideosController extends Controller {
    @BindView(R.id.videosRecycler)
    public RecyclerView videosRecycler;
    private VideosAdapter videosAdapter;

    private SharedPreferences sp;

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
}
