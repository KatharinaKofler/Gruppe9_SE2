package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.DragEvent;
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
    //private int[] emptyFliesenOrder = {R.drawable.fliese_color1, R.drawable.fliese_color2, R.drawable.fliese_color3, R.drawable.fliese_color4, R.drawable.fliese_color5};


    //todo save all information about this Wand, so it can be a JSON
    JSONArray wand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        // create JSON
        wand = new JSONArray();

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wand, container, false);

        // Fill the 5x5 Grid with ImageViews
        GridLayout gridLayout = view.findViewById(R.id.gridWand);

        for (int i = 0; i < 25; i++) {
            // fill JSON with data
            try {
                JSONObject field = new JSONObject();
                field.put("assigned", false);
                wand.put(i, field);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // create a linearLayout, every one holds one image at a time (image changes), has a drop event listener, and saves it index in a tag
            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set 3 tags
            linearLayout.setTag(R.id.index_id, i); // never changes
            linearLayout.setTag(R.id.acceptable_color_id, getColorId(i)); // never changes
            linearLayout.setTag(R.id.assigned, false); // changes

            // create imageview for the prev created linear Layout, imageviews can be dragged, but it never changes position, just the imageRessource id changes, tag holds the current drawableId and colorId
            ImageView image = new ImageView(requireContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            image.setPadding(5,5,5,5);
            image.setImageResource(getEmptyFLieseId(i));
            // set 2 tags
            image.setTag(R.id.drawable_id, getEmptyFLieseId(i)); // changes, represents the current image shown
            image.setTag(R.id.color_id, getColorId(i)); // never changes important to know what color it has (0: rot, 1: grün, 2: blau, 3: lila, 4: orange)

            //todo remove the Listener, its only for images added this round
            // start the drag on a long click
            //todo change to OnTouch with MotionEvent ACTION_DOWN
            image.setOnLongClickListener(v -> {

                // start the drag of imageView

                ClipData.Item item = new ClipData.Item(v.getTag(R.id.drawable_id).toString());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag(R.id.drawable_id).toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(image);

                v.startDrag(dragData, myShadow, image, 0);

                // this sets the image to an emptyFliese image in the correct color
                int colorId = (int) v.getTag(R.id.color_id);
                ((ImageView) v).setImageResource(emptyFliesenOrder[colorId]);
                return true;
            });

            // set the listeners for drag events
            linearLayout.setOnDragListener((v, event) -> {

                // other Events: ACTION_DRAG_STARTED, ACTION_DRAG_ENTERED, ACTION_DRAG_LOCATION, ACTION_DRAG_EXITED, ACTION_DRAG_ENDED
                // Drop Event fires, when dragged Object is dropped on this View
                if (event.getAction() == DragEvent.ACTION_DROP) {

                    ImageView dragView = (ImageView) event.getLocalState();

                    boolean alreadyAssigned = (boolean) v.getTag(R.id.assigned);
                    int acceptableColor = (int) v.getTag(R.id.acceptable_color_id);
                    int dragColor = (int) dragView.getTag(R.id.color_id);
                    boolean correctColor = (acceptableColor == dragColor);
                    if((!alreadyAssigned) && correctColor){

                        // drop is ok -> change view
                        v.setTag(R.id.assigned, true);
                        // update JSON
                        try {
                            int index = (int) v.getTag(R.id.index_id);
                            JSONObject updated =  wand.getJSONObject(index);
                            updated.remove("assigned");
                            updated.put("assigned", true);
                            wand.put(index, updated);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // set dropView to the image dragged there
                        ImageView dropView = (ImageView) ((LinearLayout) v).getChildAt(0);
                        int drawableId = (int) dragView.getTag(R.id.drawable_id);
                        dropView.setImageResource(drawableId);
                        dropView.setTag(R.id.drawable_id, drawableId);

                        //todo send Server what was dragged and dropped where
                        // let Server send Event to other Fragment where the drag came from
                    }

                    else{
                        //todo print error message to screen, you can't drop here
                        // and maybe tell Musterreihe Fragment
                        return false;
                    }
                }
                return true;
            });

            linearLayout.addView(image);
            gridLayout.addView(linearLayout, i);
        }

        return view;
    }

    private int getEmptyFLieseId(int index){
        int color = ((index % 5) + (index / 5)) % 5;
        return emptyFliesenOrder[color];
    }

    private int getColorId(int index){
        return ((index % 5) + (index / 5)) % 5;
    }

}
