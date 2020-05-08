package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.ScaleFadeChangeHandler;
import com.example.neobrain.utils.BundleBuilder;
import com.example.neobrain.widget.ElasticDragDismissFrameLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Контроллер для показа фотографии
@SuppressLint("ValidController")
public class PhotoController extends Controller {
    private final ElasticDragDismissFrameLayout.ElasticDragDismissCallback dragDismissListener = new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
        public void onDragDismissed() {
            overridePopHandler(new ScaleFadeChangeHandler());
            getRouter().popController(PhotoController.this);
        }
    };

    private int photoId;
    private ImageView photo;
    private TextView textView;

    PhotoController(int photoId) {
        this(new BundleBuilder(new Bundle())
                .putInt("photoId", photoId).build());
    }

    public PhotoController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.photoId = args.getInt("photoId");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.photo_controller, container, false);
        ButterKnife.bind(this, view);
        onViewBound(view);
        photo = view.findViewById(R.id.photo);
        textView = view.findViewById(R.id.title);
        getPhoto();
        return view;
    }

    private void getPhoto() {
        Call<Photo> photoCall = DataManager.getInstance().getPhoto(photoId);
        photoCall.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                assert response.body() != null;
                String photoEncoded = response.body().getPhoto();
                byte[] decodedString = Base64.decode(photoEncoded.getBytes(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                textView.setVisibility(View.INVISIBLE);
                photo.setImageBitmap(decodedByte);
            }

            @Override
            public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                photo.setImageResource(R.drawable.disappointed_but_relieved_face);
                textView.setText(R.string.errors_with_connection);
            }
        });
    }

    private void onViewBound(@NonNull View view) {
        ((ElasticDragDismissFrameLayout) view).addListener(dragDismissListener);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);

        ((ElasticDragDismissFrameLayout) view).removeListener(dragDismissListener);
    }
}
