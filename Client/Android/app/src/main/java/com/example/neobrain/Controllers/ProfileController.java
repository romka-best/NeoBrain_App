package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.PostAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер для работы с Профилем
@SuppressLint("ValidController")
public class ProfileController extends Controller {

    @BindView(R.id.postRecycler)
    public RecyclerView postRecycler;
    private PostAdapter postAdapter;

    private ImageView avatar;
    private SwipeRefreshLayout swipeContainer;
    private TextView nameAndSurname;
    private TextView nickname;
    private TextView followersCount;
    private TextView subscribersCount;
    private FloatingActionButton fabAdd;
    private MaterialButton buttonEdit;
    private ProgressBar progressBar;
    private ImageView emoji;
    private TextView textError;
    private TextView cityAgeGenderText;

    private static final int CAMERA_REQUEST = 100;
    private static final int PICK_IMAGE = 101;
    private static final int RESULT_OK = -1;

    private int photoId;

    public int getPhotoId() {
        return photoId;
    }

    private void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    private SharedPreferences sp;

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
                            case 0:
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType("image/*");
                                startActivityForResult(i, PICK_IMAGE);
                                break;
                            case 1:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA_REQUEST);
                                break;
                            case 2:
                                getRouter().pushController(RouterTransaction.with(new PhotoController(photoId))
                                        .popChangeHandler(new VerticalChangeHandler(false))
                                        .pushChangeHandler(new VerticalChangeHandler()));
                                break;
                            case 3:
                                new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()), R.style.AlertDialogCustom)
                                        .setMessage(R.string.delete_question)
                                        .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                                            if (photoId == 2) {
                                                return;
                                            }
                                            Call<Status> call = DataManager.getInstance().deletePhoto(photoId);
                                            call.enqueue(new Callback<Status>() {
                                                @Override
                                                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                                    String nicknameSP = sp.getString("nickname", null);
                                                    User user = new User();
                                                    user.setPhotoId(2);
                                                    Call<Status> userCall = DataManager.getInstance().editUser(nicknameSP, user);
                                                    userCall.enqueue(new Callback<Status>() {
                                                        @Override
                                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                                            setPhotoId(2);
                                                        }

                                                        @Override
                                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                                }
                                            });
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
        followersCount = view.findViewById(R.id.followers_count);
        subscribersCount = view.findViewById(R.id.subscribe_count);
        cityAgeGenderText = view.findViewById(R.id.city_age_gender);

        progressBar = view.findViewById(R.id.progress_circular);
        emoji = view.findViewById(R.id.emoji);
        textError = view.findViewById(R.id.titleError);

        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setColorFilter(Color.argb(255, 255, 255, 255));
        fabAdd.setOnClickListener(v -> {
                    getRouter().pushController(RouterTransaction.with(new PostController()));
                }
        );

        buttonEdit = view.findViewById(R.id.fabEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            getProfile();
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        getProfile();
        swipeContainer.setVisibility(View.INVISIBLE);
        fabAdd.setVisibility(View.INVISIBLE);
        buttonEdit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
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
                    followersCount.setText(user.getFollowersCount().toString());
                    subscribersCount.setText(user.getSubscriptionsCount().toString());
                    List<String> cityAgeGender = new ArrayList<>();
                    if (!user.getRepublic().equals("")) {
                        cityAgeGender.add(user.getRepublic());
                    }
                    if (!user.getCity().equals("")) {
                        cityAgeGender.add(user.getCity());
                    }
                    if (user.getAge() != null) {
                        cityAgeGender.add(user.getAge().toString());
                    }
                    if (user.getGender() != null) {
                        if (user.getGender() == 0) {
                            cityAgeGender.add(Objects.requireNonNull(getResources()).getString(R.string.gender_w));
                        } else
                            cityAgeGender.add(Objects.requireNonNull(getResources()).getString(R.string.gender_m));
                    }
                    StringBuffer sb = new StringBuffer();
                    boolean flag = false;
                    for (int i = 0; i < cityAgeGender.size(); i++) {
                        if (sb.length() >= 20 && !flag) {
                            sb.append("\n");
                            flag = true;
                        }
                        if (i != cityAgeGender.size() - 1) {
                            sb.append(cityAgeGender.get(i)).append(" | ");
                        } else {
                            sb.append(cityAgeGender.get(i));
                        }
                    }
                    cityAgeGenderText.setText(sb);
                    setPhotoId(user.getPhotoId());
                    Call<Photo> photoCall = DataManager.getInstance().getPhoto(getPhotoId());
                    photoCall.enqueue(new Callback<Photo>() {
                        @Override
                        public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                            assert response.body() != null;
                            if (response.isSuccessful()) {
                                String photo = response.body().getPhoto();
                                byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                avatar.setImageBitmap(decodedByte);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            swipeContainer.setVisibility(View.VISIBLE);
                            fabAdd.setVisibility(View.VISIBLE);
                            buttonEdit.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                if (t.toString().startsWith("java.net.SocketTimeoutException")) {
                    progressBar.setVisibility(View.INVISIBLE);
                    emoji.setVisibility(View.VISIBLE);
                    textError.setVisibility(View.VISIBLE);
                }
            }
        });
        getPosts();
    }

    private void getPosts() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        postRecycler.setLayoutManager(mLayoutManager);
        postRecycler.setItemAnimator(new DefaultItemAnimator());
        String nicknameSP = sp.getString("nickname", "");
        Call<PostModel> call = DataManager.getInstance().getPosts(nicknameSP);
        call.enqueue(new Callback<PostModel>() {
            @Override
            public void onResponse(@NotNull Call<PostModel> call, @NotNull Response<PostModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Post> posts = response.body().getPosts();
                    ArrayList<Post> mPosts = new ArrayList<>();
                    for (Post post : posts) {
                        mPosts.add(new Post(post.getTitle(), post.getText(), post.getPhotoId(), post.getCreatedDate()));
                    }
                    postAdapter = new PostAdapter(mPosts);
                    postRecycler.setAdapter(postAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostModel> call, @NotNull Throwable t) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnailBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    assert thumbnailBitmap != null;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);

                    avatar.setImageBitmap(thumbnailBitmap);

                    String nicknameSP = sp.getString("nickname", null);
                    User user = new User();
                    user.setPhoto(new String(encoded));
                    Call<Status> call = DataManager.getInstance().editUser(nicknameSP, user);
                    call.enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        }

                        @Override
                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                        }
                    });
                }
                break;
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        assert imageUri != null;
                        final InputStream imageStream = Objects.requireNonNull(getApplicationContext()).getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap newSelectedImage = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight(), matrix, true);
                        avatar.setImageBitmap(newSelectedImage);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        newSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);

                        String nicknameSP = sp.getString("nickname", null);
                        User user = new User();
                        user.setPhoto(new String(encoded));
                        Call<Status> call = DataManager.getInstance().editUser(nicknameSP, user);
                        call.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
