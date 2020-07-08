package com.itschool.neobrain.controllers;

// Импортируем нужные библиотеки

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.API.models.ChatModel;
import com.itschool.neobrain.API.models.People;
import com.itschool.neobrain.API.models.PeopleModel;
import com.itschool.neobrain.API.models.Person;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.PhotoModel;
import com.itschool.neobrain.API.models.Post;
import com.itschool.neobrain.API.models.PostList;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.MainActivity;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.PostAdapter;
import com.itschool.neobrain.utils.BundleBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Контроллер для работы с Профилем */
@SuppressLint("ValidController")
public class ProfileController extends Controller {

    @BindView(R.id.postRecycler)
    public RecyclerView postRecycler;
    private PostAdapter postAdapter;
    @BindView(R.id.coordinatorLayout)
    public CoordinatorLayout coordinatorLayout;
    @BindView(R.id.avatar)
    public ImageView avatar;
    @BindView(R.id.swipeContainer)
    public SwipeRefreshLayout swipeContainer;
    @BindView(R.id.status_circle)
    public View statusCircle;
    @BindView(R.id.name_surname)
    public TextView nameAndSurname;
    @BindView(R.id.nickname)
    public TextView nickname;
    @BindView(R.id.followers_count)
    public TextView followersCount;
    @BindView(R.id.subscribe_count)
    public TextView subscribersCount;
    @BindView(R.id.fabAdd)
    public FloatingActionButton fabAdd;
    @BindView(R.id.fabEdit)
    public MaterialButton buttonEdit;
    @BindView(R.id.infoButton)
    public MaterialButton buttonInfo;
    @BindView(R.id.moreButton)
    public ImageButton moreButton;
    @BindView(R.id.progress_circular)
    public ProgressBar progressBar;
    @BindView(R.id.emoji)
    public ImageView emoji;
    @BindView(R.id.titleError)
    public TextView textError;
    @BindView(R.id.city_age_gender)
    public TextView cityAgeGenderText;
    private BottomNavigationView bottomNavigationView;

    private SharedPreferences sp;

    private static final int CAMERA_REQUEST = 100;
    private static final int PICK_IMAGE = 101;
    private static final int RESULT_OK = -1;

    private int photoId;
    private int userId = 0;
    private boolean inSubscribe = false;
    private boolean bottomIsGone = false;
    private boolean isLoaded = false;
    private Integer userIdSP;

    // Несколько конструкторов для передачи необходимых значений в разных ситуациях
    public ProfileController() {

    }

    public ProfileController(int userId) {
        this(new BundleBuilder(new Bundle())
                .putInt("userId", userId)
                .build());
    }

    public ProfileController(int userId, boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putInt("userId", userId)
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }

    public ProfileController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.userId = args.getInt("userId");
        this.bottomIsGone = args.getBoolean("bottomIsGone");
    }

    /* Геттер и сеттер для PhotoId*/
    public int getPhotoId() {
        return photoId;
    }

    private void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.profile_controller, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);


        // Присваиваем переменным значения, ставим обработчики нажатий и анимации перехода

        View imagesButton = view.findViewById(R.id.button_first);
        imagesButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new PhotosController((userId == 0) ? userIdSP : userId, bottomIsGone))
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View peopleButton = view.findViewById(R.id.button_second);
        peopleButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new PeopleController((userId == 0) ? userIdSP : userId, bottomIsGone, false))
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View musicButton = view.findViewById(R.id.button_third);
        musicButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new MusicController(bottomIsGone))
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View achievementsButton = view.findViewById(R.id.button_fourth);
        achievementsButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new AchievementsController((userId == 0) ? userIdSP : userId, bottomIsGone))
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        View videosButton = view.findViewById(R.id.button_fifth);
        videosButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new VideosController(bottomIsGone))
                .popChangeHandler(new VerticalChangeHandler())
                .pushChangeHandler(new VerticalChangeHandler())));

        CardView avatarCard = view.findViewById(R.id.avatar_card);
        avatarCard.setPreventCornerOverlap(false);
        avatarCard.setOnClickListener(v -> launchPhoto());

        // Проверяем не принадлежит ли профиль текцщему пользователю
        userIdSP = sp.getInt("userId", -1);
        if (userId == userIdSP) {
            userId = 0;
        }

        fabAdd.setColorFilter(Color.argb(255, 255, 255, 255));
        fabAdd.setOnClickListener(v -> {
                    // Если это профиль текущего пользователя,
                    // убираем bottomNavigationView и создаём новый контроллер постов
                    if (userId == 0) {
                        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                        bottomNavigationView.setVisibility(View.GONE);
                        getRouter().pushController(RouterTransaction.with(new PostController()));
                    }
                    // Иначе, проверяем подписан ли текущий пользователь на данного,
                    // в зависимости от этого вырисовываем нужную картинку
                    else {
                        if (inSubscribe) {
                            Call<Status> deleteCall = DataManager.getInstance().deletePeople(userIdSP, userId);
                            deleteCall.enqueue(new Callback<Status>() {
                                @Override
                                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                    if (response.isSuccessful()) {
                                        fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_add, Objects.requireNonNull(getActivity()).getTheme()));
                                        inSubscribe = false;
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                                }
                            });
                        } else {
                            PeopleModel people = new PeopleModel(userIdSP, userId);
                            Call<Status> addCall = DataManager.getInstance().createPeople(people);
                            addCall.enqueue(new Callback<Status>() {
                                @Override
                                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                    if (response.isSuccessful()) {
                                        fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_remove, Objects.requireNonNull(getActivity()).getTheme()));
                                        inSubscribe = true;
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                                }
                            });
                        }
                    }
                }
        );

        // Устанавливаем слушатель на кнопку редактирования
        buttonEdit.setOnClickListener(v -> {
            // Скрываем BottomNavigationView
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
            // Если это текущий пользователь, то открываем соответствующий контроллер,
            // иначе перебрасываем текущего пользователя в чат с данным
            if (userId == 0) {
                getRouter().pushController(RouterTransaction.with(new ProfileEditController(true)));
            } else {
                Call<ChatModel> chatModelCall = DataManager.getInstance().getUsersChat(userIdSP, userId);
                chatModelCall.enqueue(new Callback<ChatModel>() {
                    @Override
                    public void onResponse(@NotNull Call<ChatModel> call, @NotNull Response<ChatModel> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            Chat chat = response.body().getChat();
                            if (chat == null) {
                                chat = new Chat();
                                chat.setId(-1);
                            }
                            chat.setPhotoId(getPhotoId());
                            getRouter().pushController(RouterTransaction.with(new MessagesController(chat, userId)));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ChatModel> call, @NotNull Throwable t) {

                    }
                });
            }
        });

        // Устанавливаем слушатель на кнопку с информацией
        buttonInfo.setOnClickListener(v -> {
            getRouter().pushController(RouterTransaction.with(new ProfileInfoController((userId == 0) ? userIdSP : userId, true))
                    .popChangeHandler(new VerticalChangeHandler())
                    .pushChangeHandler(new VerticalChangeHandler()));
        });

        // Устанавливаем слушатель на кнопку дополнительной информации
        moreButton.setOnClickListener(v -> {
            if (userId == 0) {
                getRouter().pushController(RouterTransaction.with(new SettingsController())
                        .popChangeHandler(new HorizontalChangeHandler())
                        .pushChangeHandler(new HorizontalChangeHandler()));
            } else {
                // TODO
            }
        });

        // Устанавливаем слушатель для обновления страницы профиля при свайпе вверх
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            // Обновляем информацию
            getProfile();
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        // Получаем данные о пользователе
        getProfile();
        swipeContainer.setVisibility(View.INVISIBLE);
        fabAdd.setVisibility(View.INVISIBLE);
        buttonEdit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    /* Метод, получающий нужные данные о пользователе,
     посредством запроса к серверу и воводящий их */
    private void getProfile() {
        Call<UserModel> call;
        if (userId == 0) {
            call = DataManager.getInstance().getUser(userIdSP);
        } else {
            call = DataManager.getInstance().getUser(userId);
        }
        // Парсим ответ сервера, получаем данные
        call.enqueue(new Callback<UserModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    User user = response.body().getUser();
                    ArrayList<PhotoModel> photos = (ArrayList<PhotoModel>) response.body().getPhotos();
                    assert user != null;
                    nameAndSurname.setText(user.getName() + " " + user.getSurname());
                    nickname.setText(user.getNickname());
                    if (user.getFollowersCount() >= 1000 && user.getFollowersCount() < 1000000) {
                        followersCount.setText(user.getFollowersCount().toString().charAt(0) + "K");
                    } else {
                        followersCount.setText(user.getFollowersCount().toString());
                    }

                    subscribersCount.setText(user.getSubscriptionsCount().toString());
                    if (user.getStatus() == 0 && userId != 0) {
                        statusCircle.setBackgroundResource(R.drawable.circle_offline);
                    } else {
                        statusCircle.setBackgroundResource(R.drawable.circle_online);
                    }
                    List<String> cityAgeGender = new ArrayList<>();
                    if (user.getRepublic() != null) {
                        cityAgeGender.add(user.getRepublic());
                    }
                    if (user.getCity() != null) {
                        cityAgeGender.add(user.getCity());
                    }
                    if (user.getAge() != null) {
                        cityAgeGender.add(user.getAge().toString());
                    }
                    if (user.getGender() != null) {
                        if (user.getGender() == 0) {
                            cityAgeGender.add(Objects.requireNonNull(getResources()).getString(R.string.gender_w));
                        } else if (user.getGender() == 1) {
                            cityAgeGender.add(Objects.requireNonNull(getResources()).getString(R.string.gender_m));
                        }
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
                    for (PhotoModel photo : photos) {
                        if (photo.getPhoto().getAvatar()) {
                            // Загружаем фото пользователя
                            setPhotoId(photo.getPhoto().getId());
                            break;
                        }
                    }
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
                            // Если это не текущий пользователь, показываем кнопку "Написать сообщение"
                            if (userId != 0) {
                                buttonEdit.setText(Objects.requireNonNull(getResources()).getString(R.string.write_message));
                                moreButton.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_more_vert, Objects.requireNonNull(getActivity()).getTheme()));
                                // Получаем друзей пользователя, парсим ответ
                                Call<People> peopleCall = DataManager.getInstance().getPeople(userIdSP);
                                peopleCall.enqueue(new Callback<People>() {
                                    @Override
                                    public void onResponse(@NotNull Call<People> call, @NotNull Response<People> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            People people = response.body();
                                            List<Person> personList = people.getPeople();
                                            if (personList.size() == 0) {
                                                fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_add, Objects.requireNonNull(getActivity()).getTheme()));
                                                return;
                                            }
                                            for (int i = 0; i < personList.size(); i++) {
                                                if (personList.get(i).getUserId() == userId) {
                                                    fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_remove, Objects.requireNonNull(getActivity()).getTheme()));
                                                    inSubscribe = true;
                                                    break;
                                                } else {
                                                    fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_add, Objects.requireNonNull(getActivity()).getTheme()));
                                                }
                                            }
                                            isLoaded = true;
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<People> call, @NotNull Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        }
                    });
                }
            }

            // Если что-то пошло не так и запрос к серверу не удался, сообщаем об этом
            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                if (!isLoaded) {
                    long mills = 1000L;
                    Vibrator vibrator = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);

                    assert vibrator != null;
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(mills);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    emoji.setVisibility(View.VISIBLE);
                    textError.setVisibility(View.VISIBLE);
                }
            }
        });
        // Получаем посты пользователя
        getPosts();
    }

    /* Метод, получающий и выводящий посты пользователя */
    private void getPosts() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        postRecycler.setLayoutManager(mLayoutManager);
        postRecycler.setItemAnimator(new DefaultItemAnimator());
        Call<PostList> call;
        if (userId == 0) {
            call = DataManager.getInstance().getPosts(userIdSP, userIdSP);
        } else {
            call = DataManager.getInstance().getPosts(userId, userIdSP);
        }
        // Парсим ответ сервера
        call.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(@NotNull Call<PostList> call, @NotNull Response<PostList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Post> posts = response.body().getPosts();
                    ArrayList<Post> mPosts = new ArrayList<>();
                    for (Post post : posts) {
                        mPosts.add(new Post(post.getId(), post.getTitle(), post.getText(), post.getPhotoId(), post.getCreatedDate(), post.getUserId(), post.getAuthor()));
                        if (post.getLikeEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setLikeEmojiCount(post.getLikeEmojiCount());
                            mPosts.get(mPosts.size() - 1).setLikeEmoji(post.getLikeEmoji());
                        }
                        if (post.getLaughterEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setLaughterEmojiCount(post.getLaughterEmojiCount());
                            mPosts.get(mPosts.size() - 1).setLaughterEmoji(post.getLaughterEmoji());
                        }
                        if (post.getHeartEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setHeartEmojiCount(post.getHeartEmojiCount());
                            mPosts.get(mPosts.size() - 1).setHeartEmoji(post.getHeartEmoji());
                        }
                        if (post.getDisappointedEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setDisappointedEmojiCount(post.getDisappointedEmojiCount());
                            mPosts.get(mPosts.size() - 1).setDisappointedEmoji(post.getDisappointedEmoji());
                        }
                        if (post.getSmileEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setSmileEmojiCount(post.getSmileEmojiCount());
                            mPosts.get(mPosts.size() - 1).setSmileEmoji(post.getSmileEmoji());
                        }
                        if (post.getAngryEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setAngryEmojiCount(post.getAngryEmojiCount());
                            mPosts.get(mPosts.size() - 1).setAngryEmoji(post.getAngryEmoji());
                        }
                        if (post.getSmileWithHeartEyesCount() != null) {
                            mPosts.get(mPosts.size() - 1).setSmileWithHeartEyesCount(post.getSmileWithHeartEyesCount());
                            mPosts.get(mPosts.size() - 1).setSmileWithHeartEyes(post.getSmileWithHeartEyes());
                        }
                        if (post.getScreamingEmojiCount() != null) {
                            mPosts.get(mPosts.size() - 1).setScreamingEmojiCount(post.getScreamingEmojiCount());
                            mPosts.get(mPosts.size() - 1).setScreamingEmoji(post.getScreamingEmoji());
                        }
                    }

                    // Сортируем посты по времени
                    Collections.sort(mPosts, Post.COMPARE_BY_TIME);
                    // Подключаем нужный адаптер
                    postAdapter = new PostAdapter(mPosts, getRouter(), false);
                    postRecycler.setNestedScrollingEnabled(false);
                    postRecycler.setAdapter(postAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostList> call, @NotNull Throwable t) {
            }
        });
    }

    /* Метод, получающий результат от Activity */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Запрос к камере и обновлении аватара
            case CAMERA_REQUEST:
                try {
                    if (resultCode == RESULT_OK) {
                        Bitmap thumbnailBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        assert thumbnailBitmap != null;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        byte[] byteArray = stream.toByteArray();
                        byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);

                        avatar.setImageBitmap(thumbnailBitmap);

                        Integer userIdSP = sp.getInt("userId", -1);
                        User user = new User();
                        user.setPhoto(new String(encoded));
                        user.setAvatar(true);
                        Call<Status> call = DataManager.getInstance().editUser(userIdSP, user);
                        call.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                if (response.isSuccessful()) {
                                    Status post = response.body();
                                    assert post != null;
                                    setPhotoId(Integer.parseInt(post.getMessage()));
                                    getProfile();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                            }
                        });
                    }
                } catch (RuntimeException e) {
                    Snackbar.make(getView(), R.string.errors_with_size, Snackbar.LENGTH_LONG).show();
                }
                break;
            // Запрос к выборе фото для аватара из уже существующих
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        assert imageUri != null;
                        final InputStream imageStream = Objects.requireNonNull(getApplicationContext()).getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        int rotation = getBitmapOrientation(getRealPathFromURI(imageUri));
                        Bitmap newSelectedImage = rotateBitmap(selectedImage, rotation);
                        assert newSelectedImage != null;
                        avatar.setImageBitmap(newSelectedImage);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        newSelectedImage.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        byte[] byteArray = stream.toByteArray();
                        byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);

                        Integer userIdSP = sp.getInt("userId", -1);
                        User user = new User();
                        user.setPhoto(new String(encoded));
                        user.setAvatar(true);
                        Call<Status> call = DataManager.getInstance().editUser(userIdSP, user);
                        call.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                if (response.isSuccessful()) {
                                    Status post = response.body();
                                    assert post != null;
                                    getProfile();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        Snackbar.make(getView(), R.string.errors_with_size, Snackbar.LENGTH_LONG).show();
                    }
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* Обработчик нажатия на аватар пользователя */
    @OnClick(R.id.avatar_card)
    void launchPhoto() {
        if (userId == 0) {
            // Если это текущий пользователь, предлагаем ему определённые возможности с помощью диалогового окна
            String[] testArray = new String[]{Objects.requireNonNull(getResources()).getString(R.string.load_device), Objects.requireNonNull(getResources()).getString(R.string.do_photo), Objects.requireNonNull(getResources()).getString(R.string.open), Objects.requireNonNull(getResources()).getString(R.string.delete)};
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                    .setTitle(Objects.requireNonNull(getResources()).getString(R.string.photo))
                    .setItems(testArray, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                // Запрашиваем доступ к чтению и записи файлов
                                int permissionStatusRead = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE);
                                int permissionStatusWrite = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                if (permissionStatusRead == PackageManager.PERMISSION_GRANTED && permissionStatusWrite == PackageManager.PERMISSION_GRANTED) {
                                    Intent i = new Intent(Intent.ACTION_PICK);
                                    i.setType("image/*");
                                    startActivityForResult(i, PICK_IMAGE);
                                } else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MainActivity.PERMISSION_REQUEST_CODE);
                                }
                                break;
                            case 1:
                                // Запрашиваем доступ к камере
                                int permissionStatusCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                                if (permissionStatusCamera == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAMERA_REQUEST);
                                } else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                                            MainActivity.PERMISSION_REQUEST_CODE);
                                }
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
                                                    if (response.isSuccessful()) {
                                                        setPhotoId(2);
                                                        getProfile();
                                                    }
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
        } else {
            // Если это не текущий пользователь, открываем фото в большом формате
            getRouter().pushController(RouterTransaction.with(new PhotoController(photoId))
                    .popChangeHandler(new VerticalChangeHandler(false))
                    .pushChangeHandler(new VerticalChangeHandler()));
        }
    }

    /* Метод, для получения битового представление пути */
    private static int getBitmapOrientation(String path) {
        ExifInterface exif;
        int orientation = 0;
        try {
            exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orientation;
    }

    /* Метод, для переворота картинки */
    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Метод, для получения строкового пути из URI объекта */
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = Objects.requireNonNull(getActivity()).managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
}
