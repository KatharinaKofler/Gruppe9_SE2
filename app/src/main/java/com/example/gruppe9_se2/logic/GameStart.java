package com.example.gruppe9_se2.logic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

import static java.util.Collections.singletonList;

public class GameStart extends AppCompatActivity {

    private Socket mSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamestart);

        Button btnStart = this.findViewById(R.id.btn_startGame);
        //ToDo Button only visible for Lobby owner
        btnStart.setOnClickListener(v -> {
            // Send Start Game Event to server
            mSocket.emit("startGame", "");
            //Close current activity and start Game
            finish();
            Intent intent = new Intent(this, Game.class);
            startActivity(intent);
        });

        Button btnLeave = this.findViewById(R.id.btn_leaveLobby);
        btnLeave.setOnClickListener(v -> {
            // Send Leave Lobby Event to server
            mSocket.emit("leaveLobby", "");
            //ToDo insert the correct message parameter
            //ToDo implement leave Lobby on server
        });


        // Get lobby Id
        Bundle b = getIntent().getExtras();
        String lobbyID = b.getString("LobbyID");

        mSocket = SocketManager.makeSocket(lobbyID);

        // check for successfull connection
        mSocket.on("connect", args -> {
            Log.i("myLogs", "Connection successfull");
        });

        // error if connect failed
        mSocket.on("connect_failed", args -> {
            Log.e("myLogs", "connect_failed");
        });

        // sync Event after connection with websocket
        mSocket.on("sync", lobby -> {
            // lobby is the current lobby we joined, with current player list
            Log.i("myLogs", "sync called");
            //ToDo get Lobby information (Player List)
        });

        mSocket.on("playerJoin", uid->{
            // User Id after a new User join the current Lobby
            Log.i("myLogs", "user joined");
            //ToDo Add Player to Player List
        });

        mSocket.on("playerLeave", uid->{
            // User Id after a player left the current Lobby
            Log.i("myLogs", "user left");
            //ToDo Remove Player from Player List
        });

        mSocket.on("disbandLobby", args->{
            // Close Lobby
            Log.i("myLogs", "Lobby will be closed");
            //ToDo Exit to Lobby overview
        });

        mSocket.connect();
    }
}
