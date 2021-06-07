package com.example.gruppe9_se2.api.lobbieGet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface LobbieGetApi {

    @GET("lobbies")
    Call<ArrayList<Lobbies>> executeLobbyGet(
            @Header("Authorization") String token
    );
}
