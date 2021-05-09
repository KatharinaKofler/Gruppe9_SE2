package com.example.gruppe9_se2.api.base;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;

import java.io.IOException;

public class ApiHelper {
    public static String getErrorMessage(Response response) {
        try {
            String body = response.errorBody().string();
            try {
                new JSONObject(body);
                Gson gson = new Gson();
                ApiError error = gson.fromJson(body, ApiError.class);
                return error.getMessage();
            } catch (JSONException e) {
                return body;
            }
        } catch (IOException ignored) {
        }

        return null;
    }
}
