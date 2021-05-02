package com.example.gruppe9_se2.user;

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

public class LobbyOverviewFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobbies_overview, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.lobbyList);
        recyclerView.setHasFixedSize(true);
        LobbyListAdapter adapter = new LobbyListAdapter();

        // TODO exchange with server data



        // Client erstellne (instanz erstellen) klient soll sich zu server instanz verbinden (

        //for (int i = 0; i < 100; i++) {     adapter.insert(new Lobby("Test", 700));
        //                       }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Button btnCreate = view.findViewById(R.id.newLobby);
        btnCreate.setOnClickListener(v -> {
            ((LobbyActivity)getActivity()).newLobby();
        });

        return view;
    }
}
