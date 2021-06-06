package com.example.gruppe9_se2.logic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.user.LobbyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

public class GameStart extends AppCompatActivity {

    private Socket mSocket;

    private ArrayList<String> playerIDList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamestart);

        Bundle b = getIntent().getExtras();
        Boolean isOwner = b.getBoolean("isOwner");

        Button btnStart = this.findViewById(R.id.btn_startGame);
        if(!isOwner) btnStart.setVisibility(View.INVISIBLE);
        else{
            btnStart.setOnClickListener(v -> {
                // Send Start Game Event to server
                mSocket.emit("startGame", "");
                //Close current activity and start Game
                Bundle gameBundle = new Bundle();
                gameBundle.putInt("playerNumber", playerIDList.size()+1);
                finish();
                Intent intent = new Intent(this, Game.class);
                startActivity(intent);
            });
        }

        Button btnLeave = this.findViewById(R.id.btn_leaveLobby);
        btnLeave.setOnClickListener(v -> {
            // Send Leave Lobby Event to server
            mSocket.disconnect();
            Log.i("myLogs", "Disconnect");
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
        });


        // Get lobby Id
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
            //returns current Player List
            try {
                JSONObject lobbyObject = (JSONObject) lobby[0];
                JSONArray playersJSON = lobbyObject.getJSONArray("players");
                for (int i = 0; i < playersJSON.length(); i++) {
                    playerIDList.add(playersJSON.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mSocket.on("playerJoin", uid -> {
            // User Id after a new User join the current Lobby
            Log.i("myLogs", "user joined");
            try {
                String id = ((JSONObject) uid[0]).getString("id");
                playerIDList.add(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mSocket.on("playerLeave", uid -> {
            // User Id after a player left the current Lobby
            Log.i("myLogs", "user left");
            try {
                String id = ((JSONObject) uid[0]).getString("id");
                playerIDList.remove(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mSocket.on("disbandLobby", args -> {
            // Close Lobby
            Log.i("myLogs", "Lobby will be closed");
            playerIDList = new ArrayList<>();
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
        });

        mSocket.connect();
    }
}
