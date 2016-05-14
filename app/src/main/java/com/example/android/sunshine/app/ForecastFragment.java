package com.example.android.sunshine.app;

/**
 * Created by UTSAV on 08-05-2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;
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
        forecastAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast,
                                                         R.id.list_item_forecast_textview,new ArrayList<String>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listview_forecast = (ListView)rootView.findViewById(R.id.listview_forecast);
        listview_forecast.setAdapter(forecastAdapter);

        listview_forecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String extraWeatherData = forecastAdapter.getItem(i);
                Intent goToDetail = new Intent(getActivity(),DetailActivity.class);
                goToDetail.putExtra(Intent.EXTRA_INTENT,extraWeatherData);
                startActivity(goToDetail);
            }
        });

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
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zip = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        FetchWeatherTask f = new FetchWeatherTask();
        f.execute(zip);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private String getTemperatureFormat(double high, double low){
        SharedPreferences heatPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unit = heatPreference.getString(getString(R.string.pref_units_key),
                                               getString(R.string.pref_units_default));
        if(!unit.equals(getString(R.string.pref_units_default)))
        { high = (high*1.8) + 32.0;
          low = (low*1.8) + 32.0;}
        long max = Math.round(high);
        long min = Math.round(low);

        String formattedOutput = max + " / " + min ;
        return formattedOutput;
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getDateFormat(long time){
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM");
            return dateFormat.format(time);
        }



        private String[] getWeatherDataFromJSON(String forecastJsonStr,int numDays)
        throws JSONException{
            final String LIST = "list";
            final String DESCRIPTION = "main";
            final String TEMPERATURE = "temp";
            final String MAXIMUM = "max";
            final String MINIMUM = "min";
            final String WEATHER = "weather";

            JSONObject weather = new JSONObject(forecastJsonStr);
            JSONArray dayArray = weather.getJSONArray(LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(),dayTime.gmtoff);

            dayTime = new Time();

            String [] resultStr = new String[numDays];
            for(int i=0;i<dayArray.length();i++)
            {
                String day;
                String description;
                String heatInfo;

                long time = dayTime.setJulianDay(julianStartDay+i);
                day = getDateFormat(time);

                JSONObject dayInfo = dayArray.getJSONObject(i);
                JSONObject descriptionList = dayInfo.getJSONArray(WEATHER).getJSONObject(0);
                description = descriptionList.getString(DESCRIPTION);

                JSONObject temperatureInfo = dayInfo.getJSONObject(TEMPERATURE);
                double high = temperatureInfo.getDouble(MAXIMUM);
                double low = temperatureInfo.getDouble(MINIMUM);

                heatInfo = getTemperatureFormat(high, low);

                resultStr[i]= day + " - " + description + " - " + heatInfo;

            }

            return resultStr;
        }


        @Override
        protected String[] doInBackground(String... params) {
            if(params==null)
            {
                return null;
            }

            HttpURLConnection urlConn = null;
            BufferedReader reader = null;
            String forecastJSONStr=null;
            String mode = "json";
            String units = "metric";
            int cnt = 7;

            try {

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String FORMAT_PARAM = "mode";
                final String Query_PARAM = "q";
                final String UNIT_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(Query_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,mode)
                        .appendQueryParameter(UNIT_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(cnt))
                        .appendQueryParameter(APPID_PARAM,BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("GET");
                urlConn.connect();

                InputStream inputStream = urlConn.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJSONStr = buffer.toString();
            }
            catch(Exception e){
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
            try {
                if(forecastJSONStr!=null)
                 return getWeatherDataFromJSON(forecastJSONStr, cnt);
            }catch (JSONException e){e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if(result!=null)
            {
                forecastAdapter.clear();
                for (String s:result) {
                    forecastAdapter.add(s);
                }

            }
        }
    }
}