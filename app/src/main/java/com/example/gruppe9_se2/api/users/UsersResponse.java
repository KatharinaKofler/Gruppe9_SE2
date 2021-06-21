package com.example.gruppe9_se2.api.users;

import com.google.gson.annotations.SerializedName;

public class UsersResponse {
    @SerializedName("username")
    public String username;

    public String getUsername() {
        return username;
    }
}