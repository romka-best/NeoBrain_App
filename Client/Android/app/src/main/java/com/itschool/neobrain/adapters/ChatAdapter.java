package com.itschool.neobrain.adapters;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
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
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.controllers.MessagesController;
import com.itschool.neobrain.utils.BaseViewHolder;
import com.itschool.neobrain.utils.TimeFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/* Адаптер для чатов */
public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "ChatAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Chat> mChatsList;
    private Router childRouter;
    private Chat chat;

    public ChatAdapter(ArrayList<Chat> mChatsList, Router childRouter) {
        this.mChatsList = mChatsList;
        this.childRouter = childRouter;
    }

    @NonNull
    @Override

    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_chat, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_empty_item_chat, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mChatsList != null && mChatsList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mChatsList != null && mChatsList.size() > 0) {
            return mChatsList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Chat> ChatList) {
        mChatsList.addAll(ChatList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.avatar)
        ImageView coverImageView;
        @BindView(R.id.title)
        TextView titleTextView;
        @BindView(R.id.lastMessage)
        TextView lastMessTextView;
        @BindView(R.id.time)
        TextView timeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            coverImageView.setImageDrawable(null);
            titleTextView.setText("");
            lastMessTextView.setText("");
            timeTextView.setText("");
        }

        @SuppressLint("ResourceType")
        public void onBind(int position) {
            super.onBind(position);
            final Chat mChat = mChatsList.get(position);
            if (mChat.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mChat.getPhotoId());
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

            if (mChat.getLastMessage() != null) {
                lastMessTextView.setText(mChat.getLastMessage());
            }
            if (mChat.getLastTimeMessage() != null) {
                timeTextView.setText(new TimeFormatter(mChat.getLastTimeMessage()).timeForChat(itemView.getContext()));
            }
            if (mChat.getName() != null) {
                titleTextView.setText(mChat.getName());
            }
            itemView.setOnClickListener(v -> {
                chat = mChatsList.get(position);
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(childRouter.getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
                childRouter.pushController(RouterTransaction.with(new MessagesController(chat))
                        .popChangeHandler(new FadeChangeHandler())
                        .pushChangeHandler(new FadeChangeHandler()));
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
