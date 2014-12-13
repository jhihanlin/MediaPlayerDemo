package com.example.mediaplayerdemo.activity;

import java.io.IOException;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.controller.MediaPlayerControlView;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class MediaPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, MediaPlayerControlView.MediaPlayerControl {

	// all possible internal states
	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;

	// mediaPlayer State
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private SurfaceHolder mSurfaceHolder = null;
	private SurfaceView surfaceView;
	private MediaPlayer mediaPlayer;
	private ImageButton playButton;
	MediaPlayerControlView controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surface_media_player);

		surfaceView = (SurfaceView) findViewById(R.id.surface_media_player);
		playButton = (ImageButton) findViewById(R.id.playButton);

		mediaPlayer = new MediaPlayer();
		SurfaceHolder videoHolder = surfaceView.getHolder();
		videoHolder.addCallback(this);
		controller = new MediaPlayerControlView(this);
		playButton.setOnClickListener(mOnclickListener);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
		initialMediaPlayer(mSurfaceHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCurrentState != STATE_PAUSED) {
			mSurfaceHolder = null;
			release(true);
		}
	}

	private void initialMediaPlayer(SurfaceHolder holder) {
		try {

			try {
				mCurrentState = STATE_PREPARING;
				mediaPlayer.reset();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setDataSource(this, Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
				mediaPlayer.setDisplay(surfaceView.getHolder());
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnErrorListener(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
			Log.d("debug", "Current State:IDLE! ");
			Log.d("debug", "Current Target State:IDLE!");
		} catch (Exception e) {
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mCurrentState = STATE_PREPARED;
		mTargetState = STATE_PREPARED;
		Log.d("debug", "mCurrentState:PREPARED!");
		controller.setMediaPlayer(this);
		controller.setAnchorView((FrameLayout) findViewById(R.id.surface_container));
	}

	public boolean onTouchEvent(MotionEvent event) {
		Log.d("debug", "onTouch");
		controller.show();
		pause();
		return false;

	};

	private boolean isInPlaybackState() {
		return (mediaPlayer != null && mCurrentState != STATE_ERROR &&
				mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	@Override
	public void start() {
			Log.d("debug", "mCurrentState" + mCurrentState);
			if (isInPlaybackState()) {
				mediaPlayer.start();
				Log.d("debug", "MediaPlayer start!");

				mCurrentState = STATE_PLAYING;
				playButton.setVisibility(View.INVISIBLE);
				Log.d("debug", "mCurrentState:PLAYING!");
			}
			mTargetState = STATE_PLAYING;
	}

	@Override
	public void pause() {
		if (isInPlaybackState()) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
				Log.d("debug", "mCurrentState:PAUSED!");
			}
		}
		mTargetState = STATE_PAUSED;
	}

	public void release(boolean cleartargetstate) {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
				Log.d("debug", "mediaPlayer is relese!");

				if (cleartargetstate) {
					mCurrentState = STATE_IDLE;
					mTargetState = STATE_IDLE;
					Log.d("debug", "mCurrentState:IDLE!");

				}
			}
		} catch (Exception e2) {
		}

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
		return isInPlaybackState() && mediaPlayer.isPlaying();
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

	private View.OnClickListener mOnclickListener = new View.OnClickListener() {
		public void onClick(View v) {
			Log.d("debug", "click paly!");
			start();
		}
	};

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mCurrentState = STATE_ERROR;
		mTargetState = STATE_ERROR;
		Log.d("debug", "Current State:Error! ");
		Log.d("debug", "Current Target State:Error! ");
		return false;
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mCurrentState = STATE_PLAYBACK_COMPLETED;
		mTargetState = STATE_PLAYBACK_COMPLETED;
		if (mediaPlayer != null) {
			// mediaPlayer.hide();
		}
	}
}
