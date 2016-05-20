package com.example.android.sunshine.app;

/**
 * Created by UTSAV on 08-05-2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

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
        FetchWeatherTask f = new FetchWeatherTask(getActivity(),forecastAdapter);
        f.execute(zip);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
    }