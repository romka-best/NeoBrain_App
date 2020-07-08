package com.itschool.neobrain.controllers;

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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.API.models.ChatUsers;
import com.itschool.neobrain.API.models.Chats;
import com.itschool.neobrain.API.models.PhotoModel;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.ChatAdapter;
import com.itschool.neobrain.changehandler.ScaleFadeChangeHandler;

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

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Контроллер чатов */
@SuppressLint("ValidController")
public class ChatController extends Controller {
    @BindView(R.id.ChatsRecycler)
    public RecyclerView chatRecycler;
    private ChatAdapter chatAdapter;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerViewContainer;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.filter_chats_button)
    ImageButton filterButton;
    @BindView(R.id.search_chats_button)
    ImageButton searchChatsButton;

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

        floatingActionButton.setColorFilter(Color.argb(255, 255, 255, 255));

        shimmerViewContainer.startShimmer();

        // Ставим слушатель - при свайпе вверх обновляем
        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(true);
            // Получаем и выводим чаты
            getChats();
            swipeContainer.setRefreshing(false);
        });
        swipeContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        // Слушатель на кнопку поиска по чатам
        searchChatsButton.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
            getRouter().pushController(RouterTransaction.with(new SearchController((short) 3))
                    .popChangeHandler(new HorizontalChangeHandler())
                    .pushChangeHandler(new HorizontalChangeHandler()));
        });

        // Слушатель на кнопку создания нового чата

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
                getRouter().pushController(RouterTransaction.with(new PeopleController(sp.getInt("userId", -1), true, true))
                        .popChangeHandler(new ScaleFadeChangeHandler())
                        .pushChangeHandler(new ScaleFadeChangeHandler()));
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
        // Получаем и выводим чаты
        getChats();
        return view;
    }

    /* Метод, получающий и выводящий чаты пользователя с помощью запросов на сервер */
    private void getChats() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        chatRecycler.setLayoutManager(mLayoutManager);
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        Integer userIdSP = sp.getInt("userId", -1);
        Call<Chats> call = DataManager.getInstance().getChats(userIdSP);
        call.enqueue(new Callback<Chats>() {
            @Override
            public void onResponse(@NotNull Call<Chats> call, @NotNull Response<Chats> response) {
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
                                                        ArrayList<PhotoModel> photos = (ArrayList<PhotoModel>) response.body().getPhotos();
                                                        curNameChat = user.getName() + " " + user.getSurname();
                                                        for (PhotoModel photo : photos) {
                                                            if (photo.getPhoto().getAvatar()) {
                                                                curPhotoId = photo.getPhoto().getId();
                                                                break;
                                                            }
                                                        }
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
                                                    Snackbar.make(getView(), R.string.errors_with_connection, Snackbar.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }


                                @Override
                                public void onFailure(@NotNull Call<ChatUsers> call, @NotNull Throwable t) {
                                    Snackbar.make(getView(), R.string.errors_with_connection, Snackbar.LENGTH_LONG).show();
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
            public void onFailure(@NotNull Call<Chats> call, @NotNull Throwable t) {
                assert getView() != null;
                Snackbar.make(getView(), R.string.errors_with_connection, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /* Метод, вызываемый при окончании запроса к серверу (когда все чаты нашлись)*/
    private void allChatsSearched() {
        if (mChats.size() >= 2) {
            // Сортируем чаты по времени
            Collections.sort(mChats, Chat.COMPARE_BY_TIME);
        }
        // Устанавливаем нужный адаптер
        chatAdapter = new ChatAdapter(mChats, getRouter());
        chatRecycler.setAdapter(chatAdapter);
        shimmerViewContainer.stopShimmer();
        shimmerViewContainer.setVisibility(View.GONE);
    }
}