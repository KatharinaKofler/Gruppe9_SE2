package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;


import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.logic.GameStart;
import com.example.gruppe9_se2.logic.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class BoardFragment extends Fragment {

    private View view;
    private GameStart gameStart;

    private final int[] tileResourceMap = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};
    private static int[][] plates;
    private static int[] center;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board, container, false);
        return view;
    }

    public void updateTiles(GameStart gameStart, Object[] args){
        this.gameStart = gameStart;
        JSONObject tilesObject = (JSONObject) args[0];
        Log.d("myLogs", "update available tiles called");
        try {
            JSONObject platesObject = tilesObject.getJSONObject("plates");
            int numberPlates = platesObject.length();

            if(plates==null){
                plates = new int[numberPlates][4];
                center = new int[5];
            }

            for (int i = 0; i < platesObject.length(); i++) {
                JSONArray singlePlateArray = platesObject.getJSONArray("plate"+i);
                if(singlePlateArray.length()<4){
                    for (int j = 0; j < 4; j++) {
                        plates[i][j] = 0;
                    }
                }
                else{
                    for (int j = 0; j < 4; j++) {
                        plates[i][j] = singlePlateArray.getInt(j);
                    }
                }
            }

            JSONObject centerObject = tilesObject.getJSONObject("center");
            center[0] = centerObject.getInt("black");
            center[1] = centerObject.getInt("white");
            center[2] = centerObject.getInt("blue");
            center[3] = centerObject.getInt("yellow");
            center[4] = centerObject.getInt("red");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // update plates
        generatePlates(gameStart, plates);

        // update center
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < center[i]; j++) {
                addToCenter(i, center[i]);
            }
        }
    }

    public void startTurn() {
        // for plate tiles
        GridLayout board = view.findViewById(R.id.gridPlates);
        for (int i = 0; i < board.getChildCount(); i++) {
            GridLayout plate = (GridLayout) board.getChildAt(i);
            for (int j = 0; j < plate.getChildCount(); j++) {
                ImageView tile = (ImageView) plate.getChildAt(j);
                int color = (tile.getTag(R.id.color_id)!=null) ? (int) tile.getTag(R.id.color_id) : 0;
                if(color != -1) tile.setOnTouchListener(new TileTouchListener());
            }
        }
        // for center tiles
        GridLayout center = view.findViewById(R.id.gridCenter);
        for (int i = 0; i < center.getChildCount(); i++) {
            ImageView tile = (ImageView) center.getChildAt(i);
            int color = (tile.getTag(R.id.color_id)!=null) ? (int) tile.getTag(R.id.color_id) : 0;
            if(color != -1)tile.setOnTouchListener(new TileTouchListener());
        }
    }

    public void disableOnTouch() {
        // for plate tiles
        GridLayout board = view.findViewById(R.id.gridPlates);
        for (int i = 0; i < board.getChildCount(); i++) {
            GridLayout plate = (GridLayout) board.getChildAt(i);
            for (int j = 0; j < plate.getChildCount(); j++) {
                ImageView tile = (ImageView) plate.getChildAt(j);
                tile.setOnTouchListener(null);
            }
        }
        // for center tiles
        GridLayout center = view.findViewById(R.id.gridCenter);
        for (int i = 0; i < center.getChildCount(); i++) {
            ImageView tile = (ImageView) center.getChildAt(i);
            tile.setOnTouchListener(null);
        }
    }

    private final class TileTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("tile", view.getTag(R.id.color_id) + "|" + view.getTag(R.id.count_id));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    private void generatePlates(GameStart gameStart, int[][] tileColors) {

        int numberPlates = tileColors.length;
        GridLayout plates = view.findViewById(R.id.gridPlates);

        for(int i=0; i<numberPlates; i++){
            GridLayout tiles = (GridLayout) plates.getChildAt(i);

            int[] countColor = new int[5];
            for(int color : tileColors[i]){
                if(color!=0) countColor[color-1]++;
            }

            for (int j = 0; j < 4; j++) {
                int color = tileColors[i][j];
                ImageView tile = (ImageView) tiles.getChildAt(j);

                gameStart.runOnUiThread(() -> {
                    if(color == 0) tile.setImageResource(R.drawable.empty_fliese);
                    else {
                        tile.setImageResource(tileResourceMap[color - 1]);
                        tile.setTag(R.id.count_id, countColor[color - 1]);
                    }
                    tile.setTag(R.id.color_id, color - 1);
                    tile.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    public void initTiles(GameStart gameStart, int numberPlates){
        GridLayout plates = view.findViewById(R.id.gridPlates);

        for(int i=0; i<numberPlates; i++){
            GridLayout tiles = new GridLayout(requireContext());

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(10, 10, 10, 10);
            tiles.setLayoutParams(params);
            tiles.setColumnCount(2);
            tiles.setRowCount(2);
            tiles.setBackgroundResource(R.drawable.plate_design);
            tiles.setPadding(10, 10, 10, 10);

            for (int j = 0; j < 4; j++) {
                ImageView tile = new ImageView(requireContext());

                int size = (int) getResources().getDimension(R.dimen.fliese_size);
                tile.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                tile.setPadding(5,5,5,5);
                tile.setTag(R.id.plateNr_id, i);
                tile.setTag(R.id.isCenter, 0);

                tiles.addView(tile);
            }

            int finalI = i;
            gameStart.runOnUiThread(() -> plates.addView(tiles, finalI));
        }
    }

    private void addToCenter(int color, int count){
        GridLayout center = view.findViewById(R.id.gridCenter);
        ImageView tile = new ImageView(requireContext());
        int size = (int) getResources().getDimension(R.dimen.fliese_size);
        tile.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        tile.setPadding(5,5,5,5);

        tile.setImageResource(tileResourceMap[color]);
        tile.setTag(R.id.color_id, color);
        tile.setTag(R.id.count_id, count);
        tile.setTag(R.id.isCenter, 1);

        gameStart.runOnUiThread(() -> center.addView(tile));

    }
}
