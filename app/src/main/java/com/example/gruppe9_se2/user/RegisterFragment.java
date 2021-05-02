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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // retrieve argument of name if available

        Bundle args = getArguments();
        if (args != null) {
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
            if (name.getText().toString().equals("")) {
                nameLayout.setError("Enter a nickname");
            } else {
                nameLayout.setError(null);
            }

            // all Password Validations
            String regexNoSpace = "^(?=\\S+$).+$"; /* contains no white space */
            String regexLowerUppercase = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&\\-+=()]).{8,20}$";
            /* contains at least one lower and upper case letter and at least 8 and at most 20 characters and at least one of these special characters and one number*/
            // todo nice to have: error message icon, that doesn't show up over the password visible icon (eye)
            String p = password.getText().toString();
            if (password.getText().toString().equals("")) {
                passwordLayout.setError("Enter a password");
            } else if (!Pattern.compile(regexNoSpace).matcher(password.getText().toString()).matches()) {
                passwordLayout.setError("Enter a valid password, containing no white spaces");
            } else if (!Pattern.compile(regexLowerUppercase).matcher(p).matches()) {
                passwordLayout.setError("A valid password (8-20) needs at least one lowercase and uppercase letter, one special character and one number");
            } else {
                passwordLayout.setError(null);
            }

            // todo check username if already in use
            // todo add user to db and authenticate user and lead to home page
        });

        return view;
    }
}
