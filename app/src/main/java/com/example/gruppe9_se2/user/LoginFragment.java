package com.example.gruppe9_se2.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gruppe9_se2.MainActivity;
import com.example.gruppe9_se2.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.EventListener;

public class LoginFragment extends Fragment implements EventListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // retrieve argument of name if available

        Bundle args = getArguments();
        if(args != null){
            String name = args.getString("name", "");
            EditText et = view.findViewById(R.id.et_name);
            et.setText(name);
        }

        // set onClickListeners

        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {

            EditText name = view.findViewById(R.id.et_name);
            EditText password = view.findViewById(R.id.et_password);
            TextInputLayout nameLayout = view.findViewById(R.id.et_name_layout);
            TextInputLayout passwordLayout = view.findViewById(R.id.et_password_layout);

            // print error message if name or password empty
            if(name.getText().toString().equals("")){
                nameLayout.setError("Enter your username");
            }
            else {
                nameLayout.setError(null);
            }
            if(password.getText().toString().equals("")){
                passwordLayout.setError("Enter your password");
            }
            else {
                passwordLayout.setError(null);
            }


            // todo check login data for correctness
            // todo print error message if incorrect
            // todo authenticate user if correct and lead to home page
        });

        return view;
    }
}
