package com.example.messenger;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class recipientmessageholder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText, dateText;


    recipientmessageholder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.othermessagetextview);
        dateText = (TextView) itemView.findViewById(R.id.otherdatetextview);
        timeText = (TextView) itemView.findViewById(R.id.othertimestamptextview);
        nameText = (TextView) itemView.findViewById(R.id.othernametextview);
    }

    void bind(Messageobj message) {
        messageText.setText(message.getText());

        Date d = new Date((long)message.getTime()*1000);
        DateFormat f = new SimpleDateFormat("hh:mm");
        DateFormat f1 = new SimpleDateFormat("MMMM d");
        timeText.setText(f.format(d));
        dateText.setText(f1.format(d));
        nameText.setText(message.getName());

    }
}
