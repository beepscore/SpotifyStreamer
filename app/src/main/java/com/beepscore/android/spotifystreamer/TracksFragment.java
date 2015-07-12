package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * Shows an Artist's top tracks.
 */
public class TracksFragment extends Fragment {

    private String artistId = "";
    private String artistName = "";
    TracksArrayAdapter adapter = null;

    public TracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tracksView = inflater.inflate(R.layout.fragment_tracks, container, false);

        // get the intent the activity was started with
        // http://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
        Intent intent = getActivity().getIntent();

        configureArtistIdAndName(intent);
        configureActionBarSubtitle(artistName);

        List<Track> list = new ArrayList<Track>();

        // adapter creates views for each list item
        adapter = new TracksArrayAdapter(getActivity(), list);

        ListView listView = (ListView) tracksView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        fetchTracks(artistId);

        return tracksView;
    }

    private void configureArtistIdAndName(Intent intent) {
        String ARTIST_KEY = getActivity().getString(R.string.ARTIST_KEY);
        if (intent != null && intent.hasExtra(ARTIST_KEY)) {
            ArtistParcelable artistParcelable = intent.getParcelableExtra(ARTIST_KEY);

            if (artistParcelable != null) {
                if (artistParcelable.id != null) {
                    artistId = artistParcelable.id;
                }
                if (artistParcelable.name != null) {
                    artistName = artistParcelable.name;
                }
            }
        }
    }

    private void configureActionBarSubtitle(String artistName) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null
                && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setSubtitle(artistName);
        }
    }

    private void fetchTracks(String artistId) {
        FetchTracksTask fetchTracksTask = new FetchTracksTask();
        fetchTracksTask.execute(artistId);
    }

////////////////////////////////////////////////////////////////////////////

    // http://stackoverflow.com/questions/9671546/asynctask-android-example?rq=1
    // first parameter is doInBackground first argument params[0].
    // second is onProgressUpdate integer argument.
    // third is doInBackground return type and onPostExecute argument type.
    private class FetchTracksTask extends AsyncTask<String, Void, Tracks> {

        private final String LOG_TAG = FetchTracksTask.class.getSimpleName();

        /**
         * The system calls doInBackground on a worker (background) thread
         * @param params first and only element is artistName to make request to Spotify
         * @return Tracks SpotifyApi Java object.
         */
        @Override
        protected Tracks doInBackground(String... params) {

            String artistId = params[0];

            // don't use try/catch/finally here. Assume SpotifyAPI is handling it's errors.

            // https://docs.google.com/presentation/d/1Q8LwzD5ODqirWG7K_e4sklE3fEFY_dr4kH4hfoRa0BQ/pub?start=false&loop=false&delayms=15000#slide=id.ga25585343_0_137
            // TODO: Consider use spotifyApi as a singleton
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();

            //https://developer.spotify.com/web-api/get-artists-top-tracks/
            Map<String, Object> options = new HashMap<>();
            options.put("country", "US");
            Tracks tracks = spotifyService.getArtistTopTrack(artistId, options);
            return tracks;
        }

        /**
         * Use to update UI. The system calls this on the UI thread.
         * @param tracks is the result returned from doInBackground()
         */
        @Override
        protected void onPostExecute(Tracks tracks) {
            super.onPostExecute(tracks);

            if (isTracksNullOrEmpty(tracks)) {
                Toast toast = Toast.makeText(getActivity(),
                        getActivity().getString(R.string.search_found_no_tracks),
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // https://developer.spotify.com/web-api/object-model
                adapter.clear();
                // addAll calls adapter.notifyDataSetChanged()
                adapter.addAll(tracks.tracks);
            }
        }

        private boolean isTracksNullOrEmpty(Tracks tracks) {
            return (tracks == null
                    || tracks.tracks == null
                    || tracks.tracks.size() == 0);
        }
    }
}
