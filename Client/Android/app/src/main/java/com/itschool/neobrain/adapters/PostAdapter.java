package com.itschool.neobrain.adapters;

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
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.Post;
import com.itschool.neobrain.API.models.PostModel;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.controllers.ProfileController;
import com.itschool.neobrain.utils.BaseViewHolder;
import com.itschool.neobrain.utils.TimeFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Адаптер для постов */
public class PostAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "PostAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY_LENTA = 2;

    private List<Post> mPostList;
    private Router mRouter;
    private SharedPreferences sp;
    private Integer authorId;
    private boolean isLenta;

    public PostAdapter(List<Post> postList, Router router, boolean isLenta) {
        mPostList = postList;
        mRouter = router;
        sp = Objects.requireNonNull(router.getActivity()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        this.isLenta = isLenta;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_post, parent, false)
                );
            case VIEW_TYPE_EMPTY_LENTA:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item_lenta, parent, false)
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
        } else if (isLenta) {
            return VIEW_TYPE_EMPTY_LENTA;
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

    public void editItem(int position, Post post) {
        mPostList.set(position, post);
        notifyItemChanged(position, post);
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
            if (!mPost.getAuthor()) {
                moreButton.setVisibility(View.GONE);
            }
            if (mPost.getLikeEmojiCount() != null && mPost.getLikeEmojiCount() != -1) {
                chip1.setVisibility(View.VISIBLE);
                if (!mPost.getLikeEmoji()) {
                    Objects.requireNonNull(chip1.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip1.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getLikeEmojiCount() != 0) {
                    chip1.setText(mPost.getLikeEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip1.setText("");
                }
            }
            if (mPost.getLaughterEmojiCount() != null && mPost.getLaughterEmojiCount() != -1) {
                chip2.setVisibility(View.VISIBLE);
                if (!mPost.getLaughterEmoji()) {
                    Objects.requireNonNull(chip2.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip2.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getLaughterEmojiCount() != 0) {
                    chip2.setText(mPost.getLaughterEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip2.setText("");
                }
            }
            if (mPost.getHeartEmojiCount() != null && mPost.getHeartEmojiCount() != -1) {
                chip3.setVisibility(View.VISIBLE);
                if (!mPost.getHeartEmoji()) {
                    Objects.requireNonNull(chip3.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip3.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getHeartEmojiCount() != 0) {
                    chip3.setText(mPost.getHeartEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip3.setText("");
                }
            }
            if (mPost.getDisappointedEmojiCount() != null && mPost.getDisappointedEmojiCount() != -1) {
                chip4.setVisibility(View.VISIBLE);
                if (!mPost.getDisappointedEmoji()) {
                    Objects.requireNonNull(chip4.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip4.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getDisappointedEmojiCount() != 0) {
                    chip4.setText(mPost.getDisappointedEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip4.setText("");
                }
            }
            if (mPost.getSmileEmojiCount() != null && mPost.getSmileEmojiCount() != -1) {
                chip5.setVisibility(View.VISIBLE);
                if (!mPost.getSmileEmoji()) {
                    Objects.requireNonNull(chip5.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip5.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getSmileEmojiCount() != 0) {
                    chip5.setText(mPost.getSmileEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip5.setText("");
                }
            }
            if (mPost.getAngryEmojiCount() != null && mPost.getAngryEmojiCount() != -1) {
                chip6.setVisibility(View.VISIBLE);
                if (!mPost.getAngryEmoji()) {
                    Objects.requireNonNull(chip6.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip6.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getAngryEmojiCount() != 0) {
                    chip6.setText(mPost.getAngryEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip6.setText("");
                }
            }
            if (mPost.getSmileWithHeartEyesCount() != null && mPost.getSmileWithHeartEyesCount() != -1) {
                chip7.setVisibility(View.VISIBLE);
                if (!mPost.getSmileWithHeartEyes()) {
                    Objects.requireNonNull(chip7.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip7.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getSmileWithHeartEyesCount() != 0) {
                    chip7.setText(mPost.getSmileWithHeartEyesCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip7.setText("");
                }
            }
            if (mPost.getScreamingEmojiCount() != null && mPost.getScreamingEmojiCount() != -1) {
                chip8.setVisibility(View.VISIBLE);
                if (!mPost.getScreamingEmoji()) {
                    Objects.requireNonNull(chip8.getChipIcon()).setColorFilter(getFilter(true));
                } else {
                    Objects.requireNonNull(chip8.getChipIcon()).setColorFilter(getFilter(false));
                }
                if (mPost.getScreamingEmojiCount() != 0) {
                    chip8.setText(mPost.getScreamingEmojiCount().toString(), TextView.BufferType.NORMAL);
                } else {
                    chip8.setText("");
                }
            }

            authorId = sp.getInt("userId", -1);

            chip1.setOnClickListener(v -> {
                chip1.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setLikeEmoji(!mPost.getLikeEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip2.setOnClickListener(v -> {
                chip2.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setLaughterEmoji(!mPost.getLaughterEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip3.setOnClickListener(v -> {
                chip3.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setHeartEmoji(!mPost.getHeartEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip4.setOnClickListener(v -> {
                chip4.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setDisappointedEmoji(!mPost.getDisappointedEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip5.setOnClickListener(v -> {
                chip5.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setSmileEmoji(!mPost.getSmileEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip6.setOnClickListener(v -> {
                chip6.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setAngryEmoji(!mPost.getAngryEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip7.setOnClickListener(v -> {
                chip7.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setSmileWithHeartEyes(!mPost.getSmileWithHeartEyes());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            chip8.setOnClickListener(v -> {
                chip8.setChecked(false);
                Post updatedPost = new Post();
                updatedPost.setScreamingEmoji(!mPost.getScreamingEmoji());
                updatedPost.setUserId(authorId);
                updatePost(mPost, updatedPost, position);
            });

            avatarImageView.setOnClickListener(v -> {
                if (isLenta) {
                    Call<PostModel> postCall = DataManager.getInstance().getPost(mPost.getId());
                    postCall.enqueue(new Callback<PostModel>() {
                        @Override
                        public void onResponse(@NotNull Call<PostModel> call, @NotNull Response<PostModel> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                List<PostModel> posts = response.body().getUsers();
                                for (PostModel curPostModel : posts) {
                                    Post curPost = curPostModel.getPost();
                                    if (curPost.getAuthor()) {
                                        mRouter.pushController(RouterTransaction.with(new ProfileController(curPost.getUserId(), false))
                                                .popChangeHandler(new FadeChangeHandler())
                                                .pushChangeHandler(new FadeChangeHandler()));
                                        return;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<PostModel> call, @NotNull Throwable t) {
                        }
                    });
                }
            });

            moreButton.setOnClickListener(v -> {
                if (!mPost.getAuthor()) {
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
                                                notifyItemRemoved(position);
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

        private ColorMatrixColorFilter getFilter(boolean isBlackAndWhite) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(isBlackAndWhite ? 0 : 1);

            return new ColorMatrixColorFilter(matrix);
        }

        private void updatePost(Post mPost, Post updatedPost, int position) {
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
                                    List<PostModel> posts = response.body().getUsers();
                                    for (PostModel curPostModel : posts) {
                                        Post curPost = curPostModel.getPost();
                                        if (curPost.getUserId().equals(authorId)) {
                                            editItem(position, curPost);
                                            return;
                                        }
                                    }
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
