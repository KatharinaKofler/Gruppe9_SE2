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

import java.text.MessageFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlayersFragment extends Fragment {

    @SuppressLint("UseCompatLoadingForDrawables")

    private ArrayList<String[]> playerList = new ArrayList<>();
    LinearLayout playerButtonLayout;

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
            if (player[0] == id) {
                playerList.remove(player);
            }
        }
    }

    public void initPlayerButtons(GameStart gameStart) {
        for (int i = 0; i < playerList.size(); i++) {
            Button playerButton = new Button(requireContext(), null, R.style.Widget_AppCompat_Button_Borderless);
            playerButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            playerButton.setPadding(10, 0, 10, 0);
            int[] playerIcons = {R.drawable.kiti, R.drawable.girl_0, R.drawable.girl_2, R.drawable.girl_1};
            @SuppressLint("UseCompatLoadingForDrawables") Drawable icon = getResources().getDrawable(playerIcons[i]);
            icon.setBounds(0, 0, 100, 100);
            playerButton.setCompoundDrawables(null, icon, null, null);

            String playerId = playerList.get(i)[0];
            playerButton.setTag(R.id.playerId, playerId);
            playerButton.setTag(R.id.points, 0);
            playerButton.setOnClickListener(view -> gameStart.requestPlayerBoard(playerId));
            playerButtonLayout.addView(playerButton);
        }
        getNamesForIds();
    }

    public void getNamesForIds() {
        for (String[] player : playerList) {
            // Post Request getUser
            String token = "Bearer ";
            token += ApiManager.getToken();

            Retrofit retrofit = ApiManager.getInstance();
            UsersApi service = retrofit.create(UsersApi.class);
            Call<UsersResponse> call = service.executeUsers(player[0], token);
            call.enqueue(new Callback<UsersResponse>() {
                @Override
                public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                    if (response.isSuccessful()) {
                        player[1] = response.body().getUsername();
                    } else {
                        Log.e("PlayersFragment", "Response not successfull - getPlayerList");
                    }
                }

                @Override
                public void onFailure(Call<UsersResponse> call, Throwable t) {
                    Log.e("PlayersFragment", "Response Failure - getPlayerList");
                }
            });
        }
        showNames();
    }

    private void showNames() {
        for (int i = 0; i < playerButtonLayout.getChildCount(); i++) {
            Button playerButton = (Button) playerButtonLayout.getChildAt(i);
            playerButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            playerButton.setTextColor(Color.parseColor("#000000"));
            playerButton.setTypeface(playerButton.getTypeface(), Typeface.BOLD);
            playerButton.setText(MessageFormat.format("{0}\n{1} Points", playerList.get(i)[1], 0));
        }
    }

    public void updatePoints(String id, int points) {
        for (int i = 0; i < playerButtonLayout.getChildCount(); i++) {
            Button playerButton = (Button) playerButtonLayout.getChildAt(i);
            if (playerButton.getTag(R.id.playerId).equals(id)) {
                playerButton.setTag(R.id.points, points);
                playerButton.setText(MessageFormat.format("{0}\n{1} Points", playerList.get(i)[1], points));
            }
        }
    }

    public void responsePlayerBoard(JSONObject playerBoard) {

        try {
            // todo check if JSONObject has all the elements and everything is accessed correctly
            String playerId = playerBoard.getString("playerId");

            for (int i = 0; i < playerList.size(); i++) {
                if(playerList.get(i)[0].equals(playerId)){
                    Bundle bundle = new Bundle();

                    bundle.putString("id", playerList.get(i)[0]);
                    bundle.putString("name", playerList.get(i)[1]);
                    bundle.putInt("points", (Integer) playerButtonLayout.getChildAt(i).getTag(R.id.points));

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

    public void markCurrentPlayer(JSONObject args) {
        try {
            String currentPlayer = args.getString("id");
            for (int i = 0; i < playerList.size(); i++) {
                Button currentPlayerButton = (Button)playerButtonLayout.getChildAt(i);
                if(playerList.get(i)[0].equals(currentPlayer)){
                    currentPlayerButton.setTextColor(getResources().getColor(R.color.errorTextColor));
                }
                else{
                    currentPlayerButton.setTextColor(getResources().getColor(R.color.primaryTextColor));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
