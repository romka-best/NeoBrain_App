package com.itschool.neobrain.adapters;

// Импортируем нужные библиотеки

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itschool.neobrain.API.models.Message;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.utils.BaseViewHolder;
import com.itschool.neobrain.utils.TimeFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Адаптер сообщений */
public class MessageAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "MessageAdapter";
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_MESSAGE_INCOMING = 1;
    private static final int VIEW_TYPE_MESSAGE_OUTGOING = 2;
	// Этот тип сообщения для вывода даты по центру RecyclerView
    private static final int VIEW_TYPE_HELPER_MESSAGE = 3;
    private User user;
    private Integer userId;
    private List<Message> mMessageList;
    private SharedPreferences sp;
    private Context context;


    public MessageAdapter(ArrayList<Message> mMessageList, Context context) {
        this.mMessageList = mMessageList;
        this.context = context;
        sp = Objects.requireNonNull(context).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        userId = sp.getInt("userId", -1);
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
        Collections.sort(mMessageList, Message.COMPARE_BY_TIME);
		// Определяем тип сообщения
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_INCOMING:
                return new IncomingMessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_incoming_message, parent, false));
            case VIEW_TYPE_MESSAGE_OUTGOING:
                return new OutgoingMessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_view_item_outgoing_message, parent, false));
            case VIEW_TYPE_HELPER_MESSAGE:
                return new HelperMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_helper_message, parent, false));
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
            if (mMessageList.get(position).getAuthorId().equals(-1)) {
                return VIEW_TYPE_HELPER_MESSAGE;
            } else if (mMessageList.get(position).getAuthorId().equals(userId)) {
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
                if (!mMessage.getShowDate()) {
                    time.setVisibility(View.GONE);
                } else {
                    time.setVisibility(View.VISIBLE);
                }
                time.setText(new TimeFormatter(mMessage.getCreatedDate()).timeForMessageHolder());
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
            TimeFormatter nowMessage = new TimeFormatter(mMessage.getCreatedDate());
            if (mMessage.getText() != null) {
                messageTextView.setText(mMessage.getText());
            }
            if (mMessage.getCreatedDate() != null) {
                if (!mMessage.getShowDate()) {
                    time.setVisibility(View.GONE);
                } else {
                    time.setVisibility(View.VISIBLE);
                }
                time.setText(new TimeFormatter(mMessage.getCreatedDate()).timeForMessageHolder());
            }
            itemView.setOnLongClickListener(v -> {
                long mills = 100L;
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                assert vibrator != null;
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(mills);
                }
                return false;
            });
        }
    }

    public class HelperMessage extends BaseViewHolder {
        @BindView(R.id.messageTextView)
        TextView messageTextView;


        public HelperMessage(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            final Message mMessage = mMessageList.get(position);
            if (mMessage.getText() != null) {
                messageTextView.setText(mMessage.getText());
            }

        }

        @Override
        protected void clear() {

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