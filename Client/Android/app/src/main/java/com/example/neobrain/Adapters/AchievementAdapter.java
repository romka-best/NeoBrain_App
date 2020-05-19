package com.example.neobrain.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.API.model.Achievement;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

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
        ImageView achivImage;
        @BindView(R.id.title)
        TextView achivTitle;
        @BindView(R.id.is_got)
        ImageView isGotView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
            isGotView.setImageDrawable(null);
            achivImage.setImageDrawable(null);
            achivTitle.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final Achievement mAchievement = mAchievementList.get(position);
            if (mAchievement.getTitle() != null) {
                achivTitle.setText(mAchievement.getTitle());
            }
            if (mAchievement.getGot() != null) {
                if (mAchievement.getGot()) {
                    isGotView.setImageResource(R.drawable.done);
                } else {
                    isGotView.setImageResource(R.drawable.black_circle);
                }
            }
            if (mAchievement.getPhoto_id() != null && !hasImage(achivImage)) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mAchievement.getPhoto_id());
                call.enqueue(new retrofit2.Callback<Photo>() {
                    @Override
                    public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            String photo = response.body().getPhoto();
                            byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            achivImage.setImageBitmap(decodedByte);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        Log.e("E", "Чёрт...");
                    }
                });
            }
            itemView.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(itemView.getContext())
                        .setTitle(R.string.statement)
                        .setMessage(mAchievement.getDescription())
                        .show();
            });
        }

        private boolean hasImage(@NonNull ImageView view) {
            Drawable drawable = view.getDrawable();
            boolean hasImage = (drawable != null);
            if (hasImage && (drawable instanceof BitmapDrawable)) {
                hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
            }
            return hasImage;
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
