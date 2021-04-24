package com.example.gruppe9_se2.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;

public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // retrieve argument of name if available

        Bundle args = getArguments();
        if(args != null){
            String name = args.getString("name", "");
            EditText et = view.findViewById(R.id.et_name);
            et.setText(name);
        }

        // set onClickListeners

        Button btnLogin = view.findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(v -> {
            // todo check username if already in use
            // todo check password for form
            // todo print error message if incorrect
            // todo add user to db and authenticate user and lead to home page
        });

        return view;
    }
}
