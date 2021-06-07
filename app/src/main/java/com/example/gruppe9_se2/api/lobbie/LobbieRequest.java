package com.example.gruppe9_se2.api.lobbie;

import com.google.gson.annotations.SerializedName;

public class LobbieRequest {

    @SerializedName("token")
    public String token;

    public LobbieRequest(String token) {
        this.token = token;
    }
}
