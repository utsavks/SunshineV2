package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    private static final int DETAIL_LOADER = 2;

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

    private static final String[] DETAIL_COLUMNS={WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };

    static final int COL_DATE = 0;
    static final int COL_DESC = 1;
    static final int COL_MAX_TEMP = 2;
    static final int COL_MIN_TEMP = 3;
    static final int COL_HUMIDITY = 4;
    static final int COL_WIND_SPEED = 5;
    static final int COL_DEGREES = 6;
    static final int COL_PRESSURE = 7;

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


    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private  ShareActionProvider mShareActionProvider;
        private  String mForecast;



        public DetailFragment() {
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER,savedInstanceState,this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri detailURI = getActivity().getIntent().getData();
            return new CursorLoader(getActivity(),detailURI,DETAIL_COLUMNS,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if(!cursor.moveToFirst()){return;}
            String date = Utility.formatDate(cursor.getLong(COL_DATE));
            String description = cursor.getString(COL_DESC);
            boolean isMetric = Utility.isMetric(getActivity());
            String max_temp = Utility.formatTemperature(cursor.getDouble(COL_MAX_TEMP),isMetric);
            String min_temp = Utility.formatTemperature(cursor.getDouble(COL_MIN_TEMP), isMetric);
            String humidity = cursor.getString(COL_HUMIDITY);
            String windSpeed = cursor.getString(COL_WIND_SPEED);
            String windDirection = cursor.getString(COL_DEGREES);
            String pressure = cursor.getString(COL_PRESSURE);
            mForecast = "DATE - "+ date + "\n" +
                    "Weather Description - "+ description+"\n"+
                    "Temperature - " + max_temp + " / "
                    + min_temp + "\n"+
                    "Humidity - "+ humidity+"\n"+
                    "Speed of wind - "+windSpeed+"/"+windDirection+" degrees"+
                    "\n"+ "Pressure - "+pressure;

            TextView detailTV = (TextView)getView().findViewById(R.id.detail_textview);
            detailTV.setText(mForecast);
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail_fragment,menu);

            MenuItem item = menu.findItem(R.id.action_share);
            ShareActionProvider mActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if(mActionProvider!=null)
            mActionProvider.setShareIntent(createShareIntent());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            return rootView;


        }

        private Intent createShareIntent(){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast+" "+FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

    }
}