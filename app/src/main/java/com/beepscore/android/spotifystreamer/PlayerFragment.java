package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 *
 */
public class PlayerFragment extends Fragment {

    private final String LOG_TAG = PlayerFragment.class.getSimpleName();

    private TrackParcelable trackParcelable;

    private AudioPlayer mPlayer = new AudioPlayer();

    private ImageButton mNextButton;
    private ImageButton mPlayButton;
    private ImageButton mPreviousButton;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the intent the activity was started with
        // http://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
        Intent intent = getActivity().getIntent();
        configureTrackParcelable(intent);

        // TODO: Use setRetainInstance to avoid interrupting audio during rotation?
        // retain fragment. When user rotates device, activity will be destroyed
        // but fragment instance with audio player will be passed to new activity.
        // setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View playerView = inflater.inflate(R.layout.fragment_player, container, false);

        configureActionBarTitle(getString(R.string.app_name));

        if (trackParcelable != null) {

            TextView artistView = (TextView)playerView.findViewById(R.id.artist_view);
            artistView.setText(trackParcelable.artistName);

            TextView albumView = (TextView)playerView.findViewById(R.id.album_view);
            albumView.setText(trackParcelable.albumName);

            ImageView imageView = (ImageView)playerView.findViewById(R.id.image_view);
            if (trackParcelable.imageWidestUrl == null
                    || trackParcelable.imageWidestUrl.equals("")) {
                // show placeholder image
                Picasso.with(getActivity()).load(R.mipmap.ic_launcher).into(imageView);
            } else {
                Picasso.with(getActivity()).load(trackParcelable.imageWidestUrl).into(imageView);
            }

            final TextView trackView = (TextView)playerView.findViewById(R.id.track_view);
            trackView.setText(trackParcelable.name);

            mPlayButton = (ImageButton)playerView.findViewById(R.id.play_button);
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // TODO: Toggle button between play and pause or play and stop
                    // toggle image and action
                    // alternatively put 2 buttons on top of each other and show/hide them
                    // TODO: get url for entire track, not just previewUrl
                    try {
                        mPlayer.play(getActivity(), trackParcelable.previewUrl);
                    } catch (IllegalArgumentException e) {
                        Log.e(LOG_TAG, e.getLocalizedMessage());
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.getLocalizedMessage());
                    }
                }
            });

        }

        return playerView;
    }

    private void configureTrackParcelable(Intent intent) {
        String TRACK_KEY = getActivity().getString(R.string.TRACK_KEY);
        if (intent != null && intent.hasExtra(TRACK_KEY)) {
            trackParcelable = intent.getParcelableExtra(TRACK_KEY);
        }
    }

    private void configureActionBarTitle(String title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null
                && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }
}
