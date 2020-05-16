package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.example.neobrain.API.model.User;
import com.example.neobrain.API.model.Users;
import com.example.neobrain.DataManager;
import com.example.neobrain.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

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

    public SearchController() {
        currentItem = 0;
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

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.search_controller, container, false);
        ButterKnife.bind(this, view);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentItem);
        tabLayout.setupWithViewPager(viewPager);
        searchView.setIconified(false);

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

    private ObservableSource<String> dataFromNetwork(String query) {
        return new Observable<String>() {
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
                        List<String> chatStrings = Arrays.asList(s);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                }
            }
        };
    }

    @Override
    public boolean handleBack() {
        BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        return super.handleBack();
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        try {
            BottomNavigationView bottomNavigationView = Objects.requireNonNull(getRouter().getActivity()).findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
        } catch (NullPointerException ignored) {
        }
    }
}
