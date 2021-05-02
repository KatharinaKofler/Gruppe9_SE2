package com.example.gruppe9_se2.api.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {
    @POST("auth/login")
    Call<LoginResponse> executeLogin(
            @Body LoginRequest request
    );
}
