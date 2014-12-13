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
import android.widget.SeekBar;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.widget.MyVideoView;

public class MediaPlayerControlView extends FrameLayout {

    private Context mContext;
    private View mRoot;
    private ImageButton mPauseButton;
    private MyVideoView myVideoView;
    private SeekBar mSeekBar;

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

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        addView(mRoot, frameParams);
    }

    private void setListeners() {
        myVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("debug", "onTouch");
                myVideoView.getMediaPlayer().pause();
                showController();
                return false;
            }
        });

        mPauseButton.requestFocus();
        mPauseButton.setOnClickListener(mPauseListener);
    }

    private void findViews() {
        myVideoView = (MyVideoView) mRoot.findViewById(R.id.surface_media_player);
        mPauseButton = (ImageButton) mRoot.findViewById(R.id.mPauseButton);
        mSeekBar = (SeekBar) mRoot.findViewById(R.id.seekBar);
    }

    public void showController() {
//        if (mPauseButton != null) {
//            mPauseButton.requestFocus();
//        }
        setControllerVisibility(View.VISIBLE);
        updatePausePlay();
    }

    public void hideController() {
        setControllerVisibility(View.GONE);
        updatePausePlay();
    }

    private void setControllerVisibility(int visibility) {
        mPauseButton.setVisibility(visibility);
        mSeekBar.setVisibility(visibility);
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
        } else {
            myVideoView.start();
            hideController();
        }
        updatePausePlay();
    }

    public void autoPlay() {
        myVideoView.autoPlay();
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };
}