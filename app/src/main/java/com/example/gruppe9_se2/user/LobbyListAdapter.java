package com.example.gruppe9_se2.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;

import java.util.ArrayList;
import java.util.List;

public class LobbyListAdapter extends RecyclerView.Adapter<LobbyListAdapter.ViewHolder> {

    private List<Lobby> localData;

    public LobbyListAdapter() {
        super();
        localData = new ArrayList<>();
    }

    public void insert(Lobby lobby) {
        localData.add(lobby);
        notifyItemInserted(localData.size() - 1);
    }

    public void insertAll(List<Lobby> data) {
        localData.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lobby_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lobby l = localData.get(position);
        holder.getName().setText(l.name);
        holder.getDetails().setText("Players: " + l.playerCount);
    }

    @Override
    public int getItemCount() {
        return localData.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView details;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = (TextView) view.findViewById(R.id.lobbyName);
            details = (TextView) view.findViewById(R.id.lobbyDetails);
        }

        public TextView getName() {
            return name;
        }

        public TextView getDetails() {
            return details;
        }
    }
}
