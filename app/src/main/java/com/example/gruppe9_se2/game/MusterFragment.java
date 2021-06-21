package com.example.gruppe9_se2.game;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.helper.ResourceHelper;
import com.example.gruppe9_se2.logic.GameStart;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EventListener;

public class MusterFragment extends Fragment implements EventListener {
    // GameStart
    private GameStart gameStart;
    private GridLayout gridLayout;
    private ImageView newTileImage;
    private TextView newTileText;

    // Elements-Array to store Musterreihe
    private final Element[] elements = new Element[5];

    private boolean starterMarker = false;

    private int lastRowDrop;
    private int lastColorDrop;
    private int lastCountDrop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize empty Elements-Array
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new Element();
        }

        // Fragment Notifications
        getParentFragmentManager().setFragmentResultListener("pattern", this, (requestKey, bundle) -> {
            boolean isClearNewTileField = bundle.getBoolean("clearNewTileField");
            if (isClearNewTileField) {
                clearNewTileField();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_muster, container, false);

        // Size of Tile
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        // Fill the 5x5 Grid with ImageViews
        gridLayout = view.findViewById(R.id.gridMuster);
        newTileImage = view.findViewById(R.id.newTileImage);
        newTileText = view.findViewById(R.id.newTileText);

        // Create Musterreihe
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                LinearLayout linearLayout = new LinearLayout(requireContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (j > (3 - i)) {
                    ImageView image = new ImageView(requireContext());
                    image.setImageResource(R.drawable.empty_fliese);
                    image.setTag((i + 1) + "|" + (j + 1));
                    image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    image.setPadding(5, 5, 5, 5);

                    // Test Drop nur auf letzter Spalte
                    if (j == 4) {
                        image.setOnDragListener(new MusterDragListener());
                    }

                    linearLayout.addView(image);
                }
                gridLayout.addView(linearLayout, i * 5 + j);
            }
        }

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addNewTileField(int color, int count) {
        // Set tile color
        newTileImage.setImageResource(ResourceHelper.getFlieseResId(color + 1));
        newTileImage.setTag((color + 1) + "|" + count);
        newTileImage.setOnTouchListener(new MyTouchListener());
        newTileImage.setTag(R.id.fromBoard, false);

        // Set tile text
        newTileText.setText(String.valueOf(count));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clearNewTileField() {
        // Clear tile color
        newTileImage.setImageResource(R.drawable.empty_fliese);
        newTileImage.setOnTouchListener(null);

        // Clear tile text
        newTileText.setText(null);
    }

    public void dragListenerNewTileField(GameStart gameStart) {
        this.gameStart = gameStart;
        newTileImage.setOnDragListener(new NewTileDragListener());
    }

    private class NewTileDragListener implements View.OnDragListener {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable allowedShape = getResources().getDrawable(R.drawable.shape_drop_target_allowed);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if ((boolean) ((ImageView) event.getLocalState()).getTag(R.id.fromBoard)) {
                        v.setBackground(allowedShape);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(null);
                    break;
                case DragEvent.ACTION_DROP:

                    gameStart.finishMyTurn();

                    ImageView imageView = (ImageView) event.getLocalState();

                    if((boolean) imageView.getTag(R.id.starterMarker)) addStarterMarker();

                    int color = (int) imageView.getTag(R.id.color_id);
                    lastColorDrop = color;
                    int count = (int) imageView.getTag(R.id.count_id);
                    lastCountDrop = count;

                    // check if Image comes from Center or Plates
                    int i = (int) imageView.getTag(R.id.isCenter);
                    if (i == 1) {
                        // Center
                        // args.color = int 1 bis 5
                        JSONObject args = new JSONObject();
                        try {
                            args.put("color", color + 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        gameStart.takeCenterTiles(args);
                    } else {
                        // Plates
                        // args.plate string plate0 bis plate8 und args.color = int 1 bis 5
                        int plate = (int) imageView.getTag(R.id.plateNr_id);
                        JSONObject args = new JSONObject();
                        try {
                            args.put("plate", "plate" + plate);
                            args.put("color", color + 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        gameStart.takePlateTiles(args);
                    }
                    imageView.setOnDragListener(null);
                    addNewTileField(color, count);
                    gameStart.disableOnTouchBoard();
                    break;
                default:
                    break;
            }
            return true;
        }
    }



    private static final class MyTouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("tile", view.getTag().toString());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private class MusterDragListener implements View.OnDragListener {

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable allowedShape = getResources().getDrawable(R.drawable.shape_drop_target_allowed);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable forbiddenShape = getResources().getDrawable(R.drawable.shape_drop_target_forbidden);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (!(boolean) ((ImageView) event.getLocalState()).getTag(R.id.fromBoard)) {
                        int row = Integer.parseInt(v.getTag().toString().split("\\|")[0]);
                        Element element = elements[row - 1];

                        // If row already full, drop not allowed
                        if (element.getCount() == row) {
                            v.setBackground(forbiddenShape);
                        }
                        else{
                            v.setBackground(allowedShape);
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(null);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    boolean dropped = event.getResult();
                    Log.e("MusterFragment", "Dropped: "+dropped);
                    if(!dropped) {
                        clearNewTileField();
                        addNewTileField(lastColorDrop, lastCountDrop);
                    }
                    v.setBackground(null);
                    break;
                case DragEvent.ACTION_DROP:
                    if ((boolean) ((ImageView) event.getLocalState()).getTag(R.id.fromBoard)) {
                        return false;
                    }

                    clearNewTileField();

                    ClipData data = event.getClipData();
                    String[] tile = data.getItemAt(0).getText().toString().split("\\|");
                    int count = Integer.parseInt(tile[1]);
                    Log.e("MusterFragment", "count: "+count);
                    if (count > 0) {
                        int row = Integer.parseInt(v.getTag().toString().split("\\|")[0]);
                        lastRowDrop = row;
                        int color = Integer.parseInt(tile[0]);

                        if(gameStart.isColorInRow(color, row)) return false;

                        Element element = elements[row - 1];

                        // If row is empty set color of row
                        if (element.getColor() == 0) {
                            Log.e("MusterFragment", "element.getColor == 0");
                            element.setColor(color);
                        }

                        // Row color different, drop not allowed
                        if (element.getColor() != color) {
                            Log.e("MusterFragment", "element.getColor != 0");
                            return false;
                        }

                        // Row already full, drop not allowed
                        if (element.getCount() == row) {
                            Log.e("MusterFragment", "element.getCount == row");
                            return false;
                        }

                        // If no empty row than activate floor as drop target
                        boolean activateFloorDrag = true;
                        for (Element e : elements) {
                            if (e.getColor() == 0) {
                                //activateFloorDrag = false;
                                break;
                            }
                        }
                        if (activateFloorDrag) {
                            Bundle result = new Bundle();
                            result.putBoolean("activateFloorDrag", true);
                            getParentFragmentManager().setFragmentResult("floor", result);
                        }

                        // Set elements and notify server
                        setMusterElement(row, element, count);

                        if(starterMarker){
                            addStarterMarker();
                            starterMarker = false;
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    public void clearPatternRow(){
        for (int j = 0; j < 5; j++) {
            if (j > (3 - lastRowDrop)) {
                LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt((lastRowDrop * 5) + j);
                ImageView imageView = (ImageView) linearLayout.getChildAt(0);
                imageView.setImageResource(R.drawable.empty_fliese);
            }
        }
    }

    private void clearMusterElemente(int row) {
        for (int j = 0; j < 5; j++) {
            if (j > (3 - row)) {
                LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt((row * 5) + j);
                ImageView imageView = (ImageView) linearLayout.getChildAt(0);
                imageView.setImageResource(R.drawable.empty_fliese);
            }
        }
    }

    private void setMusterElement(int row, Element element, int count) {
        Log.e("MusterFragment", "setMusterElement");
        int resId = ResourceHelper.getFlieseResId(element.getColor());
        int floor = element.addCount(row, count);

        Log.e("MusterFragment", "element.getCount "+element.getCount());
        for (int i = 0; i < element.getCount(); i++) {

            int tilePos = (row - 1) * 5 + (4 - i);
            Log.e("MusterFragment", "tilePos "+tilePos);
            LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(tilePos);
            ImageView image = (ImageView) linearLayout.getChildAt(0);
            if (image != null) {
                image.setImageResource(resId);
            }
        }

        if (element.getColor() != 0) {
            if (floor > 0) {
                Bundle result = new Bundle();
                result.putInt("color", element.getColor());
                result.putInt("count", floor);
                getParentFragmentManager().setFragmentResult("floor", result);
            }

            // Send finishTurn to server
            gameStart.finishTurn(row);
        }
    }

    private void addStarterMarker(){
        Bundle result = new Bundle();
        result.putInt("color", -1);
        result.putInt("count", 1);
        getParentFragmentManager().setFragmentResult("floor", result);
    }

    public void startRound(WandFragment wandFragment) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getCount() == i + 1) {
                // If row is full then add to Wandfragment
                wandFragment.add(i, elements[i].getColor());

                // Clear row
                clearMusterElemente(i);

                // Clear element
                Element e = elements[i];
                e.clear();
            }
        }
    }

    private static class Element {
        private int color = 0;
        private int count = 0;

        public Element() {
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getCount() {
            return count;
        }

        public int addCount(int row, int count) {
            int temp = this.count + count;
            Log.e("MusterFragment", "temp "+temp);
            if (temp <= row) {
                this.count = temp;
                return 0;
            } else {
                this.count = row;
                return temp - row;
            }
        }

        public void clear() {
            this.color = 0;
            this.count = 0;
        }
    }
}
