package com.example.neobrain.Controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.Adapters.MessagesAdapter;
import com.example.neobrain.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatController extends Controller {
    @BindView(R.id.messagesRecycler)
    public RecyclerView messagesRecycler;
    private MessagesAdapter messagesAdapter;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.chat_controller, container, false);
        ButterKnife.bind(this, view);
        setUp();
        return view;
    }

    private void setUp() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messagesRecycler.setLayoutManager(mLayoutManager);
        messagesRecycler.setItemAnimator(new DefaultItemAnimator());
        /*
        Это пример! Если хотите посмотреть как это будет выглядеть (+-), раскомментируйте!
        ArrayList<Chat> mChats = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            mChats.add(new Chat("Я давно хотел тебе это сказать" + i, "12:30",
                    "https://androidwave.com/media/images/img_baseball.jpg", "Лёха"));
        }
        messagesAdapter = new MessagesAdapter(mChats);
        */
        messagesAdapter = new MessagesAdapter(new ArrayList<>());
        messagesRecycler.setAdapter(messagesAdapter);
    }
}