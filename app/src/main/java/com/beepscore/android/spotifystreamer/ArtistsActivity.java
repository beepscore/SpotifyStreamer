package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class ArtistsActivity extends AppCompatActivity
        implements ArtistsFragment.Callback, TracksFragment.Callback, PlayerFragment.Callback {

    private final String LOG_TAG = ArtistsActivity.class.getSimpleName();
    private static final String ARTISTS_FRAGMENT_TAG = "ARTISTS_FRAGMENT_TAG";
    private static final String PLAYERFRAGMENT_TAG = "PLAYERFRAGMENT_TAG";
    protected boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        mTwoPane = LayoutUtils.isTwoPane(this);

        if ((mTwoPane == true) && (savedInstanceState == null)) {
            // show the detail fragment in this activity
            // add or replace the detail fragment using a fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tracks_detail_container, new TracksFragment(), ARTISTS_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////

    // ArtistsFragment.Callback
    @Override
    public void onArtistSelected(ArtistParcelable artistParcelable) {
        if (mTwoPane == true) {
            // Two pane mode
            // In ArtistsActivity show ArtistsFragment as master and TracksFragment as detail

            // Every fragment has a property of type Bundle named "arguments".
            // This Bundle is separate from the savedInstanceState Bundle.
            // Use fragment.setArguments to pass info to TracksFragment.
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.ARTIST_KEY), artistParcelable);

            TracksFragment fragment = new TracksFragment();
            fragment.setArguments(args);

            // Use fragment transaction to replace the detail fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tracks_detail_container, fragment, ARTISTS_FRAGMENT_TAG)
                    .commit();

        } else {
            // One pane mode
            // start TracksActivity to show TracksFragment
            // Use intent to pass info to TracksActivity.
            // TracksActivity can use fragment.setArguments to pass info to TracksFragment.
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(this.getString(R.string.ARTIST_KEY), artistParcelable);
            startActivity(intent);
        }
    }
    ///////////////////////////////////////////////////////////////////////////

    // TracksFragment.Callback

    /**
     * show PlayerFragment as dialog
     * @param bundle will be passed to PlayerFragment as arguments
     */
    @Override
    public void onTrackSelected(Bundle bundle) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), PLAYERFRAGMENT_TAG);
    }
    ///////////////////////////////////////////////////////////////////////////

    // PlayerFragment.Callback

    /**
     * show PlayerFragment as dialog
     * @param bundle will be passed to PlayerFragment as arguments
     */
    @Override
    public void onNextSelected(Bundle bundle) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), PLAYERFRAGMENT_TAG);
    }

    /**
     * show PlayerFragment as dialog
     * @param bundle will be passed to PlayerFragment as arguments
     */
    @Override
    public void onPreviousSelected(Bundle bundle) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), PLAYERFRAGMENT_TAG);
    }
    ///////////////////////////////////////////////////////////////////////////
}
