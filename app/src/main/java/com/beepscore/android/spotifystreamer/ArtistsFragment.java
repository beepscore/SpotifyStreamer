package com.beepscore.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistsView = inflater.inflate(R.layout.fragment_artists, container, false);

        final EditText editText = (EditText) artistsView.findViewById(R.id.edit_text);
        configureEditTextListener(editText);

        List<Artist> list = new ArrayList<Artist>();

        // adapter creates views for each list item
        adapter = new ArtistsArrayAdapter(getActivity(), list);

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
    private void configureEditTextListener(final EditText editText) {
        // Call fetchArtists when user taps Done, not after every letter.
        // Use setOnEditorActionListener IME_ACTION_DONE instead of addTextChangedListener
        // http://stackoverflow.com/questions/2004344/android-edittext-imeoptions-done-track-finish-typing
        // http://stackoverflow.com/questions/14524393/catch-done-key-press-from-soft-keyboard?lq=1
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String artistName = editText.getText().toString();
                    // guard against malformed uri request which could crash app
                    if ((artistName != null)
                            && !artistName.equals("")) {
                        fetchArtists(artistName);
                    }
                }
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
