package com.example.mediaplayerdemo.controller;

import com.example.mediaplayerdemo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MediaPlayerControlView extends FrameLayout {
	private MediaPlayerControl mPlayer;
	private Context mContext;
	private View mRoot;
	private ImageButton mPauseButton;
	private ViewGroup mAnchor;

	public MediaPlayerControlView(Context context) {
		super(context);
		mContext = context;
	}

	public MediaPlayerControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRoot = null;
		mContext = context;
	}

	public MediaPlayerControlView(Context context, boolean useFastForward) {
		super(context);
		mContext = context;
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}

	@Override
	public void onFinishInflate() {
		if (mRoot != null)
			initControllerView(mRoot);
	}

	public void setAnchorView(ViewGroup view) {
		if (mAnchor != null) {
			mAnchor.removeView(this);
			Log.d("debug", "removeView!!!!");
		}
		mAnchor = view;

		FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
				);

		removeAllViews();
		View v = makeControllerView();
		addView(v, frameParams);
	}

	protected View makeControllerView() {
		LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflate.inflate(R.layout.controller_media_control, null);

		initControllerView(mRoot);

		return mRoot;
	}

	private void initControllerView(View v) {
		Log.d("debug", "initControllerView!!!!!!");
		mPauseButton = (ImageButton) v.findViewById(R.id.mPauseButton);
		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setOnClickListener(mPauseListener);
		}
	}

	public void show() {
		if (mAnchor != null) {
			mAnchor.removeView(this);
			Log.d("debug", "removeView!!!!");
		}
		if (mPauseButton != null) {
			mPauseButton.requestFocus();
		}

		FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER
				);

		mAnchor.addView(this, tlp);
		updatePausePlay();

	}

	public void updatePausePlay() {
		if (mRoot == null || mPauseButton == null || mPlayer == null) {
			return;
		}

		if (mPlayer.isPlaying()) {
			mPauseButton.setImageResource(R.drawable.video_pause);
		} else {
			mPauseButton.setImageResource(R.drawable.video_play);
		}
	}

	private void doPauseResume() {
		if (mPlayer == null) {
			return;
		}

		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mPlayer.start();
		}
		updatePausePlay();
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
		}
	};

	public interface MediaPlayerControl {
		void start();

		void pause();

		int getDuration();

		int getCurrentPosition();

		void seekTo(int pos);

		boolean isPlaying();

		int getBufferPercentage();

		boolean canPause();

		boolean canSeekBackward();

		boolean canSeekForward();

		boolean isFullScreen();

		void toggleFullScreen();
	}
}