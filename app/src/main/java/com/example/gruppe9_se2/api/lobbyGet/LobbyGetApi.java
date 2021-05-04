package com.example.gruppe9_se2.api.lobbyGet;

import com.example.gruppe9_se2.api.lobby.LobbyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface LobbyGetApi {

    @GET("lobbies")
    Call<LobbyResponse> executeLobbyGet(
            @Header("Authorization") String token
    );
}
