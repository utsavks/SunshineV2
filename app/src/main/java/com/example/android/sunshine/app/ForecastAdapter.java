package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

import static com.example.android.sunshine.app.ForecastFragment.*;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * Prepare the weather high/lows for presentation.
     */
    private String[] formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        return new String[]{Utility.formatTemperature(high, isMetric) , Utility.formatTemperature(low, isMetric)};
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String[] convertCursorRowToUXFormat(Cursor cursor) {

        return formatHighLows(
                cursor.getDouble(COL_WEATHER_MAX_TEMP),
                cursor.getDouble(COL_WEATHER_MIN_TEMP));

    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        String[] temperatureValues = convertCursorRowToUXFormat(cursor);
        String maxTemperature = temperatureValues[0];
        String minTemperature = temperatureValues[1];
        String description = cursor.getString(COL_WEATHER_DESC);

        String date = Utility.getFriendlyDayString(context,cursor.getLong(COL_WEATHER_DATE));

        TextView date_textview = (TextView) view.findViewById(R.id.list_item_date_textview);
        TextView forecast_textview = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        TextView high_textview = (TextView)view.findViewById(R.id.list_item_high_textview);
        TextView low_textview = (TextView)view.findViewById(R.id.list_item_low_textview);

        date_textview.setText(date);
        forecast_textview.setText(description);
        high_textview.setText(maxTemperature+"°");
        low_textview.setText(minTemperature+"°");

    }
}