package com.example.neobrain.Controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.ProfileInfoAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileInfoController extends Controller {
    @BindView(R.id.infoRecycler)
    public RecyclerView profileInfoRecycler;
    private ProfileInfoAdapter profileInfoAdapter;

    private boolean bottomIsGone = false;
    private int userId;

    public ProfileInfoController() {

    }

    public ProfileInfoController(int userId, boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putInt("userId", userId)
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }

    public ProfileInfoController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
        this.userId = args.getInt("userId");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.profile_info_controller, container, false);
        ButterKnife.bind(this, view);

        getProfileInfo();
        return view;
    }

    private void getProfileInfo() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        profileInfoRecycler.setLayoutManager(mLayoutManager);
        profileInfoRecycler.setItemAnimator(new DefaultItemAnimator());
        Call<UserModel> userModelCall = DataManager.getInstance().getUser(userId);
        userModelCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    User user = response.body().getUser();
                    profileInfoAdapter = new ProfileInfoAdapter(user, getRouter(), false);
                    profileInfoRecycler.setAdapter(profileInfoAdapter);
                    profileInfoRecycler.getRecycledViewPool().clear();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {

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
            if (bottomIsGone) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
            }
        } catch (NullPointerException ignored) {
        }
    }
}
