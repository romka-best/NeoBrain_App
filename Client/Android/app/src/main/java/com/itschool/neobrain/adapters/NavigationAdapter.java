package com.itschool.neobrain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itschool.neobrain.API.models.Navigation;
import com.itschool.neobrain.R;
import com.itschool.neobrain.utils.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/* Адаптер для навигации */
public class NavigationAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "NavigationAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Navigation> mNavigationList;

    public NavigationAdapter(ArrayList<Navigation> navigationList) {
        this.mNavigationList = navigationList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_navigation, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_navigation, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mNavigationList != null && mNavigationList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mNavigationList != null && mNavigationList.size() > 0) {
            return mNavigationList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Navigation> navigationList) {
        mNavigationList.addAll(navigationList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.titleNavigation)
        TextView titleNavigationTextView;
        @BindView(R.id.emoji)
        ImageView emojiImageView;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

}
