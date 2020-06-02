package com.example.neobrain.Adapters;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.example.neobrain.API.model.PostModel;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;
import com.example.neobrain.utils.TimeFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Адаптер постов
public class PostAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "PostAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Post> mPostList;
    private Router mRouter;
    private SharedPreferences sp;
    private Integer authorId;

    public PostAdapter(List<Post> postList, Router router) {
        mPostList = postList;
        mRouter = router;
        sp = Objects.requireNonNull(router.getActivity()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
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

        @BindView(R.id.chip1)
        Chip chip1;
        @BindView(R.id.chip2)
        Chip chip2;
        @BindView(R.id.chip3)
        Chip chip3;
        @BindView(R.id.chip4)
        Chip chip4;
        @BindView(R.id.chip5)
        Chip chip5;
        @BindView(R.id.chip6)
        Chip chip6;
        @BindView(R.id.chip7)
        Chip chip7;
        @BindView(R.id.chip8)
        Chip chip8;

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

        @SuppressLint("SetTextI18n")
        public void onBind(int position) {
            super.onBind(position);
            final Post mPost = mPostList.get(position);

            if (mPost.getPhotoId() != null && !hasImage(avatarImageView)) {
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
                timeTextView.setText(new TimeFormatter(mPost.getCreatedDate()).timeForChat(itemView.getContext()));
            }
            if (mPost.getLikeEmojiCount() != null && mPost.getLikeEmojiCount() != -1) {
                chip1.setVisibility(View.VISIBLE);
                if (!mPost.getLikeEmoji()) {
                    Objects.requireNonNull(chip1.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getLikeEmojiCount() != 0) {
                    chip1.setText(mPost.getLikeEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getLaughterEmojiCount() != null && mPost.getLaughterEmojiCount() != -1) {
                chip2.setVisibility(View.VISIBLE);
                if (!mPost.getLaughterEmoji()) {
                    Objects.requireNonNull(chip2.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getLaughterEmojiCount() != 0) {
                    chip2.setText(mPost.getLaughterEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getHeartEmojiCount() != null && mPost.getHeartEmojiCount() != -1) {
                chip3.setVisibility(View.VISIBLE);
                if (!mPost.getHeartEmoji()) {
                    Objects.requireNonNull(chip3.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getHeartEmojiCount() != 0) {
                    chip3.setText(mPost.getHeartEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getDisappointedEmojiCount() != null && mPost.getDisappointedEmojiCount() != -1) {
                chip4.setVisibility(View.VISIBLE);
                if (!mPost.getDisappointedEmoji()) {
                    Objects.requireNonNull(chip4.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getDisappointedEmojiCount() != 0) {
                    chip4.setText(mPost.getDisappointedEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getSmileEmojiCount() != null && mPost.getSmileEmojiCount() != -1) {
                chip5.setVisibility(View.VISIBLE);
                if (!mPost.getSmileEmoji()) {
                    Objects.requireNonNull(chip5.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getSmileEmojiCount() != 0) {
                    chip5.setText(mPost.getSmileEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getAngryEmojiCount() != null && mPost.getAngryEmojiCount() != -1) {
                chip6.setVisibility(View.VISIBLE);
                if (!mPost.getAngryEmoji()) {
                    Objects.requireNonNull(chip6.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getAngryEmojiCount() != 0) {
                    chip6.setText(mPost.getAngryEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getSmileWithHeartEyesCount() != null && mPost.getSmileWithHeartEyesCount() != -1) {
                chip7.setVisibility(View.VISIBLE);
                if (!mPost.getSmileWithHeartEyes()) {
                    Objects.requireNonNull(chip7.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getSmileWithHeartEyesCount() != 0) {
                    chip7.setText(mPost.getSmileWithHeartEyesCount().toString(), TextView.BufferType.NORMAL);
                }
            }
            if (mPost.getScreamingEmojiCount() != null && mPost.getScreamingEmojiCount() != -1) {
                chip8.setVisibility(View.VISIBLE);
                if (!mPost.getScreamingEmoji()) {
                    Objects.requireNonNull(chip8.getChipIcon()).setColorFilter(getFilter());
                }
                if (mPost.getScreamingEmojiCount() != 0) {
                    chip8.setText(mPost.getScreamingEmojiCount().toString(), TextView.BufferType.NORMAL);
                }
            }

            authorId = sp.getInt("userId", -1);

            chip1.setOnClickListener(v -> {
                chip1.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setLikeEmoji(!mPost.getLikeEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            Call<PostModel> postCall = DataManager.getInstance().getPost(mPost.getId());
                            postCall.enqueue(new Callback<PostModel>() {
                                @Override
                                public void onResponse(@NotNull Call<PostModel> call, @NotNull Response<PostModel> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
//                                        Post post = response.body().getPost();
//                                        mPost.setLikeEmojiCount(post.getLikeEmojiCount());
//                                        chip1.setChipIcon(Objects.requireNonNull(mRouter.getActivity()).getDrawable(R.drawable.thumbs_up_sign));
//                                        notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<PostModel> call, @NotNull Throwable t) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip2.setOnClickListener(v -> {
                chip2.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setLaughterEmoji(!mPost.getLaughterEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip3.setOnClickListener(v -> {
                chip3.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setHeartEmoji(!mPost.getHeartEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip4.setOnClickListener(v -> {
                chip4.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setDisappointedEmoji(!mPost.getDisappointedEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip5.setOnClickListener(v -> {
                chip5.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setSmileEmoji(!mPost.getSmileEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip6.setOnClickListener(v -> {
                chip6.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setAngryEmoji(!mPost.getAngryEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip7.setOnClickListener(v -> {
                chip7.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setSmileWithHeartEyes(!mPost.getSmileWithHeartEyes());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            chip8.setOnClickListener(v -> {
                chip8.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setScreamingEmoji(mPost.getScreamingEmoji());
                updatedPost.setUserId(authorId);
                Call<Status> call = DataManager.getInstance().editPost(mPost.getId(), updatedPost);
                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            // TODO
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
            });

            moreButton.setOnClickListener(v -> {
                if (!mPost.getUserId().equals(authorId)) {
                    return;
                }
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

        private boolean hasImage(@NonNull ImageView view) {
            Drawable drawable = view.getDrawable();
            boolean hasImage = (drawable != null);
            if (hasImage && (drawable instanceof BitmapDrawable)) {
                hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
            }
            return hasImage;
        }

        private ColorMatrixColorFilter getFilter() {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            return new ColorMatrixColorFilter(matrix);
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
