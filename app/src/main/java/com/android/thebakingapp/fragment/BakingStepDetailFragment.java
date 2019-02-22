package com.android.thebakingapp.fragment;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.thebakingapp.R;
import com.android.thebakingapp.dto.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.android.thebakingapp.util.Constants.BAKING_STEPS;
import static com.android.thebakingapp.util.Constants.BAKING_STEP_DESCRIPTION;
import static com.android.thebakingapp.util.Constants.BAKING_STEP_VIDEO_URL;
import static com.android.thebakingapp.util.Constants.CURRENT_BAKING_STEP;
import static com.android.thebakingapp.util.Constants.CURRENT_CLIP_POS;
import static com.android.thebakingapp.util.Constants.PLAY_VIDEO_ONLOAD;

public class BakingStepDetailFragment extends Fragment {
    public static final String STEP_ID = "step_id";
    private static final String TAG = BakingStepDetailFragment.class.getSimpleName();
    private List<Step> bakingSteps = new ArrayList<>();
    private int currentStep = 0;
    private int totalSteps = 0;

    private String videoURL = "";
    private String stepDescription = "";

    private SimpleExoPlayer simpleExoPlayer;
    private long currentClipPosition;
    private boolean playVideoWhenReady = true;

    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder mStateBuilder;
    private PlayerView exoPlayerView;

    private TextView tvBakingStepDescription;
    private TextView tvBackButton;
    private TextView tvNextButton;

    private ImageView ivBakingStepDetail;
    private ImageView ivNoVideoURL;

    public BakingStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View bakingStepDetailView = inflater.inflate(R.layout.step_detail, container, false);

        bakingSteps = Parcels.unwrap(getArguments().getParcelable(BAKING_STEPS));
        currentStep = getArguments().getInt(CURRENT_BAKING_STEP);

        videoURL = Objects.requireNonNull(getArguments()).getString(BAKING_STEP_VIDEO_URL);
        stepDescription = getArguments().getString(BAKING_STEP_DESCRIPTION);

        totalSteps = bakingSteps.size();

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_CLIP_POS)) {

            bakingSteps = Parcels.unwrap(savedInstanceState.getParcelable(BAKING_STEPS));
            stepDescription = savedInstanceState.getString(BAKING_STEP_DESCRIPTION);

            videoURL = savedInstanceState.getString(BAKING_STEP_VIDEO_URL);
            currentClipPosition = savedInstanceState.getLong(CURRENT_CLIP_POS);
            playVideoWhenReady = savedInstanceState.getBoolean(PLAY_VIDEO_ONLOAD);

            currentStep = savedInstanceState.getInt(CURRENT_BAKING_STEP);

        }

        exoPlayerView = bakingStepDetailView.findViewById(R.id.playerView);
        ivNoVideoURL = bakingStepDetailView.findViewById(R.id.iv_noVideoURL);
        ivBakingStepDetail = bakingStepDetailView.findViewById(R.id.iv_bakingStepDetail);

        tvBakingStepDescription = bakingStepDetailView.findViewById(R.id.tv_bakingStepDescription);

        navigateToNextStep(bakingStepDetailView);

        navigateToPreviousStep(bakingStepDetailView);

        handleLandscapeOrientation();

        return bakingStepDetailView;
    }

    private void handleLandscapeOrientation() {
        if (!getResources().getBoolean(R.bool.isTablet) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
            Objects.requireNonNull(
                    ((AppCompatActivity) getActivity()).getSupportActionBar()).hide();

            tvBackButton.setVisibility(View.GONE);
            tvNextButton.setVisibility(View.GONE);
            tvBakingStepDescription.setVisibility(View.GONE);

            exoPlayerView.setLayoutParams
                    (new RelativeLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void navigateToPreviousStep(View bakingStepDetailView) {
        tvBackButton = bakingStepDetailView.findViewById(R.id.tv_backButton);

        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentStep > 0) {
                    if (simpleExoPlayer != null) {

                        simpleExoPlayer.stop();
                        simpleExoPlayer.release();
                        simpleExoPlayer = null;

                        mediaSessionCompat.setActive(false);
                    }
                    currentClipPosition = 0;
                    playVideoWhenReady = true;

                    currentStep = currentStep - 1;
                    populateBakingSteps(
                            bakingSteps.get(currentStep).getVideoURL(),
                            bakingSteps.get(currentStep).getThumbnailURL(),
                            bakingSteps.get(currentStep).getDescription());
                }
            }
        });
    }

    private void navigateToNextStep(View bakingStepDetailView) {
        tvNextButton = bakingStepDetailView.findViewById(R.id.tv_nextButton);

        tvNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentStep < totalSteps - 1) {
                    if (simpleExoPlayer != null) {

                        simpleExoPlayer.stop();
                        simpleExoPlayer.release();
                        simpleExoPlayer = null;

                        mediaSessionCompat.setActive(false);
                    }
                    currentClipPosition = 0;
                    playVideoWhenReady = true;

                    currentStep = currentStep + 1;
                    populateBakingSteps(
                            bakingSteps.get(currentStep).getVideoURL(),
                            bakingSteps.get(currentStep).getThumbnailURL(),
                            bakingSteps.get(currentStep).getDescription());
                }
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (simpleExoPlayer != null) {
            currentClipPosition = simpleExoPlayer.getCurrentPosition();
            playVideoWhenReady = simpleExoPlayer.getPlayWhenReady();
        }
        outState.putString(BAKING_STEP_VIDEO_URL, videoURL);
        outState.putLong(CURRENT_CLIP_POS, currentClipPosition);
        outState.putBoolean(PLAY_VIDEO_ONLOAD, playVideoWhenReady);

        outState.putParcelable(BAKING_STEPS, Parcels.wrap(bakingSteps));
        outState.putInt(CURRENT_BAKING_STEP, currentStep);
        outState.getString(BAKING_STEP_DESCRIPTION, stepDescription);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            populateBakingSteps(
                    bakingSteps.get(currentStep).getVideoURL(),
                    bakingSteps.get(currentStep).getThumbnailURL(),
                    bakingSteps.get(currentStep).getDescription());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || simpleExoPlayer == null)) {
            populateBakingSteps(
                    bakingSteps.get(currentStep).getVideoURL(),
                    bakingSteps.get(currentStep).getThumbnailURL(),
                    bakingSteps.get(currentStep).getDescription());
        }
    }


    private void populateBakingSteps(String videoURL, String thumbnailURL, String stepDescription) {

        if (StringUtils.isEmpty(videoURL)) {
            exoPlayerView.setVisibility(View.GONE);
            ivNoVideoURL.setVisibility(View.VISIBLE);
        } else {
            exoPlayerView.setVisibility(View.VISIBLE);
            ivNoVideoURL.setVisibility(View.GONE);

            initializeMediaSession();

            initSimpleExoPlayer(videoURL);
        }

        tvBakingStepDescription.setText(stepDescription);

        try {
            if (!StringUtils.isEmpty(thumbnailURL)) {
                ivBakingStepDetail.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(thumbnailURL)
                        .into(ivBakingStepDetail);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception loading thumbnail - " + e.getLocalizedMessage());
        }
    }

    private void initSimpleExoPlayer(String videoURL) {

        if (simpleExoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    getActivity(), trackSelector, loadControl);

            exoPlayerView.setPlayer(simpleExoPlayer);

            String userAgent = Util.getUserAgent(getActivity(),
                    Objects.requireNonNull(getContext()).getString(R.string.app_name));

            MediaSource mediaSource =
                    new ExtractorMediaSource(
                            Uri.parse(videoURL),
                            new DefaultDataSourceFactory(Objects.requireNonNull(getActivity()), userAgent),
                            new DefaultExtractorsFactory(),
                            null, null);

            simpleExoPlayer.prepare(mediaSource);
        }

        if (currentClipPosition != 0) {
            simpleExoPlayer.seekTo(currentClipPosition);
        }

        simpleExoPlayer.setPlayWhenReady(playVideoWhenReady);

    }

    private void initializeMediaSession() {
        mediaSessionCompat = new MediaSessionCompat(Objects.requireNonNull(getActivity()), TAG);

        // https://stackoverflow.com/questions/38247050/mediabuttonreceiver-not-working-with-mediabrowserservicecompat

        mediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mediaSessionCompat.setMediaButtonReceiver(null);

        mStateBuilder =
                new PlaybackStateCompat.Builder()
                        .setActions(
                                PlaybackStateCompat.ACTION_PLAY |
                                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                        PlaybackStateCompat.ACTION_PAUSE |
                                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        mediaSessionCompat.setPlaybackState(mStateBuilder.build());
        mediaSessionCompat.setActive(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            stopAndReleaseExoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            stopAndReleaseExoPlayer();
        }
    }

    private void stopAndReleaseExoPlayer() {
        if (simpleExoPlayer != null) {

            currentClipPosition = simpleExoPlayer.getCurrentPosition();
            playVideoWhenReady = simpleExoPlayer.getPlayWhenReady();

            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

}