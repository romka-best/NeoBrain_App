package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

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
import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.Adapters.AchievementAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Контроллер достижений
public class AchievementsController extends Controller {
    @BindView(R.id.achievementRecycler)
    public RecyclerView achievementRecycler;
    private AchievementAdapter achievementAdapter;

    private SharedPreferences sp;

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
        List<Achievement> achievementList = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            if (i % 2 == 0) {
                achievementList.add(new Achievement(i, "desc", true, 0, "Название"));
            } else {
                achievementList.add(new Achievement(i, "desc", false, 0, "Название"));
            }
        }
        achievementAdapter = new AchievementAdapter(achievementList);
        achievementRecycler.setAdapter(achievementAdapter);
    }
}
