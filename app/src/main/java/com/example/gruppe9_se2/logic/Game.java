package com.example.gruppe9_se2.logic;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.game.MusterFragment;
import com.example.gruppe9_se2.game.PlayersFragment;
import com.example.gruppe9_se2.game.WandFragment;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show content activity game
        setContentView(R.layout.activity_game);

        // hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // create FragmentContainerViews to show each Fragment, like Spielmitte, Musterreihe, Wand, ...

        int containerId = findViewById(R.id.wandFragment).getId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, new WandFragment());
        ft.commit();

        containerId = findViewById(R.id.musterFragment).getId();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, new MusterFragment());
        ft.commit();

        containerId = findViewById(R.id.playerFragment).getId();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, new PlayersFragment());
        ft.commit();
    }
}
