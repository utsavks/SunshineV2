package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



//    private static final String[] FORECAST_COLUMNS = {
//                            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
//                            WeatherContract.WeatherEntry.COLUMN_DATE,
//                            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
//                            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//                            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
//                            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
//                            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
//                            WeatherContract.WeatherEntry.COLUMN_DEGREES,
//                            WeatherContract.WeatherEntry.COLUMN_PRESSURE
//                    };
//
//    // these constants correspond to the projection defined above, and must change if the
//    // projection changes
//    private static final int COL_WEATHER_ID = 0;
//    private static final int COL_WEATHER_DATE = 1;
//    private static final int COL_WEATHER_DESC = 2;
//    private static final int COL_WEATHER_MAX_TEMP = 3;
//    private static final int COL_WEATHER_MIN_TEMP = 4;



}