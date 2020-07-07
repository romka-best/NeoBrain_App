package com.itschool.neobrain.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.R;
import com.itschool.neobrain.adapters.SearchAdapter;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Контроллер для поиска по чатам */
public class SearchChatsController extends Controller {
    @BindView(R.id.chatRecycler)
    public RecyclerView chatRecycler;
    private SearchAdapter chatAdapter;
    private ArrayList<Chat> mChats = new ArrayList<>();
    private String curNameChat;
    private Integer curPhotoId;

    private SharedPreferences sp;
    private Router mainRouter;
    private boolean found;
    private String mChatString;

    // Несколько конструкторов для первого создания и последующего обновления контроллера
    public SearchChatsController() {
        found = true;
        mChatString = "";
    }

    public SearchChatsController(ArrayList<Chat> mChats, String mChatString, Router router) {
        this.mChats = mChats;
        this.mChatString = mChatString;
        this.mainRouter = router;
        found = true;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_chats_controller, container, false);
        ButterKnife.bind(this, view);

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        // Получаем чаты для поиска и выводим на экран
        getChats();
        return view;
    }

    /* Метод для получения и отображения чатов при поиске*/
    private void getChats() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        chatRecycler.setLayoutManager(mLayoutManager);
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        if (!mChatString.equals("")) {
            found = false;
        }

        // Присваиваем нужный адаптер
        chatAdapter = new SearchAdapter(mainRouter, found);
        chatAdapter.setChatList(mChats);
        chatAdapter.getFilter().filter(mChatString);
        chatRecycler.setAdapter(chatAdapter);
    }
}
