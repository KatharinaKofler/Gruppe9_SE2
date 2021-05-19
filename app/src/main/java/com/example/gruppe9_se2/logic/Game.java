package com.example.gruppe9_se2.logic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show content activity game
        setContentView(R.layout.activity_game);

        // create FragmentContainerViews to show each Fragment, like Spielmitte, Musterreihe, Wand, ...



    }
}
