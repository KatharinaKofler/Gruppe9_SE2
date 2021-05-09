package com.example.gruppe9_se2.api.lobby;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LobbyApi {

    @POST("lobbies")
    Call<LobbyResponse> executeLobby(
            @Header("Authorization") String token ,
            @Body LobbyRequest request
    );

}
