package com.example.neobrain.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.API.model.App;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.util.BaseViewHolder;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class AppsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "AppsAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<App> mAppsList;

    public AppsAdapter(ArrayList<App> mAppsList) {
        this.mAppsList = mAppsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_app, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_app, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mAppsList != null && mAppsList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mAppsList != null && mAppsList.size() > 0) {
            return mAppsList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<App> AppsList) {
        mAppsList.addAll(AppsList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.image)
        ImageView coverImageView;
        @BindView(R.id.title)
        TextView titleTextView;
        @BindView(R.id.secondaryText)
        TextView secondaryTextView;
        @BindView(R.id.description)
        TextView descriptionTextView;
        @BindView(R.id.button1)
        MaterialButton button1;
        @BindView(R.id.button2)
        MaterialButton button2;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            coverImageView.setImageDrawable(null);
            titleTextView.setText("");
            secondaryTextView.setText("");
            descriptionTextView.setText("");
            button1.setText("");
            button2.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final App mApp = mAppsList.get(position);

            if (mApp.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mApp.getPhotoId());
                call.enqueue(new retrofit2.Callback<Photo>() {
                    @Override
                    public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            String photo = response.body().getPhoto();
                            byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            coverImageView.setImageBitmap(decodedByte);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        Log.e("E", "Чёрт...");
                    }
                });
            }

            if (mApp.getTitle() != null) {
                titleTextView.setText(mApp.getTitle());
            }
            if (mApp.getSecondaryText() != null) {
                secondaryTextView.setText(mApp.getSecondaryText());
            }
            if (mApp.getDescription() != null) {
                descriptionTextView.setText(mApp.getDescription());
            }
            if (mApp.getButtonText1() != null) {
                button1.setText(mApp.getButtonText1());
            }
            if (mApp.getButtonText2() != null) {
                button2.setText(mApp.getButtonText2());
            }
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.emoji)
        ImageView emoji;
        @BindView(R.id.titleApp)
        TextView titleApp;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
