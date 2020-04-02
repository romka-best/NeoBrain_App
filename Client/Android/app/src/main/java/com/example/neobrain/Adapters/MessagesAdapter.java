package com.example.neobrain.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.R;
import com.example.neobrain.util.BaseViewHolder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "ChatAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Chat> mChatsList;
    private Callback mCallback;

    public MessagesAdapter(ArrayList<Chat> mChatsList) {
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
        mCallback = callback;
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
            if (mChat.getImageURL() != null) {
                Glide.with(itemView.getContext())
                        .load(mChat.getImageURL())
                        .into(coverImageView);
            }
            if (mChat.getText() != null) {
                lastMessTextView.setText(mChat.getText());
            }
            if (mChat.getTime() != null) {
                timeTextView.setText(mChat.getTime());
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
        @BindView(R.id.tv_message)
        TextView messageTextView;
        @BindView(R.id.buttonRetry)
        TextView buttonRetry;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            // buttonRetry.setOnClickListener(v -> mCallback.onEmptyViewRetryClick());
            buttonRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, R.string.empty_screen, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        protected void clear() {
        }
    }

}
