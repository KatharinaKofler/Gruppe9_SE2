package com.example.gruppe9_se2.helper;

import com.example.gruppe9_se2.R;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceHelperTest {

    @Test
    public void getFlieseResId() {
        assertEquals(R.drawable.empty_fliese, ResourceHelper.getFlieseResId(283902));
        assertEquals(R.drawable.fliese_color1, ResourceHelper.getFlieseResId(1));
        assertEquals(R.drawable.fliese_color2, ResourceHelper.getFlieseResId(2));
        assertEquals(R.drawable.fliese_color3, ResourceHelper.getFlieseResId(3));
        assertEquals(R.drawable.fliese_color4, ResourceHelper.getFlieseResId(4));
        assertEquals(R.drawable.fliese_color5, ResourceHelper.getFlieseResId(5));
    }
}