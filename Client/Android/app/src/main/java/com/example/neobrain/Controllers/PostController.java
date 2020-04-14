package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер для выкладывания/редактирования поста
public class PostController extends Controller {
    private MultiAutoCompleteTextView postText;

    private SharedPreferences sp;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.post_controller, container, false);
        ButterKnife.bind(this, view);

        ImageButton cancelButton = view.findViewById(R.id.cancelButton);
        ImageButton acceptButton = view.findViewById(R.id.acceptButton);
        TextView title = view.findViewById(R.id.title);
        postText = view.findViewById(R.id.postText);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        cancelButton.setOnClickListener(v -> getRouter().popCurrentController());
        acceptButton.setOnClickListener(v -> setPost());


        return view;
    }

    private void setPost() {
        Integer userIdSP = sp.getInt("userId", -1);
        Post post = new Post();
        post.setText(postText.getText().toString());
        post.setUserId(userIdSP);
        Call<Status> call = DataManager.getInstance().createPost(post);
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                getRouter().popCurrentController();
            }

            @Override
            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

            }
        });

    }
}
