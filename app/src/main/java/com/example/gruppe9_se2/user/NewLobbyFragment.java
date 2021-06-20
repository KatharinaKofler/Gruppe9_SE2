package com.example.gruppe9_se2.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiHelper;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.lobbie.LobbieApi;
import com.example.gruppe9_se2.api.lobbie.LobbieRequest;
import com.example.gruppe9_se2.api.lobbie.LobbieResponse;
import com.example.gruppe9_se2.logic.GameStart;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewLobbyFragment extends Fragment {

    InviteListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_lobby, container, false);

        adapter = new InviteListAdapter();

        Button btnInvite = view.findViewById(R.id.btn_createLobby);
        btnInvite.setOnClickListener(v -> {

            // Post Request Lobby
            String token = "Bearer ";
            token += ApiManager.getToken();

            Retrofit retrofit = ApiManager.getInstance();
            LobbieRequest request = new LobbieRequest(token);
            LobbieApi service = retrofit.create(LobbieApi.class);
            Call<LobbieResponse> call = service.executeLobby(token, request);
            call.enqueue(new Callback<LobbieResponse>() {
                @Override
                public void onResponse(@NotNull Call<LobbieResponse> call, @NotNull Response<LobbieResponse> response) {
                    if (response.isSuccessful()) {

                        //Lobby id speichern
                        assert response.body() != null;
                        String id = response.body().getId();

                        Intent intent = new Intent(getContext(), GameStart.class);
                        Bundle b = new Bundle();
                        b.putString("LobbyID", id);
                        b.putBoolean("isOwner", true);
                        intent.putExtras(b);

                        startActivity(intent);
                    } else {
                        String error = ApiHelper.getErrorMessage(response);
                        TextView createError = view.findViewById(R.id.createError);
                        createError.setText(error);
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(@NotNull Call<LobbieResponse> call, @NotNull Throwable t) {
                    TextView createError = view.findViewById(R.id.createError);
                    createError.setText("Problem accessing server !!!");
                }
            });
        });

        return view;
    }
}
