package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.neobrain.API.model.Achievements;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.API.model.Users;
import com.example.neobrain.Adapters.AchievementAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collection;
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
            public void onResponse(Call<Achievements> call, Response<Achievements> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Achievement> achievements = response.body().getAchievements();
                    achievementList.addAll(achievements);
                    Collections.sort(achievementList, Achievement.COMPARE_BY_ID);
                    Call<UserModel> call_user = DataManager.getInstance().getUser(userId);
                    call_user.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (!achievementList.get(0).getGot() && response.body().getUser().getPhotoId() != 2) {
                                    Achievement achiv_new = achievementList.get(0);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(1).getGot() && response.body().getUser().getCount_outgoing_messages() >= 50) {
                                    Achievement achiv_new = achievementList.get(1);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(2).getGot() && response.body().getUser().getCount_outgoing_messages() >= 500) {
                                    Achievement achiv_new = achievementList.get(2);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(3).getGot() && response.body().getUser().getCount_outgoing_messages() >= 2000) {
                                    Achievement achiv_new = achievementList.get(3);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(4).getGot() && response.body().getUser().getCount_incoming_messages() >= 50) {
                                    Achievement achiv_new = achievementList.get(4);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(5).getGot() && response.body().getUser().getCount_incoming_messages() >= 500) {
                                    Achievement achiv_new = achievementList.get(5);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(6).getGot() && response.body().getUser().getCount_incoming_messages() >= 2000) {
                                    Achievement achiv_new = achievementList.get(6);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(2).getGot() && response.body().getUser().getCount_incoming_messages() >= 50) {
                                    Achievement achiv_new = achievementList.get(2);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(7).getGot() && response.body().getUser().getFollowersCount() >= 100) {
                                    Achievement achiv_new = achievementList.get(7);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(8).getGot() && response.body().getUser().getFollowersCount() >= 1000) {
                                    Achievement achiv_new = achievementList.get(8);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(9).getGot() && response.body().getUser().getFollowersCount() >= 100000) {
                                    Achievement achiv_new = achievementList.get(9);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });

                                }
                                if (!achievementList.get(10).getGot() && response.body().getUser().getSubscriptionsCount() >= 10) {
                                    Achievement achiv_new = achievementList.get(10);
                                    getAchievements();
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(11).getGot() && response.body().getUser().getFollowersCount() >= 100) {
                                    Achievement achiv_new = achievementList.get(11);
                                    getAchievements();
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                                if (!achievementList.get(12).getGot() && response.body().getUser().getFollowersCount() >= 1000) {
                                    Achievement achiv_new = achievementList.get(12);
                                    achiv_new.setGot(true);
                                    Call<Status> putAchivCall = DataManager.getInstance().editAchievements(userId, achiv_new);
                                    putAchivCall.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                getAchievements();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                        }
                    });
                    achievementAdapter = new AchievementAdapter(achievementList);
                    achievementRecycler.setAdapter(achievementAdapter);
                }
            }

            @Override
            public void onFailure(Call<Achievements> call, Throwable t) {
            }
        });
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
