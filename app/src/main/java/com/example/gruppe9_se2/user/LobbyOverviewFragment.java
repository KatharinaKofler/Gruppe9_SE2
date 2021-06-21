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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.api.base.ApiHelper;
import com.example.gruppe9_se2.api.base.ApiManager;
import com.example.gruppe9_se2.api.lobbieGet.LobbieGetApi;
import com.example.gruppe9_se2.api.lobbieGet.Lobbies;

import org.jetbrains.annotations.NotNull;

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

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swiperefresh);


        refreshLayout.setOnRefreshListener(() -> loadData(view));

        loadData(view);

        Button btnCreate = view.findViewById(R.id.newLobby);
        btnCreate.setOnClickListener(v -> ((LobbyActivity) requireActivity()).newLobby());

        Button btnLogout = view.findViewById(R.id.logoutLobby);
        btnLogout.setOnClickListener(v -> ((LobbyActivity) requireActivity()).logoutLobby());

        return view;
    }

    private void loadData(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.lobbyList);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swiperefresh);
        recyclerView.setHasFixedSize(true);
        LobbyListAdapter adapter = new LobbyListAdapter(getContext());

        String token = "Bearer ";
        token += ApiManager.getToken();

        Retrofit retrofit = ApiManager.getInstance();
        LobbieGetApi service = retrofit.create(LobbieGetApi.class);
        Call<ArrayList<Lobbies>> call = service.executeLobbyGet(token);
        call.enqueue(new Callback<ArrayList<Lobbies>>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(@NotNull Call<ArrayList<Lobbies>> call, @NotNull Response<ArrayList<Lobbies>> response) {
                if (response.isSuccessful()) {

                    // response.body() ist die ArrayList mit allen Lobbies
                    assert response.body() != null;
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
                    refreshLayout.setRefreshing(false);
                } else {
                    String error = ApiHelper.getErrorMessage(response);
                    TextView tvError = view.findViewById(R.id.tvError);
                    tvError.setText(error);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NotNull Call<ArrayList<Lobbies>> call, @NotNull Throwable t) {
                TextView tvError = view.findViewById(R.id.tvError);
                tvError.setText("Problem accessing server !!!");
            }
        });
    }
}
