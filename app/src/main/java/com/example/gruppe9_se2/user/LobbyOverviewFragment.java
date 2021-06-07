package com.example.gruppe9_se2.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiHelper;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.lobbieGet.Lobbies;
import com.example.gruppe9_se2.api.lobbieGet.LobbieGetApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LobbyOverviewFragment extends Fragment {




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobbies_overview, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.lobbyList);
        recyclerView.setHasFixedSize(true);
        LobbyListAdapter adapter = new LobbyListAdapter(getContext());



        // TODO exchange with server data

        // Post Request Lobby
        final String base_URL = "https://gruppe9-se2-backend.herokuapp.com/";
        String token = "Bearer ";
        token += ApiManager.getToken();

        Retrofit retrofit = ApiManager.getInstance();
        LobbieGetApi service = retrofit.create(LobbieGetApi.class);
        Call<ArrayList<Lobbies>> call = service.executeLobbyGet(token);
        call.enqueue(new Callback<ArrayList<Lobbies>>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<ArrayList<Lobbies>> call, Response<ArrayList<Lobbies>> response) {
                if (response.isSuccessful()) {

                    // response.body() ist die ArrayList mit allen Lobbies
                    int max = response.body().size();

                    for (int i = 0; i < max; i++) {
                        String id = response.body().get(i).getId();
                        String owner = response.body().get(i).getOwner();
                        adapter.insert(new Lobby(id, owner));
                    }

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    TextView tvError = view.findViewById(R.id.tvError);
                    tvError.setText("");

                } else {
                    String error = ApiHelper.getErrorMessage(response);
                    TextView tvError = view.findViewById(R.id.tvError);
                    tvError.setText(error);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Lobbies>> call, Throwable t) {
                TextView tvError = view.findViewById(R.id.tvError);
                tvError.setText("Problem accessing server !!!");
            }
        });


        Button btnCreate = view.findViewById(R.id.newLobby);
        btnCreate.setOnClickListener(v -> {
            ((LobbyActivity)getActivity()).newLobby();
        });

        Button btnLogout = view.findViewById(R.id.logoutLobby);
        btnLogout.setOnClickListener(v -> {
            ((LobbyActivity)getActivity()).logoutLobby();
        });

        return view;
    }
}
