package com.example.gruppe9_se2.game;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EventListener;

public class WandFragment extends Fragment implements EventListener {

    private final int[] emptyFliesenOrder = {R.drawable.empty_fliese_color1, R.drawable.empty_fliese_color2, R.drawable.empty_fliese_color3, R.drawable.empty_fliese_color4, R.drawable.empty_fliese_color5};
    private final int[] fullFliesenOrder = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};

    static JSONArray wand = new JSONArray();
    static boolean firstCreate = true;
    GridLayout gridLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate and build the layout from JSONArray
        View view = inflater.inflate(R.layout.fragment_wand, container, false);

        if(firstCreate){
            createEmptyArray(wand);
            //firstCreate = false;
        }
        init(view, wand);

        return view;
    }

    // Fill the 5x5 Grid with ImageViews
    private void init(View view, JSONArray jsonArray) {
        // Imagesize
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        // -- GRIDLAYOUT --
        gridLayout = view.findViewById(R.id.gridWand);

        for (int i = 0; i < 25; i++) {
            // read JSONArray data
            boolean assigned = false;
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                assigned = jsonObject.getBoolean("assigned");
            } catch (JSONException e) {
                // JSONArray not in correct form
                Log.e("WandFragment", "JSONArray incorrect");
                e.printStackTrace();
            }

            // -- LINEARLAYOUT --
            // create a linearLayout, every one holds one image at a time (image changes), has a drop event listener, and saves it index in a tag
            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set 3 tags
            linearLayout.setTag(R.id.index_id, i); // never changes
            linearLayout.setTag(R.id.acceptable_color_id, getColorId(i)); // never changes
            linearLayout.setTag(R.id.assigned, assigned); // changes

            // -- IMAGEVIEW --
            // create imageview for the prev created linear Layout, imageviews can be dragged, but it never changes position, just the imageRessource id changes, tag holds the current drawableId and colorId
            ImageView image = new ImageView(requireContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            image.setPadding(5,5,5,5);
            if(assigned) image.setImageResource(getFullFLieseId(i));
            else image.setImageResource(getEmptyFLieseId(i));
            // set 2 tags
            image.setTag(R.id.drawable_id, getEmptyFLieseId(i)); // changes, represents the current image shown
            image.setTag(R.id.color_id, getColorId(i)); // never changes important to know what color it has (0: rot, 1: grün, 2: blau, 3: lila, 4: orange)

            // add ImageView to LinearLayout, and LinearLayout to GridLayout
            linearLayout.addView(image);
            gridLayout.addView(linearLayout, i);
        }
    }

    public void add(int row, int color){ // row 0 to 4, color 0 to 4
        color--;
        int imageId = fullFliesenOrder[color];
        // find LinearLayout where to add add the imageId
        int index = color + (6*row);
        if(index > (5-color)*5) index -= 5;
        // update assigned in JSONArray
        try {
            JSONObject updated = wand.getJSONObject(index);
            updated.remove("assigned");
            updated.put("assigned", true);
            wand.put(index, updated);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // update assigned in linearLayout
        LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(index);
        linearLayout.setTag(R.id.assigned, true);
        // update image
        ImageView imageView = (ImageView) linearLayout.getChildAt(0);
        imageView.setImageResource(imageId);
        imageView.setTag(R.id.drawable_id, imageId);
    }

    private void createEmptyArray(JSONArray jsonArray){
        for(int i=0;i<25;i++){
            try {
                JSONObject field = new JSONObject();
                field.put("assigned", false);
                jsonArray.put(i, field);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int getEmptyFLieseId(int index){
        int color = ((index % 5) - (index / 5));
        if(color<0) color += 5;
        return emptyFliesenOrder[color];
    }

    private int getFullFLieseId(int index){
        int color = ((index % 5) - (index / 5));
        if(color<0) color += 5;
        return fullFliesenOrder[color];
    }

    private int getColorId(int index){
        return ((index % 5) + (index / 5)) % 5;
    }

}
