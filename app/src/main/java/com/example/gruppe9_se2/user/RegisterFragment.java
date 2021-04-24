package com.example.gruppe9_se2.user;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.google.android.material.textfield.TextInputLayout;

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

            EditText name = view.findViewById(R.id.et_name);
            EditText password = view.findViewById(R.id.et_password);
            TextInputLayout nameLayout = view.findViewById(R.id.et_name_layout);
            TextInputLayout passwordLayout = view.findViewById(R.id.et_password_layout);

            // print error message if name or password empty
            if(name.getText().toString().equals("")){
                nameLayout.setError("Enter a nickname");
            }
            else {
                nameLayout.setError(null);
            }
            if(password.getText().toString().equals("")){
                passwordLayout.setError("Enter a password");
            }
            else {
                passwordLayout.setError(null);
            }

            // todo check username if already in use
            // todo check password for form
            // todo print error message if incorrect
            // todo add user to db and authenticate user and lead to home page
        });

        return view;
    }
}
