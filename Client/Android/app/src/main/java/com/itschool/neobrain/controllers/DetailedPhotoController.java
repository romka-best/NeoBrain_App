package com.itschool.neobrain.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.Controller;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itschool.neobrain.R;
import com.itschool.neobrain.utils.BundleBuilder;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
/* Контроллер для работы с фото, которое не является аватаром */
public class DetailedPhotoController extends Controller {
    private BottomNavigationView bottomNavigationView;
    private boolean bottomIsGone = true;
    private String photo;
    @BindView(R.id.photo)
    ImageView photoImageView;

    // Несколько конструкторов для передачи необходимых значений в разных ситуациях
    public DetailedPhotoController() {
    }
    public DetailedPhotoController(boolean bottomIsGone, String photo) {
        this(new BundleBuilder(new Bundle())
                .putBoolean("bottomIsGone", bottomIsGone)
                .putString("mPhoto", photo)
                .build());
    }
    public DetailedPhotoController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
        this.photo = args.getString("mPhoto");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.detailed_photo_controller, container, false);
        ButterKnife.bind(this, view);
        // Загружаем фото
        loadPhoto();

        return view;
    }

    /* Метод загрузки фото */
    private void loadPhoto() {
        byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
        Bitmap original = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap decoded = Bitmap.createScaledBitmap(original, original.getWidth() / 2, original.getHeight() / 2, false);
        photoImageView.setImageBitmap(decoded);
    }

    /* Вызывается, когда контроллер связывается с активностью */
    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        // Пробуем скрыть BottomNavigationView, если уже скрыта, ставим заглушку
        try {
            if (bottomIsGone) {
                bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }

    /* Метод, определяющий поведение при нажатии на кнопку "назад" на устройстве */
    @Override
    public boolean handleBack() {
        // Показываем BottomNavigationView
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }
}
