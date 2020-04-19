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
import com.example.neobrain.Adapters.MusicAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// Контроллер с музыкой
public class MusicController extends Controller {
    @BindView(R.id.musicRecycler)
    public RecyclerView musicRecycler;
    private MusicAdapter musicAdapter;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.music_controller, container, false);
        ButterKnife.bind(this, view);

        getMusic();
        return view;
    }

    private void getMusic() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        musicRecycler.setLayoutManager(mLayoutManager);
        musicRecycler.setItemAnimator(new DefaultItemAnimator());
        musicAdapter = new MusicAdapter(new ArrayList<>());
        musicRecycler.setAdapter(musicAdapter);
    }
}
