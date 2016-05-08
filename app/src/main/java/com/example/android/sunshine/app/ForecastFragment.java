package com.example.android.sunshine.app;

/**
 * Created by UTSAV on 08-05-2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            FetchWeatherTask f = new FetchWeatherTask();
            f.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class FetchWeatherTask extends AsyncTask<Void,Void,Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
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
                Log.e(LOG_TAG,"Error",e);}
            finally {
                if (urlConn != null)
                    urlConn.disconnect();
                if (reader != null)
                {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}