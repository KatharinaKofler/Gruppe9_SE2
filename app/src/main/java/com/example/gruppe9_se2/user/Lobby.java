package com.example.gruppe9_se2.user;

public class Lobby {
    public final String id;
    public final String owner;

    public Lobby(String id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getName() {
        return owner + "'s lobby";
    }
}
