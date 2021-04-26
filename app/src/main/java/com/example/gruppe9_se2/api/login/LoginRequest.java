package com.example.gruppe9_se2.api.login;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;
}
