package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.People;
import com.example.neobrain.API.model.PeopleModel;
import com.example.neobrain.API.model.Person;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.PostAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер для работы с Профилем
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

    private SharedPreferences sp;

    private static final int CAMERA_REQUEST = 100;
    private static final int PICK_IMAGE = 101;
    private static final int RESULT_OK = -1;

    private int photoId;
    private int userId = 0;
    private boolean inSubscribe = false;
    private Integer userIdSP;

    public int getPhotoId() {
        return photoId;
    }

    private void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public ProfileController() {

    }

    public ProfileController(int userId) {
        this(new BundleBuilder(new Bundle())
                .putInt("userId", userId)
                .build());
    }

    public ProfileController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.userId = args.getInt("userId");
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

        View imagesButton = view.findViewById(R.id.button_first);
        imagesButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new PhotosController())
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

        CardView avatarCard = view.findViewById(R.id.avatar_card);
        avatarCard.setPreventCornerOverlap(false);
        avatarCard.setOnClickListener(v -> launchPhoto());

        userIdSP = sp.getInt("userId", -1);

        fabAdd.setColorFilter(Color.argb(255, 255, 255, 255));
        fabAdd.setOnClickListener(v -> {
            if (userId == 0) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
                getRouter().pushController(RouterTransaction.with(new PostController()));
            } else {
                if (inSubscribe) {
                    Call<Status> deleteCall = DataManager.getInstance().deletePeople(userIdSP, userId);
                    deleteCall.enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                            if (response.isSuccessful()) {

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

        buttonEdit.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
            if (userId == 0) {
                getRouter().pushController(RouterTransaction.with(new ProfileEditController()));
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

        buttonInfo.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
            getRouter().pushController(RouterTransaction.with(new ProfileInfoController()));
        });

//        moreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) coordinatorLayout.getLayoutParams();
//                // узнаем размеры экрана из класса Display
//                Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
//                DisplayMetrics metricsB = new DisplayMetrics();
//                display.getMetrics(metricsB);
//                params.setMarginEnd(metricsB.widthPixels / 100 * 75);
//                coordinatorLayout.setLayoutParams(params);
//            }
//        });

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
        Call<UserModel> call;
        if (userId == 0) {
            call = DataManager.getInstance().getUser(userIdSP);
        } else {
            call = DataManager.getInstance().getUser(userId);
        }
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
                    if (user.getStatus() == 0) {
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
                            if (userId != 0) {
                                buttonEdit.setText(Objects.requireNonNull(getResources()).getString(R.string.write_message));
                                moreButton.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_more_vert, Objects.requireNonNull(getActivity()).getTheme()));
                                Call<People> peopleCall = DataManager.getInstance().getPeople(userIdSP);
                                peopleCall.enqueue(new Callback<People>() {
                                    @Override
                                    public void onResponse(@NotNull Call<People> call, @NotNull Response<People> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            People people = response.body();
                                            List<Person> personList = people.getPeople();
                                            for (int i = 0; i < personList.size(); i++) {
                                                if (personList.get(i).getUserId() == userId) {
                                                    fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_add_disabled, Objects.requireNonNull(getActivity()).getTheme()));
                                                    inSubscribe = true;
                                                    break;
                                                } else {
                                                    fabAdd.setImageDrawable(Objects.requireNonNull(getResources()).getDrawable(R.drawable.ic_person_add, Objects.requireNonNull(getActivity()).getTheme()));
                                                }
                                            }
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

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
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
        });
        getPosts();
    }

    private void getPosts() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        postRecycler.setLayoutManager(mLayoutManager);
        postRecycler.setItemAnimator(new DefaultItemAnimator());
        Call<PostModel> call;
        if (userId == 0) {
            call = DataManager.getInstance().getPosts(userIdSP);
        } else {
            call = DataManager.getInstance().getPosts(userId);
        }
        call.enqueue(new Callback<PostModel>() {
            @Override
            public void onResponse(@NotNull Call<PostModel> call, @NotNull Response<PostModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Post> posts = response.body().getPosts();
                    ArrayList<Post> mPosts = new ArrayList<>();
                    for (Post post : posts) {
                        mPosts.add(new Post(post.getId(), post.getTitle(), post.getText(), post.getPhotoId(), post.getCreatedDate()));
                    }
                    Collections.sort(mPosts, Post.COMPARE_BY_TIME);
                    postAdapter = new PostAdapter(mPosts, getRouter());
                    postRecycler.setNestedScrollingEnabled(false);
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

                    Integer userIdSP = sp.getInt("userId", -1);
                    User user = new User();
                    user.setPhoto(new String(encoded));
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
                break;
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
                        newSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);

                        Integer userIdSP = sp.getInt("userId", -1);
                        User user = new User();
                        user.setPhoto(new String(encoded));
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
                    }
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.avatar_card)
    void launchPhoto() {
        if (userId == 0) {
            String[] testArray = new String[]{Objects.requireNonNull(getResources()).getString(R.string.load_device), Objects.requireNonNull(getResources()).getString(R.string.do_photo), Objects.requireNonNull(getResources()).getString(R.string.open), Objects.requireNonNull(getResources()).getString(R.string.delete)};
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                    .setTitle(Objects.requireNonNull(getResources()).getString(R.string.photo))
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
            getRouter().pushController(RouterTransaction.with(new PhotoController(photoId))
                    .popChangeHandler(new VerticalChangeHandler(false))
                    .pushChangeHandler(new VerticalChangeHandler()));
        }
    }

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

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = Objects.requireNonNull(getActivity()).managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
