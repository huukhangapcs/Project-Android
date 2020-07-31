package com.example.moneyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddCurrency extends AppCompatActivity {
    String target;
    ArrayList<Country>resultCountry;
    ArrayList<Country>availableCountry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);
        ListView listView = (ListView)findViewById(R.id.listViewAddCurrency);
        final Intent intent = getIntent();
        availableCountry = (ArrayList<Country>) intent.getSerializableExtra("available");;
        resultCountry= (ArrayList<Country>) intent.getSerializableExtra("result");;

        CustomAdapterAdd customAdapter = new CustomAdapterAdd();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent result = new Intent();
                Country target = availableCountry.get(i);
                availableCountry.remove(target);
                resultCountry.add(target);
                result.putExtra("available", availableCountry);
                result.putExtra("result",resultCountry);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private class CustomAdapterAdd extends BaseAdapter {

        @Override
        public int getCount() {
            return availableCountry.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.add_list_view, null);
            ImageView imageView = (ImageView)view1.findViewById(R.id.addFlag);
            TextView textView = (TextView)view1.findViewById(R.id.addName);
            TextView textView2 = (TextView)view1.findViewById(R.id.addDetail);
            imageView.setImageResource(availableCountry.get(i).getFlag());
            textView.setText(availableCountry.get(i).getName());
            textView2.setText(availableCountry.get(i).getDetail());
            return view1;
        }
    }
}