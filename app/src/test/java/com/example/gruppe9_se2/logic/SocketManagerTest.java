package com.example.gruppe9_se2.logic;

import com.example.gruppe9_se2.api.base.ApiManager;

import org.junit.Before;
import org.junit.Test;

import io.socket.client.Socket;

import static org.junit.Assert.*;

public class SocketManagerTest {

    @Before
    public void prepareApiManager(){
        ApiManager.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZGY4ODc1YS1jNTNjLTQwNDgtYTM2OC02MDg1N2NlZmJjMTIiLCJpYXQiOjE2MjQyNjUxMTUsImV4cCI6MTYyNDQzNzkxNX0.x1Qm7hJk0xZGMPK6xpl8iRAdKvIVpUZ3lsqNMhgpWpA");
    }

    @Test
    public void makeSocket() {
        assertNull(SocketManager.getSocket());
        Socket s  = SocketManager.makeSocket("test");
        assertEquals(s, SocketManager.getSocket());
    }
}