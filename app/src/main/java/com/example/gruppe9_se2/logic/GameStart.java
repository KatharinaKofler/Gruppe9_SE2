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
    private int playerCount;
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
    protected void onStart() {
        super.onStart();
        loadingAnimation.start();
    }

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
            boardFragment.updateTiles(gameStart, args);
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

    private void setupGamestart() {
        // setup Image Animation
        ImageView loadingImage = findViewById(R.id.loadingAnimation);
        loadingImage.setBackgroundResource(R.drawable.loading_animation_list);
        loadingAnimation = (AnimationDrawable) loadingImage.getBackground();

        // set LoadingText invisible
        findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);

        // setup game start button
        b = getIntent().getExtras();
        Button btnStart = this.findViewById(R.id.btn_startGame);
        if (!b.getBoolean("isOwner")) {
            // not Owner -> set start button invisible and change text
            btnStart.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.gamestartText)).setText("Wait for all your players to join");
        } else {
            // is Owner -> add onClickListener for start game button
            btnStart.setEnabled(false);
            btnStart.setOnClickListener(v -> {
                // Send Start Game Request Event to server
                mSocket.emit("gameStartRequest");
                findViewById(R.id.loadingText).setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);
                boardFragment.initTiles(gameStart, playerCount * 2 + 1);
            });
        }

        // setup lobby leave button
        Button btnLeave = this.findViewById(R.id.btn_leaveLobby);
        btnLeave.setOnClickListener(v -> {
            // Send Leave Lobby Event to server
            mSocket.disconnect();
            Log.i("myLogs", "Disconnect");
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
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

            playerCount = 1 + playersJSON.length();
            runOnUiThread(() -> ((TextView) findViewById(R.id.playercountText)).setText(playerText(playerCount)));
            if (playerCount > 1) {
                runOnUiThread(() -> ((TextView) findViewById(R.id.btn_startGame)).setEnabled(true));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playerJoin(Object[] args) {
        // User Id after a new User join the current Lobby
        Log.i("myLogs", "user joined");

        playerCount++;
        runOnUiThread(() -> ((TextView) findViewById(R.id.playercountText)).setText(playerText(playerCount)));
        if (playerCount > 1) {
            runOnUiThread(() -> ((TextView) findViewById(R.id.btn_startGame)).setEnabled(true));
        }
    }

    private void playerLeave(Object[] args) {
        // User Id after a player left the current Lobby
        Log.i("myLogs", "user left");

        playerCount--;
        ((TextView) findViewById(R.id.playercountText)).setText(playerText(playerCount));
        if (playerCount < 2) {
            runOnUiThread(() -> ((TextView) findViewById(R.id.btn_startGame)).setEnabled(false));
        }
    }

    private void dispandLobby(Object[] args) {
        // Close Lobby
        Log.i("myLogs", "disbandLobby");
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    private CharSequence playerText(int count) {
        if (count == 1) {
            return "1 Player";
        }
        return count + " Players";
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
        boardFragment.startTurn();
        musterFragment.dragListenerNewTileField(gameStart);
    }

    public void disableOnTouchBoard() {
        boardFragment.disableOnTouch();
    }

    public void takeCenterTiles(JSONObject args) {
        SocketManager.getSocket().emit("takeCenterTiles", args);
    }

    public void takePlateTiles(JSONObject args) {
        SocketManager.getSocket().emit("takePlateTiles", args);
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
