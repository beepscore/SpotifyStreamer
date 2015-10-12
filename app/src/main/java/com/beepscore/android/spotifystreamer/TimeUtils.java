package com.beepscore.android.spotifystreamer;

import android.text.format.DateUtils;

/**
 * Created by stevebaker on 10/11/15.
 */
public class TimeUtils {

    public static String minutesSecondsStringFromMsec(int durationMsec) {
        final int MILLISECONDS_PER_SECOND = 1000;
        final int SECONDS_PER_MINUTE = 60;
        double durationSeconds = durationMsec / MILLISECONDS_PER_SECOND;
        double durationMinutes = durationSeconds / SECONDS_PER_MINUTE;
        String minutesSecondsString = DateUtils.formatElapsedTime((long) durationSeconds);
        if (durationMinutes < 10) {
            // remove leading 0
            minutesSecondsString = minutesSecondsString.substring(1);
        }
        return minutesSecondsString;
    }

    public static int getPercentProgress(int current, int total) {
        int percentProgress;
        if(total < 1) {
            percentProgress = 0;
        } else {
            percentProgress = (int) (100 * ((float)current / (float)total));
        }
        return percentProgress;
    }
}
