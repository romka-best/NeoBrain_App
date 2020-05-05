package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.ChatUsers;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.ChatAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер чатов
@SuppressLint("ValidController")
public class ChatController extends Controller {
    @BindView(R.id.ChatsRecycler)
    public RecyclerView messagesRecycler;
    private ChatAdapter chatAdapter;
    private FloatingActionButton floatingActionButton;
    private ShimmerFrameLayout shimmerViewContainer;
    private SwipeRefreshLayout swipeContainer;
    private ImageButton searchChatsButton;
    private SharedPreferences sp;
    private LayoutInflater inflater;
    private ArrayList<Chat> mChats = new ArrayList<>();
    private String curNameChat;
    private Integer curPhotoId;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.chat_controller, container, false);
        ButterKnife.bind(this, view);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        this.inflater = inflater;

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setColorFilter(Color.argb(255, 255, 255, 255));
        searchChatsButton = view.findViewById(R.id.search_chats_button);

        shimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        shimmerViewContainer.startShimmer();

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            getChats();
            swipeContainer.setRefreshing(false);
        });
        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        searchChatsButton.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new SearchController())
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler())));

        getChats();
        return view;
    }

    private void getChats() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messagesRecycler.setLayoutManager(mLayoutManager);
        messagesRecycler.setItemAnimator(new DefaultItemAnimator());
        Integer userIdSP = sp.getInt("userId", -1);
        Call<ChatModel> call = DataManager.getInstance().getChats(userIdSP);
        call.enqueue(new Callback<ChatModel>() {
            @Override
            public void onResponse(@NotNull Call<ChatModel> call, @NotNull Response<ChatModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    final List<Chat> chats = response.body().getChats();
                    if (chats.size() == 0) {
                        allChatsSearched();
                        return;
                    }
                    mChats = new ArrayList<>();
                    for (Chat chat : chats) {
                        curNameChat = "";
                        curPhotoId = 2;
                        if (chat.getTypeOfChat() == 0) {
                            Call<ChatUsers> chatUsersCall = DataManager.getInstance().searchUsersInChat(chat.getId());
                            chatUsersCall.enqueue(new Callback<ChatUsers>() {
                                @Override
                                public void onResponse(@NotNull Call<ChatUsers> call, @NotNull Response<ChatUsers> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        List<Integer> users = response.body().getUsers();
                                        for (Integer userId : users) {
                                            if (userId.equals(userIdSP)) {
                                                continue;
                                            }
                                            Call<UserModel> userCall = DataManager.getInstance().getUser(userId);
                                            userCall.enqueue(new Callback<UserModel>() {
                                                @Override
                                                public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                                                    if (response.isSuccessful()) {
                                                        assert response.body() != null;
                                                        User user = response.body().getUser();
                                                        curNameChat = user.getName() + " " + user.getSurname();
                                                        curPhotoId = user.getPhotoId();
                                                        for (Chat queueChat : mChats) {
                                                            if (queueChat.getId().equals(chat.getId())) {
                                                                return;
                                                            }
                                                        }
                                                        mChats.add(new Chat(chat.getId(), chat.getLastMessage(), chat.getLastTimeMessage(), curNameChat, curPhotoId));

                                                    }
                                                    if (mChats.size() == chats.size()) {
                                                        allChatsSearched();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                                                }
                                            });
                                        }
                                    }
                                }


                                @Override
                                public void onFailure(@NotNull Call<ChatUsers> call, @NotNull Throwable t) {
                                }
                            });
                        } else {
                            curNameChat = chat.getName();
                            curPhotoId = chat.getPhotoId();
                            mChats.add(new Chat(chat.getId(), chat.getLastMessage(), chat.getLastTimeMessage(), curNameChat, curPhotoId));
                            if (mChats.size() == chats.size()) {
                                allChatsSearched();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ChatModel> call, @NotNull Throwable t) {
            }
        });
    }

    private void allChatsSearched() {
        if (mChats.size() >= 2) {
            Collections.sort(mChats, Chat.COMPARE_BY_TIME);
        }
        chatAdapter = new ChatAdapter(mChats, getRouter());
        messagesRecycler.setAdapter(chatAdapter);
        shimmerViewContainer.stopShimmer();
        shimmerViewContainer.setVisibility(View.GONE);
    }
}