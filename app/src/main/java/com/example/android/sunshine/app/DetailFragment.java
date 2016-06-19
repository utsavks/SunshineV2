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
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAIL_LOADER = 2;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private ShareActionProvider mShareActionProvider;
    private  String mForecast;

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
    static final int COL_WIND_DEGREES = 6;
    static final int COL_PRESSURE = 7;

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
        String day = Utility.getDayName(getActivity(),cursor.getLong(COL_DATE));
        String date = Utility.getFormattedMonthDay(getActivity(),cursor.getLong(COL_DATE));
        String description = cursor.getString(COL_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        String max_temp = Utility.formatTemperature(getActivity(),cursor.getDouble(COL_MAX_TEMP),isMetric);
        String min_temp = Utility.formatTemperature(getActivity(),cursor.getDouble(COL_MIN_TEMP), isMetric);
        String humidity = getActivity().getString(R.string.format_humidity, cursor.getFloat(COL_HUMIDITY));
        String wind = Utility.getFormattedWind(getActivity(),cursor.getFloat(COL_WIND_SPEED),cursor.getFloat(COL_WIND_DEGREES));
        String pressure = getActivity().getString(R.string.format_pressure, cursor.getFloat(COL_PRESSURE));


        TextView day_textview = (TextView)getView().findViewById(R.id.detail_day_textview);
        TextView date_textview = (TextView)getView().findViewById(R.id.detail_date_textview);
        TextView high_textview = (TextView)getView().findViewById(R.id.detail_high_textview);
        TextView low_textview = (TextView)getView().findViewById(R.id.detail_low_textview);
        TextView wind_textview = (TextView)getView().findViewById(R.id.detail_wind_textview);
        TextView pressure_textview = (TextView)getView().findViewById(R.id.detail_pressure_textview);
        TextView humidity_textview = (TextView)getView().findViewById(R.id.detail_humidity_textview);
        ImageView desc_imageview = (ImageView)getView().findViewById(R.id.detail_imageview);
        TextView desc_textview = (TextView)getView().findViewById(R.id.detail_desc_textview);

        day_textview.setText(day);
        date_textview.setText(date);
        high_textview.setText(max_temp);
        low_textview.setText(min_temp);
        wind_textview.setText(wind);
        pressure_textview.setText(pressure);
        humidity_textview.setText(humidity);
        desc_textview.setText(description);
        desc_imageview.setImageResource(R.drawable.ic_launcher);


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