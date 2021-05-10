package com.example.gruppe9_se2.api.register;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterApi {
    @POST("auth/register")
    Call<RegisterResponse> executeRegister(
            @Body RegisterRequest request
    );
}
