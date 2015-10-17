package com.beepscore.android.spotifystreamer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by stevebaker on 9/27/15.
 * http://developer.android.com/reference/android/app/Service.html
 * http://developer.android.com/guide/components/services.html
 * http://www.vogella.com/tutorials/AndroidServices/article.html
 * http://stackoverflow.com/questions/2298946/android-service-controlling-mediaplayer
 * https://discussions.udacity.com/t/service-life-is-bonded-with-main-activity-problem-on-rotation/25569/12
 */
public class AudioService extends Service {

    private final String LOG_TAG = AudioService.class.getSimpleName();

    private BSMediaPlayer mBSMediaPlayer;

    /**
     * Class for clients to access.
     * Because we know this service always runs in the same process as its clients,
     * we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle methods

    @Override
    public void onCreate() {
        super.onCreate();
        mBSMediaPlayer = new BSMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG + " AudioService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mBSMediaPlayer != null) {
            mBSMediaPlayer.stop();
        }
        super.onDestroy();
    }

    // https://commonsware.com/blog/2010/09/29/another-use-getapplicationcontext-binding-rotation.html
    // https://discussions.udacity.com/t/service-life-is-bonded-with-main-activity-problem-on-rotation/25569/9
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.
    // See RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    ///////////////////////////////////////////////////////////////////////////
    // status accessor/getter methods

    /**
     * accessor/getter method, provides Java equivalent of "read only" access
     * http://stackoverflow.com/questions/8151342/do-we-have-a-readonly-field-in-java-which-is-set-able-within-the-scope-of-the-c
     * @return true if player is prepared to play
     */
    public boolean isPrepared() {
        if (mBSMediaPlayer == null) {
            return false;
        } else {
            return mBSMediaPlayer.isPrepared();
        }
    }

    public boolean isPlaying() {
        if (mBSMediaPlayer == null) {
            return false;
        } else {
            return mBSMediaPlayer.isPlaying();
        }
    }

    /**
     * @return duration in msec
     */
    public int getDurationMsec() {
        if (mBSMediaPlayer == null) {
            return 0;
        } else {
            return mBSMediaPlayer.getDuration();
        }
    }

    /**
     * @return current position in msec
     */
    public int getCurrentPositionMsec() {
        if (mBSMediaPlayer == null) {
            return 0;
        } else {
            return mBSMediaPlayer.getCurrentPosition();
        }
    }

    /**
     * @return time remaining as (duration - current position) in msec
     */
    public int getTimeRemainingMsec() {
        return (getDurationMsec() - getCurrentPositionMsec());
    }

    ///////////////////////////////////////////////////////////////////////////

    public void start() {
        if (mBSMediaPlayer != null) {
            mBSMediaPlayer.start();
        }
    }

    public void play(Context context, String url) throws IOException {
        if (mBSMediaPlayer != null) {
            mBSMediaPlayer.play(context, url);
        }
    }

    public void pause() {
        if (mBSMediaPlayer != null) {
            mBSMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mBSMediaPlayer != null) {
            mBSMediaPlayer.stop();
        }
    }

    public void seekTo(int msec) {
        if (mBSMediaPlayer != null) {
            mBSMediaPlayer.seekTo(msec);
        }
    }
}
