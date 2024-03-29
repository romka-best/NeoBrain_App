package com.itschool.neobrain.controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.itschool.neobrain.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Контроллер с навигацией по разделу о короновирусе (настройки дизайна, переключение ViewPager-а) */
@SuppressLint("ValidController")
public class CoronaController extends Controller {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private short currentItem = 0;

    private final RouterPagerAdapter pagerAdapter;

    public CoronaController() {
        currentItem = 0;
        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            router.setRoot(RouterTransaction.with(new RussiaCoronaController()));
                            break;
                        case 1:
                            router.setRoot(RouterTransaction.with(new WorldCoronaController()));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return Objects.requireNonNull(getResources()).getString(R.string.inRussia);
                    case 1:
                        return Objects.requireNonNull(getResources()).getString(R.string.inOtherCountries);
                    default:
                        return "Page " + position;
                }
            }
        };
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.navigation_controller, container, false);
        ButterKnife.bind(this, view);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentItem);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
