package com.beepscore.android.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

// Only used in 1 pane mode
// References:
// Sunshine / DetailActivity
// https://github.com/udacity/Sunshine-Version-2/blob/3.02_create_detail_activity/app/src/main/java/com/example/android/sunshine/app/DetailActivity.java

public class TracksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        if (savedInstanceState == null) {

            // get extra from the intent that started TracksActivity
            ArtistParcelable artistParcelable = getIntent().getParcelableExtra(this.getString(R.string.ARTIST_KEY));

            TracksFragment tracksFragment = new TracksFragment();

            // Pass info to fragment via fragment.setArguments
            Bundle arguments = new Bundle();
            arguments.putParcelable(getString(R.string.ARTIST_KEY), artistParcelable);

            tracksFragment.setArguments(arguments);

            // Dynamically add TracksFragment to tracks_detail_container using a fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tracks_detail_container, tracksFragment)
                    .commit();
        }
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
