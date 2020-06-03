package com.example.neobrain.Controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Corona;
import com.example.neobrain.API.model.Coronas;
import com.example.neobrain.Adapters.CoronaAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorldCoronaController extends Controller {

    @BindView(R.id.corona_recycler)
    public RecyclerView corona_recycler;
    private CoronaAdapter coronaAdapter;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.world_corona, container, false);
        ButterKnife.bind(this, view);

        getCoronaCountries();

        return view;
    }

    private void getCoronaCountries() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        corona_recycler.setLayoutManager(mLayoutManager);
        corona_recycler.setItemAnimator(new DefaultItemAnimator());
        List<Corona> mCorona = new ArrayList<>();
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
                    }
                } else {
                    Log.e("Error", "fail_request");
                }
            }

            @Override
            public void onFailure(@NotNull Call<Coronas> call, @NotNull Throwable t) {

            }
        });
    }
}
