package com.itschool.neobrain.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.itschool.neobrain.API.models.Corona;
import com.itschool.neobrain.API.models.Coronas;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.CoronaAdapter;
import com.itschool.neobrain.utils.SpacesItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Контроллер работы с разделом о COVID-19 во всём мире */
public class WorldCoronaController extends Controller {

    @BindView(R.id.corona_recycler)
    public RecyclerView corona_recycler;
    private CoronaAdapter coronaAdapter;
    private Boolean space = false;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.world_corona, container, false);
        ButterKnife.bind(this, view);
        // Получаем информацию и выводим данные
        getCoronaCountries();

        return view;
    }

    /* Метод, получающий информацию по коронавирусу на данный момент */
    private void getCoronaCountries() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        corona_recycler.setLayoutManager(mLayoutManager);
        corona_recycler.setItemAnimator(new DefaultItemAnimator());
        List<Corona> mCorona = new ArrayList<>();
        // Запрос на сервер, вытаскиваем информацию, заполняем адаптер
        Call<Coronas> all_corona = DataManager.getInstance().getAllCoronaCountry();
        all_corona.enqueue(new Callback<Coronas>() {
            @Override
            public void onResponse(@NotNull Call<Coronas> call, @NotNull Response<Coronas> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    int len_rec = response.body().getCountries().size();
                    for (int i = 1; i <= len_rec; i++) {
                        mCorona.add(new Corona(i));
                    }
                    if (mCorona.size() < 1) {
                        Log.e("Error", "fail_request");
                    } else {
                        coronaAdapter = new CoronaAdapter(mCorona);
                        corona_recycler.setAdapter(coronaAdapter);
                        if (!space) {
                            corona_recycler.addItemDecoration(new SpacesItemDecoration(20));
                            space = true;
                        }
                        if (mCorona.size() > 0) {
                            corona_recycler.scrollToPosition(0);
                        }
                    }
                } else {
                    Log.e("Error", "fail_request");
                }
            }

            @Override
            public void onFailure(@NotNull Call<Coronas> call, @NotNull Throwable t) {

            }
        });
        // Присваиваем адаптер для RecyclerView
        coronaAdapter = new CoronaAdapter(new ArrayList<>());
        corona_recycler.setAdapter(coronaAdapter);
    }
}
