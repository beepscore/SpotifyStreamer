package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();

    public ArtistsFragment() {
    }

    ArtistsArrayAdapter adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Artist> list = new ArrayList<Artist>();
        // adapter creates views for each list item
        adapter = new ArtistsArrayAdapter(getActivity(), list);

        // retain fragment so if user rotates device, populated list remains visible
        // setRetainInstance only affects fragments not added to the "back stack".
        // http://developer.android.com/reference/android/app/Fragment.html#setRetainInstance%28boolean%29
        // TODO: Consider use fragmentManager to save fragment on back stack
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistsView = inflater.inflate(R.layout.fragment_artists, container, false);

        final SearchView searchView = (SearchView) artistsView.findViewById(R.id.search_view);
        configureSearchViewListener(searchView);

        ListView listView = (ListView) artistsView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = adapter.getItem(i);
                String artistIdName = getArtistIdName(artist);

                Intent intent = new Intent(getActivity(), TracksActivity.class);
                // Use text extra, simpler to implement than Parcelable object
                intent.putExtra(Intent.EXTRA_TEXT, artistIdName);
                startActivity(intent);
            }
        });

        return artistsView;
    }
    
    private void configureSearchViewListener(final SearchView searchView) {
        // Call fetchArtists when user taps close, not after every letter.
        // https://discussions.udacity.com/t/about-the-searchview/24834/4
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                String artistName = searchView.getQuery().toString();
                // guard against malformed uri request which could crash app
                if ((artistName != null)
                        && !artistName.equals("")) {
                    fetchArtists(artistName);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });
    }

    /**
     * @return artistId and artistName in one string
     * separated by comma ","
     */
    private String getArtistIdName(Artist artist) {
        String separator = ",";
        return artist.id + separator + artist.name;
    }

    private void fetchArtists(String artistName) {
        FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
        fetchArtistsTask.execute(artistName);
    }

////////////////////////////////////////////////////////////////////////////

    // http://stackoverflow.com/questions/9671546/asynctask-android-example?rq=1
    // first parameter is doInBackground first argument params[0].
    // second is onProgressUpdate integer argument.
    // third is doInBackground return type and onPostExecute argument type.
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

            if (isArtistsPagerArtistsNullOrEmpty(artistsPager)) {
                Toast toast = Toast.makeText(getActivity(),
                        getActivity().getString(R.string.search_found_no_artists),
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // https://developer.spotify.com/web-api/object-model
                List<Artist> artistsList = artistsPager.artists.items;
                adapter.clear();
                // addAll calls adapter.notifyDataSetChanged()
                adapter.addAll(artistsList);
            }
        }

        private boolean isArtistsPagerArtistsNullOrEmpty(ArtistsPager artistsPager) {
            // Note: In UI, searching for artistName space " "
            // supplies artistsPager that causes this method to return true
            return artistsPager == null
                    || artistsPager.artists == null
                    || artistsPager.artists.items == null
                    || artistsPager.artists.items.size() == 0;
        }

    }
}
