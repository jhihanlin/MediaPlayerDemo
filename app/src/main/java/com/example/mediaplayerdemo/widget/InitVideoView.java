package com.example.mediaplayerdemo.widget;

import com.example.mediaplayerdemo.util.WindowSizeUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class InitVideoView extends VideoView {

	private PauseListener pauseListener;
	private StartListener startListener;

	// private OnSeekCompleteListener seekListener;

	public InitVideoView(Context context) {
		super(context);
		onMeasure(WindowSizeUtils.getWindowWidthSize(context), WindowSizeUtils.getWindowHeightSize(context));
	}

	public InitVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InitVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void pause() {
		super.pause();
		if (pauseListener != null)
			pauseListener.onPause();
	}

	@Override
	public void start() {
		super.start();
		if (startListener != null)
			startListener.onStart();
	}

	@Override
	public int getCurrentPosition() {
		return super.getCurrentPosition();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public void setPauseListener(PauseListener listener) {
		this.pauseListener = listener;
	}

	public void setStartListener(StartListener listener) {
		this.startListener = listener;
	}

	/**
	 * Pause Listener to fetch pause event
	 * */
	public interface PauseListener {
		void onPause();
	}

	/**
	 * Pause Listener to fetch pause event
	 * */
	public interface StartListener {
		void onStart();
	}

	/**
	 * SeekBar Listener
	 * */
	public interface SeekBarListener {
		void seekTo();
	}

	// public void
	// setOnSeekBarCompleteListener(MediaPlayer.OnSeekCompleteListener listener)
	// {
	// seekListener = listener;
	// }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

}
