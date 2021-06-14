package com.example.gruppe9_se2.api.users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface UsersApi {
    @GET("users/{id}")
    Call<UsersResponse> executeUsers(
            @Path("id") String id,
            @Header("Authorization") String token
    );
}