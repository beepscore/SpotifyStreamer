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
        // http://stackoverflow.com/questions/5832368/tablet-or-phone-android?lq=1
        // e.g. (res/layout-sw400dp)
        boolean isTwoPane = activity.getString(R.string.screen_type)
                .equals(activity.getString(R.string.SCREEN_TYPE_LARGE));
        return isTwoPane;
    }
}
