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
import com.example.neobrain.Adapters.PhotosAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// Контроллер с Фотографиями
public class PhotosController extends Controller {
    @BindView(R.id.photoRecycler)
    public RecyclerView photoRecycler;
    private PhotosAdapter photosAdapter;

    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.photos_controller, container, false);
        ButterKnife.bind(this, view);

        getPhotos();
        return view;
    }

    private void getPhotos() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        photoRecycler.setLayoutManager(mLayoutManager);
        photoRecycler.setItemAnimator(new DefaultItemAnimator());
        photosAdapter = new PhotosAdapter(new ArrayList<>());
        photoRecycler.setAdapter(photosAdapter);
    }
}
