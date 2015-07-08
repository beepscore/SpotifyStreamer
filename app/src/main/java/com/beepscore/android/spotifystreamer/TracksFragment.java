package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Shows an Artist's top tracks.
 */
public class TracksFragment extends Fragment {

    // TODO: use intent extra (e.g. artistId, artist) to get top tracks and show them in a list

    private String artistId = "";

    public TracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        // get the intent the activity was started with
        // http://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            TextView textView = (TextView)rootView.findViewById(R.id.tracks_text_view);
            textView.setText(artistId);
        }

        return rootView;
    }

}
