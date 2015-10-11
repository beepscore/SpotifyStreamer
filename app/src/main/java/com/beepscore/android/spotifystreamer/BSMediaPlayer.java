package com.beepscore.android.spotifystreamer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by stevebaker on 8/13/15.
 * References
 * https://developer.android.com/reference/android/media/MediaPlayer.html
 *
 * book "Android Programming", The Big Nerd Ranch Guide
 * authors Bill Phillips, Brian Hardy
 * http://www.bignerdranch.com/we-write/android-programming
 * Chapters 13-15
 * HelloMoon
 * https://github.com/beepscore/HelloMoon.git
 */
public class BSMediaPlayer
        implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
        //, MediaPlayer.OnInfoListener {

    private final String LOG_TAG = BSMediaPlayer.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    public int durationMilliseconds = 0;

    private boolean isPrepared = false;
    /**
     * accessor/getter method, provides Java equivalent of "read only" access
     * http://stackoverflow.com/questions/8151342/do-we-have-a-readonly-field-in-java-which-is-set-able-within-the-scope-of-the-c
     * @return true if player is prepared to play
     */
    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void play(Context c, String url) throws IllegalArgumentException, IOException {

        // prevent creation of multiple media players.
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setDataSource(url);
            configureMediaPlayerListeners(mMediaPlayer);

            mMediaPlayer.prepareAsync();

        } else {
            if (isPrepared) {
                start();
            }
        }
    }

    /**
     * @return duration in msec
     * return 0 if internal media player is null
     */
    int getDuration() {
        if (mMediaPlayer == null) {
            return 0;
        } else {
            return mMediaPlayer.getDuration();
        }
    }

    /**
     * @return current position in msec
     * return 0 if internal media player is null
     */
    public int getCurrentPosition() {
        if (mMediaPlayer == null) {
            return 0;
        } else {
            return mMediaPlayer.getCurrentPosition();
        }
    }

    private void configureMediaPlayerListeners(MediaPlayer player) {
        player.setOnErrorListener(this);
        //player.setOnInfoListener(this);
        player.setOnPreparedListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnCompletionListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaPlayer listeners

    @Override
    // Caution: You must either catch or pass IllegalArgumentException and IOException when using setDataSource(),
    // because the file you are referencing might not exist.
    // http://developer.android.com/reference/android/media/MediaPlayer.OnErrorListener.html
    public boolean onError(MediaPlayer player, int what, int extra) {
        // react appropriately
        // The MediaPlayer has moved to the Error state, must be reset!
        Log.e(LOG_TAG, "MediaPlayer error, resetting");
        player.reset();

        // return true to indicate method handled the error
        return true;
    }

    /** Note for Spotify onInfo may not be called
     * generally (at least when successful) MediaPlayer calls onPrepared
     * without any prior or subsequent call to onInfo
     * So don't override, to avoid improper handling/discarding info for onError
     */
    /*
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // return false to indicate method didn't handle the info
        return false;
    }
    */

    /** Called when MediaPlayer is ready
     * http://developer.android.com/reference/android/media/MediaPlayer.OnPreparedListener.html
     */
    public void onPrepared(MediaPlayer player) {
        isPrepared = true;
        // https://discussions.udacity.com/t/duration-of-the-track/25936
        durationMilliseconds = player.getDuration();
        player.start();
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        // as soon as playback is done, call stop to release media player
        stop();
    }

    @Override
    public void onSeekComplete(MediaPlayer player) {
        player.start();
    }

    ///////////////////////////////////////////////////////////////////////////

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        isPrepared = false;
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(msec);
        }
    }

}