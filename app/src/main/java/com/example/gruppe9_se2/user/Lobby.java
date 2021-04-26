package com.example.gruppe9_se2.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;

public class Lobby extends AppCompatActivity {
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

        Fragment overview = new LobbyOverviewFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, overview);
        ft.commit();
    }

    public void newLobby() {
        Fragment newLobby = new NewLobbyFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, newLobby);
        ft.commit();
    }
}
