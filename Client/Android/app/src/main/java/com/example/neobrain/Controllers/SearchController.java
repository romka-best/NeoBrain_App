package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.example.neobrain.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchController extends Controller {
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private final RouterPagerAdapter pagerAdapter;

    public SearchController() {
        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            router.setRoot(RouterTransaction.with(new SearchAllController()));
                            break;
                        case 1:
                            router.setRoot(RouterTransaction.with(new SearchPeopleController()));
                            break;
                        case 2:
                            router.setRoot(RouterTransaction.with(new SearchGroupsController()));
                            break;
                        case 3:
                            router.setRoot(RouterTransaction.with(new SearchChatsController()));
                            break;
                        case 4:
                            router.setRoot(RouterTransaction.with(new SearchMusicController()));
                            break;
                        case 5:
                            router.setRoot(RouterTransaction.with(new SearchAppsController()));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return Objects.requireNonNull(getResources()).getString(R.string.all);
                    case 1:
                        return Objects.requireNonNull(getResources()).getString(R.string.people);
                    case 2:
                        return Objects.requireNonNull(getResources()).getString(R.string.groups);
                    case 3:
                        return Objects.requireNonNull(getResources()).getString(R.string.chats);
                    case 4:
                        return Objects.requireNonNull(getResources()).getString(R.string.music);
                    case 5:
                        return Objects.requireNonNull(getResources()).getString(R.string.apps);
                    default:
                        return "Page " + position;
                }
            }
        };
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_controller, container, false);
        ButterKnife.bind(this, view);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        if (!Objects.requireNonNull(getActivity()).isChangingConfigurations()) {
            viewPager.setAdapter(null);
        }
        tabLayout.setupWithViewPager(null);
        super.onDestroyView(view);
    }
}
