package com.example.messenger;


import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class listofdialogs extends ListActivity {

    private String[] mNames = {"Петя", "Рома", "Коля", "Вася"};

    private String[] mDates = {"Привет", "Пока",
            "Как жизнь?", "Что делаешь?"};

    int[] mImageIds = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};

    private myAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new myAdapter(this);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String selection = mAdapter.getString(position);
        Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
    }

    private class myAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        myAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mNames.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.activity_listofdialogs, null);

            ImageView image = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            image.setImageResource(mImageIds[position]);

            TextView signTextView = (TextView) convertView.findViewById(R.id.textViewName);
            signTextView.setText(mNames[position]);

            TextView dateTextView = (TextView) convertView.findViewById(R.id.textViewmessage);
            dateTextView.setText(mDates[position]);

            return convertView;
        }

        String getString(int position) {
            return mNames[position] + " (" + mDates[position] + ")";
        }
    }
}