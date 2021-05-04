package com.example.gruppe9_se2.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiHelper;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.lobby.LobbyApi;
import com.example.gruppe9_se2.api.lobby.LobbyRequest;
import com.example.gruppe9_se2.api.lobby.LobbyResponse;
import com.example.gruppe9_se2.logic.GameStart;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewLobbyFragment extends Fragment {

    //private Socket mSocket;


    InviteListAdapter adapter;
    //Socket Initialization


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_lobby, container, false);

       // RecyclerView recyclerView = view.findViewById(R.id.inviteList);
//
//        recyclerView.setHasFixedSize(true);
        adapter = new InviteListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
       // recyclerView.setLayoutManager(layoutManager);
       // recyclerView.setAdapter(adapter);


        Button btnInvite = view.findViewById(R.id.btn_createLobby);
        btnInvite.setOnClickListener(v -> {

            //final String[] lobbyID = new String[1];
            // Post Request Lobby
            final String base_URL = "https://gruppe9-se2-backend.herokuapp.com/";
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
                        String owner = response.body().getOwner();
                        //lobbyID[0] = id;


                        Intent intent = new Intent(getContext(), GameStart.class);
                        startActivity(intent);




                    } else {
                        String error = ApiHelper.getErrorMessage(response);
                        TextInputLayout etLobbyName = view.findViewById(R.id.et_lobby_name);
                        etLobbyName.setError(error);
                    }
                }

                @Override
                public void onFailure(Call<LobbyResponse> call, Throwable t) {
                    TextInputLayout etLobbyName = view.findViewById(R.id.et_lobby_name);
                    etLobbyName.setError("Problem accessing server !!!");
                }
            });

            //Open Web Socket
           /* mSocket.connect();

            IO.Options options = IO.Options.builder()
                    .setExtraHeaders(singletonMap("x-lobby-id", singletonList(lobbyID[0])))
                    .build();

            mSocket = IO.socket(URI.create("https://gruppe9-se2-backend.herokuapp.com/"), options);*/

        });

        Button button_back = view.findViewById(R.id.btn_back);
        button_back.setOnClickListener(v -> {

            ((LobbyActivity)getActivity()).backToOverview();
        });
        return view;
    }
}
