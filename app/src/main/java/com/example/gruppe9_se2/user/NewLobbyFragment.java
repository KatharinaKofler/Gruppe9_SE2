package com.example.gruppe9_se2.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public class NewLobbyFragment extends Fragment {

    private Socket mSocket;


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

            // ToDo Post Request Lobby
            final String base_URL = "https://gruppe9-se2-backend.herokuapp.com/";
            AuthRequest request = new AuthRequest();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();






            //Open Web Socket
            mSocket.connect();
            EditText etLobbyName = view.findViewById(R.id.lobbyname);
            String lobbyID = etLobbyName.getText().toString();

            IO.Options options = IO.Options.builder()
                    .setExtraHeaders(singletonMap("x-lobby-id", singletonList("lobbyID")))
                    .build();

            mSocket = IO.socket(URI.create("https://gruppe9-se2-backend.herokuapp.com/"), options);



            //adapter.insert(username.getText().toString());
        });

        Button button_back = view.findViewById(R.id.btn_back);
        button_back.setOnClickListener(v -> {

            ((LobbyActivity)getActivity()).backToOverview();
        });



        return view;
    }
}
