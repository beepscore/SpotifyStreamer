package com.beepscore.android.spotifystreamer;

import android.net.Uri;

/**
 * Created by stevebaker on 7/5/15.
 * Supports making requests to Spotify web service.
 */
public class SpotifyHelper {

    /**
     * URI for artist query to Spotify
     * @param name specifies an artist name.
     * Name may contain spaces, they will be escaped
     * @return uri
     */
    protected static Uri searchSpotifyArtistUri(String name) {
        final String ARTIST = "artist";
        return searchSpotifyUri(ARTIST, name);
    }

    /**
     * URI for query to Spotify
     * @param filterType specifies the type
     * @param value specifies the type's value
     * Value may contain spaces, they will be escaped
     * @return uri
     */
    protected static Uri searchSpotifyUri(String filterType, String value) {

        final String SCHEME = "https";
        final String BASE_URL = "api.spotify.com";
        final String API_VERSION = "v1";
        final String SEARCH = "search";
        final String QUERY_PARAM = "q";
        final String FILTER_TYPE = "type";

        // http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(BASE_URL)
                .appendPath(API_VERSION)
                .appendPath(SEARCH)
                .appendQueryParameter(QUERY_PARAM, value)
                .appendQueryParameter(FILTER_TYPE, filterType);

        Uri uri = builder.build();
        return uri;
    }
}
