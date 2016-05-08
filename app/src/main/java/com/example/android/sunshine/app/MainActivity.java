package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            String[] forecastArray ={"Today - Sunny - 88/63","Tomorrow - Foggy - 70/46",
                    "Weds - Cloudy - 72/63","Thurs - Rainy - 64/51",
                    "Fri - Foggy - 70/46","Sat - Sunny - 76/68"};
            List<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));
            ListAdapter forecastAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weekForecast);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView listview_forecast = (ListView)rootView.findViewById(R.id.listview_forecast);
            listview_forecast.setAdapter(forecastAdapter);

            HttpURLConnection urlConn = null;
            BufferedReader reader = null;
            String forecastJSONStr = null;

            try {
                String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=247667&mode=json&units=metric&cnt=7";
                String appID = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;

                URL url = new URL(baseURL.concat(appID));

                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.connect();

                InputStream inputStream = urlConn.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJSONStr = buffer.toString();
            }catch(IOException e){
                Log.e("PlaceHolder","Error",e);}
            finally {
                if (urlConn != null)
                    urlConn.disconnect();
                if (reader != null)
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceHolder", "Error closing stream", e);
                    }
            }

            return rootView;

        }
    }
}
