package com.example.gruppe9_se2.game;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;

import java.util.EventListener;

public class BodenFragment extends Fragment implements EventListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boden, container, false);

        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        GridLayout gridLayout = view.findViewById(R.id.gridBoden);

        for (int i = 0; i < 7; i++) {
            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(requireContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

            String label;
            if (i < 2) {
                label = "-1";
            } else if (i < 5) {
                label = "-2";
            } else {
                label = "-3";
            }
            textView.setText(label);
            textView.setTypeface(null, Typeface.BOLD);

            linearLayout.addView(textView);
            gridLayout.addView(linearLayout, i);
        }

        for (int i = 7; i < 14; i++) {
            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView imageView = new ImageView(requireContext());
            imageView.setImageResource(R.drawable.empty_fliese);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            imageView.setPadding(5, 5, 5, 5);

            linearLayout.addView(imageView);
            gridLayout.addView(linearLayout, i);
        }

        return view;
    }
}
