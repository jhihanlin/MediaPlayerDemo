package com.example.mediaplayerdemo.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.widget.MyVideoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Formatter;
import java.util.Locale;

import android.os.Handler;

public class MediaPlayerControlView extends FrameLayout {

    private Context mContext;
    private View mRoot;
    private ImageButton mPauseButton;
    private MyVideoView myVideoView;
    private SeekBar mSeekBar;
    private TextView mCurrentTime, mEndTime;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private LinearLayout mLinearLayout;
    private AdView mAdView;
    private AdRequest adRequest;
    private Handler mHandler = new MessageHandler(this);

    public MediaPlayerControlView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MediaPlayerControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MediaPlayerControlView(Context context, boolean useFastForward) {
        super(context);
        mContext = context;
        init();
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            init();
    }

    protected void init() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.controller_media_control, null);

        findViews();
        setListeners();
        setAdView();
        setControllerVisibility(View.GONE);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        addView(mRoot, frameParams);
    }

    private void setAdView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.controller_pause_adview, null);
        mAdView = (AdView) v.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (mLinearLayout != null) {
            mLinearLayout.removeAllViews();
            ViewGroup parent = (ViewGroup) mAdView.getParent();
            if (parent != null) {
                parent.removeView(mAdView);
            }
            mLinearLayout.addView(mAdView);
        }
    }

    private void refreshAdView() {
        mAdView.loadAd(new AdRequest.Builder().build());
    }

    private void setAdViewVisibility(int visibility) {
        mLinearLayout.setVisibility(visibility);
    }

    private void setListeners() {
        try {
            myVideoView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("debug", "onTouch");
//                    myVideoView.getMediaPlayer().pause();
                    showController();
                    return false;
                }
            });

            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser == false) {
                        return;
                    }
                    long duration = myVideoView.getDuration();
                    long newPosition = (duration * progress) / 100L;
                    myVideoView.seekTo((int) newPosition);
                    mCurrentTime.setText(stringForTime((int) newPosition));
                    Log.d("debug", "onProgressChanged " + progress);
                    Log.d("debug", "mCurrentTime " + newPosition);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    showController();
                    updatePausePlay();
                    updateSeekBar();
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void findViews() {
        myVideoView = (MyVideoView) mRoot.findViewById(R.id.surface_media_player);
        mPauseButton = (ImageButton) mRoot.findViewById(R.id.mPauseButton);
        mSeekBar = (SeekBar) mRoot.findViewById(R.id.seekBar);
        mCurrentTime = (TextView) mRoot.findViewById(R.id.mCurrentTime);
        mEndTime = (TextView) mRoot.findViewById(R.id.mEndTime);
        mLinearLayout = (LinearLayout) mRoot.findViewById(R.id.adLinearLayout);
    }

    private int updateSeekBar() {

        int position = myVideoView.getCurrentPosition();
        int duration = myVideoView.getDuration();
        if (duration > 0) {
            int seekBarPosition = (int) (((double) position / duration) * 100);
            mSeekBar.setProgress(seekBarPosition);
            Log.d("debug", "updateSeekBar " + seekBarPosition);
            Log.d("debug", "updateSeekBar " + position);
            Log.d("debug", "updateSeekBar " + duration);
        }
        int percent = myVideoView.getBufferPercentage();
        mSeekBar.setSecondaryProgress(percent * 10);

        mEndTime.setText(stringForTime(duration));
        mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void showController() {
        setControllerVisibility(View.VISIBLE);
        updatePausePlay();
        updateSeekBar();
    }

    public void hideController() {
        setControllerVisibility(View.GONE);
        updatePausePlay();
        updateSeekBar();
    }

    private void setControllerVisibility(int visibility) {
        mPauseButton.setVisibility(visibility);
        mSeekBar.setVisibility(visibility);
        mCurrentTime.setVisibility(visibility);
        mEndTime.setVisibility(visibility);
    }

    public void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || myVideoView == null) {
            return;
        }

        if (myVideoView.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.video_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.video_play);
        }
    }

    public void doPauseResume() {
        if (myVideoView.isPlaying()) {
            myVideoView.pause();
            showController();
            setAdViewVisibility(View.VISIBLE);
        } else {
            myVideoView.start();
            hideController();
            setAdViewVisibility(View.GONE);
        }
        updatePausePlay();
        updateSeekBar();
    }

    public void autoPlay() {
        myVideoView.autoPlay();
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private class MessageHandler extends Handler {
        public MessageHandler(MediaPlayerControlView mediaPlayerControlView) {
        }
    }
}