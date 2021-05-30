package com.example.gruppe9_se2.game;

import android.annotation.SuppressLint;
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

public class PlayersFragment extends Fragment {
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_players, container, false);

        LinearLayout linearLayout = view.findViewById(R.id.playersLinearLayout);
        //TODO take Number of Players from server
        int playersNr = 4;
        String playerName = "Test";
        int[] playerIcons = {R.drawable.kiti, R.drawable.girl_0, R.drawable.girl_2, R.drawable.girl_1};

        for(int i = 0; i<playersNr; i++){
            Button playerButton = new Button(requireContext(), null, R.style.Widget_AppCompat_Button_Borderless);
            playerButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            playerButton.setTextColor(Color.parseColor("#000000"));
            Drawable icon =  getResources().getDrawable(playerIcons[i]);
            icon.setBounds(0,0,100,100);
            playerButton.setCompoundDrawables(null,icon, null,null);
            playerButton.setPadding(10,0,10,0);
            playerButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            playerButton.setTypeface(playerButton.getTypeface(), Typeface.BOLD);
            playerButton.setText(playerName);
            linearLayout.addView(playerButton);
        }
        return view;
    }
}
