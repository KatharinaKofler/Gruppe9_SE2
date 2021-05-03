package com.example.gruppe9_se2.api.register;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
