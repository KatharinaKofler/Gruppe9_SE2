package com.example.gruppe9_se2.user;

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
import com.example.gruppe9_se2.api.lobby.LobbyApi;
import com.example.gruppe9_se2.api.lobby.LobbyRequest;
import com.example.gruppe9_se2.api.lobby.LobbyResponse;
import com.example.gruppe9_se2.logic.GameStart;

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
            LobbyRequest request = new LobbyRequest(token);
            LobbyApi service = retrofit.create(LobbyApi.class);
            Call<LobbyResponse> call = service.executeLobby(token, request);
            call.enqueue(new Callback<LobbyResponse>() {
                @Override
                public void onResponse(Call<LobbyResponse> call, Response<LobbyResponse> response) {
                    if (response.isSuccessful()) {

                        //Lobby id speichern
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

                @Override
                public void onFailure(Call<LobbyResponse> call, Throwable t) {
                    TextView createError = view.findViewById(R.id.createError);
                    createError.setText("Problem accessing server !!!");
                }
            });
        });

        return view;
    }
}
