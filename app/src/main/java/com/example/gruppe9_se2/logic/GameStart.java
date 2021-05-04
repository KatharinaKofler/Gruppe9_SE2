package com.example.gruppe9_se2.logic;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;

import java.net.URI;

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

        //Migrate LobbyID from LobbyOverviewFragment
        Bundle b = getIntent().getExtras();
        String lobbyID = b.getString("LobbyID");

        //Open Web Socket

        IO.Options options = IO.Options.builder()
                .setExtraHeaders(singletonMap("x-lobby-id", singletonList(lobbyID)))
                .build();

        mSocket = IO.socket(URI.create("https://gruppe9-se2-backend.herokuapp.com/"), options);

        mSocket.connect();
    }
}
