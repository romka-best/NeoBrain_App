package com.itschool.neobrain.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Базовый класс для работы с RecyclerView, в частности, для работы адаптеров
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    public void onBind(int position) {
        this.mCurrentPosition = position;
        clear();
    }

    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }
}
