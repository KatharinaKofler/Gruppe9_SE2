package com.example.gruppe9_se2.game;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.logic.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class PlayerPopup extends AppCompatActivity {

    private final int[] emptyFliesen = {R.drawable.empty_fliese_color1, R.drawable.empty_fliese_color2, R.drawable.empty_fliese_color3, R.drawable.empty_fliese_color4, R.drawable.empty_fliese_color5};
    private final int[] fullFliesen = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};
    private boolean caught = false;
    String playerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playerpopup);

        // hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        Bundle b = getIntent().getExtras();
        caught = b.getBoolean("caught");

        String playerName = b.getString("name");
        ((TextView)findViewById(R.id.playerName)).setText(playerName);

        int playerPoints = b.getInt("points");
        ((TextView)findViewById(R.id.playerPoints)).setText(String.valueOf(playerPoints));

        playerId = b.getString("id");

        //createWall(b);
        //createPattern(b);

        ImageButton exitPopUp = (ImageButton) findViewById(R.id.closePopUp);
        exitPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Intent intent = new Intent(PlayerPopup.this, GameStart.class);
                //PlayerPopup.this.startActivity(intent);
            }
        });

        ImageButton accuse = (ImageButton) findViewById(R.id.accuse);
        if(caught){
            accuse.setVisibility(View.INVISIBLE);
        } else {
            accuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Socket socket = SocketManager.getSocket();
                    if (socket != null) {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("id", playerId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        socket.emit("accuse", object);

                    }
                }
            });
        }

    }

    private void createPattern(Bundle b) {
        // PATTERN
        GridLayout grid = findViewById(R.id.gridPattern);
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        // get wall from bundle and build wall
        int[] pattern = b.getIntArray("pattern");

        for(int i = 0; i < 5; i++){ // rows

            //Binary division -> get bitmap
            int[] bitmap = binaryDivision(pattern[i], 8);
            // get color for this row
            int colorIndex = bitmap[0] * 4 + bitmap[1] * 2 + bitmap[2];

            for(int j = 0; j<5; j++){ // column

                // add imageView
                ImageView image = new ImageView(this);
                image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                image.setPadding(5,5,5,5);

                if(bitmap[j+3] == 1) image.setImageResource(fullFliesen[colorIndex-1]);
                else{
                    // i == 0 -> 0 0 0 0 0 -> if j >= 4 (4-i)
                    // i == 1 -> 0 0 0 1 1 -> if j >= 3 (4-i)
                    // i == 2 -> 0 0 1 1 1 -> if j >= 2 (4-i)
                    if(j>=(4-i)){
                        if(colorIndex!=0) {
                            image.setImageResource(emptyFliesen[colorIndex - 1]);
                        }
                        else image.setImageResource(R.drawable.empty_fliese);
                    }

                }

                grid.addView(image, i*5+j);
            }
        }

    }

    private void createWall(Bundle b){
        // WALL
        GridLayout grid = findViewById(R.id.gridWall);
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        // get wall from bundle and build wall
        int[] wall = b.getIntArray("wall");
        // for every row:
        for(int i = 0; i<5; i++){ // rows

            //Binary division -> get bitmap, storing which field is full
            int[] bitmap = binaryDivision(wall[i], 5);

            for(int k = 0; k < 5; k++){
                // add imageView
                ImageView image = new ImageView(this);
                image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                image.setPadding(5,5,5,5);
                int colorIndex = ((k)+(i)) % 5; // (column + row) % 5

                if(bitmap[k] == 1) image.setImageResource(fullFliesen[colorIndex]);
                else image.setImageResource(emptyFliesen[colorIndex]);

                grid.addView(image, i*5+k);
            }
        }
    }

    private int[] binaryDivision(int value, int length){
        int[] bitmap = new int[length];
        for(int l = 0; l < length; l++){
            bitmap[length-1-l] = value%2;
            value = value/2;
        }
        return bitmap;
    }
}
