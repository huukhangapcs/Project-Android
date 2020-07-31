package com.example.moneyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends ArrayAdapter<Country> {

    private int layoutResource;

    public ResultAdapter(Context context, int layoutResource, ArrayList<Country> resultCountry) {
        super(context, layoutResource, resultCountry);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Country country = getItem(position);

        if (country != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.resultFlag);
            TextView textView = (TextView) view.findViewById(R.id.resultName);
            TextView textView1 = (TextView) view.findViewById(R.id.resultMoney);
            TextView textView2 = (TextView) view.findViewById(R.id.resultDetail);
            imageView.setImageResource(country.getFlag());
            textView.setText(country.getName());
            textView1.setText(String.valueOf(country.getExchangeResult()));
            textView2.setText(country.getDetail());
        }

        return view;
    }
}
