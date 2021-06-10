package com.example.gruppe9_se2.api.users;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsersApi {
    @POST("users/{id}")
    Call<UsersResponse> executeUsers(
            @Path("id") String id,
            @Header("Authorization") String token
    );
}