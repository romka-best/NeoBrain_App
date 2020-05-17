package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер для выкладывания/редактирования поста
public class PostController extends Controller {
    @BindView(R.id.acceptButton)
    public ImageButton acceptButton;
    @BindView(R.id.cancelButton)
    public ImageButton cancelButton;
    @BindView(R.id.postText)
    public MultiAutoCompleteTextView postText;

    private SharedPreferences sp;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.post_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        cancelButton.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
            getRouter().popCurrentController();
        });

        return view;
    }

    @OnClick({R.id.acceptButton})
    void setPost() {
        assert getView() != null;
        if (postText.getText().toString().equals("")) {
            Snackbar.make(getView(), R.string.empty_field, Snackbar.LENGTH_LONG).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(acceptButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        Integer userIdSP = sp.getInt("userId", -1);
        Post post = new Post();
        post.setText(postText.getText().toString().trim());
        post.setUserId(userIdSP);
        Call<Status> call = DataManager.getInstance().createPost(post);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.VISIBLE);
                getRouter().popCurrentController();
            }

            @Override
            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

            }
        });

    }

    @Override
    public boolean handleBack() {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        try {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
        } catch (NullPointerException ignored) {
        }
    }
}
