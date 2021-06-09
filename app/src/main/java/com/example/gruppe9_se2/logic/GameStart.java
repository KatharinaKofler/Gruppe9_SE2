package com.example.gruppe9_se2.logic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.game.BoardFragment;
import com.example.gruppe9_se2.game.BodenFragment;
import com.example.gruppe9_se2.game.MusterFragment;
import com.example.gruppe9_se2.game.PlayersFragment;
import com.example.gruppe9_se2.game.ShakeDetector;
import com.example.gruppe9_se2.game.WandFragment;
import com.example.gruppe9_se2.user.LobbyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import io.socket.client.Socket;

public class GameStart extends AppCompatActivity {
    private Socket mSocket;
    private AnimationDrawable loadingAnimation;

    private Bundle b;

    GameStart gameStart;
    // all game fragments
    BoardFragment boardFragment;
    BodenFragment bodenFragment;
    MusterFragment musterFragment;
    PlayersFragment playersFragment;
    WandFragment wandFragment;

    // everything for shakedetector
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector shakeDetector;

    // everything for Cheat
    private boolean hasCheated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamestart);
        gameStart = this;

        // get bundle
        b = getIntent().getExtras();

        // hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // hide game, show gamestart
        findViewById(R.id.game).setVisibility(View.INVISIBLE);
        findViewById(R.id.gamestart).setVisibility(View.VISIBLE);

        setupGamestart();

        setupGame();

        setupSocket();

        setupShakeDetector();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // to register the Session Manager Listener onResume
        mSensorManager.registerListener(shakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        // to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(shakeDetector);
        super.onPause();
    }

    private void setupSocket() {
        String lobbyID = b.getString("LobbyID");
        mSocket = SocketManager.makeSocket(lobbyID);

        SocketManager.getSocket().connect();
        // check for successfull connection
        SocketManager.getSocket().on("connect", args -> {
            Log.i("myLogs", "Connection successfull");
        });

        // error if connect failed
        SocketManager.getSocket().on("connect_failed", args -> {
            Log.e("myLogs", "connect_failed");
        });

        // gamestart Listeners
        SocketManager.getSocket().on("sync", this::sync);
        SocketManager.getSocket().on("playerJoin", this::playerJoin);
        SocketManager.getSocket().on("playerLeave", this::playerLeave);
        SocketManager.getSocket().on("disbandLobby", this::dispandLobby);
        SocketManager.getSocket().on("gameStartAnnounce", this::gameStartAnnounce);
        // game Listeners
        SocketManager.getSocket().on("updateAvailableTiles", args -> {
        });
        SocketManager.getSocket().on("nextPlayer", args -> {
        });
        SocketManager.getSocket().on("startTurn", args -> startTurn());
        SocketManager.getSocket().on("startRound", args -> {
        });
        SocketManager.getSocket().on("boardLookupResponse", args -> {
        });
        SocketManager.getSocket().on("gameEnd", args -> {
        });
        SocketManager.getSocket().on("error", errorMessage -> {
            // print error message somewhere
            Log.e("SOCKET_ERROR", (String) errorMessage[0]);
        });

    }


    private void setupGame() {
        int containerId = findViewById(R.id.wandFragment).getId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        wandFragment = new WandFragment();
        ft.replace(containerId, wandFragment);
        ft.commit();

        containerId = findViewById(R.id.musterFragment).getId();
        ft = getSupportFragmentManager().beginTransaction();
        musterFragment = new MusterFragment();
        ft.replace(containerId, musterFragment);
        ft.commit();

        containerId = findViewById(R.id.bodenFragment).getId();
        ft = getSupportFragmentManager().beginTransaction();
        bodenFragment = new BodenFragment();
        ft.replace(containerId, bodenFragment);
        ft.commit();

        containerId = findViewById(R.id.playerFragment).getId();
        ft = getSupportFragmentManager().beginTransaction();
        playersFragment = new PlayersFragment();
        ft.replace(containerId, playersFragment);
        ft.commit();

        containerId = findViewById(R.id.boardFragment).getId();
        ft = getSupportFragmentManager().beginTransaction();
        boardFragment = new BoardFragment();
        ft.replace(containerId, boardFragment);
        ft.commit();
    }

    // gamestart Methods

    private void sync(Object[] args) {
        // lobby is the current lobby we joined, with current player list
        Log.i("myLogs", "sync called");
        //returns current Player List
        try {
            JSONObject lobbyObject = (JSONObject) args[0];
            JSONArray playersJSON = lobbyObject.getJSONArray("players");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playerJoin(Object[] args) {
        // User Id after a new User join the current Lobby
        Log.i("myLogs", "user joined");
    }

    private void playerLeave(Object[] args) {
        // User Id after a player left the current Lobby
        Log.i("myLogs", "user left");

    }

    private void dispandLobby(Object[] args) {
        // Close Lobby
        Log.i("myLogs", "disbandLobby");
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    private void gameStartAnnounce(Object[] args) {
        // hide gamestart, show game
        runOnUiThread(() -> {
            findViewById(R.id.game).setVisibility(View.VISIBLE);
            findViewById(R.id.gamestart).setVisibility(View.INVISIBLE);
        });

        // remove gamestart event listeners
        SocketManager.getSocket().off("sync");
        SocketManager.getSocket().off("playerJoin");
        SocketManager.getSocket().off("playerLeave");

    }

    // game Methods

    private void startTurn() {
        GameStart gameStart = this;
        gameStart.runOnUiThread(() -> Toast.makeText(gameStart, "It's your turn!", Toast.LENGTH_LONG).show());
    }

    // cheat function

    private void setupShakeDetector() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setCallback(this::cheat);
    }

    private void cheat() {
        if (hasCheated) return;

        Socket socket = SocketManager.getSocket();
        socket.emit("cheat");
        hasCheated = true;
        // TODO handle response from server
    }

}
