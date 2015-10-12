package com.beepscore.android.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 *
 */
public class PlayerFragment extends Fragment
        implements SeekBar.OnSeekBarChangeListener {

    private final String LOG_TAG = PlayerFragment.class.getSimpleName();

    private TrackParcelable trackParcelable;

    private AudioService mAudioService;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    private boolean mIsBound;

    private SeekBar mSeekBar;
    private TextView mTimeElapsedTextView;
    private TextView mTimeRemainingTextView;

    private ImageButton mNextButton;
    private ImageButton mPlayButton;
    private ImageButton mPreviousButton;

    private Handler mHandler;

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the intent the activity was started with
        // http://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
        Intent intent = getActivity().getIntent();
        configureTrackParcelable(intent);

        // Use setRetainInstance to avoid interrupting audio during rotation.
        // When user rotates device, activity will be destroyed
        // but fragment instance with audio player will be retained and passed to new activity.
        setRetainInstance(true);

        //Intent mPlayIntent = new Intent(getActivity().getApplicationContext(), AudioService.class);
        // use startService instead of only bindService so service persists upon rotation??
        // http://stackoverflow.com/questions/3514287/android-service-startservice-and-bindservice
        //getActivity().getApplicationContext().startService(mPlayIntent);

        // Handler runs on the thread it was created in.
        // https://www.youtube.com/watch?v=GaO1uHeIcj0
        // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#concurrency_handler2
        // http://stackoverflow.com/questions/31286196/how-do-i-update-seekbar-mp3-progress-every-second-with-a-threadcode-pics-inclu?lq=1
        // http://stackoverflow.com/questions/5242918/android-media-player-and-seekbar-sync-issue?lq=1
        // http://stackoverflow.com/questions/17168215/seekbar-and-media-player-in-android?lq=1
        mHandler = new Handler();

        doBindService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View playerView = inflater.inflate(R.layout.fragment_player, container, false);

        configureActionBarTitle(getString(R.string.app_name));

        if (trackParcelable != null) {

            TextView artistView = (TextView) playerView.findViewById(R.id.artist_view);
            artistView.setText(trackParcelable.artistName);

            TextView albumView = (TextView) playerView.findViewById(R.id.album_view);
            albumView.setText(trackParcelable.albumName);

            ImageView imageView = (ImageView) playerView.findViewById(R.id.image_view);
            if (trackParcelable.imageWidestUrl == null
                    || trackParcelable.imageWidestUrl.equals("")) {
                // show placeholder image
                Picasso.with(getActivity()).load(R.mipmap.ic_launcher).into(imageView);
            } else {
                Picasso.with(getActivity()).load(trackParcelable.imageWidestUrl).into(imageView);
            }

            final TextView trackView = (TextView) playerView.findViewById(R.id.track_view);
            trackView.setText(trackParcelable.name);

            mSeekBar = (SeekBar) playerView.findViewById(R.id.seek_bar);
            mTimeElapsedTextView = (TextView) playerView.findViewById(R.id.time_elapsed);
            mTimeRemainingTextView = (TextView) playerView.findViewById(R.id.time_remaining);

            mSeekBar.setOnSeekBarChangeListener(this);

            mPlayButton = (ImageButton) playerView.findViewById(R.id.play_button);
            mPlayButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (mAudioService != null
                            && mIsBound
                            && mAudioService.isPrepared()) {
                        mTimeElapsedTextView.setText(TimeUtils.minutesSecondsStringFromMsec(mAudioService.getCurrentPositionMsec()));
                        mTimeRemainingTextView.setText(TimeUtils.minutesSecondsStringFromMsec(mAudioService.getTimeRemainingMsec()));
                    }

                    // Toggle between play and pause
                    if (mAudioService != null
                            && mAudioService.isPrepared()
                            && mAudioService.isPlaying()) {
                        mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                        mAudioService.pause();
                    } else {
                        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                        if (mAudioService != null
                                && mIsBound) {
                            try {
                                // Use Android’s MediaPlayer API to stream the track preview of a currently selected track.
                                // Apparently full song uri requires Spotify sdk player.
                                // References
                                // Spotify Streamer implementation guide, task 2
                                // https://discussions.udacity.com/t/spotify-track-url/21127
                                // http://stackoverflow.com/questions/20087804/should-have-subtitle-controller-already-set-mediaplayer-error-android
                                mAudioService.play(getActivity(), trackParcelable.previewUrl);


                            } catch (IllegalArgumentException e) {
                                Log.e(LOG_TAG, e.getLocalizedMessage());
                            } catch (IOException e) {
                                Log.e(LOG_TAG, e.getLocalizedMessage());
                            }
                        }
                    }
                }
            });

        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (mAudioService != null) {
                    int percentProgress = TimeUtils.getPercentProgress(mAudioService.getCurrentPositionMsec(),
                            mAudioService.getDurationMsec());
                    mSeekBar.setProgress(percentProgress);
                    mTimeElapsedTextView.setText(TimeUtils.minutesSecondsStringFromMsec(mAudioService.getCurrentPositionMsec()));
                    mTimeRemainingTextView.setText(TimeUtils.minutesSecondsStringFromMsec(mAudioService.getTimeRemainingMsec()));
                    final int delayMsec = 1000;
                    mHandler.postDelayed(this, delayMsec);
                }
            }
        };

        // Make sure you update Seekbar on UI thread
        //PlayerActivity.this.runOnUiThread(runnable);
        //PlayerActivity.this.post(runnable);
        mHandler.post(runnable);

        return playerView;
    }

    private void configureTrackParcelable(Intent intent) {
        String TRACK_KEY = getActivity().getString(R.string.TRACK_KEY);
        if (intent != null && intent.hasExtra(TRACK_KEY)) {
            trackParcelable = intent.getParcelableExtra(TRACK_KEY);
        }
    }

    private void configureActionBarTitle(String title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null
                && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    // OnSeekBarChangeListener
    // https://developer.android.com/reference/android/widget/SeekBar.OnSeekBarChangeListener.html
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(LOG_TAG, "onProgressChanged durationMsec " + String.valueOf(mAudioService.getDurationMsec()));
        // by default progress range is 0-100
        Log.d(LOG_TAG, "onProgressChanged progress " + String.valueOf(progress));

        String timeElapsedString = TimeUtils.minutesSecondsStringFromMsec(mAudioService.getCurrentPositionMsec());
        mTimeElapsedTextView.setText(timeElapsedString);
        Log.d(LOG_TAG, "onProgressChanged time elapsed " + timeElapsedString);

        String timeRemainingString = TimeUtils.minutesSecondsStringFromMsec(mAudioService.getTimeRemainingMsec());
        mTimeRemainingTextView.setText(timeRemainingString);
        Log.d(LOG_TAG, "onProgressChanged time remaining " + timeRemainingString);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        double seekBarProgressFraction = seekBar.getProgress()/100.;
        int seekTimeMsec = (int)(seekBarProgressFraction * mAudioService.getDurationMsec());
        Log.d(LOG_TAG, "onStopTrackingTouch seekTo " + String.valueOf(seekTimeMsec));
        mAudioService.seekTo(seekTimeMsec);
    }

    ///////////////////////////////////////////////////////////////////////////

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mAudioService = ((AudioService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(getActivity(), R.string.audio_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mAudioService = null;
            Toast.makeText(getActivity(), R.string.audio_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).

        // To ensure your app is secure, always use an explicit intent when starting a Service
        // and do not declare intent filters for your services.
        // https://developer.android.com/guide/components/intents-filters.html
        Intent intent = new Intent(getActivity(), AudioService.class);

        getActivity().getApplicationContext().bindService(intent,
                mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            getActivity().getApplicationContext().unbindService(mConnection);
            mIsBound = false;
        }
    }

}
