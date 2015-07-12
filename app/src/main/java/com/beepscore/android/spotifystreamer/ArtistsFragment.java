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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * Shows artists
 */
public class ArtistsFragment extends Fragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();
    ArrayList<ArtistParcelable> artistsList;
    ArtistsArrayAdapter adapter = null;
    SearchView searchView = null;

    public ArtistsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When device is rotated fragment will be recreated.
        // Google recommends using savedInstanceState to restore the fragment
        final String ARTISTS_KEY = getActivity().getString(R.string.ARTISTS_KEY);
        if (savedInstanceState == null
                || !savedInstanceState.containsKey(ARTISTS_KEY)) {
            artistsList = new ArrayList<ArtistParcelable>();
        } else {
            // We have a previously saved instance state, use it.
            // This avoids having to make an extra network call to Spotify
            // to repopulate artistsList
            artistsList = savedInstanceState.getParcelableArrayList(ARTISTS_KEY);
        }

        // adapter creates views for each list item
        adapter = new ArtistsArrayAdapter(getActivity(), artistsList);

        // TODO: Consider use fragmentManager to save fragment on back stack
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistsView = inflater.inflate(R.layout.fragment_artists, container, false);

        searchView = (SearchView) artistsView.findViewById(R.id.search_view);
        configureSearchViewListener(searchView);

        ListView listView = (ListView) artistsView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArtistParcelable artistParcelable = adapter.getItem(i);

                Intent intent = new Intent(getActivity(), TracksActivity.class);
                intent.putExtra(getActivity().getString(R.string.ARTIST_KEY), artistParcelable);
                startActivity(intent);
            }
        });

        return artistsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // hide keyboard when user navigates back or up from Tracks view
        // http://stackoverflow.com/questions/16184703/unable-to-hide-the-virtual-keyboard-of-searchview-iconfiedbydefaultfalse
        // big discussion but answers more complicated than above
        // http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard?rq=1
        // this code didn't work when located in onCreateView
        searchView.clearFocus();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save fragment state using array of Parcelable objects.
        final String ARTISTS_KEY = getActivity().getString(R.string.ARTISTS_KEY);
        outState.putParcelableArrayList(ARTISTS_KEY, artistsList);

        super.onSaveInstanceState(outState);
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

    private void fetchArtists(String artistName) {
        FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
        fetchArtistsTask.execute(artistName);
    }

////////////////////////////////////////////////////////////////////////////

    // http://stackoverflow.com/questions/9671546/asynctask-android-example?rq=1
    // first parameter is doInBackground first argument params[0].
    // second is onProgressUpdate integer argument.
    // third is doInBackground return type and onPostExecute argument type.
    private class FetchArtistsTask extends AsyncTask<String, Void, ArrayList<ArtistParcelable>> {

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

        /**
         * The system calls doInBackground on a worker (background) thread
         * @param params first and only element is artistName to make request to Spotify
         * @return ArrayList<ArtistParcelable>
         */
        @Override
        protected ArrayList<ArtistParcelable> doInBackground(String... params) {

            String artistName = params[0];

            // don't use try/catch/finally here. Assume SpotifyAPI is handling it's errors.

            // https://docs.google.com/presentation/d/1Q8LwzD5ODqirWG7K_e4sklE3fEFY_dr4kH4hfoRa0BQ/pub?start=false&loop=false&delayms=15000#slide=id.ga25585343_0_137
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            ArtistsPager artistsPager = spotifyService.searchArtists(artistName);

            ArrayList<ArtistParcelable> results = getArtistParcelables(artistsPager);
            return results;
        }

        /**
         * Use to update UI. The system calls this on the UI thread.
         * @param artistsList is the result returned from doInBackground()
         */
        @Override
        protected void onPostExecute(ArrayList<ArtistParcelable> artistsList) {
            super.onPostExecute(artistsList);

            if (isArtistsListsNullOrEmpty(artistsList)) {
                Toast toast = Toast.makeText(getActivity(),
                        getActivity().getString(R.string.search_found_no_artists),
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                adapter.clear();
                // addAll calls adapter.notifyDataSetChanged()
                adapter.addAll(artistsList);
            }
        }

        private ArrayList<ArtistParcelable> getArtistParcelables(ArtistsPager artistsPager) {
            // https://developer.spotify.com/web-api/object-model
            ArrayList<ArtistParcelable> results = new ArrayList<>();

            if (!isArtistsPagerArtistsNullOrEmpty(artistsPager)) {

                for (Artist artist : artistsPager.artists.items) {

                    String imageUrl = getArtistImageUrl(artist);
                    ArtistParcelable artistParcelable =
                            new ArtistParcelable(artist.id, artist.name, imageUrl);
                    results.add(artistParcelable);
                }
            }
            return results;
        }

        private boolean isArtistsPagerArtistsNullOrEmpty(ArtistsPager artistsPager) {
            // Note: In UI, searching for artistName space " "
            // supplies artistsPager that causes this method to return true
            return artistsPager == null
                    || artistsPager.artists == null
                    || artistsPager.artists.items == null
                    || artistsPager.artists.items.size() == 0;
        }

        private boolean isArtistsListsNullOrEmpty(ArrayList<ArtistParcelable> artistsList) {
            // Note: In UI, searching for artistName space " "
            // supplies artistsList that causes this method to return true
            return (artistsList == null
                    || artistsList.size() == 0);
        }

        private String getArtistImageUrl(Artist artist) {
            String imageUrl = "";

            // https://github.com/kaaes/spotify-web-api-android/blob/master/src/main/java/kaaes/spotify/webapi/android/models/Image.java
            if (artist.images.size() > 0) {
                // get the last image because images is sorted decreasing size
                Image artistLastImage = artist.images.get(artist.images.size() - 1);
                imageUrl = artistLastImage.url;
            }
            return imageUrl;
        }

    }
}
