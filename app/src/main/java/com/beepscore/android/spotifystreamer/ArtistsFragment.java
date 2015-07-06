package com.beepscore.android.spotifystreamer;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * Shows artists
 */
public class ArtistsFragment extends Fragment {

    public ArtistsFragment() {
    }

    ArrayAdapter<String> adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistsView = inflater.inflate(R.layout.fragment_artists, container, false);

        List<String> list = new ArrayList<String>();
        // add a placeholder element
        list.add(getActivity().getString(R.string.dash));

        // adapter creates views for each list item
        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item,
                R.id.list_item_textview,
                list);

        ListView listView = (ListView) artistsView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        fetchArtists("Beyonce");

        return artistsView;
    }

    private void fetchArtists(String artistName) {
        FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
        fetchArtistsTask.execute(artistName);
    }

////////////////////////////////////////////////////////////////////////////

    // first parameter is input, second is integer for onProgressUpdate, third is return for onPostExecute
    private class FetchArtistsTask extends AsyncTask<String, Void, ArtistsPager> {

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

        /**
         * The system calls doInBackground on a worker (background) thread
         * @param params first and only element is artistName to make request to Spotify
         * @return ArtistsPager SpotifyApi Java object.
         */
        @Override
        protected ArtistsPager doInBackground(String... params) {

            String artistName = params[0];

            // don't use try/catch/finally here. Assume SpotifyAPI is handling it's errors.

            // https://docs.google.com/presentation/d/1Q8LwzD5ODqirWG7K_e4sklE3fEFY_dr4kH4hfoRa0BQ/pub?start=false&loop=false&delayms=15000#slide=id.ga25585343_0_137
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistsPager = spotifyService.searchArtists(artistName);
            return artistsPager;
        }

        /**
         * Use to update UI. The system calls this on the UI thread.
         * @param artistsPager is the result returned from doInBackground()
         */
        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            super.onPostExecute(artistsPager);

            // https://developer.spotify.com/web-api/object-model
            List<Artist> artistList = artistsPager.artists.items;

            ArrayList<String> artistsStrings = new ArrayList<String>();

            for (Artist artist : artistList) {
                // also can get artist.id
                artistsStrings.add(artist.name);
            }
            updateAdapter(adapter, artistsStrings);
        }

        private void updateAdapter(ArrayAdapter anAdapter, ArrayList<String> artistsStrings) {
            anAdapter.clear();
            anAdapter.addAll(artistsStrings);
        }

    }
}
