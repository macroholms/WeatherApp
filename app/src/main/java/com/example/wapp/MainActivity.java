package com.example.wapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherLV = (ListView) findViewById(R.id.weatherLV);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherLV.setAdapter(weatherArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.flBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationET = (EditText) findViewById(R.id.EditText);
                URL url = createURL(locationET.getText().toString());
                if (url != null){
                    dismissKeyboard(locationET);
                    GetWeatherTask getWeatherTask = new GetWeatherTask();
                    getWeatherTask.execute(url);
                }else{
                    Snackbar.make(findViewById(R.id.linearLayout),
                            "Invalid URL", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void dismissKeyboard (View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private URL createURL(String city) {
        String apikey = getString(R.string.api_key);
        String baseUrl3 = getString(R.string.web_service_url_forecast);
        try {
            String urlString2 = baseUrl3+ URLEncoder.encode(city, "UTF-8")
                    + "&appid=" + apikey + "&units=metric";
            return new URL(urlString2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK){
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    )){
                        String line;
                        while ((line = reader.readLine()) != null){
                            builder.append(line);
                        }
                    }catch (IOException e){
                        Snackbar.make(findViewById(R.id.linearLayout),
                                "Unable to read weather data", Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                }else{
                    Snackbar.make(findViewById(R.id.linearLayout),
                            "Unable to connect to OpenWeatherMap.org", Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Snackbar.make(findViewById(R.id.linearLayout),
                        "Unable to connect to OpenWeatherMap.org", Snackbar.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.e("!", e.getMessage());
            }finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList(weather);
            weatherArrayAdapter.notifyDataSetChanged();
            weatherLV.smoothScrollToPosition(0);
        }
    }

    private void convertJSONtoArrayList(JSONObject forecast){
        weatherList.clear();
        try{
            JSONArray list = forecast.getJSONArray("list");

            for (int i = 0; i < list.length(); ++i){
                JSONObject day = list.getJSONObject(i);
                JSONObject temperatures = day.getJSONObject("main");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);


                weatherList.add(new Weather(
                        day.getLong("dt"),
                        temperatures.getDouble("temp_min"),
                        temperatures.getDouble("temp_max"),
                        temperatures.getDouble("humidity"),
                        weather.getString("description"),
                        weather.getString("icon")
                ));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}