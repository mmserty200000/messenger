package com.example.messenger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class dialogueadapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_LOADING = 3;
    private Context context;
    private ArrayList<Messageobj> messagelist;
    private String userUID;

    public dialogueadapter(Context in_context, ArrayList<Messageobj> in_messagelist, String in_uid){
        context = in_context;
        messagelist = in_messagelist;
        userUID = in_uid;
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }
    @Override
    public int getItemViewType(int position) {
        Messageobj message = (Messageobj) messagelist.get(position);
        if(message.getTime() < 0){
            return VIEW_TYPE_LOADING;
        }
        else if (message.getUid().equals(userUID)) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loadingmessagelayout, parent, false);
            return new loadingmessageholder(view);
        }else if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sendermessagelayout, parent, false);
            return new sendermessageholder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipientmessagelayout, parent, false);
            return new recipientmessageholder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messageobj message = (Messageobj) messagelist.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((sendermessageholder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((recipientmessageholder) holder).bind(message);
        }
    }
    private class loadingmessageholder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public loadingmessageholder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

    }

}
