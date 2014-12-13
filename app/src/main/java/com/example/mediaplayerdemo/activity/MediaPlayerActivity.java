package com.example.mediaplayerdemo.activity;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.controller.MediaPlayerControlView;
import com.example.mediaplayerdemo.widget.MyVideoView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MediaPlayerActivity extends Activity {

	private ImageButton playButton;
    private MediaPlayerControlView mediaPlayerControlView;

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            mediaPlayerControlView.autoPlay();
            playButton.setVisibility(View.GONE);
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surface_media_player);

        mediaPlayerControlView = (MediaPlayerControlView) findViewById(R.id.surface_container);

        playButton = (ImageButton) findViewById(R.id.playButton);
		playButton.setOnClickListener(mOnclickListener);
	}

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
