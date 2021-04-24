package com.example.gruppe9_se2.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.example.gruppe9_se2.R;
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
            EditText et = view.findViewById(R.id.et_name_login);
            et.setText(name);
        }

        // set onClickListeners

        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            // todo check login data for correctness
            // todo print error message if incorrect
            // todo authenticate user if correct and lead to home page
        });

        Button btnRouteRegister = view.findViewById(R.id.btn_routeRegister);
        btnRouteRegister.setOnClickListener(v -> {
            // todo run function register in MainActivity (change to register)
            // todo get inputted name and add it to argument
        });
        return view;
    }
}
