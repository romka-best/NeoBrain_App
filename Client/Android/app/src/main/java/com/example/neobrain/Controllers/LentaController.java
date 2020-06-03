package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.PostList;
import com.example.neobrain.Adapters.PostAdapter;
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
    private PostAdapter lentaAdapter;

    @BindView(R.id.swipeContainer)
    public SwipeRefreshLayout swipeContainer;
    @BindView(R.id.progress_circular)
    public ProgressBar progressBar;
    @BindView(R.id.emoji)
    public ImageView emoji;
    @BindView(R.id.titleError)
    public TextView textError;

    private SharedPreferences sp;
    private boolean isLoaded = false;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.lenta_controller, container, false);
        ButterKnife.bind(this, view);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            getPosts();
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        getPosts();
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    private void getPosts() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        lentaRecycler.setLayoutManager(mLayoutManager);
        lentaRecycler.setItemAnimator(new DefaultItemAnimator());

        Integer userIdSP = sp.getInt("userId", -1);
        Call<PostList> call = DataManager.getInstance().getLenta(userIdSP);
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
                    progressBar.setVisibility(View.INVISIBLE);
                    Collections.sort(mPosts, Post.COMPARE_BY_TIME);
                    lentaAdapter = new PostAdapter(mPosts, getRouter(), true);
                    lentaRecycler.setAdapter(lentaAdapter);
                    isLoaded = true;
                }
            }

            @Override
            public void onFailure(@NotNull Call<PostList> call, @NotNull Throwable t) {
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

        Date nowDate = new Date();
        Calendar calendar_now = Calendar.getInstance();
        calendar_now.setTime(nowDate);
        calendar_now.add(Calendar.HOUR_OF_DAY, 3);
        nowDate = calendar_now.getTime();

        if (sp.getString("lastEntrance", "").equals("")) {
            String[] myResArray = Objects.requireNonNull(getResources()).getStringArray(R.array.advices_list);
            String advice = myResArray[rnd(myResArray.length - 1)];
            new MaterialAlertDialogBuilder(lentaRecycler.getContext())
                    .setTitle(R.string.advice)
                    .setMessage(advice)
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
    }

    public static int rnd(int max) {
        return (int) (Math.random() * ++max);
    }
}
