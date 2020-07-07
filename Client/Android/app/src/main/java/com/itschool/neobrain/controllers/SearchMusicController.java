package com.itschool.neobrain.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.itschool.neobrain.R;

import butterknife.ButterKnife;

/* Контроллер для поиска по музыке */
public class SearchMusicController extends Controller {
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_music_controller, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
