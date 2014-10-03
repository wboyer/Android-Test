package com.bill_boyer.androidtest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.bill_boyer.media.catalog.Segment;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer implements MediaPlayer.OnInfoListener
{
    Timer mTimer;
    Activity mActivity;
    VideoView mVideoView;
    MediaPlayer mMediaPlayer;
    ToggleButton mPlayPauseButton;
    private Segment mPlayingSegment;
    boolean mIsVisible;

    private static final String LOG = "VideoPlayer";

    public Segment getPlayingSegment()
    {
        return mPlayingSegment;
    }

    public void setPlayingSegment(Segment mPlayingSegment)
    {
        this.mPlayingSegment = mPlayingSegment;
    }

    public boolean getIsVisible()
    {
        return mIsVisible;
    }

    public VideoPlayer(Activity activity)
    {
        mActivity = activity;

        mVideoView = (VideoView)mActivity.findViewById(R.id.video_view);
        mVideoView.setOnInfoListener(this);

        mPlayPauseButton = (ToggleButton)mActivity.findViewById(R.id.play_pause_button);

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mVideoView.isPlaying())
                    mVideoView.pause();
                else
                    mVideoView.start();
            }
        });

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlayPauseButton.setEnabled(mMediaPlayer != null);
                        try {
                            mPlayPauseButton.setChecked(mVideoView.isPlaying());
                        }
                        catch (Exception e) {}
                    }
                });
            }}, 0, 1000);

        setPlayingSegment(null);
    }

    public void setIsVisible(boolean isVisible)
    {
        mIsVisible = isVisible;

        if (isVisible)
            Log.v(LOG, "VideoPlayer is visible");
        else {
            Log.v(LOG, "VideoPlayer is not visible");

            if ((getPlayingSegment() != null) && mVideoView.isPlaying())
                mVideoView.pause();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        mMediaPlayer = mediaPlayer;
        return false;
    }

    public void play(Segment segment)
    {
        setPlayingSegment(segment);

        mVideoView.setVideoURI(Uri.parse(segment.getMediaURL().toString()));

        if (getIsVisible())
            mVideoView.start();
    }
}
