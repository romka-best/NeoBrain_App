package com.example.neobrain.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.example.neobrain.API.model.App;
import com.example.neobrain.API.model.Chat;
import com.example.neobrain.API.model.Music;
import com.example.neobrain.API.model.Photo;
import com.example.neobrain.API.model.User;
import com.example.neobrain.Controllers.MessagesController;
import com.example.neobrain.Controllers.ProfileController;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.example.neobrain.utils.BaseViewHolder;
import com.example.neobrain.utils.TimeFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class SearchAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "SearchAdapter";
    private static final short VIEW_TYPE_EMPTY = 0;
    private static final short VIEW_TYPE_NORMAL = 1;
    private static final short VIEW_TYPE_PEOPLE = 2;
    private static final short VIEW_TYPE_GROUPS = 3;
    private static final short VIEW_TYPE_CHATS = 4;
    private static final short VIEW_TYPE_MUSIC = 5;
    private static final short VIEW_TYPE_APPS = 6;
    private static final short VIEW_TYPE_NOT_FOUND = 7;


    private List<Object> mAllList;
    private List<User> mPersonList;
    //    private List<Group> mGroupsList;
    private List<Chat> mChatList;
    private List<Music> mMusicList;
    private List<App> mAppsList;

    private Chat chat;
    private Router mRouter;
    private boolean found = false;

    public SearchAdapter(Router router) {
        this.mRouter = router;
    }

    public SearchAdapter(Router router, boolean found) {
        this.mRouter = router;
        this.found = found;
    }

    public List<User> getPersonList() {
        return mPersonList;
    }

    public void setPersonList(List<User> mPersonList) {
        this.mPersonList = mPersonList;
    }

    public List<Chat> getChatList() {
        return mChatList;
    }

    public void setChatList(List<Chat> mChatList) {
        this.mChatList = mChatList;
    }

    public List<Object> getAllList() {
        return mAllList;
    }

    public void setAllList(List<Object> mAllList) {
        this.mAllList = mAllList;
    }

    public List<Music> getMusicList() {
        return mMusicList;
    }

    public void setMusicList(List<Music> mMusicList) {
        this.mMusicList = mMusicList;
    }

    public List<App> getAppsList() {
        return mAppsList;
    }

    public void setAppsList(List<App> mAppsList) {
        this.mAppsList = mAppsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                // TODO
                return new AllViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_empty_item_all, parent, false));
            case VIEW_TYPE_PEOPLE:
                return new PeopleViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_people, parent, false));
            case VIEW_TYPE_GROUPS:
                return new GroupViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_group, parent, false));
            case VIEW_TYPE_CHATS:
                return new ChatViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_chat, parent, false));
            case VIEW_TYPE_MUSIC:
                return new MusicViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_music, parent, false));
            case VIEW_TYPE_APPS:
                return new AppViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_item_app, parent, false));
            case VIEW_TYPE_NOT_FOUND:
                return new NotFoundViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_not_found_item, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                // TODO
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_view_empty_item_all, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public int getItemViewType(int position) {
        if (mAllList != null && mAllList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else if (mPersonList != null && mPersonList.size() > 0) {
            return VIEW_TYPE_PEOPLE;
        }
//        else if(mGroupsList != null && mGroupsList.size() > 0){
//            return VIEW_TYPE_GROUPS;
//        }
        else if (mChatList != null && mChatList.size() > 0) {
            return VIEW_TYPE_CHATS;
        } else if (mMusicList != null && mMusicList.size() > 0) {
            return VIEW_TYPE_MUSIC;
        } else if (mAppsList != null && mAppsList.size() > 0) {
            return VIEW_TYPE_APPS;
        } else {
            if (found) {
                return VIEW_TYPE_EMPTY;
            } else {
                return VIEW_TYPE_NOT_FOUND;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mAllList != null && mAllList.size() > 0) {
            return mAllList.size();
        } else if (mPersonList != null && mPersonList.size() > 0) {
            return mPersonList.size();
        }
//        else if(mGroupsList != null && mGroupsList.size() > 0){
//            return mGroupsList.size();
//        }
        else if (mChatList != null && mChatList.size() > 0) {
            return mChatList.size();
        } else if (mMusicList != null && mMusicList.size() > 0) {
            return mMusicList.size();
        } else if (mAppsList != null && mAppsList.size() > 0) {
            return mAppsList.size();
        } else {
            return 1;
        }
    }

    public void addItems(ArrayList<Object> arrayList) {
        // TODO
        notifyDataSetChanged();
    }

    public class AllViewHolder extends BaseViewHolder {
        public AllViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }

    public class PeopleViewHolder extends BaseViewHolder {
        @BindView(R.id.title)
        TextView titleTextView;

        @BindView(R.id.avatar)
        ImageView avatarImageView;

        @BindView(R.id.city_age_gender)
        TextView textTextView;

        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void onBind(int position) {
            super.onBind(position);
            final User mPerson = mPersonList.get(position);
            if (mPerson.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mPerson.getPhotoId());
                call.enqueue(new retrofit2.Callback<Photo>() {
                    @Override
                    public void onResponse(@NotNull Call<Photo> call, @NotNull Response<Photo> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            String photo = response.body().getPhoto();
                            byte[] decodedString = Base64.decode(photo.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            avatarImageView.setImageBitmap(decodedByte);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Photo> call, @NotNull Throwable t) {
                        Log.e(TAG, "Чёрт...");
                    }
                });
            }
            if (mPerson.getName() != null && mPerson.getSurname() != null) {
                titleTextView.setText(mPerson.getName() + " " + mPerson.getSurname());
            }
            List<String> cityAgeGender = new ArrayList<>();
            if (mPerson.getRepublic() != null) {
                cityAgeGender.add(mPerson.getRepublic());
            }
            if (mPerson.getCity() != null) {
                cityAgeGender.add(mPerson.getCity());
            }
            if (mPerson.getAge() != null) {
                cityAgeGender.add(mPerson.getAge().toString());
            }
            if (mPerson.getGender() != null) {
                if (mPerson.getGender() == 0) {
                    cityAgeGender.add(Objects.requireNonNull(mRouter.getActivity()).getResources().getString(R.string.gender_w));
                } else
                    cityAgeGender.add(Objects.requireNonNull(mRouter.getActivity()).getResources().getString(R.string.gender_m));
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < cityAgeGender.size(); i++) {
                if (i != cityAgeGender.size() - 1) {
                    sb.append(cityAgeGender.get(i)).append(" | ");
                } else {
                    sb.append(cityAgeGender.get(i));
                }
            }
            textTextView.setText(sb);

            itemView.setOnClickListener(v -> {
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(mRouter.getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(itemView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mRouter.pushController(RouterTransaction.with(new ProfileController(mPerson.getId()))
                        .popChangeHandler(new FadeChangeHandler())
                        .pushChangeHandler(new FadeChangeHandler()));
            });
        }

        @Override
        protected void clear() {
            avatarImageView.setImageBitmap(null);
            titleTextView.setText("");
            textTextView.setText("");
        }
    }

    public class GroupViewHolder extends BaseViewHolder {
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }

    public class ChatViewHolder extends BaseViewHolder {
        @BindView(R.id.avatar)
        ImageView coverImageView;
        @BindView(R.id.title)
        TextView titleTextView;
        @BindView(R.id.lastMessage)
        TextView lastMessTextView;
        @BindView(R.id.time)
        TextView timeTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
            final Chat mChat = mChatList.get(position);
            if (mChat.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mChat.getPhotoId());
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
                        Log.e("E", "Чёрт...");
                    }
                });
            }

            if (mChat.getLastMessage() != null) {
                lastMessTextView.setText(mChat.getLastMessage());
            }
            if (mChat.getLastTimeMessage() != null) {
                timeTextView.setText(new TimeFormatter(mChat.getLastTimeMessage()).timeForChat(itemView.getContext()));
            }
            if (mChat.getName() != null) {
                titleTextView.setText(mChat.getName());
            }
            itemView.setOnClickListener(v -> {
                chat = mChatList.get(this.getCurrentPosition());
                BottomNavigationView bottomNavigationView = Objects.requireNonNull(mRouter.getActivity()).findViewById(R.id.bottom_navigation);
                bottomNavigationView.setVisibility(View.GONE);
                mRouter.pushController(RouterTransaction.with(new MessagesController(chat))
                        .popChangeHandler(new FadeChangeHandler())
                        .pushChangeHandler(new FadeChangeHandler()));
            });
        }

        @Override
        protected void clear() {
            coverImageView.setImageDrawable(null);
            titleTextView.setText("");
            lastMessTextView.setText("");
            timeTextView.setText("");
        }
    }

    public class MusicViewHolder extends BaseViewHolder {
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }

    public class AppViewHolder extends BaseViewHolder {
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }

    public class EmptyViewHolder extends BaseViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }

    public class NotFoundViewHolder extends BaseViewHolder {
        public NotFoundViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        protected void clear() {

        }
    }
}
