package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    String mLocation;
    String FORECASTFRAGMENT_TAG = "ForecastFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(),FORECASTFRAGMENT_TAG)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        if(mLocation!=location&&location!=null)
        {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager()
                                  .findFragmentByTag(FORECASTFRAGMENT_TAG);
            if(ff!=null)
            {ff.onLocationChanged();}
            mLocation = location;
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
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.action_map)
        {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String zip = Utility.getPreferredLocation(this);
        Uri geolocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", zip).build();
        Intent mapOpener = new Intent(Intent.ACTION_VIEW);
        mapOpener.setData(geolocation);
        if (mapOpener.resolveActivity(getPackageManager()) != null) {
            startActivity(mapOpener);
        }

    }
}
