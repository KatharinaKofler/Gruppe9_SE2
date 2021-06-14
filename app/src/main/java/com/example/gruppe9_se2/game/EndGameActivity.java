package com.example.gruppe9_se2.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.user.LobbyActivity;

import java.util.ArrayList;

public class EndGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_endgame);

        Bundle b = getIntent().getExtras();

        ArrayList<PlayerResult> results = (ArrayList<PlayerResult>) b.getSerializable("results");

        RecyclerView recyclerView = findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);

        ResultListAdapter adapter = new ResultListAdapter();

        for (PlayerResult r : results) {
            adapter.insert(r);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Button btnLeave = findViewById(R.id.btn_leaveResult);
        btnLeave.setOnClickListener((e) -> {
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
        });
    }
}
