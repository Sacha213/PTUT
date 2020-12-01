package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Message> listeMessage;
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;


    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView message, date;
        public MessageInViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text);
            date = itemView.findViewById(R.id.date_text);
        }

        void bind(int position) {
            Message messageIn = listeMessage.get(position);
            message.setText(messageIn.message);
            date.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageIn.messageDate));
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView message, date;
        public MessageOutViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text);
            date = itemView.findViewById(R.id.date_text);
        }

        void bind(int position) {
            Message messageIn = listeMessage.get(position);
            message.setText(messageIn.message);
            date.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageIn.messageDate));
        }
    }

    public MessageAdapter(List<Message> listeMessage) {
        this.listeMessage = listeMessage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println(viewType);
        if (viewType == MESSAGE_TYPE_OUT) {
            return new MessageOutViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_out, parent, false));
        }
        return new MessageInViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_in, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (listeMessage.get(position).messageType == MESSAGE_TYPE_OUT) {
            ((MessageOutViewHolder)holder).bind(position);
        } else {
            ((MessageInViewHolder)holder).bind(position);
        }
    }


    @Override
    public int getItemCount() {
        return listeMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listeMessage.get(position).messageType;
    }


}