package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.user.Lobby;

import java.util.EventListener;

import static java.lang.Integer.getInteger;

public class WandFragment extends Fragment implements EventListener {

    String logTag = "wandFragmentLogs";
    //todo save all information about this Wand, so it can be a JSON

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //todo get the size of the Spielbrett Wand
        int size = 70;

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wand, container, false);

        // Fill the 5x5 Grid with ImageViews
        GridLayout gridLayout = view.findViewById(R.id.gridWand);

        for (int i = 0; i < 25; i++) {
            LinearLayout linearLayout = new LinearLayout(requireContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView image = new ImageView(requireContext());
            //todo create drawable for empty Image State
            int imageId = R.drawable.fliese1;
            image.setImageResource(imageId);
            image.setTag(imageId);
            image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            image.setPadding(5,5,5,5);

            //todo remove the Listener, its only for images added this round
            // start the drag on a long click
            //todo change to OnTouch with MotionEvent ACTION_DOWN
            image.setOnLongClickListener(v -> {

                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag().toString());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(image);

                v.startDrag(dragData, myShadow, image, 0);
                v.setVisibility(View.INVISIBLE);
                return true;
            });

            // set the listeners for drag events
            linearLayout.setOnDragListener((v, event) -> {

                // other Events: ACTION_DRAG_STARTED, ACTION_DRAG_ENTERED, ACTION_DRAG_LOCATION, ACTION_DRAG_EXITED, ACTION_DRAG_ENDED
                // Drop Event fires, when dragged Object is dropped on this View
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    //todo check if drop allowed
                    //todo return false if not allowed to drop here

                    // drop is ok -> change view
                    ImageView dragView = (ImageView) event.getLocalState();
                    ImageView dropView = (ImageView) ((LinearLayout) v).getChildAt(0);
                    // set dropView to the image dragged there
                    int tag = (int) dragView.getTag();
                    dropView.setImageResource(tag);
                    dropView.setTag(tag);
                    dropView.setVisibility(View.VISIBLE);

                    //todo send Server what was dragged and dropped where
                    //let Server send Event to other Fragment where the drag came from

                    // for showcasing that drag and drop works
                    /*int imageIdDrag = R.drawable.fliese2;
                    dragView.setImageResource(imageIdDrag);
                    dragView.setTag(imageIdDrag);
                    dragView.setVisibility(View.VISIBLE);*/
                }
                return true;
            });

            linearLayout.addView(image);
            gridLayout.addView(linearLayout, i);
        }

        return view;
    }

}
