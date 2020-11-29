package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RecyclerViewHolder> {

    List<Affichage> listePseudo;
    private OnNoteListener onNoteListener;

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView pseudo;
        OnNoteListener onNoteListener;

        public RecyclerViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            pseudo = itemView.findViewById(R.id.nomSender);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public ListAdapter(List<Affichage> listePseudo, OnNoteListener onNoteListener) {
        this.listePseudo = listePseudo;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, onNoteListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        Affichage affichage = listePseudo.get(position);
        holder.pseudo.setText(affichage.getPseudo());

    }

    @Override
    public int getItemCount() {

        return listePseudo.size();
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }


}
