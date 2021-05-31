package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
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
    GridLayout gridLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //todo get the size of the Spielbrett Muster
        int size = 150;

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_muster, container, false);

        // Fill the 5x5 Grid with ImageViews
        gridLayout = view.findViewById(R.id.gridMuster);

        // Sample for Musterreihe
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                LinearLayout linearLayout = new LinearLayout(requireContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Test Fliese
                if (i == 0 && j == 0) {
                    String color = String.valueOf((int) (1 + Math.random() * 4));
                    String count = String.valueOf((int) (1 + Math.random() * 4));
                    int resId = getFlieseResId(color);

                    ImageView testImage = new ImageView(requireContext());
                    testImage.setImageResource(resId);
                    testImage.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    testImage.setPadding(5, 5, 5, 5);

                    testImage.setTag(color + "|" + count);

                    testImage.setOnTouchListener(new MyTouchListener());

                    linearLayout.addView(testImage);
                }

                if (j >= 4 - i) {
                    ImageView image = new ImageView(requireContext());
                    //todo create drawable for empty Image State
                    int imageId = R.drawable.empty_fliese;
                    image.setImageResource(imageId);
                    image.setTag((i) + "|" + (j));
                    image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    image.setPadding(5, 5, 5, 5);

                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] pos = v.getTag().toString().split("\\|");
                            Toast.makeText(getContext(), "Click Row: " + pos[0]+1 + " Col: " + pos[1]+1, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Test Drop nur auf letzter Spalte
                    if (j == 4) {
                        image.setOnDragListener(new MyDragListener());
                    }

                    linearLayout.addView(image);
                }

                gridLayout.addView(linearLayout, i * 5 + j);
            }
        }

        return view;
    }


    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                String[] tags = view.getTag().toString().split("\\|");
//                int color = (int) (1 + Math.random() * 4);
                ClipData data = ClipData.newPlainText("tile", view.getTag().toString());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
//                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
//                    ((View) event.getLocalState()).setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DROP:
                    ClipData data = event.getClipData();
                    String[] tile = data.getItemAt(0).getText().toString().split("\\|");
                    if (Integer.parseInt(tile[1]) > 0) {
                        int resId = getFlieseResId(tile[0]);
                        String[] pos = v.getTag().toString().split("\\|");
                        for (int i = 0; i < Integer.parseInt(tile[1]); i++) {
                            int tilePos = Integer.parseInt(pos[0]) * 5 + (4-i);

                            LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(tilePos);
                            ImageView image = (ImageView) linearLayout.getChildAt(0);
                            if (image != null) {
                                image.setImageResource(resId);
                            }
                        }

//                        ((ImageView) v).setImageResource(resId);

                        //TODO








                    }

                    // Dropped, reassign View to ViewGroup
//                    View view = (View) event.getLocalState();
//                    ViewGroup owner = (ViewGroup) view.getParent();
//                    owner.removeView(view);
//                    LinearLayout container = (LinearLayout) v;
//                    container.addView(view);
//                    view.setVisibility(View.VISIBLE);


                    Toast.makeText(getContext(), "Dropped", Toast.LENGTH_SHORT).show();


                    View view = (View) event.getLocalState();
                    String color = String.valueOf((int) (1 + Math.random() * 4));
                    String count = String.valueOf((int) (1 + Math.random() * 4));
                    int resId = getFlieseResId(color);
                    ((ImageView) view).setImageResource(resId);
                    view.setTag(color + "|" + count);
                    view.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private int getFlieseResId(String color) {
        int resId;
        switch (color) {
            case "1":
                resId = R.drawable.fliese_color1;
                break;
            case "2":
                resId = R.drawable.fliese_color2;
                break;
            case "3":
                resId = R.drawable.fliese_color3;
                break;
            case "4":
                resId = R.drawable.fliese_color4;
                break;
            case "5":
                resId = R.drawable.fliese_color5;
                break;
            default:
                resId = R.drawable.empty_fliese;
        }
        return resId;
    }
}
