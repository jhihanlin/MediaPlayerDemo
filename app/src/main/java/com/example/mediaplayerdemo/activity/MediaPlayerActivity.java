package com.example.mediaplayerdemo.activity;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.widget.MyVideoView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MediaPlayerActivity extends Activity {

	private ImageButton playButton;

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("debug", "click paly!");
            start();
        }
    };
    private MyVideoView myVideoView;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surface_media_player);

		myVideoView = (MyVideoView) findViewById(R.id.surface_media_player);
		playButton = (ImageButton) findViewById(R.id.playButton);

		playButton.setOnClickListener(mOnclickListener);
	}

    public void start() {
        Log.d("debug", "mCurrentState" + myVideoView.mCurrentState);
        if (myVideoView.isInPlaybackState()) {
            myVideoView.mediaPlayer.start();
            Log.d("debug", "MediaPlayer start!");

            myVideoView.mCurrentState = MyVideoView.STATE_PLAYING;
            playButton.setVisibility(View.INVISIBLE);
            Log.d("debug", "mCurrentState:PLAYING!");
        }
        myVideoView.mTargetState = MyVideoView.STATE_PLAYING;
    }

}
