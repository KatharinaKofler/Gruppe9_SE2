package com.example.gruppe9_se2.api.lobby;

import com.google.gson.annotations.SerializedName;

public class LobbyRequest {

    @SerializedName("token")
    public String token;

    public LobbyRequest(String token) {
        this.token = token;
    }
}
