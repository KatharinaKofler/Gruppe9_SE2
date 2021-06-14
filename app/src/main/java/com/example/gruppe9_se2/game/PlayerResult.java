package com.example.gruppe9_se2.game;

import java.io.Serializable;

public class PlayerResult implements Serializable {
    public int rank;
    public String username;
    public int points;

    public PlayerResult(int rank, String username, int points) {
        this.rank = rank;
        this.username = username;
        this.points = points;
    }

    public String getDisplayRank() {
        switch (rank) {
            case 1:
                return String.format("%dst", rank);
            case 2:
                return String.format("%dnd", rank);
            case 3:
                return String.format("%drd", rank);
            default:
                return String.format("%dth", rank);
        }
    }

    public String getDisplayPoints() {
        return String.format("%d pts", points);
    }
}
