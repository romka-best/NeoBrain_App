package com.example.neobrain.Adapters;

// Импортируем нужные библиотеки
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.util.BaseViewHolder;

// Адаптер сообщений (самого чата)
public class MessageAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
