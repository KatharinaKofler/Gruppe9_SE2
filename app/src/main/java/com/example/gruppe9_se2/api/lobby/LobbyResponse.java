package com.example.gruppe9_se2.api.lobby;

import com.google.gson.annotations.SerializedName;

public class LobbyResponse {
    @SerializedName("id")
    public String id;

    @SerializedName("owner")
    public String owner;

    @SerializedName("ownerId")
    public String ownerId;

    public String getId(){
        return id;
    }
    public String getOwner(){
        return owner;
    }
    public String getOwnerId(){
        return ownerId;
    }
}

