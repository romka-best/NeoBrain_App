package com.itschool.neobrain.adapters;

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

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.controllers.DetailedPhotoController;
import com.itschool.neobrain.utils.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "PhotosAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Photo> mPhotoList;
    private Router childRouter;

    public PhotosAdapter(ArrayList<Photo> photoList, Router childRouter) {
        this.mPhotoList = photoList;
        this.childRouter = childRouter;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_photo, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_photos, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mPhotoList != null && mPhotoList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mPhotoList != null && mPhotoList.size() > 0) {
            return mPhotoList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Photo> photoList) {
        mPhotoList.addAll(photoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.photo)
        ImageView photoImageView;
        String photoString;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);

            final Photo mPhoto = mPhotoList.get(position);
            Call<Photo> call = DataManager.getInstance().getPhoto(mPhoto.getId());
            call.enqueue(new retrofit2.Callback<Photo>() {
                @Override
                public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String photo = response.body().getPhoto();
                        photoString = photo;
                        byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                        Bitmap original = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Bitmap decoded = Bitmap.createScaledBitmap(original, original.getWidth() / 2, original.getHeight() / 2, false);
                        photoImageView.setImageBitmap(decoded);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                    Log.e(TAG, t.toString() + "");
                }
            });

            photoImageView.setOnClickListener(v -> {
                if (photoString != null) {
                    childRouter.pushController(RouterTransaction.with(new DetailedPhotoController(true, photoString))
                            .popChangeHandler(new VerticalChangeHandler())
                            .pushChangeHandler(new VerticalChangeHandler()));
                }
            });

            photoImageView.setOnLongClickListener(v -> {
                new MaterialAlertDialogBuilder(Objects.requireNonNull(childRouter.getActivity()), R.style.AlertDialogCustom)
                        .setMessage(R.string.delete_question)
                        .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                            Call<Status> call1 = DataManager.getInstance().deletePhoto(mPhoto.getId());
                            call1.enqueue(new Callback<Status>() {
                                @Override
                                public void onResponse(@NotNull Call<Status> call1, @NotNull Response<Status> response) {
                                    if (response.isSuccessful()) {
                                        mPhotoList.remove(mPhoto);
                                        // TODO Добавить CallBack (Если фотка последняя, изменить на LinearLayoutManager)
                                        notifyItemRemoved(position);
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<Status> call1, @NotNull Throwable t) {
                                }
                            });
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            });
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.titlePhoto)
        TextView titlePhotoTextView;
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
