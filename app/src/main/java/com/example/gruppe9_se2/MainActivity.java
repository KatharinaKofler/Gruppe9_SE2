package com.example.gruppe9_se2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.gruppe9_se2.user.LoginFragment;
import com.example.gruppe9_se2.user.RegisterFragment;

import java.util.ArrayList;
import java.util.EventListener;

public class MainActivity extends AppCompatActivity {

    int containerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show content activity main
        setContentView(R.layout.activity_main);
        // get id from fragment container view
        containerId = findViewById(R.id.fragment_container_view).getId();
        // create fragment transaction
        login(null);
    }

    public void login(String name){
        // get Fragment Register
        Fragment loginFragment = new LoginFragment();
        // if name not null add it to fragment arguments
        if(name != null){
            Bundle args = new Bundle();
            args.putString("name", name);
            loginFragment.setArguments(args);
        }
        // create fragment transaction add fragment to it and commit
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(containerId, loginFragment);
        ft.commit();
    }

    public void register(String name){
        // get Fragment Register
        Fragment registerFragment = new RegisterFragment();
        // if name not null add it to fragment arguments
        if(name != null){
            Bundle args = new Bundle();
            args.putString("name", name);
            registerFragment.setArguments(args);
        }
        // create fragment transaction add fragment to it and commit
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(containerId, registerFragment);
        ft.commit();
    }
    
}