package com.example.moneyconverter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText enterNumber;
    TextView operationView;
    ListView listViewResult;
    Button addButton;
    ImageView imageView;
    ArrayList<Country>resultCountry;
    ArrayList<Country>availableCountry;
    ResultAdapter resultAdapter;
    int digit;
    long var1;
    long number =0;
    private int errorFlag = 0;
    int flag =-1;
    DecimalFormat df = new DecimalFormat("#.##");
    String error = "ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.VietnamFlag);
        enterNumber = (EditText)findViewById(R.id.editTextAmount);
        operationView = (TextView)findViewById(R.id.textViewOperation);
        listViewResult = (ListView)findViewById(R.id.listViewCurrency);
        addButton = (Button)findViewById(R.id.add_button);
        initCountry();
        resultAdapter = new ResultAdapter(this, R.layout.result_view, resultCountry);
        listViewResult.setAdapter(resultAdapter);
        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Country target = resultCountry.get(i);
                resultCountry.remove(target);
                availableCountry.add(target);
                resultAdapter.clear();
                resultAdapter.addAll(resultCountry);
                resultAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initCountry() {
        availableCountry = new ArrayList<>();
        resultCountry = new ArrayList<>();
        resultCountry.add(new Country(R.drawable.usa, "USD", "United States dollar $",23255));
        availableCountry.add(new Country(R.drawable.euro, "EUR", "Euro \u20ac",27107));
        availableCountry.add(new Country(R.drawable.japan, "JPY", "Japanese Yen \uffe5",219));
        availableCountry.add(new Country(R.drawable.british, "GBP", "Pound Sterling \uffe1",29747));
    }

    @Override
    protected void onStart() {
        super.onStart();

        imageView.setImageResource(R.drawable.vietnam);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddCurrency.class);
                i.putExtra("available",availableCountry);
                i.putExtra("result", resultCountry);
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode==RESULT_OK)
        {
            availableCountry = (ArrayList<Country>) data.getSerializableExtra("available");
            resultCountry = (ArrayList<Country>) data.getSerializableExtra("result");
            convertMoney();
            resultAdapter.clear();
            resultAdapter.addAll(resultCountry);
            resultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        availableCountry = (ArrayList<Country>) savedInstanceState.getSerializable("available");
        resultCountry = (ArrayList<Country>) savedInstanceState.getSerializable("result");
        number = savedInstanceState.getLong("enteredNum");
        resultAdapter.clear();
        resultAdapter.addAll(resultCountry);
        resultAdapter.notifyDataSetChanged();

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("available", availableCountry);
        outState.putSerializable("result", resultCountry);
        outState.putLong("enteredNum", number);

    }

    public void ButtonPressed(View view) {
        Button button = (Button)view;
        String s = button.getText().toString();
        if (s.equals("\u003D"))
            showResult();
        else if (s.equals("+") || s.equals("-") || s.equals("x") ||s.equals("\u00F7") )
        {
            setFlag(s);
            number = Long.parseLong(enterNumber.getText().toString());
            var1 = number;
            enterNumber.setText(String.valueOf(0));

            TextView textView = (TextView)findViewById(R.id.textViewOperation);
            textView.setText(String.valueOf(var1) + s);
        }
        else if (s.equals("."))
        {
            //It is no use to implement this method in VND, so I skip this.
        }
        else if (s.equals("\u232b"))
        {
            deleteOneNumber();
        }
        else
        {
            numberPressed(s);
        }
    }

    private void setFlag(String s) {
        if (s.equals("+")) flag = 0;
        else if (s.equals("-")) flag =1;
        else if (s.equals("x")) flag = 2;
        else if (s.equals("\u00F7")) flag =3;
    }

    private void numberPressed(String s) {
        digit = Integer.parseInt(s);
        number = Long.parseLong(enterNumber.getText().toString());
        if (errorFlag ==1)
        {
            TextView errView = (TextView)findViewById(R.id.textViewOperation);
            errView.setText("");
            errView.setTextColor(Color.parseColor("#000000"));
            errorFlag = 0;
            number = Long.valueOf(digit);
        }
        else number = number * 10+ digit;
        enterNumber.setText(String.valueOf(number));
    }

    private void deleteOneNumber() {
        number = Long.parseLong(enterNumber.getText().toString());
        number = number/10;
        enterNumber.setText(String.valueOf(number));
    }

    private void showResult() {
        TextView errView = (TextView)findViewById(R.id.textViewOperation);
        number = Long.parseLong(enterNumber.getText().toString());
        if (flag == 0)
            number += var1;
        else if (flag == 1)
            number = var1 - number;
        else if (flag == 2)
            number *= var1;
        else if (flag ==3)
        {
            if (number == 0)
                number = (long) -1;
            else number = var1/number;
        }
        flag =-1;
        if (number < 0)
        {
            errView.setText(error);
            errView.setTextColor(Color.parseColor("#FF0000"));
            errorFlag = 1;
        }
        else
        {
            enterNumber.setText(String.valueOf(number));
            convertMoney();

        }
    }

    private void convertMoney() {
        number = Long.parseLong(enterNumber.getText().toString());;
        int n = resultCountry.size();
        for (int i =0; i<n; i++)
        {
            int t = resultCountry.get(i).getExchangeRate();
            resultCountry.get(i).setExchangeResult((float)number/t);
//            resultAdapter.clear();
//            resultAdapter.addAll(resultCountry);
            resultAdapter.notifyDataSetChanged();
        }
    }
}