package com.example.gruppe9_se2.logic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.game.PlayersFragment;
import com.example.gruppe9_se2.game.WandFragment;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show content activity game
        setContentView(R.layout.activity_game);

        // create FragmentContainerViews to show each Fragment, like Spielmitte, Musterreihe, Wand, ...

        int containerId = findViewById(R.id.wandFragment).getId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, new WandFragment());
        ft.commit();

        int playerContainerId = findViewById(R.id.playerFragment).getId();
        FragmentTransaction ftPlayer = getSupportFragmentManager().beginTransaction();
        ftPlayer.replace(playerContainerId, new PlayersFragment());
        ftPlayer.commit();


    }
}
