package com.example.messenger;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class sendermessageholder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, dateText;

    sendermessageholder(View itemView) {
        super(itemView);
        dateText = (TextView) itemView.findViewById(R.id.text_gchat_date_me);
        messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
        timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
    }

    void bind(Messageobj message) {
        messageText.setText(message.getText());
        Date d = new Date((long)message.getTime()*1000);
        DateFormat f = new SimpleDateFormat("hh:mm");
        DateFormat f1 = new SimpleDateFormat("MMMM d");
        timeText.setText(f.format(d));
        dateText.setText(f1.format(d));
    }
}
