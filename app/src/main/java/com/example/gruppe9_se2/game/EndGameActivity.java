package com.example.gruppe9_se2.game;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;

public class EndGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_endgame);

        RecyclerView recyclerView = findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);

        ResultListAdapter adapter = new ResultListAdapter();

        adapter.insert(new PlayerResult(1, "Carina1", 47));
        adapter.insert(new PlayerResult(2, "Carina2", 32));
        adapter.insert(new PlayerResult(3, "Carina3", 27));
        adapter.insert(new PlayerResult(4, "Carina4", 26));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
