package com.example.mediaplayerdemo.activity;

import com.example.mediaplayerdemo.R;
import com.example.mediaplayerdemo.controller.MediaPlayerControlView;
import com.example.mediaplayerdemo.widget.MyVideoView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MediaPlayerActivity extends FragmentActivity {

    private MediaPlayerControlView mediaPlayerControlView;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_media_player);

        mediaPlayerControlView = (MediaPlayerControlView) findViewById(R.id.surface_container);
        mediaPlayerControlView.autoPlay();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","onPause");
        length = mediaPlayerControlView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResume");
    }

    @Override
    protected void onRestart() {
        Log.d("debug","onRestart");
        mediaPlayerControlView.resume(length);
        super.onRestart();
    }
}
