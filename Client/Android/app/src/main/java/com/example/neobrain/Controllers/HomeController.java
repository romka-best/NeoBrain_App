package com.example.neobrain.Controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class HomeController extends Controller {

    @BindViews({R.id.container})
    ViewGroup[] childContainers;
    private ProgressBar progressBar;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.start_controller, container, false);
        ButterKnife.bind(this, view);

        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        Router childRouterStart = getChildRouter(childContainers[0]).setPopsLastView(false);
        childRouterStart.setRoot(RouterTransaction.with(new ProfileController()));

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_menu);
//        bottomNavigationView.set
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    Router childRouter = getChildRouter(childContainers[0]).setPopsLastView(false);
                    switch (item.getItemId()) {
                        case R.id.action_lenta:
                            if (!childRouter.hasRootController()) {
                                childRouter.setRoot(RouterTransaction.with(new LentaController()));
                            } else {
                                getChildRouter(childContainers[0]).popCurrentController();
                                childRouter.setRoot(RouterTransaction.with(new LentaController()));
                            }
                            return true;
                        case R.id.action_navigation:
                            if (!childRouter.hasRootController()) {
                                childRouter.setRoot(RouterTransaction.with(new NavigationController()));
                            } else {
                                getChildRouter(childContainers[0]).popCurrentController();
                                childRouter.setRoot(RouterTransaction.with(new NavigationController()));
                            }
                            return true;
                        case R.id.action_messages:
                            if (!childRouter.hasRootController()) {
                                childRouter.setRoot(RouterTransaction.with(new ChatController()));
                            } else {
                                getChildRouter(childContainers[0]).popCurrentController();
                                childRouter.setRoot(RouterTransaction.with(new ChatController()));
                            }
                            return true;
                        case R.id.action_apps:
                            if (!childRouter.hasRootController()) {
                                childRouter.setRoot(RouterTransaction.with(new AppsController()));
                            } else {
                                getChildRouter(childContainers[0]).popCurrentController();
                                childRouter.setRoot(RouterTransaction.with(new AppsController()));
                            }
                            return true;
                        case R.id.action_menu:
                            if (!childRouter.hasRootController()) {
                                childRouter.setRoot(RouterTransaction.with(new ProfileController()));
                            } else {
                                getChildRouter(childContainers[0]).popCurrentController();
                                childRouter.setRoot(RouterTransaction.with(new ProfileController()));
                            }
                            return true;
                    }
                    return false;
                });
        return view;
    }
}
