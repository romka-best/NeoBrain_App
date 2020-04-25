package com.example.neobrain.Adapters;

// Импортируем нужные библиотеки
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neobrain.API.model.Message;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.DataManager;
import com.example.neobrain.MainActivity;
import com.example.neobrain.R;
import com.example.neobrain.util.BaseViewHolder;

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

// Адаптер сообщений
public class MessageAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "MessageAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE_INCOMING = 1;
    private static final int VIEW_TYPE_MESSAGE_OUTGOING = 2;
    private User user;
    private Integer userId;
    private List<Message> mMessageList;
    private Callback mCallback;
    private SharedPreferences sp;


    public MessageAdapter(ArrayList<Message> mMessageList, Context context) {
        this.mMessageList = mMessageList;
        sp = Objects.requireNonNull(context).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        Integer userId = sp.getInt("userId", -1);
        Call<UserModel> userCall = DataManager.getInstance().getUser(userId);
        userCall.enqueue(new retrofit2.Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                assert response.body() != null;
                user = response.body().getUser();
            }
            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
            }
        });
   }

    @NonNull
    @Override

    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_INCOMING:
                return new IncomingMessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_incoming_message, parent, false));
            case VIEW_TYPE_MESSAGE_OUTGOING:
                return new OutgoingMessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_outgoing_message, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_empty_item_messages, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mMessageList != null && mMessageList.size() > 0) {
            // TODO: заменить 5 на id текущего пользователя
            if (mMessageList.get(position).getAuthorId().equals(5)){
                return VIEW_TYPE_MESSAGE_OUTGOING;
            } else {
                return VIEW_TYPE_MESSAGE_INCOMING;
            }
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageList != null && mMessageList.size() > 0) {
            return mMessageList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<Message> MessageList) {
        mMessageList.addAll(MessageList);
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onEmptyViewRetryClick();
    }

    public class IncomingMessageViewHolder extends BaseViewHolder {
        @BindView(R.id.message)
        TextView messageTextView;
        @BindView(R.id.time)
        TextView time;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            messageTextView.setText("");
            time.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final Message mMessage = mMessageList.get(position);
            if (mMessage.getText() != null) {
                messageTextView.setText(mMessage.getText());
            }
            if (mMessage.getCreatedDate() != null) {
                time.setText(mMessage.getCreatedDate());
            }
            itemView.setOnClickListener(v -> {
                // Сделать что-нибудь
            });
        }
    }

    public class OutgoingMessageViewHolder extends BaseViewHolder {
        @BindView(R.id.message)
        TextView messageTextView;
        @BindView(R.id.time)
        TextView time;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            messageTextView.setText("");
            time.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final Message mMessage = mMessageList.get(position);
            if (mMessage.getText() != null) {
                messageTextView.setText(mMessage.getText());
            }
            if (mMessage.getCreatedDate() != null) {
                time.setText(mMessage.getCreatedDate());
            }
            itemView.setOnClickListener(v -> {
                // Сделать что-нибудь
            });
            Log.e("STRING", messageTextView.getText().toString().length() + "");
        }
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.emoji)
        ImageView emoji;
        @BindView(R.id.titleMessage)
        TextView titleMessage;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}