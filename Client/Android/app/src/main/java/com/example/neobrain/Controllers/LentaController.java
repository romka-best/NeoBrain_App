package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
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
import com.example.neobrain.Adapters.LentaAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер ленты
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

        Integer userIdSP = sp.getInt("userId", -1);
        Call<PostModel> call = DataManager.getInstance().getLenta(userIdSP);
        call.enqueue(new Callback<PostModel>() {
            @Override
            public void onResponse(@NotNull Call<PostModel> call, @NotNull Response<PostModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Post> posts = response.body().getPosts();
                    ArrayList<Post> mPosts = new ArrayList<>();
                    for (Post post : posts) {
                        mPosts.add(new Post(post.getId(), post.getTitle(), post.getText(), post.getPhotoId(), post.getCreatedDate(), post.getUserId()));
                    }
                    Collections.sort(mPosts, Post.COMPARE_BY_TIME);
                    lentaAdapter = new LentaAdapter(mPosts);
                    lentaRecycler.setAdapter(lentaAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostModel> call, @NotNull Throwable t) {

            }
        });
        lentaAdapter = new LentaAdapter(new ArrayList<>());
        lentaRecycler.setAdapter(lentaAdapter);

        Date nowDate = new Date();
        Calendar calendar_now = Calendar.getInstance();
        calendar_now.setTime(nowDate);
        calendar_now.add(Calendar.HOUR_OF_DAY, 3);
        nowDate = calendar_now.getTime();

        if (sp.getString("lastEntrance", "").equals("")) {
            new MaterialAlertDialogBuilder(lentaRecycler.getContext())
                    .setTitle(R.string.advice)
                    .setMessage("Sovet, а че?!")
                    .show();
            sp.edit().putString("lastEntrance", nowDate.toString()).apply();
        } else {
            Calendar calendar_then = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat FormatHours = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                calendar_then.setTime(Objects.requireNonNull(FormatHours.parse(sp.getString("lastEntrance", ""))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ;
            calendar_then.add(Calendar.HOUR_OF_DAY, 3);
            if (calendar_now.get(Calendar.DATE) != calendar_then.get(Calendar.DATE)) {
                sp.edit().putString("lastEntrance", nowDate.toString()).apply();
                String[] myResArray = Objects.requireNonNull(getResources()).getStringArray(R.array.advices_list);
                String advice = myResArray[rnd(myResArray.length - 1)];
                new MaterialAlertDialogBuilder(lentaRecycler.getContext())
                        .setTitle(R.string.advice)
                        .setMessage(advice)
                        .show();
            }
        }
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

    public static int rnd(int max) {
        return (int) (Math.random() * ++max);
    }
}
