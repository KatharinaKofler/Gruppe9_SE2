package com.example.gruppe9_se2.api.lobbie;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LobbieApi {

    @POST("lobbies")
    Call<LobbieResponse> executeLobby(
            @Header("Authorization") String token ,
            @Body LobbieRequest request
    );

}
