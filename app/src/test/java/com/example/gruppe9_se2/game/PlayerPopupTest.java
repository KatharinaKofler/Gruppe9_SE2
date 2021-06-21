package com.example.gruppe9_se2.game;

import junit.framework.TestCase;

public class PlayerPopupTest extends TestCase {

    public void testBinaryDivisionNull() {
        int length = 4;
        int[] expected = {0, 0, 0, 0};
        int[] actual = PlayerPopup.binaryDivision(0, length);

        for (int i = 0; i < length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    public void testBinaryDivisionNumber() {

        int length = 4;
        int[] expected = {1, 1, 0, 0};
        int[] actual = PlayerPopup.binaryDivision(12, length);

        for (int i = 0; i < length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    public void testBinaryDivisionToLong() {

        int length = 4;
        int[] expected = {0, 1, 1, 1};
        int[] actual = PlayerPopup.binaryDivision(103, length);

        for (int i = 0; i < length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }
}