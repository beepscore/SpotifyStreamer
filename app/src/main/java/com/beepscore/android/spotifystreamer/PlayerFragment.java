package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 *
 */
public class PlayerFragment extends Fragment {

    private TrackParcelable trackParcelable;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the intent the activity was started with
        // http://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
        Intent intent = getActivity().getIntent();
        configureTrackParcelable(intent);
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

            TextView trackView = (TextView)playerView.findViewById(R.id.track_view);
            trackView.setText(trackParcelable.name);
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
}
