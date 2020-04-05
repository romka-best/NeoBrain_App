package com.example.neobrain.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.Adapters.ChatAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;

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

public class ChatController extends Controller {
    @BindView(R.id.messagesRecycler)
    public RecyclerView messagesRecycler;
    private ChatAdapter chatAdapter;

    private SwipeRefreshLayout swipeContainer;

    private SharedPreferences sp;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.chat_controller, container, false);
        ButterKnife.bind(this, view);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            setUp();
            swipeContainer.setRefreshing(false);
        });
        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        setUp();
        return view;
    }

    private void setUp() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messagesRecycler.setLayoutManager(mLayoutManager);
        messagesRecycler.setItemAnimator(new DefaultItemAnimator());
        String nicknameSP = sp.getString("nickname", "");
        Call<ChatModel> call = DataManager.getInstance().getChats(nicknameSP);
        call.enqueue(new Callback<ChatModel>() {
            @Override
            public void onResponse(@NotNull Call<ChatModel> call, @NotNull Response<ChatModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Chat> chats = response.body().getChats();
                    ArrayList<Chat> mChats = new ArrayList<>();
                    for (Chat chat : chats) {
                        mChats.add(new Chat(chat.getLastMessage(), chat.getLastTimeMessage(), chat.getName(), chat.getPhotoId()));
                    }
                    chatAdapter = new ChatAdapter(mChats);
                    messagesRecycler.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ChatModel> call, @NotNull Throwable t) {

            }
        });
    }
}