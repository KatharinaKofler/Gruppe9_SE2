package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;

public class BoardFragment extends Fragment {
    private final int[] tileResourceMap = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board, container, false);

        //TODO replace test values with event handler

        int numberPlates = 9;
        int[][] tileColors = {{1, 5, 3, 5}, {3, 3, 3, 5}, {2, 4, 2, 1}, {1, 5, 3, 5}, {1, 5, 3, 5}, {1, 5, 3, 5}, {1, 5, 3, 5}, {1, 5, 3, 5}, {1, 5, 3, 5}};
        int[] centerColors = {27, 0, 0, 0, 0};

        generatePlates(numberPlates, tileColors);

        addToCenter(0, view);
        addToCenter(1, view);
        addToCenter(2, view);
        addToCenter(3, view);
        addToCenter(4, view);
        addToCenter(3, view);

        return view;
    }

    private final class TileTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                ClipData data = ClipData.newPlainText("tile", (String) view.getTag());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    public void dropped(int indexPlate, int color){
        GridLayout plate = (GridLayout) ((GridLayout) view.findViewById(R.id.gridPlates)).getChildAt(indexPlate);
        for (int i = 0; i < 4; i++) {
            ImageView image = (ImageView) plate.getChildAt(i);

            String[] pos = image.getTag().toString().split("\\|");
            int tagColor = Integer.parseInt(pos[0]);

            if(tagColor!=color){
                addToCenter(color, view);
            }
            image.setVisibility(View.INVISIBLE);
        }
    }


    private void generatePlates(int numberPlates, int[][] tileColors) {

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

            int[] countColor = new int[5];
            for(int color : tileColors[i]){
                countColor[color-1]++;
            }

            for(int color : tileColors[i]){
                ImageView tile = new ImageView(requireContext());

                int size = (int) getResources().getDimension(R.dimen.fliese_size);
                tile.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                tile.setPadding(5,5,5,5);
                tile.setImageResource(tileResourceMap[color - 1]);

                tiles.addView(tile);
            }

            plates.addView(tiles, i);
        }
    }

    private void addToCenter(int color, View view){
        GridLayout center = view.findViewById(R.id.gridCenter);
        ImageView tile = new ImageView(requireContext());
        int size = (int) getResources().getDimension(R.dimen.fliese_size);
        tile.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        tile.setPadding(5,5,5,5);

        tile.setImageResource(tileResourceMap[color]);
        int count = countColorCenter(color, view);
        tile.setTag(color + "|" + count);
        tile.setOnTouchListener(new TileTouchListener());

        center.addView(tile);
    }

    private int countColorCenter(int color, View view) {
        int count = 0;
        GridLayout center = view.findViewById(R.id.gridCenter);
        int n = center.getChildCount();
        for (int i = 0; i < n; i++) {
            ImageView image = (ImageView) center.getChildAt(i);
            String[] pos = image.getTag().toString().split("\\|");
            int tagColor = Integer.parseInt(pos[0]);
            if(tagColor == color){
                count++;
            }
        }
        return count;
    }
}