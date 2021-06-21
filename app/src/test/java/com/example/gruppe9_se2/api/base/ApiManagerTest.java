package com.example.gruppe9_se2.api.base;

import junit.framework.TestCase;

import retrofit2.Retrofit;

public class ApiManagerTest extends TestCase {

    public void testGetInstance() {
        Retrofit testRetrofit = ApiManager.getInstance();
        assertEquals(ApiManager.getInstance(), testRetrofit);
    }

    public void testGetNullToken(){
        assertNull(ApiManager.getToken());
    }

    public void testGetToken() {
        ApiManager.setToken("testToken456");
        assertEquals(ApiManager.getToken(), "testToken456");
        ApiManager.setToken(null);
    }

    public void testSetTokenString() {
        ApiManager.setToken("testToken123");
        assertEquals(ApiManager.getToken(), "testToken123");
        ApiManager.setToken(null);
    }

    public void testSetTokenInteger() {
        ApiManager.setToken(String.valueOf(101));
        assertEquals(ApiManager.getToken(), "101");
        ApiManager.setToken(null);
    }

    public void testDeleteToken() {
        ApiManager.setToken("testNotNull");
        assertEquals(ApiManager.getToken(), "testNotNull");
        ApiManager.deleteToken();
        assertNull(ApiManager.getToken());
    }
}