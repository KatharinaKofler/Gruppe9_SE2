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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.helper.ResourceHelper;

public class BodenFragment extends Fragment {
    GridLayout gridLayout;

    // Elements-Array to store Bodenreihe
    Element[] elements = new Element[7];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize empty Elements-Array
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new Element();
        }

        // Fragment Benachrichtigungen
        getParentFragmentManager().setFragmentResultListener("floor", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                int color = bundle.getInt("color");
                int count = bundle.getInt("count");

                // Wenn count 0 oder kleiner dann keine Aktion
                if (count < 1) {
                    return;
                }

                // Bodenelemente setzen
                int j = 0;
                for (int i = 0; i < elements.length; i++) {
                    Element e = elements[i];
                    if (e.getColor() != 0) {
                        continue;
                    }

                    setBodenElement(i+1, color);
                    j++;
                    if (j >= count) {
                        break;
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boden, container, false);

        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        gridLayout = view.findViewById(R.id.gridBoden);

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

    private void setBodenElement(int pos, int color) {
        LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(6 + pos);
        ImageView image = (ImageView) linearLayout.getChildAt(0);
        if (image != null) {
            int resId = ResourceHelper.getFlieseResId(color);
            image.setImageResource(resId);

            Element e = elements[pos-1];
            e.setColor(color);
        }
    }

    private class Element {
        private int color = 0;

        public Element() {
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
