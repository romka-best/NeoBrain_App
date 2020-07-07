package com.itschool.neobrain.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.itschool.neobrain.API.models.App;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.API.models.ChatModel;
import com.itschool.neobrain.API.models.Music;
import com.itschool.neobrain.API.models.Photo;
import com.itschool.neobrain.API.models.Status;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserApp;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;
import com.itschool.neobrain.controllers.MessagesController;
import com.itschool.neobrain.controllers.ProfileController;
import com.itschool.neobrain.utils.BaseViewHolder;
import com.itschool.neobrain.utils.TimeFormatter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

public class SearchAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {
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
    private List<Chat> mFilteredList;
    private ItemFilter mFilter = new ItemFilter();
    private List<Music> mMusicList;
    private List<App> mAppsList;

    private Chat chat;
    private Router mRouter;
    private SharedPreferences sp;
    private boolean found = false;

    public SearchAdapter(Router router) {
        this.mRouter = router;
    }

    public SearchAdapter(Router router, boolean found) {
        this.mRouter = router;
        this.found = found;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
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
        this.mFilteredList = mChatList;
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

        @BindView(R.id.doButton)
        ImageButton doButton;

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

            sp = Objects.requireNonNull(mRouter.getActivity()).getSharedPreferences(MY_SETTINGS,
                    Context.MODE_PRIVATE);

            if (mPerson.getId() == sp.getInt("userId", -1)) {
                doButton.setVisibility(View.GONE);
            }

            doButton.setOnClickListener(v -> {
                Call<ChatModel> chatModelCall = DataManager.getInstance().getUsersChat(sp.getInt("userId", -1), mPerson.getId());
                chatModelCall.enqueue(new Callback<ChatModel>() {
                    @Override
                    public void onResponse(@NotNull Call<ChatModel> call, @NotNull Response<ChatModel> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            Chat chat = response.body().getChat();
                            if (chat == null) {
                                chat = new Chat();
                                chat.setId(-1);
                            }
                            chat.setPhotoId(mPerson.getPhotoId());
                            mRouter.pushController(RouterTransaction.with(new MessagesController(chat, mPerson.getId())));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ChatModel> call, @NotNull Throwable t) {

                    }
                });
            });

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
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(mRouter.getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(itemView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Chat> list = mChatList;

            int count = list.size();
            final ArrayList<Chat> newList = new ArrayList<>(count);

            Chat filterableChat;

            for (int i = 0; i < count; i++) {
                filterableChat = list.get(i);
                if (filterableChat.getName().toLowerCase().contains(filterString)) {
                    newList.add(filterableChat);
                }
            }

            results.values = newList;
            results.count = newList.size();

            mChatList = newList;

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredList = (ArrayList<Chat>) results.values;
            notifyDataSetChanged();
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
        @BindView(R.id.image)
        ImageView coverImageView;
        @BindView(R.id.title)
        TextView titleTextView;
        @BindView(R.id.secondaryText)
        TextView secondaryTextView;
        @BindView(R.id.description)
        TextView descriptionTextView;
        @BindView(R.id.button)
        MaterialButton button;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(int position) {
            super.onBind(position);
            final App mApp = mAppsList.get(position);

            if (mApp.getPhotoId() != null) {
                Call<Photo> call = DataManager.getInstance().getPhoto(mApp.getPhotoId());
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
                        Log.e(TAG, "Чёрт...");
                    }
                });
            }

            if (mApp.getTitle() != null) {
                titleTextView.setText(mApp.getTitle());
            }
            if (mApp.getSecondaryText() != null) {
                secondaryTextView.setText(mApp.getSecondaryText());
            }
            if (mApp.getDescription() != null) {
                descriptionTextView.setText(mApp.getDescription());
            }
            if (mApp.getAdded()) {
                button.setText(R.string.delete);
            } else {
                button.setText(R.string.add);
            }
            itemView.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mApp.getLinkAndroid()));
                Objects.requireNonNull(mRouter.getActivity()).startActivity(browserIntent);
            });
            button.setOnClickListener(v -> {
                sp = Objects.requireNonNull(mRouter.getActivity()).getSharedPreferences(MY_SETTINGS,
                        Context.MODE_PRIVATE);
                Integer userIdSP = sp.getInt("userId", -1);
                if (mApp.getAdded()) {
                    Call<Status> call = DataManager.getInstance().deleteApp(userIdSP, mApp.getId());
                    call.enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                            if (response.isSuccessful()) {
                                mAppsList.remove(mApp);
                                notifyItemRemoved(position);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                        }
                    });
                } else {
                    UserApp userApp = new UserApp(userIdSP, mApp.getId());
                    Call<Status> call = DataManager.getInstance().addApp(userApp);
                    call.enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(@NotNull Call<Status> call, @NotNull Response<Status> response) {
                            if (response.isSuccessful()) {
                                // TODO корректно обновить recyclerview
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<Status> call, @NotNull Throwable t) {

                        }
                    });
                }
            });
        }

        @Override
        protected void clear() {
            coverImageView.setImageDrawable(null);
            titleTextView.setText("");
            secondaryTextView.setText("");
            descriptionTextView.setText("");
            button.setText("");
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

    public static class NotFoundViewHolder extends BaseViewHolder {
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
