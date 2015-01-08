package com.example.jhihanlin.dramotmediaplayer.VideoActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.jhihanlin.dramotmediaplayer.R;
import com.example.jhihanlin.dramotmediaplayer.controller.MediaPlayerControlView;


public class MediaPlayerActivity extends FragmentActivity {

    private MediaPlayerControlView mediaPlayerControlView;
    private int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_media_player);

        mediaPlayerControlView = (MediaPlayerControlView) findViewById(R.id.surface_container);
        mediaPlayerControlView.setVideo("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
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
