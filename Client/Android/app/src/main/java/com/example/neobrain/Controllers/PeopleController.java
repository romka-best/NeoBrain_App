package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

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
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.PeopleAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.StringJoiner;

import butterknife.ButterKnife;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Контроллер с людьми (Друзьями, подписчиками)
public class PeopleController extends Controller {
    private SearchView searchView;
    private RecyclerView peopleRecycler;
    private PeopleAdapter peopleAdapter;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.people_controller, container, false);
        ButterKnife.bind(this, view);

        searchView = view.findViewById(R.id.searchView);
        peopleRecycler = view.findViewById(R.id.peopleRecycler);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("onQueryTextSubmit", "Тута");
                List<String> strings = Arrays.asList(query.toLowerCase().trim().split(" "));
                StringJoiner joiner = new StringJoiner("&");
                for (int i = 0; i < strings.size(); i++) {
                    joiner.add(strings.get(i));
                }
                Call<UserModel> call = DataManager.getInstance().searchUser(String.valueOf(joiner));
                call.enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                    }

                    @Override
                    public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                        Log.e("FAILURE", t.toString());
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
        // TODO
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        peopleRecycler.setLayoutManager(mLayoutManager);
        peopleRecycler.setItemAnimator(new DefaultItemAnimator());
        peopleAdapter = new PeopleAdapter(new ArrayList<>(), getApplicationContext());
        peopleRecycler.setAdapter(peopleAdapter);
    }
}
