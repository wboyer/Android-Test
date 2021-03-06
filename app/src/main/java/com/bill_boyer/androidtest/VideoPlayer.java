package com.bill_boyer.androidtest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.bill_boyer.media.catalog.Segment;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer implements MediaPlayer.OnCompletionListener
{
    private Activity mActivity;
    private Timer mTimer;
    private VideoView mVideoView;
    private Button mBack15sButton;
    private ToggleButton mPlayPauseButton;
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
        updatePlayPauseButton();
    }

    public boolean getIsVisible()
    {
        return mIsVisible;
    }

    public VideoPlayer(final Activity activity)
    {
        mActivity = activity;

        mVideoView = (VideoView)activity.findViewById(R.id.video_view);
        mVideoView.setOnCompletionListener(this);

        mBack15sButton = (Button)activity.findViewById(R.id.back_15s_button);

        mBack15sButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                int position = mVideoView.getCurrentPosition() - 15000;

                if (position < 0)
                    position = 0;

                mVideoView.seekTo(position);
            }
        });

        mPlayPauseButton = (ToggleButton)activity.findViewById(R.id.play_pause_button);

        mPlayPauseButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mVideoView.isPlaying())
                    mVideoView.pause();
                else
                    mVideoView.start();
                updatePlayPauseButton();
            }
        });

        mTimer = new Timer();

        setPlayingSegment(null);
    }

    final Runnable mUpdatePlayPauseButton = new Runnable() {
        @Override
        public void run()
        {
            mPlayPauseButton.setEnabled(mPlayingSegment != null);
            try {
                mPlayPauseButton.setChecked(mVideoView.isPlaying());
            }
            catch (Exception e) {}
        }
    };

    private void updatePlayPauseButton()
    {
        synchronized (this)
        {
            if (mTimer != null)
                mTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        mActivity.runOnUiThread(mUpdatePlayPauseButton);
                    }
                }, 100);
        }
    }

    public void onStop()
    {
        synchronized (this)
        {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public void setIsVisible(boolean isVisible)
    {
        mIsVisible = isVisible;

        if (!isVisible)
            if ((getPlayingSegment() != null) && mVideoView.isPlaying())
                mVideoView.pause();

        updatePlayPauseButton();
    }

    public void play(Segment segment)
    {
        URL url = segment.getMediaURL();

        if (url != null) {
            mVideoView.setVideoURI(Uri.parse(url.toString()));

            if (getIsVisible())
                mVideoView.start();
        }

        setPlayingSegment(segment);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        mVideoView.stopPlayback();
        updatePlayPauseButton();
    }
}
