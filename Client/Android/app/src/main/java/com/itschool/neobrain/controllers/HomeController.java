package com.itschool.neobrain.controllers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itschool.neobrain.R;

import butterknife.ButterKnife;

/* Стартовый контроллер с BottomNavigation и ControllersContainer */
@SuppressLint("ValidController")
public class HomeController extends Controller {
    private SparseArray<Bundle> routerStates;
    private Router childRouter;

    @IdRes
    private int currentSelectedItemId = -1;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.home_controller, container, false);
        ButterKnife.bind(this, view);
        ViewGroup childContainer = view.findViewById(R.id.container);

        // Устанавливаем начальный экран ленты, если нет информации о состояниях других контроллеров
        if (routerStates == null) {
            routerStates = new SparseArray<>();
        }
        childRouter = getChildRouter(childContainer);
        if (routerStates.size() == 0) {
            currentSelectedItemId = R.id.action_lenta;
            childRouter.setRoot(RouterTransaction.with(new LentaController()));
        } else {
            childRouter.rebindIfNeeded();
        }

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        return view;
    }

    /* Вызывается при выборе предмета на навигации, открывает нужный контроллер */
    private boolean onNavigationItemSelected(MenuItem menuItem) {
        if (currentSelectedItemId == menuItem.getItemId()) {
            return true;
        }
        saveStateFromCurrentTab(currentSelectedItemId);
        currentSelectedItemId = menuItem.getItemId();
        clearStateFromChildRouter();
        Bundle bundleState = tryToRestoreStateFromNewTab(currentSelectedItemId);
        if (bundleState != null) {
            childRouter.restoreInstanceState(bundleState);
            childRouter.rebindIfNeeded();
            return true;
        }
        switch (menuItem.getItemId()) {
            case R.id.action_lenta:
                childRouter.setRoot(RouterTransaction.with(new LentaController()));
                return true;
            case R.id.action_navigation:
                childRouter.setRoot(RouterTransaction.with(new CoronaController()));
                return true;
            case R.id.action_messages:
                childRouter.setRoot(RouterTransaction.with(new ChatController()));
                return true;
            case R.id.action_apps:
                childRouter.setRoot(RouterTransaction.with(new AppsController()));
                return true;
            case R.id.action_menu:
                childRouter.setRoot(RouterTransaction.with(new ProfileController()));
                return true;
        }
        return false;
    }

    /* Метод, восстанавливающий состояния других контроллеров */
    private Bundle tryToRestoreStateFromNewTab(int itemId) {
        return routerStates.get(itemId);
    }

    /* Метод, удаляющий состояния всех дочерних контроллеров */
    private void clearStateFromChildRouter() {
        childRouter.setPopsLastView(true);
        childRouter.popToRoot();
        childRouter.popCurrentController();
        childRouter.setPopsLastView(false);
    }

    /* Метод, сохраняющий состояние дочернего контроллера */
    private void saveStateFromCurrentTab(int itemId) {
        Bundle routerBundle = new Bundle();
        childRouter.saveInstanceState(routerBundle);
        routerStates.put(itemId, routerBundle);
    }

    /* Сохранение состояния View */
    @Override
    protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
        super.onSaveViewState(view, outState);
        saveStateFromCurrentTab(currentSelectedItemId);
        outState.putSparseParcelableArray("STATE", routerStates);
    }

    /* Восстановление состояния View */
    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
        routerStates = savedViewState.getSparseParcelableArray("STATE");
    }
}
