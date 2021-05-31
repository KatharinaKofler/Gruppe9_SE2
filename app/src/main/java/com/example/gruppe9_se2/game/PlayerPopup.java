package com.example.gruppe9_se2.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.logic.Game;

import java.util.ArrayList;

public class PlayerPopup extends AppCompatActivity {

    private final int[] emptyFliesen = {R.drawable.empty_fliese_color1, R.drawable.empty_fliese_color2, R.drawable.empty_fliese_color3, R.drawable.empty_fliese_color4, R.drawable.empty_fliese_color5};
    private final int[] fullFliesen = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playerpopup);

        GridLayout grid = findViewById(R.id.gridPopUp);
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        Bundle b = getIntent().getExtras();

        String playerName = b.getString("name");
        ((TextView)findViewById(R.id.playerName)).setText(playerName);

        int playerPoints = b.getInt("points");
        ((TextView)findViewById(R.id.playerPoints)).setText(String.valueOf(playerPoints));

        for(int i = 1; i<6; i++){
            ArrayList<Integer> row = b.getIntegerArrayList(String.valueOf(i));

            for(int j = 0; j<5; j++){
                ImageView image = new ImageView(this);
                image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                image.setPadding(5,5,5,5);
                int colorIndex = (j+(i-1))%5;
                if(row.contains(colorIndex+1)) image.setImageResource(fullFliesen[colorIndex]);
                else image.setImageResource(emptyFliesen[colorIndex]);

                grid.addView(image, (i-1)*5+j);
            }
        }

        ImageButton exitPopUp = (ImageButton) findViewById(R.id.closePopUp);
        exitPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayerPopup.this, Game.class);
                PlayerPopup.this.startActivity(intent);
            }
        });
    }
}
