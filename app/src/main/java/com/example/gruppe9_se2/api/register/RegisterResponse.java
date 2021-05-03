package com.example.gruppe9_se2.api.register;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class RegisterResponse {
    @SerializedName("user")
    public JSONObject user;
}
