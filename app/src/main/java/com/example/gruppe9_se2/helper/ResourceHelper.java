package com.example.gruppe9_se2.helper;

import com.example.gruppe9_se2.R;

public class ResourceHelper {
    public static int getFlieseResId(String color) {
        return getFlieseResId(Integer.parseInt(color));
    }

    public static int getFlieseResId(int color) {
        int resId;
        switch (color) {
            case 1:
                resId = R.drawable.fliese_color1;
                break;
            case 2:
                resId = R.drawable.fliese_color2;
                break;
            case 3:
                resId = R.drawable.fliese_color3;
                break;
            case 4:
                resId = R.drawable.fliese_color4;
                break;
            case 5:
                resId = R.drawable.fliese_color5;
                break;
            default:
                resId = R.drawable.empty_fliese;
        }
        return resId;
    }
}
