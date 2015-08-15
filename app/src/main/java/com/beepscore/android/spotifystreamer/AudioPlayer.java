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
public class AudioPlayer
        implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    private final String LOG_TAG = AudioPlayer.class.getSimpleName();

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

            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setDataSource(url);

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();
        } else {
            if (isPrepared) {
                start();
            }
        }
    }

    @Override
    // Caution: You must either catch or pass IllegalArgumentException and IOException when using setDataSource(),
    // because the file you are referencing might not exist.
    // http://developer.android.com/reference/android/media/MediaPlayer.OnErrorListener.html
    public boolean onError(MediaPlayer player, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        Log.e(LOG_TAG, "MediaPlayer error, resetting");
        player.reset();

        // return true to indicate method handled the error
        return true;
    }

    /** Called when MediaPlayer is ready
     * http://developer.android.com/reference/android/media/MediaPlayer.OnPreparedListener.html
     */
    public void onPrepared(MediaPlayer player) {
        isPrepared = true;
        // https://discussions.udacity.com/t/duration-of-the-track/25936
        durationMilliseconds = player.getDuration();
        player.start();
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        isPrepared = false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // as soon as playback is done, call stop to release media player
        stop();

    }
}