package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.util.BundleBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

@SuppressLint("ValidController")
public class ProfileController extends Controller {

    private ImageView avatar;
    private SwipeRefreshLayout swipeContainer;
    private TextView nameAndSurname;
    private TextView nickname;
    private static final int CAMERA_REQUEST = 100;
    private static final int RESULT_OK = -1;
    private String nameText;
    private String surnameText;
    private String nicknameText;
    private byte[] decodedStringPhoto;

    private SharedPreferences sp;


    ProfileController(String name, String surname, String nickname, byte[] decodedString) {
        this(new BundleBuilder(new Bundle())
                .putString("name", name)
                .putString("surname", surname)
                .putString("nickname", nickname)
                .putByteArray("decodedString", decodedString)
                .build());
    }

    public ProfileController(Bundle args) {
        super(args);
        assert args != null;
        this.nameText = args.getString("name");
        this.surnameText = args.getString("surname");
        this.nicknameText = args.getString("nickname");
        this.decodedStringPhoto = args.getByteArray("decodedString");
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.profile_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        View imagesButton = view.findViewById(R.id.button_first);
        imagesButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new ImagesController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View peopleButton = view.findViewById(R.id.button_second);
        peopleButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new PeopleController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View musicButton = view.findViewById(R.id.button_third);
        musicButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new MusicController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View achievementsButton = view.findViewById(R.id.button_fourth);
        achievementsButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new AchievementsController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View videosButton = view.findViewById(R.id.button_fifth);
        videosButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new VideosController())
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));


        avatar = view.findViewById(R.id.avatar);

        CardView avatarCard = view.findViewById(R.id.avatar_card);
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
                                            // TODO Корректно удалить фотографию
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
        nameAndSurname.setText(this.nameText + " " + this.surnameText);
        nickname.setText(this.nicknameText);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(this.decodedStringPhoto, 0, decodedStringPhoto.length);
        avatar.setImageBitmap(decodedByte);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            getProfile();
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
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
                    Call<Photo> photoCall = DataManager.getInstance().getPhoto(user.getPhotoId());
                    photoCall.enqueue(new Callback<Photo>() {
                        @Override
                        public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                            assert response.body() != null;
                            String photo = response.body().getPhoto();
                            byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            avatar.setImageBitmap(decodedByte);
                        }

                        @Override
                        public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                // TODO Корректно обработать ошибку и изменить на Snackbar
                Log.e("ERROR", t.toString());
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
            assert thumbnailBitmap != null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);
            byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);

            avatar.setImageBitmap(thumbnailBitmap);

            String nicknameSP = sp.getString("nickname", null);
//            User user = new User();
//            user.setPhoto(Arrays.toString(decoded));
//            Call<Status> call = DataManager.getInstance().editUser(nicknameSP, user);
//            call.enqueue(new Callback<Status>() {
//                @Override
//                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
//                }
//
//                @Override
//                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
//                }
//            });
        }
    }
}
