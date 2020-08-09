package com.example.moneyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<HistoryRecord> {

    private int layoutResource;
    public HistoryAdapter(@NonNull Context context, int resource, ArrayList<HistoryRecord> historyRecords) {
        super(context, resource, historyRecords);
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }
        HistoryRecord record = getItem(position);
        if (record != null) {
            TextView textView = (TextView) view.findViewById(R.id.textViewHistoryDate);
            TextView textView1 = (TextView) view.findViewById(R.id.textViewHistoryDetail);
            textView.setText(record.getDate());
            textView1.setText(record.getResult());
        }
        return view;
    }
}
