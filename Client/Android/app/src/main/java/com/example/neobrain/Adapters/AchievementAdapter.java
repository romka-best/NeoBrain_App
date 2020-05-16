package com.example.neobrain.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AchievementAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "AchievementAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Achievement> mAchievementList;

    public AchievementAdapter(List<Achievement> achievementList) {
        mAchievementList = achievementList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_achievement, parent, false)
                );
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item_achievements, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mAchievementList != null && mAchievementList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mAchievementList != null && mAchievementList.size() > 0) {
            return mAchievementList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Achievement> achievementList) {
        mAchievementList.addAll(achievementList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.avatar)
        ImageView achiv_image;
        @BindView(R.id.title)
        TextView achiv_title;
        @BindView(R.id.is_got)
        ImageView is_got_view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
            is_got_view.setImageDrawable(null);
            achiv_image.setImageDrawable(null);
            achiv_title.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final Achievement mAchievement = mAchievementList.get(position);
            if (mAchievement.getTitle() != null) {
                achiv_title.setText(mAchievement.getTitle());
            }
            if (mAchievement.getGot() != null) {
                if (mAchievement.getGot()) {
                    is_got_view.setImageResource(R.drawable.done);
                } else {
                    is_got_view.setImageResource(R.drawable.black_circle);
                }
            }
            achiv_image.setImageResource(R.drawable.face_with_monocle);
            itemView.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(itemView.getContext())
                        .setTitle(R.string.statement)
                        .setMessage(mAchievement.getDescription())
                        .show();
            });
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.titleApp)
        TextView titleAppTextView;
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
