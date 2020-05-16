package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.example.neobrain.API.model.People;
import com.example.neobrain.API.model.Person;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.PeopleAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BundleBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер с людьми (Друзьями, подписчиками)
public class PeopleController extends Controller {
    @BindView(R.id.peopleRecycler)
    public RecyclerView peopleRecycler;
    private PeopleAdapter peopleAdapter;
    private ArrayList<User> mUsers = new ArrayList<>();
    private SharedPreferences sp;
    private boolean bottomIsGone = false;
    private int userId;

    public PeopleController() {

    }

    public PeopleController(int userId, boolean bottomIsGone) {
        this(new BundleBuilder(new Bundle())
                .putInt("userId", userId)
                .putBoolean("bottomIsGone", bottomIsGone)
                .build());
    }

    public PeopleController(@Nullable Bundle args) {
        super(args);
        assert args != null;
        this.bottomIsGone = args.getBoolean("bottomIsGone");
        this.userId = args.getInt("userId");
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.people_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        peopleRecycler.setLayoutManager(mLayoutManager);
        peopleRecycler.setItemAnimator(new DefaultItemAnimator());

        ImageButton searchButton = view.findViewById(R.id.search_people_button);
        searchButton.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
            getRouter().pushController(RouterTransaction.with(new SearchController((short) 1))
                    .popChangeHandler(new HorizontalChangeHandler())
                    .pushChangeHandler(new HorizontalChangeHandler()));
        });

        getPeople();
        return view;
    }

    private void getPeople() {
        Call<People> call = DataManager.getInstance().getPeople(userId);
        call.enqueue(new Callback<People>() {
            @Override
            public void onResponse(@NotNull Call<People> call, @NotNull Response<People> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    People people = response.body();
                    List<Person> personList = people.getPeople();
                    if (personList.size() == 0) {
                        allPeopleSearched();
                        return;
                    }
                    mUsers = new ArrayList<>();
                    for (int i = 0; i < personList.size(); i++) {
                        Call<UserModel> userCall = DataManager.getInstance().getUser(personList.get(i).getUserId());
                        userCall.enqueue(new Callback<UserModel>() {
                            @Override
                            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                                assert response.body() != null;
                                User user = response.body().getUser();
                                for (User queueUser : mUsers) {
                                    if (queueUser.getId().equals(user.getId())) {
                                        return;
                                    }
                                }
                                mUsers.add(new User(user.getId(), user.getPhotoId(), user.getName(), user.getSurname(), user.getRepublic(), user.getCity(), user.getAge(), user.getGender()));
                                if (mUsers.size() == personList.size()) {
                                    allPeopleSearched();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<People> call, @NotNull Throwable t) {

            }
        });
    }

    private void allPeopleSearched() {
        if (mUsers.size() >= 2) {
            Collections.sort(mUsers, User.COMPARE_BY_SURNAME);
        }
        peopleAdapter = new PeopleAdapter(mUsers, getApplicationContext(), getRouter());
        peopleRecycler.setAdapter(peopleAdapter);
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
