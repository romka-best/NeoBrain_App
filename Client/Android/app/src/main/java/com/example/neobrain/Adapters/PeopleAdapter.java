package com.example.neobrain.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.User;
import com.example.neobrain.Controllers.MessagesController;
import com.example.neobrain.Controllers.ProfileController;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

public class PeopleAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "PeopleAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<User> mUsersList;
    private Context context;
    private Router childRouter;
    private SharedPreferences sp;
    private Integer authorId;
    private boolean isFromChat;

    public PeopleAdapter(ArrayList<User> mUsersList, Context context, Router childRouter, boolean isFromChat) {
        this.mUsersList = mUsersList;
        this.context = context;
        this.childRouter = childRouter;
        sp = Objects.requireNonNull(childRouter.getActivity()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        authorId = sp.getInt("userId", -1);
        this.isFromChat = isFromChat;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_people, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_people, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mUsersList != null && mUsersList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mUsersList != null && mUsersList.size() > 0) {
            return mUsersList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<User> UsersList) {
        mUsersList.addAll(UsersList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.title)
        TextView titleTextView;

        @BindView(R.id.avatar)
        ImageView avatarImageView;

        @BindView(R.id.city_age_gender)
        TextView textTextView;

        @BindView(R.id.doButton)
        ImageButton doButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
            avatarImageView.setImageBitmap(null);
            titleTextView.setText("");
            textTextView.setText("");
        }

        @SuppressLint("SetTextI18n")
        public void onBind(int position) {
            super.onBind(position);
            final User mUser = mUsersList.get(position);

            if (mUser.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mUser.getPhotoId());
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
                        Log.e(TAG, "Чёрт...");
                    }
                });
            }
            if (mUser.getName() != null && mUser.getSurname() != null) {
                titleTextView.setText(mUser.getName() + " " + mUser.getSurname());
            }
            List<String> cityAgeGender = new ArrayList<>();
            if (mUser.getRepublic() != null) {
                cityAgeGender.add(mUser.getRepublic());
            }
            if (mUser.getCity() != null) {
                cityAgeGender.add(mUser.getCity());
            }
            if (mUser.getAge() != null) {
                cityAgeGender.add(mUser.getAge().toString());
            }
            if (mUser.getGender() != null) {
                if (mUser.getGender() == 0) {
                    cityAgeGender.add(context.getString(R.string.gender_w));
                } else
                    cityAgeGender.add(context.getString(R.string.gender_m));
            }
            if (mUser.getId().equals(authorId) || isFromChat) {
                doButton.setVisibility(View.GONE);
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < cityAgeGender.size(); i++) {
                if (i != cityAgeGender.size() - 1) {
                    sb.append(cityAgeGender.get(i)).append(" | ");
                } else {
                    sb.append(cityAgeGender.get(i));
                }
            }
            textTextView.setText(sb);

            itemView.setOnClickListener(v -> {
                if (isFromChat) {
                    toChat(mUser);
                } else {
                    childRouter.pushController(RouterTransaction.with(new ProfileController(mUser.getId()))
                            .popChangeHandler(new FadeChangeHandler())
                            .pushChangeHandler(new FadeChangeHandler()));
                }
            });

            doButton.setOnClickListener(v -> {
                toChat(mUser);
            });
        }

        private void toChat(User mUser) {
            Call<ChatModel> chatModelCall = DataManager.getInstance().getUsersChat(authorId, mUser.getId());
            chatModelCall.enqueue(new Callback<ChatModel>() {
                @Override
                public void onResponse(@NotNull Call<ChatModel> call, @NotNull Response<ChatModel> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Chat chat = response.body().getChat();
                        if (chat == null) {
                            chat = new Chat();
                            chat.setId(-1);
                        }
                        chat.setPhotoId(mUser.getPhotoId());
                        childRouter.pushController(RouterTransaction.with(new MessagesController(chat, mUser.getId())));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ChatModel> call, @NotNull Throwable t) {

                }
            });
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.emoji)
        ImageView emoji;
        @BindView(R.id.titlePost)
        TextView titlePost;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
