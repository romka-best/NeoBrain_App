package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.Adapters.AchievementAdapter;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

// Контроллер достижений
public class AchievementsController extends Controller {
    @BindView(R.id.achievementRecycler)
    public RecyclerView achievementRecycler;
    private AchievementAdapter achievementAdapter;

    private SharedPreferences sp;
    private boolean bottomIsGone = false;
    private int userId;

    public AchievementsController() {

    }

    public AchievementsController(int userId, boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putBoolean("bottomIsGone", bottomIsGone)
                .putInt("userId", userId)
                .build());
    }

    public AchievementsController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
        this.userId = args.getInt("userId");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.achievements_controller, container, false);
        ButterKnife.bind(this, view);

        getAchievements();
        return view;
    }

    private void getAchievements() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        achievementRecycler.setLayoutManager(mLayoutManager);
        achievementRecycler.setItemAnimator(new DefaultItemAnimator());
        achievementAdapter = new AchievementAdapter(new ArrayList<>());
        achievementRecycler.setAdapter(achievementAdapter);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        try {
            if (bottomIsGone) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }
}
