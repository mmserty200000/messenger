package com.example.messenger;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class listofdialogsadapter  extends BaseAdapter {

    ArrayList<Messageobj> listofdialogs;
    private LayoutInflater layoutInflater;
    private Context context;

    public listofdialogsadapter(Context aContext,  ArrayList<Messageobj> listofdialogs) {
        this.context = aContext;
        this.listofdialogs = listofdialogs;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listofdialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return listofdialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listofdialogstemplate, null);
            holder = new ViewHolder();
            holder.messageView = (TextView) convertView.findViewById(R.id.textView_message);
            holder.nameView = (TextView) convertView.findViewById(R.id.textView_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Messageobj message = this.listofdialogs.get(position);
        holder.nameView.setText(message.getName());
        holder.messageView.setText(Html.fromHtml(message.getSecondstroke()));
        return convertView;
    }


    static class ViewHolder {
        TextView nameView;
        TextView messageView;
    }

}