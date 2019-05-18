package com.codingblocks.cbonlineapp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.codingblocks.cbonlineapp.R;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.media.Track;
import com.vdocipher.aegis.player.VdoPlayer;

import java.util.ArrayList;

/**
 * A view for controlling playback via a VdoPlayer.
 */
public class VdoPlayerControlView extends FrameLayout {
    public interface ControllerVisibilityListener {
        /**
         * Called when the visibility of the controller ui changes.
         *
         * @param visibility new visibility of controller ui. Either {@link View#VISIBLE} or
         * {@link View#GONE}.
         */
        void onControllerVisibilityChange(int visibility);
    }

    public interface FullscreenActionListener {
        /**
         * @return if enter or exit fullscreen action was handled
         */
        boolean onFullscreenAction(boolean enterFullscreen);
    }

    private static final String TAG = "VdoPlayerControlView";

    public static final int DEFAULT_FAST_FORWARD_MS = 10000;
    public static final int DEFAULT_REWIND_MS = 10000;
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 3000;

    private final View playButton;
    private final View pauseButton;
    private final View fastForwardButton;
    private final View rewindButton;
    private final TextView durationView;
    private final TextView positionView;
    private final SeekBar seekBar;
    private final Button speedControlButton;
    private final ImageButton captionsButton;
    private final ImageButton qualityButton;
    private final ImageButton enterFullscreenButton;
    private final ImageButton exitFullscreenButton;
    private final ProgressBar loaderView;
    private final ImageButton errorView;
    private final TextView errorTextView;
    private final View controlPanel;
    private final View controllerBackground;

    private int ffwdMs;
    private int rewindMs;
    private int showTimeoutMs;

    private boolean scrubbing;
    private boolean isAttachedToWindow;
    private boolean fullscreen;

    private VdoPlayer player;
    private UiListener uiListener;
    private VdoPlayer.VdoInitParams lastErrorParams;
    private FullscreenActionListener fullscreenActionListener;
    private ControllerVisibilityListener visibilityListener;

    private static final float allowedSpeedList[] = new float[]{0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f};
    private static final CharSequence allowedSpeedStrList[] =
            new CharSequence[]{"0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x"};
    private int chosenSpeedIndex = 2;

    private Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public VdoPlayerControlView(Context context) {
        this(context, null);
    }

    public VdoPlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VdoPlayerControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ffwdMs = DEFAULT_FAST_FORWARD_MS;
        rewindMs = DEFAULT_REWIND_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;

        uiListener = new UiListener();

        LayoutInflater.from(context).inflate(R.layout.vdo_control_view, this);

        playButton = findViewById(R.id.vdo_play);
        playButton.setOnClickListener(uiListener);
        pauseButton = findViewById(R.id.vdo_pause);
        pauseButton.setOnClickListener(uiListener);
        pauseButton.setVisibility(GONE);
        fastForwardButton = findViewById(R.id.vdo_ffwd);
        fastForwardButton.setOnClickListener(uiListener);
        rewindButton = findViewById(R.id.vdo_rewind);
        rewindButton.setOnClickListener(uiListener);
        durationView = (TextView)findViewById(R.id.vdo_duration);
        positionView = (TextView)findViewById(R.id.vdo_position);
        seekBar = (SeekBar)findViewById(R.id.vdo_seekbar);
        seekBar.setOnSeekBarChangeListener(uiListener);
        speedControlButton = (Button)findViewById(R.id.vdo_speed);
        speedControlButton.setOnClickListener(uiListener);
        captionsButton = (ImageButton)findViewById(R.id.vdo_captions);
        captionsButton.setOnClickListener(uiListener);
        qualityButton = (ImageButton)findViewById(R.id.vdo_quality);
        qualityButton.setOnClickListener(uiListener);
        enterFullscreenButton = (ImageButton)findViewById(R.id.vdo_enter_fullscreen);
        enterFullscreenButton.setOnClickListener(uiListener);
        exitFullscreenButton = (ImageButton)findViewById(R.id.vdo_exit_fullscreen);
        exitFullscreenButton.setOnClickListener(uiListener);
        exitFullscreenButton.setVisibility(GONE);
        loaderView = (ProgressBar)findViewById(R.id.vdo_loader);
        loaderView.setVisibility(GONE);
        errorView = (ImageButton)findViewById(R.id.vdo_error);
        errorView.setOnClickListener(uiListener);
        errorView.setVisibility(GONE);
        errorTextView = (TextView)findViewById(R.id.vdo_error_text);
        errorTextView.setOnClickListener(uiListener);
        errorTextView.setVisibility(GONE);
        controlPanel = findViewById(R.id.vdo_control_panel);
        controllerBackground = findViewById(R.id.vdo_controller_bg);
        setOnClickListener(uiListener);
    }

    public void setPlayer(VdoPlayer vdoPlayer) {
        if (player == vdoPlayer) return;

        if (player != null) {
            player.removePlaybackEventListener(uiListener);
        }
        player = vdoPlayer;
        if (player != null) {
            player.addPlaybackEventListener(uiListener);
        }
    }

    public void setFullscreenActionListener(FullscreenActionListener fullscreenActionListener) {
        this.fullscreenActionListener = fullscreenActionListener;
    }

    public void setControllerVisibilityListener(ControllerVisibilityListener visibilityListener) {
        this.visibilityListener = visibilityListener;
    }

    public void show() {
        if (!controllerVisible()) {
            controlPanel.setVisibility(VISIBLE);
            updateAll();
            if (visibilityListener != null) {
                visibilityListener.onControllerVisibilityChange(controlPanel.getVisibility());
            }
        }
        hideAfterTimeout();
    }

    public void hide() {
        if (controllerVisible() && lastErrorParams == null) {
            controlPanel.setVisibility(GONE);
            removeCallbacks(hideAction);
            if (visibilityListener != null) {
                visibilityListener.onControllerVisibilityChange(controlPanel.getVisibility());
            }
        }
    }

    /**
     * Call if fullscreen in entered/exited in response to external triggers such as orientation
     * change, back button etc.
     * @param fullscreen true if fullscreen in new state
     */
    public void setFullscreenState(boolean fullscreen) {
        this.fullscreen = fullscreen;
        updateFullscreenButtons();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(hideAction);
    }

    /**
     * Call to know the visibility of the playback controls ui. VdoPlayerControlView itself doesn't
     * change visibility when hiding ui controls.
     * @return true if playback controls are visible
     */
    public boolean controllerVisible() {
        return controlPanel.getVisibility() == VISIBLE;
    }

    private void hideAfterTimeout() {
        removeCallbacks(hideAction);
        boolean playing = player != null && player.getPlayWhenReady();
        if (showTimeoutMs > 0 && isAttachedToWindow && lastErrorParams == null && playing) {
            postDelayed(hideAction, showTimeoutMs);
        }
    }

    private void updateAll() {
        updatePlayPauseButtons();
        updateSpeedControlButton();
    }

    private void updatePlayPauseButtons() {
        if (!controllerVisible() || !isAttachedToWindow) {
            return;
        }

        int playbackState = player.getPlaybackState();
        boolean playing = player != null
                && playbackState != VdoPlayer.STATE_IDLE && playbackState != VdoPlayer.STATE_ENDED
                && player.getPlayWhenReady();
        playButton.setVisibility(playing ? GONE : VISIBLE);
        pauseButton.setVisibility(playing ? VISIBLE : GONE);
    }

    private void rewind() {
        if (rewindMs > 0) {
            player.seekTo(Math.max(0, player.getCurrentTime() - rewindMs));
        }
    }

    private void fastForward() {
        if (ffwdMs > 0) {
            player.seekTo(Math.min(player.getDuration(), player.getCurrentTime() + ffwdMs));
        }
    }

    private void updateLoader(boolean loading) {
        loaderView.setVisibility(loading ? VISIBLE : GONE);
    }

    private void updateSpeedControlButton() {
        if (!controllerVisible() || !isAttachedToWindow) {
            return;
        }

        if (player != null && player.isSpeedControlSupported()) {
            speedControlButton.setVisibility(View.VISIBLE);
            float speed = player.getPlaybackSpeed();
            chosenSpeedIndex = VideoUtils.INSTANCE.getClosestFloatIndex(allowedSpeedList, speed);
            speedControlButton.setText(allowedSpeedStrList[chosenSpeedIndex]);
        } else {
            speedControlButton.setVisibility(GONE);
        }
    }

    private void toggleFullscreen() {
        if (fullscreenActionListener != null) {
            boolean handled = fullscreenActionListener.onFullscreenAction(!fullscreen);
            if (handled) {
                fullscreen = !fullscreen;
                updateFullscreenButtons();
            }
        }
    }

    private void updateFullscreenButtons() {
        if (!controllerVisible() || !isAttachedToWindow) {
            return;
        }

        enterFullscreenButton.setVisibility(fullscreen ? GONE : VISIBLE);
        exitFullscreenButton.setVisibility(fullscreen ? VISIBLE : GONE);
    }

    private void showSpeedControlDialog() {
        new AlertDialog.Builder(getContext())
                .setSingleChoiceItems(allowedSpeedStrList, chosenSpeedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (player != null) {
                            float speed = allowedSpeedList[which];
                            player.setPlaybackSpeed(speed);
                        }
                        dialog.dismiss();
                    }
                })
                .setTitle("Choose playback speed")
                .show();
    }

    private void showTrackSelectionDialog(int trackType) {
        if (player == null) {
            return;
        }

        // get all available tracks of type trackType
        Track[] availableTracks = player.getAvailableTracks();
        Log.i(TAG, availableTracks.length + " tracks available");
        ArrayList<Track> typeTrackList = new ArrayList<>();
        for (Track availableTrack : availableTracks) {
            if (availableTrack.type == trackType) {
                typeTrackList.add(availableTrack);
            }
        }

        // get the selected track of type trackType
        Track[] selectedTracks = player.getSelectedTracks();
        Track selectedTypeTrack = null;
        for (Track selectedTrack : selectedTracks) {
            if (selectedTrack.type == trackType) {
                selectedTypeTrack = selectedTrack;
                break;
            }
        }

        // get index of selected type track in "typeTrackList" to indicate selection in dialog
        int selectedIndex = -1;
        if (selectedTypeTrack != null) {
            for (int i = 0; i < typeTrackList.size(); i++) {
                if (typeTrackList.get(i).equals(selectedTypeTrack)) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        // first, let's convert tracks to array of TrackHolders for better display in dialog
        ArrayList<TrackHolder> trackHolderList = new ArrayList<>();
        for (Track track : typeTrackList) trackHolderList.add(new TrackHolder(track));

        // if captions tracks are available, lets add a DISABLE_CAPTIONS track for turning off captions
        if (trackType == Track.TYPE_CAPTIONS && trackHolderList.size() > 0) {
            trackHolderList.add(new TrackHolder(Track.DISABLE_CAPTIONS));

            // if no captions are selected, indicate DISABLE_CAPTIONS as selected in dialog
            if (selectedIndex < 0) selectedIndex = trackHolderList.size() - 1;
        } else if (trackType == Track.TYPE_VIDEO) {
            // todo auto option
            if (trackHolderList.size() == 1) {
                // just show a default track option
                trackHolderList.clear();
                trackHolderList.add(TrackHolder.DEFAULT);
            }
        }

        final TrackHolder[] trackHolders = trackHolderList.toArray(new TrackHolder[0]);
        Log.i(TAG, "total " + trackHolders.length + ", selected " + selectedIndex);

        // show the type tracks in dialog for selection
        String title = trackType == Track.TYPE_CAPTIONS ? "CAPTIONS" : "Quality";
        showSelectionDialog(title, trackHolders, selectedIndex);
    }

    private void showSelectionDialog(CharSequence title, final TrackHolder[] trackHolders, final int selectedTrackIndex) {
        ListAdapter adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_single_choice, trackHolders);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setSingleChoiceItems(adapter, selectedTrackIndex, (dialog, which) -> {
                    if (player != null) {
                        if (selectedTrackIndex != which) {
                            // set selection
                            Track selectedTrack = trackHolders[which].track;
                            Log.i(TAG, "selected track index: " + which + ", " + selectedTrack.toString());
                            player.setSelectedTracks(new Track[]{selectedTrack});
                        } else {
                            Log.i(TAG, "track selection unchanged");
                        }
                    }
                    dialog.dismiss();
                })
                .create()
                .show();

    }

    private void showError(ErrorDescription errorDescription) {
        updateLoader(false);
        controlPanel.setVisibility(GONE);
        errorView.setVisibility(VISIBLE);
        errorTextView.setVisibility(VISIBLE);
        String errMsg = "An error occurred : " + errorDescription.errorCode + "\nTap to retry";
        errorTextView.setText(errMsg);
        show();
    }

    private void retryAfterError() {
        if (lastErrorParams != null) {
            errorView.setVisibility(GONE);
            errorTextView.setVisibility(GONE);
            controlPanel.setVisibility(VISIBLE);
            player.load(lastErrorParams);
            lastErrorParams = null;
        }
    }

    private final class UiListener implements VdoPlayer.PlaybackEventListener,
            SeekBar.OnSeekBarChangeListener, OnClickListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            scrubbing = true;
            removeCallbacks(hideAction);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            scrubbing = false;
            int seekTarget = seekBar.getProgress();
            player.seekTo(seekTarget);
            hideAfterTimeout();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updatePlayPauseButtons();
            updateLoader(playbackState == VdoPlayer.STATE_BUFFERING);
        }

        @Override
        public void onClick(View v) {
            boolean hideAfterTimeout = true;
            if (player != null) {
                if (v == rewindButton) {
                    rewind();
                } else if (v == playButton) {
                    if (player.getPlaybackState() == VdoPlayer.STATE_ENDED) {
                        player.seekTo(0);
                    }
                    player.setPlayWhenReady(true);
                } else if (v == pauseButton) {
                    hideAfterTimeout = false;
                    player.setPlayWhenReady(false);
                } else if (v == fastForwardButton) {
                    fastForward();
                } else if (v == speedControlButton) {
                    hideAfterTimeout = false;
                    showSpeedControlDialog();
                } else if (v == captionsButton) {
                    hideAfterTimeout = false;
                    showTrackSelectionDialog(Track.TYPE_CAPTIONS);
                } else if (v == qualityButton) {
                    hideAfterTimeout = false;
                    showTrackSelectionDialog(Track.TYPE_VIDEO);
                } else if (v == enterFullscreenButton || v == exitFullscreenButton) {
                    toggleFullscreen();
                } else if (v == errorView || v == errorTextView) {
                    retryAfterError();
                } else if (v == VdoPlayerControlView.this) {
                    hideAfterTimeout = false;
                    if (controllerVisible()) {
                        hide();
                    } else {
                        show();
                    }
                }
            }
            if (hideAfterTimeout) {
                hideAfterTimeout();
            }
        }

        @Override
        public void onSeekTo(long millis) {}

        @Override
        public void onProgress(long millis) {
            positionView.setText(VideoUtils.INSTANCE.digitalClockTime((int)millis));
            seekBar.setProgress((int)millis);
        }

        @Override
        public void onBufferUpdate(long bufferTime) {
            seekBar.setSecondaryProgress((int)bufferTime);
        }

        @Override
        public void onPlaybackSpeedChanged(float speed) {
            updateSpeedControlButton();
        }

        @Override
        public void onLoading(VdoPlayer.VdoInitParams vdoInitParams) {
            updateLoader(true);
        }

        @Override
        public void onLoaded(VdoPlayer.VdoInitParams vdoInitParams) {
            durationView.setText(String.valueOf(VideoUtils.INSTANCE.digitalClockTime((int)player.getDuration())));
            seekBar.setMax((int)player.getDuration());
            updateSpeedControlButton();
        }

        @Override
        public void onLoadError(VdoPlayer.VdoInitParams vdoParams, ErrorDescription errorDescription) {
            lastErrorParams = vdoParams;
            showError(errorDescription);
        }

        @Override
        public void onMediaEnded(VdoPlayer.VdoInitParams vdoInitParams) {
            // todo
        }

        @Override
        public void onError(VdoPlayer.VdoInitParams vdoParams, ErrorDescription errorDescription) {
            lastErrorParams = vdoParams;
            showError(errorDescription);
        }

        @Override
        public void onTracksChanged(Track[] availableTracks, Track[] selectedTracks) {

        }
    }

    /**
     * A helper class that holds a Track instance and overrides {@link Object#toString()} for
     * captions tracks for displaying to user.
     */
    private static class TrackHolder {
        static final TrackHolder DEFAULT = new TrackHolder(null) {
            @Override
            public String toString() {
                return "Default";
            }
        };

        final Track track;

        TrackHolder(Track track) {
            this.track = track;
        }

        @Override
        public String toString() {
            if (track == Track.DISABLE_CAPTIONS) {
                return "Turn off Captions";
            } else if (track.type == Track.TYPE_VIDEO) {
                return track.height + "p (" + track.bitrate / 1024 + "kbps)";
            }

            return track.type == Track.TYPE_CAPTIONS ? track.language : track.toString();
        }
    }
}
