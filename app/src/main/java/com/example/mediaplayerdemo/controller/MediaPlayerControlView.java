package com.example.mediaplayerdemo.controller;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.util.MySeekBar;
import com.example.mediaplayerdemo.util.WindowSizeUtils;
import com.example.mediaplayerdemo.widget.MyVideoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import android.os.Handler;
import android.os.Message;

public class MediaPlayerControlView extends FrameLayout {
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private Context mContext;
    private View mRoot;
    private ImageView mPauseButton, mReportButton, mShareButton;
    private MyVideoView myVideoView;
    private MySeekBar mSeekBar;
    private TextView mCurrentTime, mEndTime;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private LinearLayout mLinearLayout;
    private AdView mAdView;
    private AdRequest adRequest;
    private Handler mHandler = new MessageHandler(this);
    private boolean mShowing;
    private boolean mDragging;
    public int resumePosition;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private ProgressBar loadingProgressBar;
    private TextView mShareText, mReportText;
    private LinearLayout mLinearLayoutSizeFixed;
    private FrameLayout mShareFrameLayout;
    private FrameLayout mReportFrameLayout;
    private int touchButtonColor = getResources().getColor(R.color.peach);

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
        loadingProgressBar.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        addView(mRoot, frameParams);
        mLinearLayoutSizeFixed.measure(WindowSizeUtils.getWindowWidthSize(mContext), WindowSizeUtils.getWindowHeightSize(mContext));
    }

    public void hide() {

        try {
            hideController();
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.d("debug", "hide");
        }
        mShowing = false;
    }

    private void setAdView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.controller_adview, null);
        mAdView = (AdView) v.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (mLinearLayout != null) {
            mLinearLayout.removeAllViews();
            ViewGroup parent = (ViewGroup) mAdView.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            mLinearLayout.addView(mAdView);
        }
    }

    private void refreshAdView() {
        mAdView.loadAd(new AdRequest.Builder().build());
        Log.d("debug", "refresh Ad!");
    }

    public void setAdViewVisibility(int visibility) {
        mLinearLayout.setVisibility(visibility);
    }

    private void setListeners() {
        try {

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("adView", "ad loaded");

                    int adHeight = mAdView.getHeight();
                    int adWidth = mAdView.getWidth();
                    Log.d("adView", "ad adHeight" + adHeight);
                    Log.d("adView", "ad adWidth" + adWidth);

                    mLinearLayout.getLayoutParams().height = adHeight;
                    mLinearLayout.getLayoutParams().width = adWidth;
                }
            });
            mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("prepared", "MediaPlayerController : onPrepared!!!!!!");
                    setAdViewVisibility(View.GONE);
                    loadingProgressBar.setVisibility(View.GONE);
                }
            };
            mOnPreparedListener.onPrepared(myVideoView.getMediaPlayer());
            myVideoView.setOnPreparedListener(mOnPreparedListener);

            myVideoView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("debug", "onTouch");
                    if (myVideoView.isInPlaybackState()) {
                        if (mShowing) {
                            hide();
                        } else {
                            showController(sDefaultTimeout);
                        }
                    }
                    return false;
                }
            });
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
            mPauseButton.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        if (mPauseButton != null) {
                            mPauseButton.setColorFilter(touchButtonColor, PorterDuff.Mode.SRC_IN);
                        }
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        mPauseButton.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                        v.performClick();
                    }
                    return true;
                }
            });

            mShareFrameLayout.setOnClickListener(mShareListener);
            mShareFrameLayout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ImageView mShareIcon = (ImageView) mShareFrameLayout.getChildAt(1);
                    TextView mShareText = (TextView) mShareFrameLayout.getChildAt(0);
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        if (mShareFrameLayout != null) {
                            mShareIcon.setColorFilter(touchButtonColor, PorterDuff.Mode.SRC_IN);
                            mShareText.setTextColor(touchButtonColor);
                        }
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        if (mShareFrameLayout != null) {
                            mShareIcon.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                            mShareText.setTextColor(0xFFFFFFFF);
                        }
                        v.performClick();
                    }
                    return true;
                }
            });
            mReportFrameLayout.setOnClickListener(mReportListener);
            mReportFrameLayout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ImageView mReportIcon = (ImageView) mReportFrameLayout.getChildAt(1);
                    TextView mReportText = (TextView) mReportFrameLayout.getChildAt(0);
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        if (mReportFrameLayout != null) {
                            mReportIcon.setColorFilter(touchButtonColor, PorterDuff.Mode.SRC_IN);
                            mReportText.setTextColor(touchButtonColor);
                        }
                    } else if (MotionEvent.ACTION_UP == event.getAction()) {
                        if (mReportFrameLayout != null) {
                            mReportIcon.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                            mReportText.setTextColor(0xFFFFFFFF);
                        }
                        v.performClick();
                    }
                    return true;
                }
            });
            mSeekBar.setMax(1000);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser == false) {
                        return;
                    }
                    long duration = myVideoView.getDuration();
                    long newPosition = (duration * progress) / 1000L;
                    myVideoView.seekTo((int) newPosition);
                    mCurrentTime.setText(stringForTime((int) newPosition));
                    Log.d("debug", "onProgressChanged " + progress);
                    Log.d("debug", "mCurrentTime " + newPosition);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mDragging = true;
                    mHandler.removeMessages(SHOW_PROGRESS);

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mDragging = false;

                    showController(sDefaultTimeout);
                    updatePausePlay();
                    updateSeekBar();
                    mHandler.sendEmptyMessage(SHOW_PROGRESS);

                }
            });

        } catch (
                Exception exception
                )

        {
            exception.printStackTrace();
        }
    }

    private void findViews() {
        myVideoView = (MyVideoView) mRoot.findViewById(R.id.surface_media_player);
        mPauseButton = (ImageView) mRoot.findViewById(R.id.mPauseButton);
        mSeekBar = (MySeekBar) mRoot.findViewById(R.id.seekBar);
        mCurrentTime = (TextView) mRoot.findViewById(R.id.mCurrentTime);
        mEndTime = (TextView) mRoot.findViewById(R.id.mEndTime);
        mLinearLayout = (LinearLayout) mRoot.findViewById(R.id.adLinearLayout);
        mReportButton = (ImageView) mRoot.findViewById(R.id.mReportButton);
        mShareButton = (ImageView) mRoot.findViewById(R.id.mShareButton);
        loadingProgressBar = (ProgressBar) mRoot.findViewById(R.id.loadingProgressBar);
        mShareText = (TextView) mRoot.findViewById(R.id.mShareText);
        mReportText = (TextView) mRoot.findViewById(R.id.mReportText);
        mLinearLayoutSizeFixed = (LinearLayout) mRoot.findViewById(R.id.mLinearLayoutSizeFixed);
        mShareFrameLayout = (FrameLayout) mRoot.findViewById(R.id.mShareFrameLayout);
        mReportFrameLayout = (FrameLayout) mRoot.findViewById(R.id.mReportFrameLayout);
    }

    private int updateSeekBar() {
        int position = myVideoView.getCurrentPosition();
        int duration = myVideoView.getDuration();
        if (duration > 0) {
            int seekBarPosition = (int) (((double) position / duration) * 1000);
            mSeekBar.setProgress(seekBarPosition);
            Log.d("debug", "updateSeekBar " + seekBarPosition);
            Log.d("debug", "updateSeekBar " + position);
            Log.d("debug", "updateSeekBar " + duration);
        }
        int percent = myVideoView.getBufferPercentage();
        Log.d("debug", "updateSeekBar " + percent);
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

    public void showController(int timeout) {
        setControllerVisibility(View.VISIBLE);
        updatePausePlay();
        updateSeekBar();

        mShowing = true;
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
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
        mReportButton.setVisibility(visibility);
        mShareButton.setVisibility(visibility);
        mShareText.setVisibility(visibility);
        mReportText.setVisibility(visibility);
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
            showController(sDefaultTimeout);
            setAdViewVisibility(View.VISIBLE);
            mAdView.resume();
            refreshAdView();
        } else {
            myVideoView.start();
            hideController();
            setAdViewVisibility(View.GONE);
            mAdView.pause();
        }
        updatePausePlay();
        updateSeekBar();
    }

    public void resume(int len) {
        Log.d("debug", "resume!!!!!!!!!!!!!!!!!!!");
        if (myVideoView != null) {
            myVideoView.pause();
            myVideoView.seekTo(len);
            Log.d("debug", "seek to!!!!!!!!!!!!!!!!!!!");
            showController(sDefaultTimeout);
            setAdViewVisibility(View.VISIBLE);
            refreshAdView();
        }
    }

    public int getCurrentPosition() {
        resumePosition = myVideoView.getCurrentPosition();
        return resumePosition;
    }

    public void autoPlay() {
        myVideoView.autoPlay();
        setAdViewVisibility(View.VISIBLE);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private OnClickListener mShareListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d("debug", "mShareListener");

        }
    };
    private OnClickListener mReportListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("debug", "mReportListener");
        }
    };

    private static class MessageHandler extends Handler {
        private final WeakReference<MediaPlayerControlView> mView;

        MessageHandler(MediaPlayerControlView view) {
            mView = new WeakReference<MediaPlayerControlView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaPlayerControlView view = mView.get();
            if (view == null || view.myVideoView == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.updateSeekBar();
                    if (!view.mDragging && view.mShowing && view.myVideoView.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }
}