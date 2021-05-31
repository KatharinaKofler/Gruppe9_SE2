package com.example.gruppe9_se2.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.logic.SocketManager;

import io.socket.client.Socket;

public class PlayersFragment extends Fragment {
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_players, container, false);

        LinearLayout linearLayout = view.findViewById(R.id.playersLinearLayout);


        Socket mySocket = SocketManager.getSocket();
        if(mySocket != null){
           /* // TODO events
            // Name, Points, Board of every Player
            mySocket.on("gameStart", args -> {
                args
            });
            mySocket.on("startNewRound", args -> {
                args
            });*/
        }

        //TODO take Number of Players from server
        int playersNr = 4;
        String playerName = "Test";
        int points = 8;
        int[] playerIcons = {R.drawable.kiti, R.drawable.girl_0, R.drawable.girl_2, R.drawable.girl_1};

        for(int i = 0; i<playersNr; i++){

            Button playerButton = new Button(requireContext(), null, R.style.Widget_AppCompat_Button_Borderless);

            //Button onclick Listener
            playerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(requireContext(), PlayerPopup.class);

                    Bundle b = new Bundle();
                    b.putString("name", playerName);
                    b.putInt("points", points);

                    int[] wall = {19, 5, 28, 0, 0}; // todo
                    // 19 ->  10011 -> erste, vierte, fünfte Fliese belegt
                    // 5 -> 00101; 28 -> 11100
                    int[] pattern = {97, 0, 39, 0, 163 };
                    // C C C F F F F F
                    // 0 1 1 0 0 0 0 1 -> one field in color 3 -> 97
                    // 0 0 1 0 0 1 1 1 -> three fields in color 1 -> 39
                    // 1 0 1 0 0 0 1 1 -> two fields in color 5 -> 163

                    b.putIntArray("wall", wall);
                    b.putIntArray("pattern", pattern);

                    intent.putExtras(b);

                    startActivity(intent);
                }
            });

            //Button Design
            playerButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            playerButton.setTextColor(Color.parseColor("#000000"));
            Drawable icon =  getResources().getDrawable(playerIcons[i]);
            icon.setBounds(0,0,100,100);
            playerButton.setCompoundDrawables(null,icon, null,null);
            playerButton.setPadding(10,0,10,0);
            playerButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            playerButton.setTypeface(playerButton.getTypeface(), Typeface.BOLD);
            playerButton.setText(playerName+"\n"+points);
            linearLayout.addView(playerButton);
        }
        return view;
    }
}
