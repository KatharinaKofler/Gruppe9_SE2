package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.helper.ResourceHelper;
import com.example.gruppe9_se2.logic.SocketManager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.EventListener;

public class MusterFragment extends Fragment implements EventListener {
    String logTag = "musterFragmentLogs";
    //todo save all information about this Muster, so it can be a JSON
    GridLayout gridLayout;
    Socket mSocket = SocketManager.getSocket();

    // Elements-Array to store Musterreihe
    Element[] elements = new Element[5];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: Remove, Test only
//        mSocket = SocketManager.makeSocket("82a1ac5f-b1dc-4034-8667-38e345bdc423");

        // Initialize empty Elements-Array
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new Element();
        }

        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_muster, container, false);

        //todo get the size of the Spielbrett Muster
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

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
                    int resId = ResourceHelper.getFlieseResId(color);

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
                    int count = Integer.parseInt(tile[1]);
                    if (count > 0) {
                        int row = Integer.parseInt(v.getTag().toString().split("\\|")[0]);
                        int color = Integer.parseInt(tile[0]);

                        Element element = elements[row];
                        if (element.getColor() == 0) {
                            element.setColor(color);
                        }

                        if (element.getColor() != color) {
                            return false;
                        }

                        int resId = ResourceHelper.getFlieseResId(color);
                        for (int i = 0; i < count; i++) {
                            int tilePos = row * 5 + (4-i);

                            LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(tilePos);
                            ImageView image = (ImageView) linearLayout.getChildAt(0);
                            if (image != null) {
                                image.setImageResource(resId);
                            }
                        }

                        int test = 1;
                        Bundle result = new Bundle();
                        result.putInt("color", color);
                        result.putInt("count", test);
                        getParentFragmentManager().setFragmentResult("floor", result);

//                        ((ImageView) v).setImageResource(resId);

                        //TODO Socket
                        if (mSocket != null) {
                            mSocket.emit("finishTurn", String.valueOf(Integer.parseInt(tile[0]) + 1));
//                        mSocket.on("error", new Emitter.Listener() {
//                            @Override
//                            public void call(Object... args) {
//                                Log.i(logTag,"getState");
//                            }
//                        });
                        }
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
                    String newColor = String.valueOf((int) (1 + Math.random() * 4));
                    String newCount = String.valueOf((int) (1 + Math.random() * 4));
                    int resId = ResourceHelper.getFlieseResId(newColor);
                    ((ImageView) view).setImageResource(resId);
                    view.setTag(newColor + "|" + newCount);
                    view.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    }


    private class Element {
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

        public void setCount(int count) {
            this.count = count;
        }
    }
}
