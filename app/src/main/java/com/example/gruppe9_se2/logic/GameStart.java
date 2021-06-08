package com.example.gruppe9_se2.logic;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
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
    private int playerCount;
    private AnimationDrawable loadingAnimation;

    @Override
    protected void onStart() {
        super.onStart();
        loadingAnimation.start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamestart);

        ImageView loadingImage = findViewById(R.id.loadingAnimation);
        loadingImage.setBackgroundResource(R.drawable.loading_animation_list);
        loadingAnimation = (AnimationDrawable) loadingImage.getBackground();

        findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);

        Bundle b = getIntent().getExtras();
        Boolean isOwner = b.getBoolean("isOwner");

        Button btnStart = this.findViewById(R.id.btn_startGame);
        if (!isOwner) {
            btnStart.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.gamestartText)).setText("Wait for all your players to join");
        } else {
            btnStart.setOnClickListener(v -> {
                // Send Start Game Request Event to server
                mSocket.emit("gameStartRequest");
                findViewById(R.id.loadingText).setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
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
                playerCount = 1 + playersJSON.length();
                ((TextView) findViewById(R.id.playercountText)).setText(playerText(playerCount));
//                for (int i = 0; i < playersJSON.length(); i++) {
//                    playerIDList.add(playersJSON.getString(i));
//                }
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
                playerCount++;
                ((TextView)findViewById(R.id.playercountText)).setText(playerText(playerCount));
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
                playerCount--;
                ((TextView)findViewById(R.id.playercountText)).setText(playerText(playerCount));
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

    private CharSequence playerText(int count) {
        if (count == 1) {
            return "1 Player";
        }
        return count + " Players";
    }
}
