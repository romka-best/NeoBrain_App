package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.User;
import com.example.neobrain.Adapters.SearchAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchChatsController extends Controller {
    @BindView(R.id.chatRecycler)
    public RecyclerView chatRecycler;
    private SearchAdapter chatAdapter;
    private ArrayList<Chat> mChats = new ArrayList<>();

    private Router mainRouter;
    private boolean found;

    public SearchChatsController() {
        found = true;
    }

    public SearchChatsController(ArrayList<Chat> mChats, Router router, boolean found) {
        this.mChats = mChats;
        this.mainRouter = router;
        this.found = found;
    }
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_chats_controller, container, false);
        ButterKnife.bind(this, view);

//        getChats();
        return view;
    }

    private void getChats() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        chatRecycler.setLayoutManager(mLayoutManager);
        chatRecycler.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new SearchAdapter(mainRouter, found);
        chatAdapter.setChatList(mChats);
        chatAdapter.notifyDataSetChanged();
        chatRecycler.setAdapter(chatAdapter);
    }
}
