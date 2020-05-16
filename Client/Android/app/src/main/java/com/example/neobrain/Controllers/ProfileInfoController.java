package com.example.neobrain.Controllers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import butterknife.ButterKnife;

public class ProfileInfoController extends Controller {
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
        return view;
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
