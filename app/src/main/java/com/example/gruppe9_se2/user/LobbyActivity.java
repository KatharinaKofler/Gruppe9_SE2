package com.example.gruppe9_se2.user;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.R;

public class LobbyActivity extends AppCompatActivity {
    //player is able to enter a lobby
    //player is able to leave a lobby
    //player is able to start game via lobby
    //player is able to join lobby

    int containerId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        containerId = findViewById(R.id.fragment_container_view).getId();

        showLobbyOverview();

    }

    public void showLobbyOverview() {
        Fragment overview = new LobbyOverviewFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, overview);
        ft.commit();

        getSupportActionBar().setTitle("Lobby Overview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void newLobby() {
        Fragment newLobby = new NewLobbyFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, newLobby);
        ft.commit();

        getSupportActionBar().setTitle("Create new lobby");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void backToOverview(){
        Fragment backToOverview = new LobbyOverviewFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, backToOverview);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showLobbyOverview();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
