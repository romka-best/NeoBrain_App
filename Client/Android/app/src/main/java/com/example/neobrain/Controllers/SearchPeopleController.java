package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.R;

import butterknife.ButterKnife;

public class SearchPeopleController extends Controller {
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_people_controller, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
