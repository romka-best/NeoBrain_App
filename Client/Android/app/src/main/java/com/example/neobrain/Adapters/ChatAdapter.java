package com.example.neobrain.Adapters;

// Импортируем нужные библиотеки
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

import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.util.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

// Адаптер чатов
public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "ChatAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Chat> mChatsList;

    public ChatAdapter(ArrayList<Chat> mChatsList) {
        this.mChatsList = mChatsList;
    }

    @NonNull
    @Override

    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_chat, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
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

    public interface Callback {
        void onEmptyViewRetryClick();
    }

    public void setCallback(Callback callback) {
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
                timeTextView.setText(mChat.getLastTimeMessage());
            }
            if (mChat.getName() != null) {
                titleTextView.setText(mChat.getName());
            }
            itemView.setOnClickListener(v -> {
                // TODO: реализовать переход на MessagesController!
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
