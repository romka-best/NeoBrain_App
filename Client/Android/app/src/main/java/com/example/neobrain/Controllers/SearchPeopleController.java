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
import com.example.neobrain.API.model.User;
import com.example.neobrain.Adapters.SearchAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchPeopleController extends Controller {
    @BindView(R.id.peopleRecycler)
    public RecyclerView peopleRecycler;
    private SearchAdapter peopleAdapter;
    private ArrayList<User> mUsers = new ArrayList<>();

    private Router mainRouter;
    private boolean found;

    public SearchPeopleController() {
        found = true;
    }

    public SearchPeopleController(ArrayList<User> mUsers, Router router, boolean found) {
        this.mUsers = mUsers;
        this.mainRouter = router;
        this.found = found;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_people_controller, container, false);
        ButterKnife.bind(this, view);

        getPeople();
        return view;
    }

    private void getPeople() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        peopleRecycler.setLayoutManager(mLayoutManager);
        peopleRecycler.setItemAnimator(new DefaultItemAnimator());
        peopleAdapter = new SearchAdapter(mainRouter, found);
        peopleAdapter.setPersonList(mUsers);
        peopleAdapter.notifyDataSetChanged();
        peopleRecycler.setAdapter(peopleAdapter);
    }
}
