package com.example.gruppe9_se2.logic;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.user.Lobby;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public class GameStart extends AppCompatActivity {


    private Socket mSocket;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamestart);

        // Get JWT from ApiManager
        String jwt = ApiManager.getToken();
        // Get lobby Id
        Bundle b = getIntent().getExtras();
        String lobbyID = b.getString("LobbyID");


        // Build Options with extra Headers
        HashMap<String, List<String>> extraHeaders = new HashMap<>();
        extraHeaders.put("x-jwt", singletonList(jwt));
        extraHeaders.put("x-lobby-id", singletonList(lobbyID));

        IO.Options options = IO.Options.builder().setExtraHeaders(extraHeaders).build();

        // Create Socket
        URI uri = URI.create("https://gruppe9-se2-backend.herokuapp.com/");

        mSocket = IO.socket(uri, options);

        // check for successfull connection
        mSocket.on("connect", args -> {
            Log.i("myLogs", "Connection successfull");
        });

        // error if connect failed
        mSocket.on("connect_failed", args -> {
            Log.e("myLogs", "connect_failed");
        });
        

        mSocket.connect();

    }


}
