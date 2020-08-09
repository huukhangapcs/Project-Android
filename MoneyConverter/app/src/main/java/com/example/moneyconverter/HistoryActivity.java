package com.example.moneyconverter;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    ArrayList<HistoryRecord> historyRecords;
    public ListView listView;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_view);
        listView = (ListView)findViewById(R.id.listViewHistory);
        historyRecords = new ArrayList<HistoryRecord>();
        loadHistory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        historyAdapter = new HistoryAdapter(getApplicationContext(), R.layout.item_view_history, historyRecords);
        listView.setAdapter(historyAdapter);
    }

    private void loadHistory() {
        FileInputStream fis = null;
        try {
            fis = openFileInput("history.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String text;
            int number = 1;
            ArrayList<String> data = new ArrayList<String>();
            while ((text = br.readLine()) != null){
                data.add(text);
            }
            String date = "";
            int i =0;
            while (i< data.size())
            {
                String results = "";
                if (!(data.get(i).equals(""))) {
                    number = Integer.parseInt(data.get(i));
                    date = data.get(i + 1);
                    for (int j = 2; j < number; j++) {
                        results = results + data.get(i + j) + "\n=";
                    }
                    results = results + data.get(i + number);
                    historyRecords.add(new HistoryRecord(results, date));
                    i = i + number;
                }
                i= i+1;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
