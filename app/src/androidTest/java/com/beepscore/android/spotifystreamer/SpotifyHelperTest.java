package com.beepscore.android.spotifystreamer;

import android.app.Application;
import android.net.Uri;
import android.test.ApplicationTestCase;

/**
 * Created by stevebaker on 7/5/15.
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class SpotifyHelperTest extends ApplicationTestCase<Application> {

    public SpotifyHelperTest() {
        super(Application.class);
    }

    public void testSearchSpotifyArtistUri() {
        Uri expected = Uri.parse("https://api.spotify.com/v1/search?q=Beyonce&type=artist");
        Uri actual = SpotifyHelper.searchSpotifyArtistUri("Beyonce");
        assertEquals(expected, actual);
    }

    public void testSearchSpotifyArtistUriEscapesSpaces() {
        Uri expected = Uri.parse("https://api.spotify.com/v1/search?q=John%20Coltrane&type=artist");
        Uri actual = SpotifyHelper.searchSpotifyArtistUri("John Coltrane");
        assertEquals(expected, actual);
    }
}
