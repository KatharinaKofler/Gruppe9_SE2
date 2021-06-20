package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.helper.ResourceHelper;
import com.example.gruppe9_se2.logic.GameStart;
import org.json.JSONException;
import org.json.JSONObject;

public class BodenFragment extends Fragment {
    // GameStart
    private final GameStart gameStart;

    GridLayout gridLayout;

    // Elements-Array to store Bodenreihe
    Element[] elements = new Element[7];

    public BodenFragment(GameStart gameStart) {
        this.gameStart = gameStart;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize empty Elements-Array
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new Element();
        }

        // Fragment Notifications
        getParentFragmentManager().setFragmentResultListener("floor", this, (requestKey, bundle) -> {
            int color = bundle.getInt("color");
            int count = bundle.getInt("count");

            fillBodenElements(count, color);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boden, container, false);

        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        gridLayout = view.findViewById(R.id.gridBoden);
        gridLayout.setOnDragListener(new BodenDragListener());

        for (int i = 0; i < 7; i++) {
            RelativeLayout layout = new RelativeLayout(requireContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView imageView = new ImageView(requireContext());
            imageView.setImageResource(R.drawable.empty_fliese);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            imageView.setPadding(5, 5, 5, 5);

            TextView textView = new TextView(requireContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            textView.setGravity(Gravity.CENTER);

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

            layout.addView(imageView);
            layout.addView(textView);
            gridLayout.addView(layout, i);
        }

        return view;
    }

    private class BodenDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(null);
                    break;
                case DragEvent.ACTION_DROP:
                    if ((boolean) ((ImageView) event.getLocalState()).getTag(R.id.fromBoard)) {
                        return false;
                    }

                    Bundle result = new Bundle();
                    result.putBoolean("clearNewTileField", true);
                    getParentFragmentManager().setFragmentResult("pattern", result);

                    ClipData data = event.getClipData();
                    String[] tile = data.getItemAt(0).getText().toString().split("\\|");
                    int count = Integer.parseInt(tile[1]);
                    int color = Integer.parseInt(tile[0]);

                    fillBodenElements(count, color);

                    // If manually dropping to Bodenreihe Send finishTurn with row 0 to server
                    gameStart.finishTurn(0);

                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void fillBodenElements(int count, int color) {
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

            setBodenElement(i + 1, color);
            j++;
            if (j >= count) {
                break;
            }
        }
    }

    private void setBodenElement(int pos, int color) {
        RelativeLayout layout = (RelativeLayout) gridLayout.getChildAt(pos - 1);
        ImageView image = (ImageView) layout.getChildAt(0);
        if (image != null) {
            int resId = ResourceHelper.getFlieseResId(color);
            image.setImageResource(resId);

            Element e = elements[pos - 1];
            e.setColor(color);
        }

        TextView text = (TextView) layout.getChildAt(1);
        if (text != null) {
            if (color != 0) {
                text.setTextColor(Color.WHITE);
            } else {
                text.setTextColor(Color.BLACK);
            }
        }
    }

    public void startRound() {
        for (int i = 0; i < elements.length; i++) {
            Element e = elements[i];
            e.color = 0;

            setBodenElement(i + 1, 0);
        }
    }

    public void cheatResponse(JSONObject arg, GameStart gameStart) {
        try {
            boolean success = arg.getBoolean("success");
            if (success) {
                clearFloor();
                gameStart.deleteShakeDetector();
            } else {
                Toast.makeText(requireContext(), "You can't cheat here", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearFloor() {
        for (int i = 0; i < 7; i++) {
            RelativeLayout layout = (RelativeLayout) gridLayout.getChildAt(i);
            ImageView image = (ImageView) layout.getChildAt(0);
            image.setImageResource(R.drawable.empty_fliese);
            elements[i].setColor(0);
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
