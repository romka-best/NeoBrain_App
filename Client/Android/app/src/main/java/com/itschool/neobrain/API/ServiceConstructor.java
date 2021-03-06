package com.itschool.neobrain.API;

// Импортируем нужные библиотеки

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/* Конструктор для работы с Retrofit */
public class ServiceConstructor {
    private static final String BASE_URL = "https://neobrain.herokuapp.com/api/";

    public static <T> T CreateService(Class<T> serviceClass) {

        // Настройка уровня логирования
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Клиент, осуществляющий все HTTP-запросы
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .authenticator(new TokenAuthenticator())
                .build();

        // Объект класса Gson, осуществляющий работу с файлами этого формата
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Первичное создание паттерна проектирования Builder,
        // класса Retrofit, для упрощения работы с серверными запросами
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(serviceClass);
    }
}
