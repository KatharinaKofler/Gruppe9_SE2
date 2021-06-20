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

public class InviteListAdapter extends RecyclerView.Adapter<InviteListAdapter.ViewHolder> {

    private final List<String> localDataSet;

    public InviteListAdapter() {
        super();
        localDataSet = new ArrayList<>();
    }

    public void insert(String name) {
        localDataSet.add(name);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void insertAll(List<String> data) {
        localDataSet.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.lobbyName);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
