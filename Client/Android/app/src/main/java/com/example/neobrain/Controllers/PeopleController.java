package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.People;
import com.example.neobrain.API.model.Person;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.API.model.Users;
import com.example.neobrain.Adapters.PeopleAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер с людьми (Друзьями, подписчиками)
public class PeopleController extends Controller {
    private RecyclerView peopleRecycler;
    private PeopleAdapter peopleAdapter;
    private ArrayList<User> mUsers = new ArrayList<>();
    private SharedPreferences sp;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.people_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        peopleRecycler = view.findViewById(R.id.peopleRecycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        peopleRecycler.setLayoutManager(mLayoutManager);
        peopleRecycler.setItemAnimator(new DefaultItemAnimator());

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> strings = Arrays.asList(query.toLowerCase().trim().split(" "));
                StringJoiner joiner = new StringJoiner("&");
                for (int i = 0; i < strings.size(); i++) {
                    joiner.add(strings.get(i));
                }
                ArrayList<User> mUsers = new ArrayList<>();
                Call<Users> call = DataManager.getInstance().searchUser(String.valueOf(joiner));
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(@NotNull Call<Users> call, @NotNull Response<Users> response) {
                        assert response.body() != null;
                        List<User> users = response.body().getUsers();
                        for (User user : users) {
                            mUsers.add(new User(user.getId(), user.getPhotoId(), user.getName(), user.getSurname(), user.getRepublic(), user.getCity(), user.getAge(), user.getGender()));
                        }
                        peopleAdapter = new PeopleAdapter(mUsers, getApplicationContext(), getRouter());
                        peopleRecycler.setAdapter(peopleAdapter);
                    }

                    @Override
                    public void onFailure(@NotNull Call<Users> call, @NotNull Throwable t) {

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                Log.e("onQueryTextChange", searchQuery.toLowerCase().trim());
                return true;
            }
        });
        getPeople();
        return view;
    }

    private void getPeople() {
        Integer userIdSP = sp.getInt("userId", -1);
        Call<People> call = DataManager.getInstance().getPeople(userIdSP);
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
                    if (personList != null) {
                        for (int i = 0; i < personList.size(); i++) {
                            Call<UserModel> userCall = DataManager.getInstance().getUser(personList.get(i).getUserId());
                            userCall.enqueue(new Callback<UserModel>() {
                                @Override
                                public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                                    assert response.body() != null;
                                    User user = response.body().getUser();
                                    for (User queueUser: mUsers) {
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
            }

            @Override
            public void onFailure(@NotNull Call<People> call, @NotNull Throwable t) {

            }
        });
    }

    public void allPeopleSearched(){
        peopleAdapter = new PeopleAdapter(mUsers, getApplicationContext(), getRouter());
        peopleRecycler.setAdapter(peopleAdapter);
    }
}
