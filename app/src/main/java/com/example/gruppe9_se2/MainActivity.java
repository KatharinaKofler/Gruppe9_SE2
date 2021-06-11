package com.example.gruppe9_se2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.user.LoginFragment;
import com.example.gruppe9_se2.user.RegisterFragment;

public class MainActivity extends AppCompatActivity {

    int containerId;
    boolean login = true; /* if true <-> shows login fragment; if false <-> shows register fragment */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set game title
        setTitle(R.string.game_title);
        
        //Intent intent = new Intent(this, Game.class);
        //startActivity(intent);
        // show content activity main
        setContentView(R.layout.activity_main);

        // set OnClickListener for switch Button
        Button btnSwitch = findViewById(R.id.btn_switch);
        btnSwitch.setOnClickListener(v -> {
            // get current inputted name, so its still displayed after switch
            EditText et = findViewById(R.id.et_name);
            String name = et.getText().toString();
            // switch boolean login and run function login or register
            if(login){
                login = false;
                updateSwitchBtnName();
                register(name);
            }
            else {
                login = true;
                updateSwitchBtnName();
                login(name);
            }
        });
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
        ft.replace(containerId, loginFragment);
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
        // create fragment transaction add fragment to it (or replace old one) and commit
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, registerFragment);
        ft.commit();
    }

    private void updateSwitchBtnName(){
        if(login){
            Button btn = findViewById(R.id.btn_switch);
            btn.setText("Or Register");
        }
        else {
            Button btn = findViewById(R.id.btn_switch);
            btn.setText("Or Login");
        }
    }

}