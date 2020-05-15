package com.example.neobrain.Controllers;

// Импортируем нужные библиотеки

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.ChatUsers;
import com.example.neobrain.API.model.Message;
import com.example.neobrain.API.model.Messages;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.Status;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.UserModel;
import com.example.neobrain.Adapters.MessageAdapter;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.SpacesItemDecoration;
import com.example.neobrain.utils.TimeFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.example.neobrain.MainActivity.MY_SETTINGS;

// Контроллер с сообщениями(Именно сам чат)
public class MessagesController extends Controller {

    private MessageAdapter messageAdapter;
    private Chat chat;
    @BindView(R.id.avatar)
    ImageView coverImageView;
    @BindView(R.id.MessagesRecycler)
    RecyclerView messagesRecycler;
    @BindView(R.id.footer_chat_edit_text)
    EditText footerChatEditText;
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
    @BindView(R.id.header)
    View header;
    @BindView(R.id.progress_circular)
    public ProgressBar progressBar;

    private BottomNavigationView bottomNavigationView;

    private int userId = 0;
    private SharedPreferences sp;

    private Boolean space = false;
    private Disposable disposable;

    public MessagesController() {
    }

    public MessagesController(Chat chat) {
        this.chat = chat;
    }

    public MessagesController(Chat chat, int userId) {
        this.chat = chat;
        this.userId = userId;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.messages_controller, container, false);
        ButterKnife.bind(this, view);

        bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        backButton.setColorFilter(Color.argb(255, 255, 255, 255));
        backButton.setOnClickListener(v -> {
                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(backButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    for (RouterTransaction routerTransaction : getRouter().getBackstack()) {
                        try {
                            if (routerTransaction.controller() == getRouter().getBackstack().get(2).controller()) {
//                        bottomNavigationView.setVisibility(View.GONE);
                                getRouter().popCurrentController();
                                return;
                            }
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                    }
//                    bottomNavigationView.setVisibility(View.VISIBLE);
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

        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);
        coverImageView.setOnClickListener(v -> getRouter().pushController(RouterTransaction.with(new ProfileController(userId))
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler())));

        getUserId();
        disposable = Observable.interval(1, 5,
                TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callMessagesEndpoint, this::onError);

        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    private void getUserId() {
        Integer userIdSP = sp.getInt("userId", -1);
        if (userId != 0) {
            getProfile();
            return;
        }
        Call<ChatUsers> chatUsersCall = DataManager.getInstance().searchUsersInChat(chat.getId());
        chatUsersCall.enqueue(new Callback<ChatUsers>() {
            @Override
            public void onResponse(@NotNull Call<ChatUsers> call, @NotNull Response<ChatUsers> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Integer> users = response.body().getUsers();
                    for (Integer curUserId : users) {
                        if (curUserId.equals(userIdSP)) {
                            continue;
                        }
                        userId = curUserId;
                        getProfile();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ChatUsers> call, @NotNull Throwable t) {
            }
        });
    }

    private void getProfile() {
        Call<UserModel> call = DataManager.getInstance().getUser(userId);
        call.enqueue(new Callback<UserModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    User user = response.body().getUser();
                    assert user != null;
                    nameAndSurname.setText(user.getName() + " " + user.getSurname());
                    if (user.getStatus() == 0) {
                        textStatus.setText(Objects.requireNonNull(getResources()).getString(R.string.offline));
                        textStatus.setTextColor(getResources().getColor(R.color.colorError, Objects.requireNonNull(getActivity()).getTheme()));
                    } else {
                        textStatus.setText(Objects.requireNonNull(getResources()).getString(R.string.online));
                        textStatus.setTextColor(getResources().getColor(R.color.colorText, Objects.requireNonNull(getActivity()).getTheme()));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {

            }
        });
    }

    @OnClick(R.id.send)
    void sendMessage() {
        if (!footerChatEditText.getText().toString().equals("")) {
            Integer userIdSP = sp.getInt("userId", -1);
            String txt = footerChatEditText.getText().toString().trim();
            if (txt.equals("")) {
                return;
            }
            if (chat.getId() == -1) {
                Chat newChat = new Chat();
                newChat.setLastMessage(txt);
                newChat.setTypeOfChat(0);
                newChat.setUserAuthorId(userIdSP);
                newChat.setUserOtherId(userId);
                Call<Status> chatCall = DataManager.getInstance().createChat(newChat);
                chatCall.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                        if (response.isSuccessful()) {
                            Status post = response.body();
                            assert post != null;
                            chat.setId(Integer.parseInt(post.getText().substring(0, post.getText().length() - 8)));
                            sendMessage();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                    }
                });
                return;
            }
            Message message = new Message(txt, userIdSP, chat.getId());
            Call<Status> call = DataManager.getInstance().createMessage(message);
            call.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                    if (response.isSuccessful()) {
                        Status status = response.body();
                        Message updateMessage = new Message();
                        updateMessage.setStatus(2);
                        assert status != null;
                        Call<Status> updateMessageCall = DataManager.getInstance().editMessage(Integer.parseInt(status.getText().substring(0, status.getText().length() - 8)), updateMessage);
                        updateMessageCall.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {

                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                            }
                        });
                        Chat chat1 = new Chat();
                        chat1.setLastMessage(txt);
                        Call<Status> updateChatCall = DataManager.getInstance().editChat(chat.getId(), chat1);
                        updateChatCall.enqueue(new Callback<Status>() {
                            @Override
                            public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                            }

                            @Override
                            public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                }
            });
            footerChatEditText.setText("");
        }
    }

    @OnClick(R.id.footer_chat_edit_text)
    void typeText() {
        messagesRecycler.smoothScrollToPosition(Objects.requireNonNull(messagesRecycler.getAdapter()).getItemCount() - 1);
    }

    @SuppressLint("CheckResult")
    private void callMessagesEndpoint(Long aLong) {
        Observable<Messages> observable = DataManager.getInstance().getMessages(chat.getId());
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .map(Messages::getMessages)
                .subscribe(this::handleResults, this::handleError);
    }

    private void handleResults(List<Message> messages) {
        progressBar.setVisibility(View.GONE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);
        messagesRecycler.setLayoutManager(mLayoutManager);
        messagesRecycler.setItemAnimator(new DefaultItemAnimator());
        ArrayList<Message> mMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            TimeFormatter timeFormater = new TimeFormatter(message.getCreatedDate());
            if (message.getAuthorId().equals(-1)) {
                continue;
            }
            if (i == 0) {
                String helperText = new TimeFormatter(message.getCreatedDate()).timeForMessageSeparator(getApplicationContext());
                mMessages.add(new Message(helperText, timeFormater.onFewEarlier(), -1));
            } else {
                if (!timeFormater.compareDatesDays(messages.get(i - 1).getCreatedDate())) {
                    String helperText = new TimeFormatter(message.getCreatedDate()).timeForMessageSeparator(getApplicationContext());
                    mMessages.add(new Message(helperText, timeFormater.onFewEarlier(), -1));
                }
                if (timeFormater.compareDates(messages.get(i - 1).getCreatedDate()) && !message.getAuthorId().equals(-1)) {
                    mMessages.add(new Message(message.getText(), message.getCreatedDate(), message.getAuthorId(), false));
                    continue;
                }
            }
            mMessages.add(new Message(message.getText(), message.getCreatedDate(), message.getAuthorId()));
        }
        Collections.sort(mMessages, Message.COMPARE_BY_TIME);
        messageAdapter = new MessageAdapter(mMessages, getApplicationContext());
        messagesRecycler.setAdapter(messageAdapter);
        if (!space) {
            messagesRecycler.addItemDecoration(new SpacesItemDecoration(20));
            space = true;
        }
        if (mMessages.size() > 0) {
            messagesRecycler.scrollToPosition(mMessages.size() - 1);
        }
    }


    private void onError(Throwable throwable) {
        assert getView() != null;
        Snackbar.make(getView(), Objects.requireNonNull(getResources()).getString(R.string.error), Snackbar.LENGTH_LONG).show();
    }

    private void handleError(Throwable t) {
        assert getView() != null;
        if (t instanceof SocketTimeoutException) {
            Snackbar.make(getView(), Objects.requireNonNull(getResources()).getString(R.string.errors_with_connection), Snackbar.LENGTH_LONG).show();
        } else if (t instanceof HttpException) {
            progressBar.setVisibility(View.GONE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            messagesRecycler.setLayoutManager(mLayoutManager);
            messagesRecycler.setItemAnimator(new DefaultItemAnimator());
            ArrayList<Message> mMessages = new ArrayList<>();
            messageAdapter = new MessageAdapter(mMessages, getApplicationContext());
            messagesRecycler.setAdapter(messageAdapter);
        }
    }

    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
        space = false;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        if (disposable.isDisposed()) {
            disposable = Observable.interval(1, 5,
                    TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::callMessagesEndpoint, this::onError);
        }
        try {
            messagesRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);
        disposable.dispose();
    }

    @Override
    public boolean handleBack() {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        for (RouterTransaction routerTransaction : getRouter().getBackstack()) {
            try {
                if (routerTransaction.controller() == getRouter().getBackstack().get(2).controller()) {
                    bottomNavigationView.setVisibility(View.GONE);
                    return super.handleBack();
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }
}
