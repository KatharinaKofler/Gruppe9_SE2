package com.example.gruppe9_se2.api.base;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static Retrofit instance = null;
    private static final String BASE_URL = "https://gruppe9-se2-backend.herokuapp.com/";
    public static String token;

    private ApiManager() {
    }

    public static synchronized Retrofit getInstance() {
        if (ApiManager.instance == null) {
            ApiManager.instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return ApiManager.instance;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        ApiManager.token = token;
    }
}
