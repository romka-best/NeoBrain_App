package com.itschool.neobrain.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.itschool.neobrain.API.models.App;
import com.itschool.neobrain.API.models.Apps;
import com.itschool.neobrain.API.models.Chat;
import com.itschool.neobrain.API.models.ChatUsers;
import com.itschool.neobrain.API.models.Chats;
import com.itschool.neobrain.API.models.User;
import com.itschool.neobrain.API.models.UserModel;
import com.itschool.neobrain.API.models.Users;
import com.itschool.neobrain.DataManager;
import com.itschool.neobrain.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.itschool.neobrain.MainActivity.MY_SETTINGS;

/* Основной и определяющий контроллер для поиска */
@SuppressLint("ValidController")
public class SearchController extends Controller {
    private String TAG = "SearchController";

    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private short currentItem;
    private final RouterPagerAdapter pagerAdapter;
    private SharedPreferences sp;

    private ArrayList<Chat> mChats;

    /* Конструктор создания раздела поиска */
    public SearchController() {
        currentItem = 0;
        // Обрабатываем нужный раздел поиска
        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            router.setRoot(RouterTransaction.with(new SearchAllController()));
                            break;
                        case 1:
                            router.setRoot(RouterTransaction.with(new SearchPeopleController()));
                            break;
                        case 2:
                            router.setRoot(RouterTransaction.with(new SearchGroupsController()));
                            break;
                        case 3:
                            router.setRoot(RouterTransaction.with(new SearchChatsController()));
                            break;
                        case 4:
                            router.setRoot(RouterTransaction.with(new SearchMusicController()));
                            break;
                        case 5:
                            router.setRoot(RouterTransaction.with(new SearchAppsController()));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 6;
            }

            /* Метод, определяющий названия под контроллеров поиска у пользователя */
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return Objects.requireNonNull(getResources()).getString(R.string.all);
                    case 1:
                        return Objects.requireNonNull(getResources()).getString(R.string.people);
                    case 2:
                        return Objects.requireNonNull(getResources()).getString(R.string.groups);
                    case 3:
                        return Objects.requireNonNull(getResources()).getString(R.string.chats);
                    case 4:
                        return Objects.requireNonNull(getResources()).getString(R.string.music);
                    case 5:
                        return Objects.requireNonNull(getResources()).getString(R.string.apps);
                    default:
                        return "Page " + position;
                }
            }
        };
    }

    /* Конструктор, переключающий на нужный контроллер поиска, в зависимости от переданного значения */
    public SearchController(short currentItem) {
        this.currentItem = currentItem;
        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            router.setRoot(RouterTransaction.with(new SearchAllController()));
                            break;
                        case 1:
                            router.setRoot(RouterTransaction.with(new SearchPeopleController()));
                            break;
                        case 2:
                            router.setRoot(RouterTransaction.with(new SearchGroupsController()));
                            break;
                        case 3:
                            router.setRoot(RouterTransaction.with(new SearchChatsController()));
                            break;
                        case 4:
                            router.setRoot(RouterTransaction.with(new SearchMusicController()));
                            break;
                        case 5:
                            router.setRoot(RouterTransaction.with(new SearchAppsController()));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 6;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return Objects.requireNonNull(getResources()).getString(R.string.all);
                    case 1:
                        return Objects.requireNonNull(getResources()).getString(R.string.people);
                    case 2:
                        return Objects.requireNonNull(getResources()).getString(R.string.groups);
                    case 3:
                        return Objects.requireNonNull(getResources()).getString(R.string.chats);
                    case 4:
                        return Objects.requireNonNull(getResources()).getString(R.string.music);
                    case 5:
                        return Objects.requireNonNull(getResources()).getString(R.string.apps);
                    default:
                        return "Page " + position;
                }
            }
        };
    }

    /* Метод, отвечающий за выведение результатов поиска */
    @SuppressLint("CheckResult")
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_controller, container, false);
        ButterKnife.bind(this, view);
        sp = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(MY_SETTINGS,
                Context.MODE_PRIVATE);

        // Подключаем адаптер
        viewPager.setAdapter(pagerAdapter);
        // Устанавливаем в нём нужное значение
        viewPager.setCurrentItem(currentItem);

        tabLayout.setupWithViewPager(viewPager);
        searchView.setIconified(false);

        // Создаём объект реактивного програмирования и "подписываем" его на изменения в поиске
        Observable.create((ObservableOnSubscribe<String>) emitter -> searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                emitter.onNext(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                emitter.onNext(newText);
                return false;
            }
        })).map(String::trim)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String text) throws Exception {
                        if (text.isEmpty()) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
                .distinctUntilChanged()
                .switchMap((Function<String, ObservableSource<String>>) this::dataFromNetwork)
                .subscribe();

        return view;
    }

    /* Метод для поиска по всем имеющимся данным */
    private ObservableSource<String> dataFromNetwork(String query) {
        return new Observable<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                String[] s = query.toLowerCase().trim().split(" ");
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        break;
                    case 1:
                        List<String> strings = Arrays.asList(s);
                        StringJoiner joiner = new StringJoiner("&");
                        if (strings.size() >= 2) {
                            joiner.add(strings.get(0));
                            joiner.add(strings.get(1));
                        } else if (strings.size() == 1) {
                            joiner.add(strings.get(0));
                        }
                        ArrayList<User> mUsers = new ArrayList<>();
                        Call<Users> call = DataManager.getInstance().searchUser(String.valueOf(joiner));
                        call.enqueue(new Callback<Users>() {
                            @Override
                            public void onResponse(@NotNull Call<Users> call, @NotNull Response<Users> response) {
                                assert response.body() != null;
                                List<User> users = response.body().getUsers();
                                for (User user : users) {
                                    mUsers.add(new User(user.getId(), user.getPhotoId(), user.getName(), user.getSurname(), user.getRepublic(), user.getCity(), user.getAge(), user.getGender()));
                                }
                                Objects.requireNonNull(pagerAdapter.getRouter(1)).setRoot(RouterTransaction.with(new SearchPeopleController(mUsers, getRouter(), users.size() != 0)));
                            }

                            @Override
                            public void onFailure(@NotNull Call<Users> call, @NotNull Throwable t) {

                            }
                        });
                        break;
                    case 2:
                        break;
                    case 3:
                        Integer userIdSP = sp.getInt("userId", -1);
                        mChats = new ArrayList<>();
                        Call<Chats> chatsCall = DataManager.getInstance().getChats(userIdSP);
                        chatsCall.enqueue(new Callback<Chats>() {
                            @Override
                            public void onResponse(@NotNull Call<Chats> call, @NotNull Response<Chats> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    final List<Chat> chats = response.body().getChats();
                                    if (chats.size() == 0) {
                                        String chatString = String.join(" ", s);
                                        Objects.requireNonNull(pagerAdapter.getRouter(3)).setRoot(RouterTransaction.with(new SearchChatsController(mChats, chatString, getRouter())));
                                        return;
                                    }
                                    for (Chat chat : chats) {
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
                                                                        String curNameChat = user.getName() + " " + user.getSurname();
                                                                        Integer curPhotoId = user.getPhotoId();
                                                                        for (Chat queueChat : mChats) {
                                                                            if (queueChat.getId().equals(chat.getId())) {
                                                                                return;
                                                                            }
                                                                        }
                                                                        mChats.add(new Chat(chat.getId(), chat.getLastMessage(), chat.getLastTimeMessage(), curNameChat, curPhotoId));
                                                                        if (chats.size() == mChats.size()) {
                                                                            String chatString = String.join(" ", s);
                                                                            Objects.requireNonNull(pagerAdapter.getRouter(3)).setRoot(RouterTransaction.with(new SearchChatsController(mChats, chatString, getRouter())));
                                                                        }
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
                                            mChats.add(new Chat(chat.getId(), chat.getLastMessage(), chat.getLastTimeMessage(), chat.getName(), chat.getPhotoId()));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Chats> call, @NotNull Throwable t) {
                                Snackbar.make(getView(), R.string.errors_with_connection, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case 4:
                        break;
                    case 5:
                        Call<Apps> appsCall = DataManager.getInstance().searchApp(String.join(" ", s));
                        ArrayList<App> mApps = new ArrayList<>();
                        appsCall.enqueue(new Callback<Apps>() {
                            @Override
                            public void onResponse(@NotNull Call<Apps> call, @NotNull Response<Apps> response) {
                                assert response.body() != null;
                                List<App> apps = response.body().getApps();
                                for (App app : apps) {
                                    mApps.add(new App(app.getId(), app.getTitle(), app.getSecondaryText(), app.getDescription(), app.getLinkAndroid(), app.getPhotoId(), false));
                                }
                                Objects.requireNonNull(pagerAdapter.getRouter(5)).setRoot(RouterTransaction.with(new SearchAppsController(mApps, getRouter(), apps.size() != 0)));
                            }

                            @Override
                            public void onFailure(@NotNull Call<Apps> call, @NotNull Throwable t) {

                            }
                        });
                        break;
                }
            }
        };
    }

    /* Метод, определяющий поведение при нажатии на кнопку "назад" на устройстве */
    @Override
    public boolean handleBack() {
        // Показываем BottomNavigationView
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }

    /* Вызывается, когда контроллер связывается с активностью */
    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        // Пробуем скрыть BottomNavigationView, если уже скрыта, ставим заглушку
        try {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
        } catch (NullPointerException ignored) {
        }
    }
}
