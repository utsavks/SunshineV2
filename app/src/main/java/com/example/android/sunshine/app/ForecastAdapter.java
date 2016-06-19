package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
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

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_OTHER_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    private String[] formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        return new String[]{Utility.formatTemperature(mContext, high, isMetric), Utility.formatTemperature(mContext, low, isMetric)};
    }


    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_OTHER_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout = getItemViewType(cursor.getPosition());
        int layoutID = -1;

        if (layout == VIEW_TYPE_TODAY)
            layoutID = R.layout.list_item_forecast_today;
        else if (layout == VIEW_TYPE_OTHER_DAY)
            layoutID = R.layout.list_item_forecast;
        else
            Log.e("Forecast Adapter", "No such layout available for inflation");

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        String[] temperatureValues = formatHighLows(
                cursor.getDouble(COL_WEATHER_MAX_TEMP),
                cursor.getDouble(COL_WEATHER_MIN_TEMP));
        String maxTemperature = temperatureValues[0];
        String minTemperature = temperatureValues[1];
        String description = cursor.getString(COL_WEATHER_DESC);

        String date = Utility.getFriendlyDayString(context, cursor.getLong(COL_WEATHER_DATE));

//        TextView date_textview = (TextView) view.findViewById(R.id.list_item_date_textview);
//        TextView forecast_textview = (TextView)view.findViewById(R.id.list_item_forecast_textview);
//        TextView high_textview = (TextView)view.findViewById(R.id.list_item_high_textview);
//        TextView low_textview = (TextView)view.findViewById(R.id.list_item_low_textview);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.date_textview.setText(date);
        viewHolder.forecast_textview.setText(description);
        viewHolder.high_textview.setText(maxTemperature);
        viewHolder.low_textview.setText(minTemperature);

    }

    public static class ViewHolder {

        TextView date_textview;
        TextView forecast_textview;
        TextView high_textview;
        TextView low_textview;

        public ViewHolder(View view) {
            date_textview = (TextView) view.findViewById(R.id.list_item_date_textview);
            forecast_textview = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            high_textview = (TextView) view.findViewById(R.id.list_item_high_textview);
            low_textview = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}