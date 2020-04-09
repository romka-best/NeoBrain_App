package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.Adapters.ChatAdapter;
import com.example.neobrain.Adapters.LentaAdapter;
import com.example.neobrain.Adapters.PostAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

public class LentaController extends Controller {
    @BindView(R.id.lentaRecycler)
    public RecyclerView lentaRecycler;
    private LentaAdapter lentaAdapter;

    private SwipeRefreshLayout swipeContainer;

    private SharedPreferences sp;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.lenta_controller, container, false);
        ButterKnife.bind(this, view);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            getPosts();
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        getPosts();
        return view;
    }

    private void getPosts() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        lentaRecycler.setLayoutManager(mLayoutManager);
        lentaRecycler.setItemAnimator(new DefaultItemAnimator());
        lentaAdapter = new LentaAdapter(new ArrayList<>());
        lentaRecycler.setAdapter(lentaAdapter);
//        String nicknameSP = sp.getString("nickname", "");
//        Call<PostModel> call = DataManager.getInstance().getPosts(nicknameSP);
//        call.enqueue(new Callback<PostModel>() {
//            @Override
//            public void onResponse(@NotNull Call<PostModel> call, @NotNull Response<PostModel> response) {
//                if (response.isSuccessful()) {
//                    assert response.body() != null;
//                    List<Post> posts = response.body().getPosts();
//                    ArrayList<Post> mPosts = new ArrayList<>();
//                    for (Post post : posts) {
//                        mPosts.add(new Post(post.getTitle(), post.getText(), post.getPhotoId(), post.getCreatedDate()));
//                    }
//                    lentaAdapter = new LentaAdapter(mPosts);
//                    lentaRecycler.setAdapter(lentaAdapter);
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<PostModel> call, @NotNull Throwable t) {
//
//            }
//        });
    }
}
