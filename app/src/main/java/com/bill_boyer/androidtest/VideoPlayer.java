package com.bill_boyer.androidtest;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.bill_boyer.media.catalog.Segment;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer
{
    Timer mTimer;
    VideoView mVideoView;
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

    public VideoPlayer(final Activity activity)
    {
        mVideoView = (VideoView)activity.findViewById(R.id.video_view);

        mPlayPauseButton = (ToggleButton)activity.findViewById(R.id.play_pause_button);

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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlayPauseButton.setEnabled(mPlayingSegment != null);
                        try {
                            mPlayPauseButton.setChecked(mVideoView.isPlaying());
                        }
                        catch (Exception e) {}
                    }
                });
            }}, 0, 1000);

        setPlayingSegment(null);
    }

    public void onStop()
    {
        mTimer.cancel();
        mTimer.purge();
    }

    public void setIsVisible(boolean isVisible)
    {
        mIsVisible = isVisible;

        if (!isVisible)
            if ((getPlayingSegment() != null) && mVideoView.isPlaying())
                mVideoView.pause();
    }

    public void play(Segment segment)
    {
        setPlayingSegment(segment);

        mVideoView.setVideoURI(Uri.parse(segment.getMediaURL().toString()));

        if (getIsVisible())
            mVideoView.start();
    }
}
