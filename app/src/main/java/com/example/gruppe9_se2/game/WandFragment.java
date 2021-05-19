package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.content.ClipDescription;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

import static java.util.Collections.singletonList;

public class WandFragment extends Fragment implements EventListener {

    private final int[] emptyFliesenOrder = {R.drawable.empty_fliese_color1, R.drawable.empty_fliese_color2, R.drawable.empty_fliese_color3, R.drawable.empty_fliese_color4, R.drawable.empty_fliese_color5};
    private final int[] fullFliesenOrder = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};

    private Socket mSocket;

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

        // for testing:
        if(firstCreate){
            add(3, 2);
            add(1, 4);
            add(2, 5);
            firstCreate = false;
        }

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
            // set listener for drop event
            linearLayout.setOnDragListener(this::addDropListener);

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

    public void add(int row, int color){ // row 0 to 4, color 1 to 5
        color--;
        int imageId = fullFliesenOrder[color];
        // find LinearLayout where to add add the imageId
        int index = ((color + (5-row)) % 5) + (row * 5);
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

        //todo calculate points

        //todo adds points to total player points
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

    // todo delete all following functions

    private boolean addDragListener(View v) {

        ClipData.Item item = new ClipData.Item(v.getTag(R.id.drawable_id).toString());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        // clip description Tag is important for setting the correct drawable on drop
        ClipData dragData = new ClipData(v.getTag(R.id.drawable_id).toString(), mimeTypes, item);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

        v.startDrag(dragData, myShadow, v, 0);
        
        // this sets the image to an emptyFliese image in the correct color
        int colorId = (int) v.getTag(R.id.color_id);
        setImage((ImageView) v, emptyFliesenOrder[colorId]);
        ((LinearLayout)v.getParent()).setTag(R.id.assigned, false);

        return true;
    }

    private void setImage(ImageView iv, int drawable_id){
        iv.setImageResource(drawable_id);
        iv.setTag(R.id.drawable_id, drawable_id);
    }

    private boolean addDropListener(View v, DragEvent event){
        // other Events: ACTION_DRAG_STARTED, ACTION_DRAG_ENTERED, ACTION_DRAG_LOCATION, ACTION_DRAG_EXITED, ACTION_DRAG_ENDED
        // Drop Event fires, when dragged Object is dropped on this View
        if (event.getAction() == DragEvent.ACTION_DROP) {

            ImageView dragView = (ImageView) event.getLocalState();
            int drawableId = Integer.parseInt((String) event.getClipData().getDescription().getLabel());

            boolean alreadyAssigned = (boolean) v.getTag(R.id.assigned);
            int acceptableColor = (int) v.getTag(R.id.acceptable_color_id);
            int dragColor = (int) dragView.getTag(R.id.color_id);
            boolean correctColor = (acceptableColor == dragColor);
            if((!alreadyAssigned) && correctColor){

                // drop is ok -> change view
                v.setTag(R.id.assigned, true);
                // update JSON
                int index = 0;
                try {
                    index = (int) v.getTag(R.id.index_id);
                    JSONObject updated =  wand.getJSONObject(index);
                    updated.remove("assigned");
                    updated.put("assigned", true);
                    wand.put(index, updated);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // set dropView to the image dragged there
                ImageView dropView = (ImageView) ((LinearLayout) v).getChildAt(0);
                setImage(dropView, drawableId);
                //todo change to OnTouch with MotionEvent ACTION_DOWN
                dropView.setOnLongClickListener(this::addDragListener);

                JSONObject drop = new JSONObject();
                try {
                    drop.put("from", (int) ((LinearLayout)dragView.getParent()).getTag(R.id.index_id));
                    drop.put("to", index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("dropOnWand", drop);
                //todo let Server send Event to other Fragment where the drag came from
            }

            else{
                //todo print error message to screen, you can't drop here
                // and maybe tell Musterreihe Fragment

                // change dragview image back to what it prev was
                setImage(dragView, drawableId);
                ((LinearLayout)dragView.getParent()).setTag(R.id.assigned, true);
                //todo remove following line, just for testing
                setImage(dragView, fullFliesenOrder[dragColor]);

                return false;
            }
        }
        return true;
    }

    private int getEmptyFLieseId(int index){
        int color = ((index % 5) + (index / 5)) % 5;
        return emptyFliesenOrder[color];
    }

    private int getFullFLieseId(int index){
        int color = ((index % 5) + (index / 5)) % 5;
        return fullFliesenOrder[color];
    }

    private int getColorId(int index){
        return ((index % 5) + (index / 5)) % 5;
    }

}
