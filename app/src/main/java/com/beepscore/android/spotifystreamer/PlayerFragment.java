package com.beepscore.android.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
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
import java.util.ArrayList;

/**
 * PlayerFragment is a DialogFragment.
 * A DialogFragment can still optionally be used as a normal fragment, if desired.
 * This is useful if you have a fragment that in some cases should be shown as a dialog
 * and others embedded in a larger UI
 * https://developer.android.com/reference/android/app/DialogFragment.html
 * see section - Selecting Between Dialog or Embedding
 */
public class PlayerFragment extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener {

    private final String LOG_TAG = PlayerFragment.class.getSimpleName();

    private ArrayList<TrackParcelable> mTracksList;
    private TrackParcelable mTrackParcelable;

    private AudioService mAudioService;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    private boolean mIsBound;
    protected boolean mTwoPane;

    private SeekBar mSeekBar;
    private TextView mTimeElapsedTextView;
    private TextView mTimeRemainingTextView;

    private ImageButton mNextButton;
    private ImageButton mPlayButton;
    private ImageButton mPreviousButton;

    private Handler mHandler;

    private int mTrackIndex;

    public interface Callback {
        /**
         * If in two pane mode, when user selects next track,
         * PlayerFragment will call the containing activity's implementation.
         */
        void onNextSelected(Bundle bundle);

        /**
         * If in two pane mode, when user selects previous track,
         * PlayerFragment will call the containing activity's implementation.
         */
        void onPreviousSelected(Bundle bundle);
    }

    public PlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate");

        // The instantiating activity sets this fragment's arguments
        // as a way to pass information to it.
        // This decouples the fragment from a particular activity.
        // Note the Bundle from getArguments() is separate from Bundle savedInstanceState
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTracksList = arguments.getParcelableArrayList(getActivity().getString(R.string.TRACKS_KEY));
            mTrackIndex = arguments.getInt(getActivity().getString(R.string.INDEX_KEY));
            mTrackParcelable = mTracksList.get(mTrackIndex);
        }

        mTwoPane = LayoutUtils.isTwoPane(getActivity());

        // Use setRetainInstance to avoid interrupting audio during rotation.
        // When user rotates device, activity will be destroyed
        // but fragment instance with audio player will be retained and passed to new activity.
        setRetainInstance(true);

        //Intent mPlayIntent = new Intent(getActivity().getApplicationContext(), AudioService.class);
        // use startService instead of only bindService so service persists upon rotation??
        // http://stackoverflow.com/questions/3514287/android-service-startservice-and-bindservice
        //getActivity().getApplicationContext().startService(mPlayIntent);

        // I *think* onCreate runs on UI thread
        // Handler runs on the thread it is attached to.
        // To avoid crash, only update UI elements such as seekbar on UI thread
        // So ok to use this handler to update UI elements
        mHandler = new Handler();

        doBindService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View playerView = inflater.inflate(R.layout.fragment_player, container, false);

        configureActionBarTitle(getString(R.string.app_name));

        if (mTrackParcelable != null) {

            configureArtistAlbumTrackTextViews(playerView);
            configureImageView(playerView);

            mTimeElapsedTextView = (TextView) playerView.findViewById(R.id.time_elapsed);
            mTimeRemainingTextView = (TextView) playerView.findViewById(R.id.time_remaining);

            mSeekBar = (SeekBar) playerView.findViewById(R.id.seek_bar);
            mSeekBar.setOnSeekBarChangeListener(this);

            configurePreviousButton(playerView);
            configureNextButton(playerView);
            configurePlayButton(playerView);
        }
        return playerView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }

        // http://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        // http://adilatwork.blogspot.com/2012/11/android-dialogfragment-dialog-sizing.html
        if (LayoutUtils.isTwoPane(getActivity()) == true) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            int dialogWidth = (int)(0.8 * screenWidth);
            int dialogHeight = (int)(0.8 * screenHeight);
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    // fix bug in two pane mode with player showing,
    // if user rotates tablet the player did not reappear.
    // This is a workaround for a possible bug in Android DialogFragment support library.
    // override onDestroyView
    // https://code.google.com/p/android/issues/detail?id=17423
    // http://stackoverflow.com/questions/14657490/how-to-properly-retain-a-dialogfragment-through-rotation?rq=1
    // http://android.codeandmagic.org/android-dialogfragment-confuses-part-2/
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setOnDismissListener(null);
        super.onDestroyView();
    }

    private void configureArtistAlbumTrackTextViews(View playerView) {

        if (mTwoPane) {
            getDialog().setTitle(mTrackParcelable.artistName);
        } else {
            TextView artistView = (TextView) playerView.findViewById(R.id.artist_view);
            artistView.setText(mTrackParcelable.artistName);
        }

        TextView albumView = (TextView) playerView.findViewById(R.id.album_view);
        albumView.setText(mTrackParcelable.albumName);

        final TextView trackView = (TextView) playerView.findViewById(R.id.track_view);
        trackView.setText(mTrackParcelable.name);
    }

    private void configureImageView(View playerView) {
        ImageView imageView = (ImageView) playerView.findViewById(R.id.image_view);
        if (mTrackParcelable.imageWidestUrl == null
                || mTrackParcelable.imageWidestUrl.equals("")) {
            // show placeholder image
            Picasso.with(getActivity()).load(R.mipmap.ic_launcher).into(imageView);
        } else {
            Picasso.with(getActivity()).load(mTrackParcelable.imageWidestUrl).into(imageView);
        }
    }

    private void configurePreviousButton(View playerView) {
        mPreviousButton = (ImageButton) playerView.findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopAndUnbindService();

                Bundle bundle = new Bundle();
                getActivity().getString(R.string.TRACKS_KEY);
                bundle.putParcelableArrayList(getActivity().getString(R.string.TRACKS_KEY),
                        mTracksList);
                bundle.putInt(getActivity().getString(R.string.INDEX_KEY),
                        ListUtils.indexPreviousWraps(mTracksList, mTrackIndex));

                if (mTwoPane == true) {
                    // Two pane mode
                    // Use callback to pass information from fragment
                    // to Activity that implements Callback
                    ((Callback)getActivity()).onPreviousSelected(bundle);

                } else {
                    // One pane mode
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void configureNextButton(View playerView) {
        mNextButton = (ImageButton) playerView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopAndUnbindService();

                Bundle bundle = new Bundle();
                getActivity().getString(R.string.TRACKS_KEY);
                bundle.putParcelableArrayList(getActivity().getString(R.string.TRACKS_KEY),
                        mTracksList);
                bundle.putInt(getActivity().getString(R.string.INDEX_KEY),
                        ListUtils.indexNextWraps(mTracksList, mTrackIndex));

                if (mTwoPane == true) {
                    // Two pane mode
                    // Use callback to pass information from fragment
                    // to Activity that implements Callback
                    ((Callback)getActivity()).onNextSelected(bundle);

                } else {
                    // One pane mode
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    protected void stopAndUnbindService() {
        if (mAudioService != null) {
            mAudioService.stop();
            doUnbindService();
        }
    }

    private void configurePlayButton(View playerView) {
        mPlayButton = (ImageButton) playerView.findViewById(R.id.play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handlePlayPauseTapped();
            }
        });
    }

    private void handlePlayPauseTapped() {
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
            handlePlayTapped();
        }
    }

    private void handlePlayTapped() {
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            if (mAudioService != null) {
                // fixed bug: tap Android Back button, then track play button didn't work
                // fixed by removing conditional check (mIsBound == true)
            try {
                // Use Android’s MediaPlayer API to stream the track preview of a currently selected track.
                // Apparently full song uri requires Spotify sdk player.
                // References
                // Spotify Streamer implementation guide, task 2
                // https://discussions.udacity.com/t/spotify-track-url/21127
                // http://stackoverflow.com/questions/20087804/should-have-subtitle-controller-already-set-mediaplayer-error-android
                mAudioService.play(getActivity(), mTrackParcelable.previewUrl);

            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
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
        stopAndUnbindService();
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    // OnSeekBarChangeListener
    // https://developer.android.com/reference/android/widget/SeekBar.OnSeekBarChangeListener.html
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mAudioService != null) {
            // Log.d(LOG_TAG, "onProgressChanged durationMsec " + String.valueOf(mAudioService.getDurationMsec()));
            // by default progress range is 0-100
            // Log.d(LOG_TAG, "onProgressChanged progress " + String.valueOf(progress));

            String timeElapsedString = TimeUtils.minutesSecondsStringFromMsec(mAudioService.getCurrentPositionMsec());
            mTimeElapsedTextView.setText(timeElapsedString);
            // Log.d(LOG_TAG, "onProgressChanged time elapsed " + timeElapsedString);

            String timeRemainingString = TimeUtils.minutesSecondsStringFromMsec(mAudioService.getTimeRemainingMsec());
            mTimeRemainingTextView.setText(timeRemainingString);
            // Log.d(LOG_TAG, "onProgressChanged time remaining " + timeRemainingString);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mAudioService != null) {
            double seekBarProgressFraction = seekBar.getProgress()/100.;
            int seekTimeMsec = (int)(seekBarProgressFraction * mAudioService.getDurationMsec());
            Log.d(LOG_TAG, "onStopTrackingTouch seekTo " + String.valueOf(seekTimeMsec));
            mAudioService.seekTo(seekTimeMsec);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // Called when the connection with the service has been established,
            // giving us the service object we can use to interact with the service.
            // Because we have bound to a explicit service that we know is running in our own process,
            // we can cast its IBinder to a concrete class and directly access it.
            mAudioService = ((AudioService.LocalBinder)service).getService();

            handlePlayPauseTapped();

            // Info on using Runnable to update MediaPlayer
            // https://www.youtube.com/watch?v=GaO1uHeIcj0
            // http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html#concurrency_handler2
            // http://stackoverflow.com/questions/5242918/android-media-player-and-seekbar-sync-issue?lq=1
            // http://stackoverflow.com/questions/17168215/seekbar-and-media-player-in-android?lq=1
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    if (mAudioService != null) {
                        int percentProgress = TimeUtils.getPercentProgress(mAudioService.getCurrentPositionMsec(),
                                mAudioService.getDurationMsec());
                        mSeekBar.setProgress(percentProgress);
                        mTimeElapsedTextView.setText(TimeUtils.minutesSecondsStringFromMsec(mAudioService.getCurrentPositionMsec()));
                        mTimeRemainingTextView.setText(TimeUtils.minutesSecondsStringFromMsec(mAudioService.getTimeRemainingMsec()));

                        if (mAudioService.isPlaying()) {
                            // e.g. if player is playing and user rotates device,
                            // when fragment reconnects to service that is playing
                            // set button image to pause.
                            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                        } else {
                            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                        }

                        final int delayMsec = 250;
                        mHandler.postDelayed(this, delayMsec);
                    }
                }
            };
            mHandler.post(runnable);
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
        // Establish a connection with the service.
        // For second parameter use an explicit class name
        // because we want a specific service implementation
        // that we know will be running in our own process
        // (and thus won't be supporting component replacement by other applications).

        // To ensure your app is secure, always use an explicit intent when starting a Service
        // and do not declare intent filters for your services.
        // https://developer.android.com/guide/components/intents-filters.html
        Intent intent = new Intent(getActivity(), AudioService.class);

        // A client can bind to the service by calling bindService().
        // When it does, it must provide an implementation of ServiceConnection,
        // which monitors the connection with the service.
        // The bindService() method returns immediately without a value,
        // but when the Android system creates the connection between the client and service,
        // it calls onServiceConnected() on the ServiceConnection,
        // to deliver the IBinder that the client can use to communicate with the service.
        // http://developer.android.com/guide/components/bound-services.html#Basics
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
