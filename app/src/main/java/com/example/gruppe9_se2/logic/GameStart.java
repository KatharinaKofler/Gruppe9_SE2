package com.example.gruppe9_se2.logic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.users.UsersApi;
import com.example.gruppe9_se2.api.users.UsersResponse;
import com.example.gruppe9_se2.game.BoardFragment;
import com.example.gruppe9_se2.game.BodenFragment;
import com.example.gruppe9_se2.game.EndGameActivity;
import com.example.gruppe9_se2.game.MusterFragment;
import com.example.gruppe9_se2.game.PlayerResult;
import com.example.gruppe9_se2.game.PlayersFragment;
import com.example.gruppe9_se2.game.ShakeDetector;
import com.example.gruppe9_se2.game.WandFragment;
import com.example.gruppe9_se2.user.LobbyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GameStart extends AppCompatActivity {
    private Socket mSocket;
    private int playerCount;
    private AnimationDrawable loadingAnimation;

    private Bundle b;
    private GameStart gameStart;

    // all game fragments
    private BoardFragment boardFragment;
    private BodenFragment bodenFragment;
    private MusterFragment musterFragment;
    private PlayersFragment playersFragment;
    private WandFragment wandFragment;

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
        mSensorManager.registerListener(shakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
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
        SocketManager.getSocket().on("updateAvailableTiles", args -> boardFragment.updateAllPlates(gameStart, args));
        SocketManager.getSocket().on("nextPlayer", args -> playersFragment.markCurrentPlayer((JSONObject) args[0]));
        SocketManager.getSocket().on("startTurn", args -> startTurn());
        SocketManager.getSocket().on("startRound", args -> updateAllPoints((JSONArray) args[0]));
        SocketManager.getSocket().on("boardLookupResponse", args -> playersFragment.responsePlayerBoard((JSONObject) args[0]));
        SocketManager.getSocket().on("cheatResponse", args -> bodenFragment.cheatResponse((JSONObject) args[0], gameStart));
        SocketManager.getSocket().on("accuseResponse", args -> accuseResponse((JSONObject) args[0]));
        SocketManager.getSocket().on("gameEnd", args -> {
            JSONArray scores = (JSONArray) args[0];
            List<PlayerResult> results = new ArrayList<>();
            for (int i = 0; i < scores.length(); i++) {
                JSONObject score = null;
                try {
                    score = scores.getJSONObject(i);
                    int points = score.getInt("points");
                    String uid = score.getString("uid");
                    results.add(new PlayerResult(0, getNameById(uid), points));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Comparator<PlayerResult> compareByPoints = (PlayerResult p1, PlayerResult p2) -> {
                Integer p1P = p1.points;
                Integer p2P = p2.points;
                return p1P.compareTo(p2P);
            };
            Collections.sort(results, compareByPoints);
            for (int i = 0; i < results.size(); i++) {
                results.get(i).rank = i + 1;
            }
            Intent intent = new Intent(this, EndGameActivity.class);
            intent.putExtra("results", (Serializable) results);
            startActivity(intent);
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
            for (int i = 0; i < playersJSON.length(); i++) {
                playersFragment.addPlayerId(playersJSON.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playerJoin(Object[] args) {
        // User Id after a new User join the current Lobby
        Log.i("myLogs", "user joined");

        try {
            String id = ((JSONObject) args[0]).getString("id");
            playersFragment.addPlayerId(id);

            playerCount++;
            runOnUiThread(() -> ((TextView) findViewById(R.id.playercountText)).setText(playerText(playerCount)));
            if (playerCount > 1) {
                runOnUiThread(() -> ((TextView) findViewById(R.id.btn_startGame)).setEnabled(true));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playerLeave(Object[] args) {
        // User Id after a player left the current Lobby
        Log.i("myLogs", "user left");

        try {
            String id = ((JSONObject) args[0]).getString("id");
            playersFragment.removePlayerId(id);
            playerCount--;
            runOnUiThread(() -> ((TextView) findViewById(R.id.playercountText)).setText(playerText(playerCount)));
            if (playerCount < 2) {
                runOnUiThread(() -> ((TextView) findViewById(R.id.btn_startGame)).setEnabled(false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

        playersFragment.initPlayerButtons(gameStart);
    }

    public void finishTurn(int row) {
        try {
            JSONObject args = new JSONObject();
            args.put("placeRow", row);
            SocketManager.getSocket().emit("finishTurn", args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestPlayerBoard(String playerId) {
        try {
            JSONObject args = new JSONObject();
            args.put("playerId", playerId);
            SocketManager.getSocket().emit("boardLookupRequest", args);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateAllPoints(JSONArray scores) {
        // todo check if its JSONArray with those parameters
        try {
            for (int i = 0; i < scores.length(); i++) {
                JSONObject playerScore = scores.getJSONObject(i);
                String id = playerScore.getString("id");
                int score = playerScore.getInt("score");
                playersFragment.updatePoints(id, score);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // game Methods

    private void startTurn() {
        GameStart gameStart = this;
        gameStart.runOnUiThread(() -> Toast.makeText(gameStart, "It's your turn!", Toast.LENGTH_LONG).show());
        boardFragment.startTurn();
        musterFragment.dragListenerNewTileField(gameStart);
        playersFragment.markMe(true);
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
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setCallback(this::cheat);
    }

    public void deleteShakeDetector() {
        shakeDetector = null;
    }

    private void cheat() {
        if (hasCheated) return;

        Socket socket = SocketManager.getSocket();
        socket.emit("cheat");
        hasCheated = true;
    }

    private void accuseResponse(JSONObject arg) {
        try {
            String accused = arg.getString("accused");
            String accuser = arg.getString("accuser");
            boolean cheated = arg.getBoolean("cheated");
            String message;
            if (cheated) {
                message = "Oh no! " + getNameById(accuser) + " caught " + getNameById(accused) + " cheating. Minus 10 points for " + getNameById(accused) + ".";
                playersFragment.playerCaught(accused);
            } else
                message = getNameById(accuser) + " thought " + getNameById(accused) + " cheated. Unfortunate for " + getNameById(accuser) + ", because " + getNameById(accused) + " didn't cheat. Minus 10 points for " + getNameById(accuser) + ".";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getNameById(String playerId) {
        final String[] name = new String[1];
        // Post Request getUser
        String token = "Bearer ";
        token += ApiManager.getToken();

        Retrofit retrofit = ApiManager.getInstance();
        UsersApi service = retrofit.create(UsersApi.class);
        Call<UsersResponse> call = service.executeUsers(playerId, token);
        call.enqueue(new Callback<UsersResponse>() {

            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                Log.e("getUser", "onResponse successfull: " + response.code());
                name[0] = response.body().getUsername();
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                Log.e("getUser", "onFailure");
            }
        });
        return name[0];
    }
}
