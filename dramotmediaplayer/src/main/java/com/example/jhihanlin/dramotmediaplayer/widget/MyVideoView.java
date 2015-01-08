package com.example.jhihanlin.dramotmediaplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;

import java.io.IOException;

/**
 * Created by jhihanlin on 12/13/14.
 */
public class MyVideoView extends SurfaceView implements SurfaceHolder.Callback,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener, MediaController.MediaPlayerControl {

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mediaPlayer State
    public int mCurrentState = STATE_IDLE;
    public int mTargetState = STATE_IDLE;

    private boolean autoPlay = false;

    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mediaPlayer;
    private Context context;
    public int mCurrentBufferPercentage;
    public MediaPlayer.OnPreparedListener newPreparedListener;
    public MediaPlayer.OnPreparedListener mPreparedListener;
    private String URIString;
    public MyVideoView(Context context) {
        super(context);
        init(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        Log.d("debug", "init*************!");
        SurfaceHolder videoHolder = this.getHolder();
        videoHolder.addCallback(this);
        setListener();
    }

    public void setListener() {
        mPreparedListener = new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mCurrentState = STATE_PREPARED;
                mTargetState = STATE_PREPARED;
                Log.d("debug", "mCurrentState:PREPARED!");
                if (newPreparedListener != null) {
                    newPreparedListener.onPrepared(mediaPlayer);
                }
                if (autoPlay) {
                    Log.d("prepared", "MyVideoView : onPrepared!!!!!!");
                    start();
                    autoPlay = false;
                }
            }
        };
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("debug", "surfaceCreated************");
        mSurfaceHolder = holder;
        if (!isInPlaybackState()) {
            Log.d("debug", "initialMediaPlayer************");
            initialMediaPlayer(mSurfaceHolder);
        } else {
            mediaPlayer.setDisplay(mSurfaceHolder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("debug", "release: isInPlaybackState is : " + isInPlaybackState());
        Log.d("debug", "release: mCurrentState is : " + String.valueOf(mCurrentState));
        if (!isInPlaybackState()) {
            mSurfaceHolder = null;
            Log.d("debug", "surfaceDestroyed************");
            release(true);
        }
    }

    private void initialMediaPlayer(SurfaceHolder holder) {
        try {

            try {
                Log.d("debug", "reset************mcurrentState" + mCurrentState);
                mCurrentState = STATE_PREPARING;
                Log.d("debug", "reset************");
                mediaPlayer.reset();
                mCurrentBufferPercentage = 0;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(context, Uri.parse(getURI()));
                mediaPlayer.setDisplay(this.getHolder());
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnPreparedListener(mPreparedListener);
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

    public boolean isInPlaybackState() {
        return (mediaPlayer != null && mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mediaPlayer.start();
            Log.d("debug", "MediaPlayer start!");

            mCurrentState = STATE_PLAYING;
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

    private void release(boolean clearTargetState) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d("debug", "mediaPlayer is release!");

                if (clearTargetState) {
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
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
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
    public int getAudioSessionId() {
        return 0;
    }

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
        mCurrentBufferPercentage = percent;

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;
        if (mediaPlayer != null) {
            // mediaPlayer.hide();
        }
    }

    public void autoPlay() {
        autoPlay = true;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        newPreparedListener = l;
    }

    public void setURI(String uri){
        URIString=uri;
    }

    public String getURI(){
        return URIString;
    }


}
