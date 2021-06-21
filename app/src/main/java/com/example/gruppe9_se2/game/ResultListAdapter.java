package com.example.gruppe9_se2.game;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gruppe9_se2.R;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {

    private final List<PlayerResult> localData;

    public ResultListAdapter() {
        super();
        localData = new ArrayList<>();
    }

    public void insert(PlayerResult result) {
        localData.add(result);
        notifyItemInserted(localData.size() - 1);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_result_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerResult r = localData.get(position);
        holder.getRank().setText(r.getDisplayRank());
        holder.getPoints().setText(r.getDisplayPoints());
        holder.getUsername().setText(r.username);
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
        private final TextView rank;
        private final TextView username;
        private final TextView points;
        private View.OnClickListener onClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            rank = view.findViewById(R.id.rank);
            username = view.findViewById(R.id.username);
            points = view.findViewById(R.id.resultPoints);
            view.setOnClickListener(this);
        }

        public TextView getRank() {
            return rank;
        }

        public TextView getUsername() {
            return username;
        }

        public TextView getPoints() {
            return points;
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
