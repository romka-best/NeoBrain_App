package com.itschool.neobrain.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.SearchAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Контроллер поиска по людям */
public class SearchPeopleController extends Controller {
    @BindView(R.id.peopleRecycler)
    public RecyclerView peopleRecycler;
    private SearchAdapter peopleAdapter;
    private ArrayList<User> mUsers = new ArrayList<>();

    private Router mainRouter;
    private boolean found;

    // Несколько конструкторов для первого создания и последующего обновления контроллера
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
        // Получаем людей для отображения
        getPeople();
        return view;
    }

    /* Метод, получающий нужных людей для поисковой системы */
    private void getPeople() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        peopleRecycler.setLayoutManager(mLayoutManager);
        peopleRecycler.setItemAnimator(new DefaultItemAnimator());
        peopleAdapter = new SearchAdapter(mainRouter, found);
        peopleAdapter.setPersonList(mUsers);
        // Уведомляем адаптер об изменениях
        peopleAdapter.notifyDataSetChanged();
        // Присваиваем нужный адаптер RecyclerView
        peopleRecycler.setAdapter(peopleAdapter);
    }
}
