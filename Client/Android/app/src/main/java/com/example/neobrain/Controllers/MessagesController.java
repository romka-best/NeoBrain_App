package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatModel;
import com.example.neobrain.API.model.Message;
import com.example.neobrain.API.model.Messages;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.Adapters.MessageAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.util.SpacesItemDecoration;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Контроллер с сообщениями(Именно сам чат)
public class MessagesController extends Controller {

    private MessageAdapter messageAdapter;
    private Chat chat;
    @BindView(R.id.avatar)
    ImageView coverImageView;
    @BindView(R.id.MessagesRecycler)
    RecyclerView messagesRecycler;
    @BindView(R.id.footer_chat_edit_text)
    EditText FooterChatEditText;
    @BindView(R.id.nameAndSurname)
    TextView nameAndSurname;
    @BindView(R.id.textStatus)
    TextView textStatus;
    @BindView(R.id.backButton)
    ImageButton backButton;
    @BindView(R.id.callButton)
    ImageButton callButton;
    @BindView(R.id.attach)
    ImageButton attachButton;
    @BindView(R.id.send)
    ImageButton sendButton;

    public MessagesController() {
    }

    public MessagesController(Chat chat) {
        this.chat = chat;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.messages_controller, container, false);
        ButterKnife.bind(this, view);
        backButton.setColorFilter(Color.argb(255, 255, 255, 255));
        backButton.setOnClickListener(v -> {
                    BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    getRouter().popCurrentController();
                }
        );
        if (chat.getPhotoId() != null) {
            Call<Photo> call = DataManager.getInstance().getPhoto(chat.getPhotoId());
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
                }
            });
        }
        if (chat.getName() != null) {
            nameAndSurname.setText(chat.getName());
        }
        getMessages();
        return view;
    }

    private void getMessages() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messagesRecycler.setLayoutManager(mLayoutManager);
        messagesRecycler.setItemAnimator(new DefaultItemAnimator());
        Call<Messages> call = DataManager.getInstance().getMessages(chat.getId());
        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(@NotNull Call<Messages> call, @NotNull Response<Messages> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Message> messages = response.body().getMessages();
                    ArrayList<Message> mMessages = new ArrayList<>();
                    for (Message message : messages) {
                        mMessages.add(new Message(message.getText(), message.getCreatedDate(), message.getAuthorId()));
                    }
                    messageAdapter = new MessageAdapter(mMessages, getApplicationContext());
                    messagesRecycler.setAdapter(messageAdapter);
                    messagesRecycler.addItemDecoration(new SpacesItemDecoration(20));
                    if (mMessages.size() > 0) {
                        messagesRecycler.smoothScrollToPosition(mMessages.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Messages> call, @NotNull Throwable t) {
            }
        });
    }

    @OnClick(R.id.send)
    void sendMessage() {
        // TODO: реализовать отправку сообщения
        FooterChatEditText.setText("");
    }

    // TODO: проматывать сообщения вниз при нажатии на edit text
    @OnClick(R.id.footer_chat_edit_text)
    void typeText() {
        messagesRecycler.smoothScrollToPosition(Objects.requireNonNull(messagesRecycler.getAdapter()).getItemCount() - 1);
    }

    @Override
    public boolean handleBack() {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }
}
