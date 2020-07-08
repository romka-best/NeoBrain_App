package com.itschool.neobrain.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.android.material.button.MaterialButton;
import com.itschool.neobrain.API.models.App;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.UserApp;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
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

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Адптер для приложений */
public class AppsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "AppsAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private CallbackInterface mCallback;
    private List<App> mAppsList;
    private Router mRouter;
    private SharedPreferences sp;

    public AppsAdapter(ArrayList<App> mAppsList, Router router) {
        this.mAppsList = mAppsList;
        mRouter = router;
        sp = Objects.requireNonNull(router.getActivity()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
    }

    public void setCallback(CallbackInterface callback) {
        this.mCallback = callback;
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

    public interface CallbackInterface {
        void onEmptyViewRetryClick();
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
        @BindView(R.id.button)
        MaterialButton button;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            coverImageView.setImageDrawable(null);
            titleTextView.setText("");
            secondaryTextView.setText("");
            descriptionTextView.setText("");
            button.setText("");
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
                        Log.e(TAG, "Чёрт...");
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
            if (mApp.getAdded()) {
                button.setText(R.string.delete);
            } else {
                button.setText(R.string.add);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mApp.getLinkAndroid()));
                    Objects.requireNonNull(mRouter.getActivity()).startActivity(browserIntent);
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer userIdSP = sp.getInt("userId", -1);
                    if (mApp.getAdded()) {
                        Call<Status> call = DataManager.getInstance().deleteApp(userIdSP, mApp.getId());
                        call.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                                if (response.isSuccessful()) {
                                    mAppsList.remove(mApp);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                            }
                        });
                    } else {
                        UserApp userApp = new UserApp(userIdSP, mApp.getId());
                        Call<Status> call = DataManager.getInstance().addApp(userApp);
                        call.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                            }
                        });
                    }
                }
            });
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.emoji)
        ImageView emoji;
        @BindView(R.id.titleApp)
        TextView titleApp;
        @BindView(R.id.reloadButton)
        MaterialButton reloadButton;

        EmptyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            reloadButton.setOnClickListener(v -> mCallback.onEmptyViewRetryClick());
        }

        @Override
        protected void clear() {
        }
    }
}
