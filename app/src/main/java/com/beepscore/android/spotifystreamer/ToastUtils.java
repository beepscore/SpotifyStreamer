package com.beepscore.android.spotifystreamer;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by stevebaker on 7/13/15.
 */
public class ToastUtils {

    public static void showToastOnUiThread(final Activity activity, final String message) {
        // http://stackoverflow.com/questions/3134683/android-toast-in-a-thread
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
