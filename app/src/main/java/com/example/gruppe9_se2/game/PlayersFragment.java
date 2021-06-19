package com.example.gruppe9_se2.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.users.UsersApi;
import com.example.gruppe9_se2.api.users.UsersResponse;
import com.example.gruppe9_se2.logic.GameStart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlayersFragment extends Fragment {

    @SuppressLint("UseCompatLoadingForDrawables")

    private ArrayList<String[]> playerList = new ArrayList<>();
    LinearLayout playerButtonLayout;
    private GameStart gameStart;
    private boolean isInit = false;
    private boolean callMarkMe = false;
    private boolean markMe;
    private JSONObject currentplayerArgs;
    private boolean [] caught = new boolean[4];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_players, container, false);

        playerButtonLayout = view.findViewById(R.id.playersLinearLayout);

        return view;
    }

    public void addPlayerId(String id) {
        String[] player = {id, null};
        playerList.add(player);
    }

    public void removePlayerId(String id) {
        for (String[] player : playerList) {
            if (player[0].equals(id)) {
                playerList.remove(player);
            }
        }
    }

    public void addPlayer(int n) {
        gameStart.runOnUiThread(() -> {
            if (!isInit) {
                for (int i = 0; i < playerList.size() + 1; i++) {
                    Button playerButton = new Button(requireContext(), null, R.style.Widget_AppCompat_Button_Borderless);
                    playerButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    playerButton.setPadding(10, 0, 10, 0);
                    int[] playerIcons = {R.drawable.kiti, R.drawable.girl_0, R.drawable.girl_2, R.drawable.girl_1};
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable icon = getResources().getDrawable(playerIcons[i]);
                    icon.setBounds(0, 0, 100, 100);
                    playerButton.setCompoundDrawables(null, icon, null, null);
                    if (i == 0) {
                        playerButton.setTag(R.id.points, 0);
                        playerButton.setText("Me\n0 Points");
                    } else {
                        String playerId = playerList.get(i - 1)[0];
                        playerButton.setTag(R.id.playerId, playerId);
                        playerButton.setTag(R.id.points, 0);
                        playerButton.setOnClickListener(view -> gameStart.requestPlayerBoard(playerId));
                    }
                    playerButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    playerButton.setTextColor(Color.parseColor("#000000"));
                    playerButton.setTypeface(playerButton.getTypeface(), Typeface.BOLD);
                    playerButtonLayout.addView(playerButton, i);
                }
                isInit = true;
                if (callMarkMe) markMe(markMe);
            }
            String text = playerList.get(n)[1] + "\n0 Points";
            ((Button) playerButtonLayout.getChildAt(n + 1)).setText(text);
        });
    }

    public void initPlayerButtons(GameStart gameStart) {
        this.gameStart = gameStart;
        for (int i = 0; i < playerList.size(); i++) {
            String[] player = playerList.get(i);
            // Post Request getUser
            String token = "Bearer ";
            token += ApiManager.getToken();

            Retrofit retrofit = ApiManager.getInstance();
            UsersApi service = retrofit.create(UsersApi.class);
            Call<UsersResponse> call = service.executeUsers(player[0], token);
            int finalI = i;
            call.enqueue(new Callback<UsersResponse>() {
                @Override
                public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                    Log.e("getUser", "onResponse successfull: " + response.code());
                    player[1] = response.body().getUsername();
                    addPlayer(finalI);
                }

                @Override
                public void onFailure(Call<UsersResponse> call, Throwable t) {
                    Log.e("getUser", "onFailure");
                }
            });
        }
    }

    public void updatePoints(String id, int points) {
        gameStart.runOnUiThread(()-> {
            boolean foundId = false;
            for (int i = 1; i < playerButtonLayout.getChildCount(); i++) {
                Button playerButton = (Button) playerButtonLayout.getChildAt(i);
                if (playerButton.getTag(R.id.playerId).equals(id)) {
                    foundId = true;
                    playerButton.setTag(R.id.points, points);
                    String text = playerList.get(i)[1] + "\n" + points + " Points";
                    playerButton.setText(text);
                }
            }
            if (!foundId) {
                Button playerButton = (Button) playerButtonLayout.getChildAt(0);
                playerButton.setTag(R.id.points, points);
                String text = "Me\n" + points + " Points";
                playerButton.setText(text);
            }
        });
    }

    public void responsePlayerBoard(JSONObject playerBoard) {

        try {
            // todo check if JSONObject has all the elements and everything is accessed correctly
            String playerId = playerBoard.getString("playerId");

            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i)[0].equals(playerId)) {
                    Bundle bundle = new Bundle();

                    bundle.putString("id", playerList.get(i)[0]);
                    bundle.putString("name", playerList.get(i)[1]);
                    bundle.putInt("points", (Integer) playerButtonLayout.getChildAt(i).getTag(R.id.points));
                    bundle.putBoolean("caught", caught[i]);

                    JSONArray pattern = playerBoard.getJSONArray("pattern");
                    int[] patternArray = new int[pattern.length()];
                    for (int j = 0; j < pattern.length(); j++) {
                        patternArray[j] = pattern.getInt(j);
                    }
                    bundle.putIntArray("pattern", patternArray);

                    JSONArray wall = playerBoard.getJSONArray("wall");
                    int[] wallArray = new int[wall.length()];
                    for (int j = 0; j < wall.length(); j++) {
                        wallArray[j] = wall.getInt(j);
                    }
                    bundle.putIntArray("wall", wallArray);

                    // start PlayerPopup Intent
                    Intent intent = new Intent(requireContext(), PlayerPopup.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void markMe(boolean markMe) {
        if (!isInit) {
            callMarkMe = true;
            this.markMe = markMe;
        } else {
            gameStart.runOnUiThread(()->{
                Button button = (Button) playerButtonLayout.getChildAt(0);
                if (markMe) button.setTextColor(getResources().getColor(R.color.errorTextColor));
                else button.setTextColor(getResources().getColor(R.color.primaryTextColor));
            });
        }
    }

    public void markCurrentPlayer(JSONObject args) {
        try {
            String currentPlayer = args.getString("id");
            for (int i = 0; i < playerList.size(); i++) {
                Button currentPlayerButton = (Button) playerButtonLayout.getChildAt(i+1);
                if (playerList.get(i)[0].equals(currentPlayer)) {
                    currentPlayerButton.setTextColor(getResources().getColor(R.color.errorTextColor));
                    markMe(false);
                } else {
                    currentPlayerButton.setTextColor(getResources().getColor(R.color.primaryTextColor));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void playerCaught (String id){
        for (int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i)[0].equals(id)){
                caught[i] = true;
            }

        }

    }
}

