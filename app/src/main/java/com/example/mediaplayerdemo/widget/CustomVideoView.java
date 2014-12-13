package com.example.mediaplayerdemo.widget;

import android.content.Context;
import android.view.SurfaceView;
import android.util.AttributeSet;

import com.example.mediaplayerdemo.controller.MediaPlayerControlView;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */

public class CustomVideoView extends SurfaceView implements MediaPlayerControlView.MediaPlayerControl {
	Context mContext;

	public CustomVideoView(Context context) {
		super(context);
		this.mContext = context;
		initVideoView();
	}

	public CustomVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
		initVideoView();
	}

	public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initVideoView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	}

	private void initVideoView() {
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFullScreen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toggleFullScreen() {
		// TODO Auto-generated method stub

	}

}