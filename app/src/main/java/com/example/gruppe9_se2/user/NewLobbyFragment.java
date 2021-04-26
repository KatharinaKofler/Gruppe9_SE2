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

public class NewLobbyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_lobby, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.inviteList);
        recyclerView.setHasFixedSize(true);
        InviteListAdapter adapter = new InviteListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Button btnInvite = view.findViewById(R.id.invitePlayer);
        btnInvite.setOnClickListener(v -> {
            EditText username = view.findViewById(R.id.username);

            adapter.insert(username.getText().toString());
        });

        return view;
    }
}
