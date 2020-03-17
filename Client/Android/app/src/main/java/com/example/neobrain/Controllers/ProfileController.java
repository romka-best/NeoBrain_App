package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.MainActivity;
import com.example.neobrain.R;
import com.example.neobrain.changehandler.FlipChangeHandler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidController")
public class ProfileController extends Controller {

    private CardView avatarCard;
    private ImageView avatar;
    private SwipeRefreshLayout swipeContainer;
    private TextView nameAndSurname;
    private TextView nickname;
    private ProgressBar progressBar;
    private static final int CAMERA_REQUEST = 100;
    private static final int RESULT_OK = -1;

    private View imagesButton;
    private View peopleButton;
    private View musicButton;
    private View achievementsButton;
    private View videosButton;

    private static final String MY_SETTINGS = "my_settings";
    SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.profile_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        imagesButton = view.findViewById(R.id.button_first);
        imagesButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new ImagesController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        peopleButton = view.findViewById(R.id.button_second);
        peopleButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new PeopleController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        musicButton = view.findViewById(R.id.button_third);
        musicButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new MusicController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        achievementsButton = view.findViewById(R.id.button_fourth);
        achievementsButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new AchievementsController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        videosButton = view.findViewById(R.id.button_fifth);
        videosButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new VideosController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));


        avatar = view.findViewById(R.id.avatar);

        avatarCard = view.findViewById(R.id.avatar_card);
        avatarCard.setPreventCornerOverlap(false);
        avatarCard.setOnClickListener(v -> {
            String[] testArray = new String[]{"Загрузить с устройства", "Сделать снимок", "Открыть", "Удалить"}; // TODO Изменить на R.string.{}
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                    .setTitle("Фотография") // TODO Изменить на R.string.{}
                    .setItems(testArray, (dialog, which) -> {
                        switch (which) {
                            case 1:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA_REQUEST);
                                break;
                            case 3:
                                new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()), R.style.AlertDialogCustom)
                                        .setMessage(R.string.delete_question)
                                        .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                                            // TODO Удаление
                                        })
                                        .setNegativeButton(R.string.cancel, null)
                                        .show();
                                break;
                        }
                    })
                    .show();
        });

        nameAndSurname = view.findViewById(R.id.name_surname);
        nickname = view.findViewById(R.id.nickname);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            // TODO Сделать обновление страницы
            swipeContainer.setRefreshing(true);
            getProfile();
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        getProfile();
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
        return view;
    }

    private void getProfile() {
        String nicknameSP = sp.getString("nickname", "");
        Call<UserModel> call = DataManager.getInstance().getUser(nicknameSP);
        call.enqueue(new Callback<UserModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    User user = response.body().getUser();
                    assert user != null;
                    nameAndSurname.setText(user.getName() + " " + user.getSurname());
                    nickname.setText(user.getNickname());
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем картинку
            Bitmap thumbnailBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            avatar.setImageBitmap(thumbnailBitmap);
            // TODO загрузку фотографии на сервер
        }
    }
}
