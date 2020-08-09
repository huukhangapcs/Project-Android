package com.example.moneyconverter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    EditText enterNumber;
    TextView operationView;
    TextView updateView;
    ListView listViewResult;
    Button addButton;
    ImageView imageView;
    ArrayList<Country> resultCountry;
    ArrayList<Country> availableCountry;
    ResultAdapter resultAdapter;
    Calculator calculator;
    int digit;
    long var1;
    long number = 0;
    private int errorFlag = 0;
    FetchJson fetchJson;
    DecimalFormat df = new DecimalFormat("#.##");
    String error = "ERROR";
    HashMap<String,String> rates;
    public static final String SHARED_PREFS = "sharedPrefs";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculator = new Calculator();
        imageView = (ImageView) findViewById(R.id.VietnamFlag);
        enterNumber = (EditText) findViewById(R.id.editTextAmount);
        operationView = (TextView) findViewById(R.id.textViewOperation);
        updateView = (TextView) findViewById(R.id.textViewUpdate);
        listViewResult = (ListView) findViewById(R.id.listViewCurrency);
        addButton = (Button) findViewById(R.id.add_button);
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
        rates = new  HashMap<String,String>();
        getRate();
        if (!rates.isEmpty()) {
            setRate();
            saveCurrencyRate();
        }
        else loadCurrencyRate();
    }

    private void initCountry() {
        availableCountry = new ArrayList<>();
        resultCountry = new ArrayList<>();
        resultCountry.add(new Country(R.drawable.usa, "USD", "United States dollar $", 23255));
        availableCountry.add(new Country(R.drawable.euro, "EUR", "Euro \u20ac", 27415));
        availableCountry.add(new Country(R.drawable.japan, "JPY", "Japanese Yen \uffe5", 219));
        availableCountry.add(new Country(R.drawable.british, "GBP", "Pound Sterling \uffe1", 30333));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) {
            Intent my_intent = new Intent(this, HistoryActivity.class);
            startActivity(my_intent);
        }
        else if (id == R.id.save)
        {
            saveHistory();
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();


        if (!rates.containsKey("USD")) Log.d("TagUSD", "NO CONNECTION");
        imageView.setImageResource(R.drawable.vietnam);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddCurrency.class);
                i.putExtra("available", availableCountry);
                i.putExtra("result", resultCountry);
                startActivityForResult(i, 1);
            }
        });
    }

    private void setRate() {
        if (rates.isEmpty()) Log.d("Tag","ERRRRRRRRRRRRRRRRRRRRRRR");
        else {
//chuyen tu base eur sang vnd
            if (rates.containsKey("VND")) {
                double rate_vnd = Double.parseDouble(rates.get("VND"));
                for (int i = 0; i < resultCountry.size(); i++) {
                    String name = resultCountry.get(i).getName();
                    if (name == "EUR") resultCountry.get(i).setExchangeRate(rate_vnd);
                    else if (rates.containsKey(name)) {
                        double value = Double.parseDouble(rates.get(name));
                        value = rate_vnd / value;
                        resultCountry.get(i).setExchangeRate(value);
                    }
                }
                for (int i = 0; i < availableCountry.size(); i++) {
                    String name = availableCountry.get(i).getName();
                    if (name == "EUR") availableCountry.get(i).setExchangeRate(rate_vnd);
                    else if (rates.containsKey(name)) {
                        double value = Double.parseDouble(rates.get(name));
                        value = rate_vnd / value;
                        availableCountry.get(i).setExchangeRate(value);
                    }
                }
            }
        }
    }

    private void getRate() {
        fetchJson = new FetchJson();
        try {
            rates = fetchJson.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
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
        Button button = (Button) view;
        String s = button.getText().toString();
        switch (s) {
            case "\u003D":
                showResult();
                break;
            case "+":
            case "-":
            case "x":
            case "\u00F7":
                calculator.setFlag(s);
                number = Long.parseLong(enterNumber.getText().toString());
                var1 = number;
                enterNumber.setText(String.valueOf(0));

                TextView textView = (TextView) findViewById(R.id.textViewOperation);
                textView.setText(String.valueOf(var1) + s);
                break;
            case ".":
                //It is no use to implement this method in VND, so I skip this.
                break;
            case "\u232b":
                deleteOneNumber();
                break;
            default:
                numberPressed(s);
                break;
        }
    }


    private void numberPressed(String s) {
        digit = Integer.parseInt(s);
        number = Long.parseLong(enterNumber.getText().toString());
        if (errorFlag == 1) {
            TextView errView = (TextView) findViewById(R.id.textViewOperation);
            errView.setText("");
            errView.setTextColor(Color.parseColor("#000000"));
            errorFlag = 0;
            number = (long) digit;
        } else number = number * 10 + digit;
        enterNumber.setText(String.valueOf(number));
    }

    private void deleteOneNumber() {
        number = Long.parseLong(enterNumber.getText().toString());
        number = number / 10;
        enterNumber.setText(String.valueOf(number));
    }

    private void showResult() {
        TextView errView = (TextView) findViewById(R.id.textViewOperation);
        number = Long.parseLong(enterNumber.getText().toString());
        number = calculator.operate(number, var1);
        calculator.resetFlag();
        if (number < 0) {
            errView.setText(error);
            errView.setTextColor(Color.parseColor("#FF0000"));
            errorFlag = 1;
        } else {
            enterNumber.setText(String.valueOf(number));
            convertMoney();
        }
    }

    private void convertMoney() {
        number = Long.parseLong(enterNumber.getText().toString());
        ;
        int n = resultCountry.size();
        for (int i = 0; i < n; i++) {
            double t = resultCountry.get(i).getExchangeRate();
            resultCountry.get(i).setExchangeResult((double) number / t);
//            resultAdapter.clear();
//            resultAdapter.addAll(resultCountry);
            resultAdapter.notifyDataSetChanged();
        }
    }

    private class FetchJson extends AsyncTask<Void, Void,HashMap<String,String>>{
        String data;
        @Override
        protected HashMap<String,String> doInBackground(Void... voids) {
            String url = "http://data.fixer.io/api/latest?access_key=5237c6367dd31965a002d9cb297e3c44";
            HashMap<String,String> results = new HashMap<String,String>();
            try {
                URL my_url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) my_url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);
                }
                Log.d("Buffer", buffer.toString());
                JSONObject jsonObject = new JSONObject(buffer.toString());
                String date = jsonObject.getString("date");
                JSONObject rates  = jsonObject.getJSONObject("rates");
                // GET CANADA currency for testing
                String VND = rates.getString("VND");
                String USD = rates.getString("USD");
                String GBP = rates.getString("GBP");
                String JPY = rates.getString("JPY");
                results.put("date", date);
                results.put("VND",VND);
                results.put("USD",USD);
                results.put("GBP",GBP);
                results.put("JPY",JPY);
                Log.d("Buffer", VND);
                updateView.setText("Exchange rate is up to date");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> results) {
            super.onPostExecute(results);
        }
    }

    private void saveCurrencyRate()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date", rates.get("date"));
        for (int i =0; i< resultCountry.size(); i++) {
            editor.putString(resultCountry.get(i).getName(), String.valueOf(resultCountry.get(i).getExchangeRate()));
        }
        for (int i =0; i< availableCountry.size(); i++) {
            editor.putString(availableCountry.get(i).getName(), String.valueOf(availableCountry.get(i).getExchangeRate()));
        }
        editor.apply();
        Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadCurrencyRate()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        String date = sharedPreferences.getString("date","");
        updateView.setText("Last updated:" + date);
        for (int i =0; i< resultCountry.size(); i++) {
            String name = resultCountry.get(i).getName();
            if (!Objects.equals(sharedPreferences.getString(name, ""), ""))
            {
                Log.d("Tag", sharedPreferences.getString(name,""));
                double value = Double.parseDouble(sharedPreferences.getString(name,""));
                resultCountry.get(i).setExchangeRate(value);
            }
        }
        for (int i =0; i< availableCountry.size(); i++) {
            String name = availableCountry.get(i).getName();
            if (!Objects.equals(sharedPreferences.getString(name, ""), ""))
            {
                Log.d("Tag", sharedPreferences.getString(name,""));
                double value = Double.parseDouble(sharedPreferences.getString(name,""));
                availableCountry.get(i).setExchangeRate(value);
            }
        }
    }
    private void saveHistory()
    {
        FileOutputStream fOS = null;
        Date currentTime = Calendar.getInstance().getTime();
        try {
            fOS = openFileOutput("history.txt", MODE_APPEND);
            fOS.write((String.valueOf(resultCountry.size() + 2) + "\n").getBytes()); // number of line for this history
            fOS.write((currentTime.toString()+"\n").getBytes()); // time
            String vnd = String.valueOf(number) + "VND\n";
            fOS.write(vnd.getBytes()); // base vnd
            for (int i =0; i<resultCountry.size(); i++) {
                String val = String.valueOf(resultCountry.get(i).getExchangeResult()) + resultCountry.get(i).getName() + "\n";
                fOS.write(val.getBytes());
            }
            Toast.makeText(this, "Saved successfully to"+ getFilesDir(), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fOS != null)
            {
                try {
                    fOS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}