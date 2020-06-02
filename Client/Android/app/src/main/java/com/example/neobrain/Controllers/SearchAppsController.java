package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.example.neobrain.API.model.App;
import com.example.neobrain.Adapters.SearchAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAppsController extends Controller {
    @BindView(R.id.appsRecycler)
    public RecyclerView appsRecycler;
    private SearchAdapter appsAdapter;
    private ArrayList<App> mApps = new ArrayList<>();

    private Router mainRouter;
    private boolean found;

    public SearchAppsController() {
        found = true;
    }

    public SearchAppsController(ArrayList<App> mApps, Router router, boolean found) {
        this.mApps = mApps;
        this.mainRouter = router;
        this.found = found;
    }
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_apps_controller, container, false);
        ButterKnife.bind(this, view);

        getApps();
        return view;
    }

    private void getApps() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        appsRecycler.setLayoutManager(mLayoutManager);
        appsRecycler.setItemAnimator(new DefaultItemAnimator());
        appsAdapter = new SearchAdapter(mainRouter, found);
        appsAdapter.setAppsList(mApps);
        appsAdapter.notifyDataSetChanged();
        appsRecycler.setAdapter(appsAdapter);
    }
}
