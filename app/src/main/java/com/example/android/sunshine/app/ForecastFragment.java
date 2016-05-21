package com.example.android.sunshine.app;

/**
 * Created by UTSAV on 08-05-2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ForecastAdapter forecastAdapter;
    public ForecastFragment() {
    }
   private static final int loader_id = 1;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle){
        String locationSetting = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        return new CursorLoader(getActivity(),weatherForLocationUri,
                FORECAST_COLUMNS, null, null, sortOrder);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        forecastAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader){
        forecastAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(loader_id,savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        forecastAdapter = new ForecastAdapter(getActivity(),null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listview_forecast = (ListView)rootView.findViewById(R.id.listview_forecast);
        listview_forecast.setAdapter(forecastAdapter);

        listview_forecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String extraWeatherData = forecastAdapter.getItem(i);
                Intent goToDetail = new Intent(getActivity(),DetailActivity.class);
//                goToDetail.putExtra(Intent.EXTRA_INTENT,extraWeatherData);
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
        FetchWeatherTask f = new FetchWeatherTask(getActivity());
        String zip = Utility.getPreferredLocation(getActivity());
        f.execute(zip);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
    }