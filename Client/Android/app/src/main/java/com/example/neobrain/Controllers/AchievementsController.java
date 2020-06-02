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

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.API.model.Achievements;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.AchievementAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        List<Achievement> achievementList = new ArrayList<>();
        Call<Achievements> call = DataManager.getInstance().getAchievements(userId);
        call.enqueue(new Callback<Achievements>() {
            @Override
            public void onResponse(@NotNull Call<Achievements> call, @NotNull Response<Achievements> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Achievement> achievements = response.body().getAchievements();
                    achievementList.addAll(achievements);
                    Collections.sort(achievementList, Achievement.COMPARE_BY_ID);
                    Call<UserModel> call_user = DataManager.getInstance().getUser(userId);
                    call_user.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (!achievementList.get(0).getGot() && response.body().getUser().getPhotoId() != 2) {
                                    Achievement achivNew = achievementList.get(0);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(1).getGot() && response.body().getUser().getCount_outgoing_messages() >= 50) {
                                    Achievement achivNew = achievementList.get(1);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(2).getGot() && response.body().getUser().getCount_outgoing_messages() >= 500) {
                                    Achievement achivNew = achievementList.get(2);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(3).getGot() && response.body().getUser().getCount_outgoing_messages() >= 2000) {
                                    Achievement achivNew = achievementList.get(3);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(4).getGot() && response.body().getUser().getCount_incoming_messages() >= 50) {
                                    Achievement achivNew = achievementList.get(4);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(5).getGot() && response.body().getUser().getCount_incoming_messages() >= 500) {
                                    Achievement achivNew = achievementList.get(5);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(6).getGot() && response.body().getUser().getCount_incoming_messages() >= 2000) {
                                    Achievement achivNew = achievementList.get(6);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(2).getGot() && response.body().getUser().getCount_incoming_messages() >= 50) {
                                    Achievement achivNew = achievementList.get(2);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(7).getGot() && response.body().getUser().getFollowersCount() >= 100) {
                                    Achievement achivNew = achievementList.get(7);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(8).getGot() && response.body().getUser().getFollowersCount() >= 1000) {
                                    Achievement achivNew = achievementList.get(8);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(9).getGot() && response.body().getUser().getFollowersCount() >= 100000) {
                                    Achievement achivNew = achievementList.get(9);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });

                                }
                                if (!achievementList.get(10).getGot() && response.body().getUser().getSubscriptionsCount() >= 10) {
                                    Achievement achivNew = achievementList.get(10);
                                    getAchievements();
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(11).getGot() && response.body().getUser().getFollowersCount() >= 100) {
                                    Achievement achivNew = achievementList.get(11);
                                    getAchievements();
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(12).getGot() && response.body().getUser().getFollowersCount() >= 1000) {
                                    Achievement achivNew = achievementList.get(12);
                                    achivNew.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achivNew);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                achievementAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                        }
                    });
                    achievementAdapter = new AchievementAdapter(achievementList);
                    achievementRecycler.setAdapter(achievementAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Achievements> call, @NotNull Throwable t) {
                assert getView() != null;
                Snackbar.make(getView(), Objects.requireNonNull(getResources()).getString(R.string.errors_with_connection), Snackbar.LENGTH_LONG).show();
            }
        });

        achievementAdapter = new AchievementAdapter(achievementList);
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
