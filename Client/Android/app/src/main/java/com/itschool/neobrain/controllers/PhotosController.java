package com.itschool.neobrain.controllers;

// Импортируем нужные библиотеки

import android.Manifest;
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
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.PhotoModel;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.MainActivity;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.PhotosAdapter;
import com.itschool.neobrain.utils.BundleBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Контроллер для работы с фотографиями */
public class PhotosController extends Controller {

    private PhotosAdapter photosAdapter;
    private BottomNavigationView bottomNavigationView;
    private boolean bottomIsGone = false;
    private int userId;

    @BindView(R.id.photoRecycler)
    public RecyclerView photoRecycler;
    @BindView(R.id.fabAdd)
    public FloatingActionButton fabAdd;

    private static final int PICK_IMAGE = 101;
    private static final int RESULT_OK = -1;

    private SharedPreferences sp;

    // Несколько конструкторов для передачи необходимых значений в разных ситуациях
    public PhotosController() {

    }

    public PhotosController(int userId, boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putInt("userId", userId)
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }

    public PhotosController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.userId = args.getInt("userId");
        this.bottomIsGone = args.getBoolean("bottomIsGone");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.photos_controller, container, false);
        ButterKnife.bind(this, view);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        // Если это текущий пользователь, при нажатии на кнопку добавления фото предоставляем
        // ему эту возможность, открываем галерею, иначе, скрываем эту кнопку
        if (userId == sp.getInt("userId", 0)) {
            fabAdd.setColorFilter(Color.argb(255, 255, 255, 255));
            fabAdd.setOnClickListener(v -> {
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
            });
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        // Получаем и выводим фотографии
        getPhotos();
        return view;
    }

    /* Метод для получения и выведения фотографий */
    private void getPhotos() {
        // Настраиваем RecyclerView
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        photoRecycler.setLayoutManager(mLayoutManager);
        photoRecycler.setItemAnimator(new DefaultItemAnimator());

        // Обращаемся к серверу, вытаскиваем нужные данные
        Call<UserModel> userCall = DataManager.getInstance().getUser(userId);
        userCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Photo> mPhotos = new ArrayList<>();
                    for (PhotoModel photoModel : response.body().getPhotos()) {
                        if (!photoModel.getPhoto().getAvatar()) {
                            mPhotos.add(photoModel.getPhoto());
                        }
                    }
                    if (mPhotos.size() == 0) {
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        photoRecycler.setLayoutManager(mLayoutManager);
                    }
                    // Устанавливаем нужный адаптер
                    photosAdapter = new PhotosAdapter(mPhotos, getRouter());
                    photoRecycler.setAdapter(photosAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
            }
        });

    }

    /* Метод успешного выбора пользователем фото из галереи (загружает фото на сервер) */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        newSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        byte[] encoded = Base64.encode(byteArray, Base64.DEFAULT);

                        Integer userIdSP = sp.getInt("userId", -1);
                        User user = new User();
                        user.setPhoto(new String(encoded));
                        user.setAvatar(false);
                        Call<Status> call = DataManager.getInstance().editUser(userIdSP, user);
                        call.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                if (response.isSuccessful()) {
                                    Status post = response.body();
                                    assert post != null;
                                    getPhotos();
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

    /* Вспомогательный метод для работы с картинками */
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

    /* Метод, для переворачивания фото */
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
