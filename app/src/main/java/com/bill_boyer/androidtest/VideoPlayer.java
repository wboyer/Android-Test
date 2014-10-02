package com.bill_boyer.androidtest;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.bill_boyer.media.catalog.Provider;
import com.bill_boyer.media.catalog.Title;
import com.bill_boyer.media.catalog.Segment;
import com.bill_boyer.media.catalog.impl.ProviderFactoryImpl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Iterator;
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

        new StartVideoTask().execute(new Object[] {mActivity, this, mVideoView});
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

    private static class StartVideoTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];

            Activity activity = (Activity)args[0];
            final VideoPlayer videoPlayer = (VideoPlayer)args[1];
            final VideoView videoView = (VideoView)args[2];

            MyHttpClient client = new MyHttpClient();

            ProviderFactoryImpl factory = new ProviderFactoryImpl(client);

            Iterator<Provider> providers = factory.getProviders().values().iterator();
            if (providers.hasNext()) {
                Provider provider = providers.next();

                Iterator<Title> titles = provider.getLatestTitles(0, 1).iterator();
                if (titles.hasNext()) {
                    Title title = titles.next();

                    Iterator<Segment> segments = title.getSegments().iterator();
                    if (segments.hasNext()) {
                        final Segment segment = segments.next();

                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                videoPlayer.setPlayingSegment(segment);
//                                videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                                videoView.setVideoURI(Uri.parse(segment.getMediaURL().toString()));
//                                videoView.seekTo(30000);
                                if (videoPlayer.getIsVisible())
                                    videoView.start();
                            }
                        });
                    }
                }
            }

            client.close();

            return null;
        }
    }

    public static class MyHttpClient implements com.bill_boyer.media.catalog.HttpClient
    {
        AndroidHttpClient mClient;

        public MyHttpClient()
        {
            mClient = AndroidHttpClient.newInstance("test");
        }

        public HttpResponse execute(HttpUriRequest request)
        {
            try {
                return mClient.execute(request);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void close()
        {
            mClient.close();
        }
    }
}
