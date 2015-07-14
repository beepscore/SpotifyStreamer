package com.beepscore.android.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TracksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
                // On Lollipop, "Home" button is at bottom center, circle shape.
                // Udacity reviewer suggested add case home
                // With or without this case, click Home navigates outside app.
                // https://developer.android.com/reference/android/support/v4/app/NavUtils.html
                // https://review.udacity.com/#!/reviews/27659
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            case R.id.action_settings: {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
