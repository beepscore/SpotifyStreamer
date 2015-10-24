package com.beepscore.android.spotifystreamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

// Only used in 1 pane mode
// References:
// Sunshine / DetailActivity
// https://github.com/udacity/Sunshine-Version-2/blob/3.02_create_detail_activity/app/src/main/java/com/example/android/sunshine/app/DetailActivity.java
public class PlayerActivity extends AppCompatActivity {

    private final String LOG_TAG = PlayerActivity.class.getSimpleName();
    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (savedInstanceState == null) {

            // getExtras from Intent that started PlayerActivity
            Bundle activity_extras = getIntent().getExtras();

            mPlayerFragment = new PlayerFragment();

            // Pass info to fragment via fragment.setArguments
            mPlayerFragment.setArguments(activity_extras);

            // Dynamically add PlayerFragment to player_container using a fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_container, mPlayerFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
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

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed");
        if (mPlayerFragment != null) {
            mPlayerFragment.stopAndUnbindService();
            // TODO: check if onBackPressed should delete retained player fragment
        }
        super.onBackPressed();
    }
}
