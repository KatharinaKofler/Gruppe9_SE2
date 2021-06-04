package com.example.gruppe9_se2.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;

public class BoardFragment extends Fragment {
    private final int[] fullFliesenOrder = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board, container, false);
        return view;
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

            for(int color : tileColors[i]){
                ImageView tile = new ImageView(requireContext());

                int size = (int) getResources().getDimension(R.dimen.fliese_size);
                tile.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                tile.setPadding(5,5,5,5);
                tile.setImageResource(fullFliesenOrder[color - 1]);

                tiles.addView(tile);
            }

            plates.addView(tiles, i);
        }
    }
}