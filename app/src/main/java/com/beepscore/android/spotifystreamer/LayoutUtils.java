package com.beepscore.android.spotifystreamer;

import android.app.Activity;

/**
 * Created by stevebaker on 10/17/15.
 */
public class LayoutUtils {

    /**
     * @param activity
     * @return true if activity is using a large-screen layout
     */
    public static boolean isTwoPane(Activity activity) {
        // If tracks_detail_container is present, app is using a large screen layout
        // e.g. (res/layout-sw400dp)
        return (activity.findViewById(R.id.tracks_detail_container) != null);
    }
}
