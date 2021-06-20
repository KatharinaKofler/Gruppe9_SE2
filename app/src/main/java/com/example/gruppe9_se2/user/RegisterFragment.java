package com.example.gruppe9_se2.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiHelper;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.login.LoginApi;
import com.example.gruppe9_se2.api.login.LoginRequest;
import com.example.gruppe9_se2.api.login.LoginResponse;
import com.example.gruppe9_se2.api.register.RegisterApi;
import com.example.gruppe9_se2.api.register.RegisterRequest;
import com.example.gruppe9_se2.api.register.RegisterResponse;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
            @SuppressLint("CutPasteId") EditText et = view.findViewById(R.id.et_name);
            et.setText(name);
        }

        // set onClickListeners

        Button btnLogin = view.findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(v -> {

            @SuppressLint("CutPasteId") EditText name = view.findViewById(R.id.et_name);
            EditText password = view.findViewById(R.id.et_password);
            TextInputLayout nameLayout = view.findViewById(R.id.et_name_layout);
            TextInputLayout passwordLayout = view.findViewById(R.id.et_password_layout);

            // print error message if name or password empty
            if (name.getText().toString().equals("")) {
                nameLayout.setError("Enter a nickname");
            } else {
                nameLayout.setError(null);

                // all Password Validations
                String regexNoSpace = "^(?=\\S+$).+$"; /* contains no white space */
                String regexLowerUppercase = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&\\-+=().!?]).{8,20}$";
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

                    // Register
                    Retrofit retrofitRegister = ApiManager.getInstance();
                    RegisterRequest requestRegister = new RegisterRequest(name.getText().toString(), password.getText().toString());
                    RegisterApi serviceRegister = retrofitRegister.create(RegisterApi.class);
                    Call<RegisterResponse> callRegister = serviceRegister.executeRegister(requestRegister);
                    callRegister.enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RegisterResponse> call, @NotNull Response<RegisterResponse> response) {
                            if (response.isSuccessful()) {
                                // Login
                                Retrofit retrofitLogin = ApiManager.getInstance();
                                LoginRequest requestLogin = new LoginRequest(name.getText().toString(), password.getText().toString());
                                LoginApi serviceLogin = retrofitLogin.create(LoginApi.class);
                                Call<LoginResponse> callLogin = serviceLogin.executeLogin(requestLogin);
                                callLogin.enqueue(new Callback<LoginResponse>() {
                                    @Override
                                    public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                                        if (response.isSuccessful()) {
                                            LoginResponse login = response.body();
                                            if (login != null) {
                                                ApiManager.setToken(login.token);

                                                // Close current activity and start Lobby
                                                requireActivity().finish();
                                                Intent intent = new Intent(getContext(), LobbyActivity.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            String error = ApiHelper.getErrorMessage(response);
                                            passwordLayout.setError(error);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                                        passwordLayout.setError("Problem accessing server !!!");
                                    }
                                });

                            } else {
                                String error = ApiHelper.getErrorMessage(response);
                                passwordLayout.setError(error);
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RegisterResponse> call, @NotNull Throwable t) {
                            passwordLayout.setError("Problem accessing server !!!");
                        }
                    });
                }
            }
        });

        return view;
    }
}
