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

import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.util.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class LentaAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "LentaAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Post> mLentaList;

    public LentaAdapter(List<Post> lentaList) {
        mLentaList = lentaList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new LentaAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_lenta, parent, false)
                );
            case VIEW_TYPE_EMPTY:
            default:
                return new PostAdapter.EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item_lenta, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mLentaList != null && mLentaList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mLentaList != null && mLentaList.size() > 0) {
            return mLentaList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Post> postList) {
        mLentaList.addAll(postList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.title)
        TextView titleTextView;

        @BindView(R.id.avatar)
        ImageView avatarImageView;

        @BindView(R.id.text)
        TextView textTextView;

        @BindView(R.id.time)
        TextView timeTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
            avatarImageView.setImageBitmap(null);
            titleTextView.setText("");
            textTextView.setText("");
            timeTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final Post mPost = mLentaList.get(position);

            if (mPost.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mPost.getPhotoId());
                call.enqueue(new retrofit2.Callback<Photo>() {
                    @Override
                    public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            String photo = response.body().getPhoto();
                            byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            avatarImageView.setImageBitmap(decodedByte);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        Log.e("E", "Чёрт...");
                    }
                });
            }
            if (mPost.getTitle() != null) {
                titleTextView.setText(mPost.getTitle());
            }
            if (mPost.getText() != null) {
                textTextView.setText(mPost.getText());
            }
            if (mPost.getCreatedDate() != null) {
                timeTextView.setText(mPost.getCreatedDate());
            }
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.titlePost)
        TextView titlePostTextView;
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
