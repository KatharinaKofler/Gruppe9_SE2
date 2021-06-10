package com.example.gruppe9_se2.game;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.helper.ResourceHelper;
import com.example.gruppe9_se2.logic.GameStart;
import com.example.gruppe9_se2.logic.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

import java.util.EventListener;

public class MusterFragment extends Fragment implements EventListener {
    String logTag = "musterFragmentLogs";

    View view;
    GridLayout gridLayout;
    GameStart gameStart;

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
        view = inflater.inflate(R.layout.fragment_muster, container, false);

        //todo get the size of the Spielbrett Muster
        int size = (int) getResources().getDimension(R.dimen.fliese_size);

        // Fill the 5x5 Grid with ImageViews
        gridLayout = view.findViewById(R.id.gridMuster);

        // Sample for Musterreihe
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                LinearLayout linearLayout = new LinearLayout(requireContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (i == 0 && j == 0) {
                    // Platzhalter für die neue Zwischenablage
                } else if (j >= 4 - i) {
                    ImageView image = new ImageView(requireContext());
                    //todo create drawable for empty Image State
                    int imageId = R.drawable.empty_fliese;
                    image.setImageResource(imageId);
                    image.setTag((i+1) + "|" + (j+1));
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
                        image.setOnDragListener(new MyPatternDragListener());
                    }

                    linearLayout.addView(image);
                    gridLayout.addView(linearLayout, i * 5 + j);
                } else {
                    gridLayout.addView(linearLayout, i * 5 + j);
                }
            }
        }

        return view;
    }

    public void dragListenerNewTileField(GameStart gameStart) {
        this.gameStart = gameStart;
        GridLayout gridLayout = view.findViewById(R.id.gridMuster);
        ImageView tile = (ImageView) ((RelativeLayout) gridLayout.getChildAt(0)).getChildAt(0);
        tile.setOnDragListener(new NewTileDragListener());
    }

    private class NewTileDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                ImageView imageView = (ImageView) event.getLocalState();

                int color = (int) imageView.getTag(R.id.color_id);
                int plate = (int) imageView.getTag(R.id.plateNr_id);
                int count = (int) imageView.getTag(R.id.count_id);

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
                gameStart.disableOnTouchBoard();
            }
            return true;
        }
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

    class MyPatternDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
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

                        Element element = elements[row-1];
                        if (element.getColor() == 0) {
                            element.setColor(color);
                        }

                        if (element.getColor() != color) {
                            return false;
                        }

                        setMusterElement(row, element, count);

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

                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void setMusterElement(int row, Element element, int count) {
        int resId = ResourceHelper.getFlieseResId(element.getColor());
        int floor = element.addCount(row, count);

        for (int i = 0; i < element.getCount(); i++) {
            int tilePos = (row - 1) * 5 + (4 - i);

            LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(tilePos);
            ImageView image = (ImageView) linearLayout.getChildAt(0);
            if (image != null) {
                image.setImageResource(resId);
            }
        }

        if (floor > 0) {
            Bundle result = new Bundle();
            result.putInt("color", element.getColor());
            result.putInt("count", floor);
            getParentFragmentManager().setFragmentResult("floor", result);
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

        public int addCount(int row, int count) {
            int temp = this.count + count;
            if (temp <= row) {
                this.count = temp;
                return 0;
            } else {
                this.count = row;
                return temp - row;
            }
        }
    }
}
