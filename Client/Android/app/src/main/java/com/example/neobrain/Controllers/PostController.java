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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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

    @BindView(R.id.chip_group)
    public ChipGroup chipGroup;
    @BindView(R.id.chip1)
    public Chip chip1;
    @BindView(R.id.chip2)
    public Chip chip2;
    @BindView(R.id.chip3)
    public Chip chip3;
    @BindView(R.id.chip4)
    public Chip chip4;
    @BindView(R.id.chip5)
    public Chip chip5;
    @BindView(R.id.chip6)
    public Chip chip6;
    @BindView(R.id.chip7)
    public Chip chip7;
    @BindView(R.id.chip8)
    public Chip chip8;

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

    @OnClick({R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4,
            R.id.chip5, R.id.chip6, R.id.chip7, R.id.chip8})
    void checkChip() {
        assert getView() != null;
        if (chipGroup.getCheckedChipIds().size() > 3) {
            chipGroup.clearCheck();
            Snackbar.make(getView(), Objects.requireNonNull(getResources()).getString(R.string.emoji_rule), Snackbar.LENGTH_LONG).show();
        }
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
        if (chip1.isChecked()) {
            post.setLikeEmojiCount(0);
        }
        if (chip2.isChecked()) {
            post.setLaughterEmojiCount(0);
        }
        if (chip3.isChecked()) {
            post.setHeartEmojiCount(0);
        }
        if (chip4.isChecked()) {
            post.setDisappointedEmojiCount(0);
        }
        if (chip5.isChecked()) {
            post.setSmileEmojiCount(0);
        }
        if (chip6.isChecked()) {
            post.setAngryEmojiCount(0);
        }
        if (chip7.isChecked()) {
            post.setSmileWithHeartEyesCount(0);
        }
        if (chip8.isChecked()) {
            post.setScreamingEmojiCount(0);
        }
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
