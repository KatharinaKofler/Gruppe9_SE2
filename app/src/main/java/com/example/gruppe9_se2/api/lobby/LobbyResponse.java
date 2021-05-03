package com.example.gruppe9_se2.api.lobby;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class LobbyResponse {
    @SerializedName("lobbyState")
    public JsonObject lobbyState;
}
