package com.example.gruppe9_se2.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;

import java.util.EventListener;

public class MusterFragment extends Fragment implements EventListener {
    String logTag = "musterFragmentLogs";
    //todo save all information about this Muster, so it can be a JSON

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //todo get the size of the Spielbrett Muster
        int size = 150;

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_muster, container, false);

        // Fill the 5x5 Grid with ImageViews
        GridLayout gridLayout = view.findViewById(R.id.gridMuster);

        // Sample for Musterreihe
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                LinearLayout linearLayout = new LinearLayout(requireContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (j >= 4 - i) {
                    ImageView image = new ImageView(requireContext());
                    //todo create drawable for empty Image State
                    int imageId = R.drawable.fliese1;
                    image.setImageResource(imageId);
                    image.setTag((i + 1) + "|" + (j + 1));
                    image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    image.setPadding(5, 5, 5, 5);

                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] pos = v.getTag().toString().split("\\|");
                            Toast.makeText(getContext(), "Click Row: " + pos[0] + " Col: " + pos[1], Toast.LENGTH_SHORT).show();
                        }
                    });

                    linearLayout.addView(image);
                }

                gridLayout.addView(linearLayout, i * 5 + j);
            }
        }

        return view;
    }
}
