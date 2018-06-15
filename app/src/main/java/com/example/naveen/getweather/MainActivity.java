package com.example.naveen.getweather;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Context;
import android.widget.Toast;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.json.JSONObject;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    EditText cityNameText;
    String cityName;
    TextView weather;
    String weatherText = "";
    String url;
    Double temp;
    String temperature;
    DecimalFormat df;
    ConstraintLayout cl;


    public void getWeather(View view) {
        GetWeather getWeatherTask = new GetWeather();
        cityName = cityNameText.getText().toString();
        InputMethodManager keyboardInput = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardInput.hideSoftInputFromWindow(cityNameText.getWindowToken(), 0);
        try {
            String encodedName = URLEncoder.encode(cityName, "UTF-8");
            weatherText = "";
            cl.setVisibility(View.VISIBLE);
            url = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedName + "&APPID=ea574594b9d36ab688642d5fbeab847e";
            Log.i("URL", url);
            getWeatherTask.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", "Failed during getWeather Method");
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameText = findViewById(R.id.nameOfCity);
        weather = findViewById(R.id.weatherText);
        weather.setText("");
        df = new DecimalFormat("####0.00");
        cl = findViewById(R.id.cl);

    }

    public class GetWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                Log.i("URL IN TRY", urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                Log.i("result: ", result);
                return result;
            } catch (Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        weather.setText("Cannot find city \"" + cityName + "\"\n1.Please spell the city name correctly\n2.Check your internet connection");

                    }
                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherPart = jsonObject.getString("weather");
                JSONObject jsonPart = jsonObject.getJSONObject("main");
                weatherText += "Humidity : " + jsonPart.getString("humidity") + "\n";
                JSONArray jsonArray = new JSONArray(weatherPart);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonMini = jsonArray.getJSONObject(i);
                    weatherText += jsonMini.getString("main") + " : " + jsonMini.getString("description") + "\n";
                }
                temperature = jsonPart.getString("temp_min").toString();
                temp = Double.parseDouble(temperature);
                temp = temp - 273;
                Log.i("temperature", Double.toString(temp));
                weatherText += "Min Temp : " + df.format(temp) + "C" + "\n";
                temperature = jsonPart.getString("temp_max").toString();
                temp = Double.parseDouble(temperature);
                temp = temp - 273;
                weatherText += "Max Temp : " + df.format(temp) + "C" + "\n";
                weather.setText(weatherText);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
