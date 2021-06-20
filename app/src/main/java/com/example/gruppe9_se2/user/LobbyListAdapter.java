package com.example.gruppe9_se2.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;
import com.example.gruppe9_se2.logic.GameStart;

import java.util.ArrayList;
import java.util.List;

public class LobbyListAdapter extends RecyclerView.Adapter<LobbyListAdapter.ViewHolder> {

    private final List<Lobby> localData;
    private final Context context;

    public LobbyListAdapter(Context context) {
        super();
        localData = new ArrayList<>();
        this.context = context;
    }

    public void insert(Lobby lobby) {
        localData.add(lobby);
        notifyItemInserted(localData.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lobby_list_item, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lobby l = localData.get(position);
        holder.getName().setText(l.getName());
        holder.getDetails().setText("Press to join.");
        holder.setOnClickListener((view) -> {
            Intent intent = new Intent(context, GameStart.class);
            Bundle b = new Bundle();
            b.putString("LobbyID", l.id);
            b.putBoolean("isOwner", false);
            intent.putExtras(b);

            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return localData.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView details;
        private View.OnClickListener onClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = (TextView) view.findViewById(R.id.lobbyName);
            details = (TextView) view.findViewById(R.id.lobbyDetails);
            view.setOnClickListener(this);
        }

        public TextView getName() {
            return name;
        }

        public TextView getDetails() {
            return details;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }
}
