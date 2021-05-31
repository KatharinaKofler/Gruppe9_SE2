package com.example.gruppe9_se2.logic;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

import static java.util.Collections.singletonList;

public class SocketManager {

    private static Socket instance;

    public static Socket makeSocket(String lobbyId){
        if(instance == null){

            // Get JWT from ApiManager
            String jwt = ApiManager.getToken();

            // Build Options with extra Headers
            HashMap<String, List<String>> extraHeaders = new HashMap<>();
            extraHeaders.put("x-jwt", singletonList(jwt));
            extraHeaders.put("x-lobby-id", singletonList(lobbyId));

            IO.Options options = IO.Options.builder().setExtraHeaders(extraHeaders).build();

            // Create Socket
            // URI uri = URI.create("https://gruppe9-se2-backend.herokuapp.com/");
            URI uri = URI.create("http://localhost:8080/");

            instance = IO.socket(uri, options);
        }
        return instance;
    }

    public static Socket getSocket(){
        return instance;
    }



}
