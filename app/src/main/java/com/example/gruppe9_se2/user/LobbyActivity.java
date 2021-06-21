package com.example.gruppe9_se2.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.MainActivity;
import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;

import java.util.Objects;

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

        Objects.requireNonNull(getSupportActionBar()).setTitle("Lobby Overview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void newLobby() {
        Fragment newLobby = new NewLobbyFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, newLobby);
        ft.commit();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Create new lobby");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void logoutLobby() {
        AppCompatActivity activity = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    dialog.dismiss();

                    // Delete token
                    ApiManager.deleteToken();

                    // Close current activity and start MainActivity
                    activity.finish();
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showLobbyOverview();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
