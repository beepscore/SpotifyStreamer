package com.beepscore.android.spotifystreamer;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * Shows an Artist's top tracks.
 */
public class TracksFragment extends Fragment {

    private String artistId = "";
    ArrayList<TrackParcelable> tracksList;
    private String artistName = "";
    TracksArrayAdapter adapter = null;
    boolean isRetrofitError = false;

    public TracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the intent the activity was started with
        // http://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
        Intent intent = getActivity().getIntent();
        configureArtistIdAndName(intent);

        // When device is rotated fragment will be recreated.
        // Google recommends using savedInstanceState to restore the fragment
        final String TRACKS_KEY = getActivity().getString(R.string.TRACKS_KEY);
        if (savedInstanceState == null
                || !savedInstanceState.containsKey(TRACKS_KEY)) {
            tracksList = new ArrayList<TrackParcelable>();
            fetchTracks(artistId);
        } else {
            // We have a previously saved instance state, use it.
            // This avoids having to make an extra network call to Spotify
            // to repopulate tracksList
            tracksList = savedInstanceState.getParcelableArrayList(TRACKS_KEY);
        }

        // adapter creates views for each list item
        adapter = new TracksArrayAdapter(getActivity(), tracksList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tracksView = inflater.inflate(R.layout.fragment_tracks, container, false);

        configureActionBarSubtitle(artistName);

        ListView listView = (ListView) tracksView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TrackParcelable trackParcelable = adapter.getItem(i);

                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(getActivity().getString(R.string.TRACK_KEY), trackParcelable);
                startActivity(intent);
            }
        });

        return tracksView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save fragment state using array of Parcelable objects.
        final String TRACKS_KEY = getActivity().getString(R.string.TRACKS_KEY);
        outState.putParcelableArrayList(TRACKS_KEY, tracksList);

        super.onSaveInstanceState(outState);
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
    private class FetchTracksTask extends AsyncTask<String, Void, ArrayList<TrackParcelable>> {

        private final String LOG_TAG = FetchTracksTask.class.getSimpleName();

        /**
         * The system calls doInBackground on a worker (background) thread
         * @param params first and only element is artistName to make request to Spotify
         * @return Tracks SpotifyApi Java object.
         */
        @Override
        protected ArrayList<TrackParcelable> doInBackground(String... params) {

            String artistId = params[0];

            // don't use try/catch/finally here. Assume SpotifyAPI is handling it's errors.

            // https://docs.google.com/presentation/d/1Q8LwzD5ODqirWG7K_e4sklE3fEFY_dr4kH4hfoRa0BQ/pub?start=false&loop=false&delayms=15000#slide=id.ga25585343_0_137
            // TODO: Consider use spotifyApi as a singleton
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();

            //https://developer.spotify.com/web-api/get-artists-top-tracks/
            Map<String, Object> options = new HashMap<>();
            options.put("country", countryCode());

            ArrayList<TrackParcelable> results = new ArrayList<>();
            // Avoid crash if device isn't connected to internet
            try {
                Tracks tracks = spotifyService.getArtistTopTrack(artistId, options);
                results = getTrackParcelables(tracks);
                isRetrofitError = false;
            } catch(RetrofitError ex){
                isRetrofitError = true;
                ToastUtils.showToastOnUiThread(getActivity(),
                        getActivity().getString(R.string.artists_retrofit_error));
            }
            return results;
        }

        private String countryCode() {
            // Spotify tracks request takes country code as a parameter
            // App can get user locale without manifest permission.
            // Locale may sometimes give bad result - e.g. locale US but person is in China.
            // http://stackoverflow.com/questions/3659809/where-am-i-get-country
            // App could get user location, but this would require another permission and probably be too intrusive.
            // App could ask user to specify a country, but this seems like it might annoy them.
            String countryCode = getActivity().getResources().getConfiguration().locale.getCountry();
            if (countryCode == null || countryCode.equals("")) {
                // default. Can't use "", that gives "bad request"
                return "US";
            } else {
                return countryCode;
            }
        }

        /**
         * Use to update UI. The system calls this on the UI thread.
         * @param tracksList is the result returned from doInBackground()
         */
        @Override
        protected void onPostExecute(ArrayList<TrackParcelable> tracksList) {
            super.onPostExecute(tracksList);

            if (!isRetrofitError
                    && isTracksListNullOrEmpty(tracksList)) {
                Toast toast = Toast.makeText(getActivity(),
                        getActivity().getString(R.string.search_found_no_tracks),
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            adapter.clear();
            // addAll calls adapter.notifyDataSetChanged()
            adapter.addAll(tracksList);
        }

        private ArrayList<TrackParcelable> getTrackParcelables(Tracks tracks) {
            // https://developer.spotify.com/web-api/object-model
            ArrayList<TrackParcelable> results = new ArrayList<>();

            if (!isTracksNullOrEmpty(tracks)) {

                for (Track track : tracks.tracks) {

                    String imageUrl = getTrackImageUrl(track);
                    TrackParcelable trackParcelable =
                            new TrackParcelable(track.album.name, track.name, imageUrl);
                    results.add(trackParcelable);
                }
            }
            return results;
        }

        private boolean isTracksNullOrEmpty(Tracks tracks) {
            return (tracks == null
                    || tracks.tracks == null
                    || tracks.tracks.size() == 0);
        }

        private boolean isTracksListNullOrEmpty(ArrayList<TrackParcelable> tracksList) {
            return (tracksList == null
                    || tracksList.size() == 0);
        }

        private String getTrackImageUrl(Track track) {
            String imageUrl = "";

            // https://github.com/kaaes/spotify-web-api-android/blob/master/src/main/java/kaaes/spotify/webapi/android/models/Image.java
            AlbumSimple album = track.album;
            List<Image> images = album.images;

            if (images.size() > 0) {
                // get the last image because images is sorted decreasing size
                Image lastImage = images.get(images.size() - 1);
                imageUrl = lastImage.url;
            }
            return imageUrl;
        }

    }
}
