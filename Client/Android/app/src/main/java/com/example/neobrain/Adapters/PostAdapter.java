package com.example.neobrain.Adapters;

// Импортируем нужные библиотеки

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Post;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Адаптер постов
public class PostAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "PostAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Post> mPostList;
    private Router mRouter;

    public PostAdapter(List<Post> postList, Router router) {
        mPostList = postList;
        mRouter = router;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_post, parent, false)
                );
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item_post, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mPostList != null && mPostList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mPostList != null && mPostList.size() > 0) {
            return mPostList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Post> postList) {
        mPostList.addAll(postList);
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

        @BindView(R.id.moreButton)
        ImageButton moreButton;

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
            final Post mPost = mPostList.get(position);

            if (mPost.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mPost.getPhotoId());
                call.enqueue(new retrofit2.Callback<Photo>() {
                    @Override
                    public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            String photo = response.body().getPhoto();
                            byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                            Bitmap original = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Bitmap decoded = Bitmap.createScaledBitmap(original, original.getWidth() / 2, original.getHeight() / 2, false);
                            avatarImageView.setImageBitmap(decoded);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        Log.e(TAG, t.toString() + "");
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

            moreButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mRouter.getActivity(), moreButton);
                popupMenu.inflate(R.menu.post_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        new MaterialAlertDialogBuilder(Objects.requireNonNull(mRouter.getActivity()), R.style.AlertDialogCustom)
                                .setMessage(R.string.delete_post)
                                .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                                    Call<Status> call = DataManager.getInstance().deletePost(mPost.getId());
                                    call.enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                            if (response.isSuccessful()) {
                                                mPostList.remove(mPost);
                                                notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                                        }
                                    });
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
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
