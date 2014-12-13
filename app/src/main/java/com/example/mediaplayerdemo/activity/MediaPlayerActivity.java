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
    private MyVideoView myVideoView;

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            myVideoView.autoPlay();
            playButton.setVisibility(View.GONE);
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surface_media_player);


        playButton = (ImageButton) findViewById(R.id.playButton);
		playButton.setOnClickListener(mOnclickListener);

        myVideoView = (MyVideoView) findViewById(R.id.surface_media_player);
        myVideoView.setAnchorView((android.widget.FrameLayout) findViewById(R.id.surface_container));
	}

}
