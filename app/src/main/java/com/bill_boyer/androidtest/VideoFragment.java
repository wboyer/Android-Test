package com.bill_boyer.androidtest;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

public class VideoFragment extends Fragment implements MediaPlayer.OnInfoListener
{
    Timer mTimer;
    Activity mActivity;
    VideoView mVideoView;
    MediaPlayer mMediaPlayer;
    ToggleButton mPlayPauseButton;
    private Segment mPlayingSegment;
    boolean mIsVisible;

    private static final String LOG = "VideoFragment";

    public static VideoFragment newInstance()
    {
        Log.v(LOG, "VideoFragment constructed");

        return new VideoFragment();
    }

    public VideoFragment() {}

    public Segment getPlayingSegment() {
        return mPlayingSegment;
    }

    public void setPlayingSegment(Segment mPlayingSegment) {
        this.mPlayingSegment = mPlayingSegment;
    }

    public boolean getIsVisible() {
        return mIsVisible;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.v(LOG, "VideoFragment started");

        mActivity = getActivity();

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
            }}, 1000, 1000);

        setPlayingSegment(null);

        new LoadProvidersTask().execute(new Object[]{mActivity, (TableLayout)mActivity.findViewById(R.id.providers_table_view)});

        new LoadTitlesTask().execute(new Object[]{mActivity, (TableLayout)mActivity.findViewById(R.id.titles_table_view)});

        new LoadSegmentsTask().execute(new Object[]{mActivity, (TableLayout)mActivity.findViewById(R.id.segments_table_view)});

        new StartVideoTask().execute(new Object[] {mActivity, this, mVideoView});
    }

    public void setIsVisible(boolean isVisible)
    {
        mIsVisible = isVisible;

        if (isVisible)
            Log.v(LOG, "VideoFragment is visible");
        else {
            Log.v(LOG, "VideoFragment is not visible");

            if ((getPlayingSegment() != null) && mVideoView.isPlaying())
                mVideoView.pause();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        mMediaPlayer = mediaPlayer;
        return false;
    }

    private static class LoadProvidersTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];

            final Activity activity = (Activity)args[0];
            final TableLayout tableLayout = (TableLayout)args[1];

            MyHttpClient client = new MyHttpClient();

            ProviderFactoryImpl factory = new ProviderFactoryImpl(client);

            final Iterator<Provider> providers = factory.getProviders().values().iterator();

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (providers.hasNext()) {
                        Provider provider = providers.next();

                        for (int i = 0; i < 4; i++) {
                            TableRow row = new TableRow(activity);
                            TextView textView = (TextView)activity.getLayoutInflater().inflate(R.layout.providers_table_row, null);
                            textView.setText("hi there");
                            row.addView(textView);
                            tableLayout.addView(row);
                        }
                    }
                }
            });

            client.close();

            return null;
        }
    }

    private static class LoadTitlesTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];

            final Activity activity = (Activity)args[0];
            final TableLayout tableLayout = (TableLayout)args[1];

            MyHttpClient client = new MyHttpClient();

            ProviderFactoryImpl factory = new ProviderFactoryImpl(client);

            final Iterator<Provider> providers = factory.getProviders().values().iterator();

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (providers.hasNext()) {
                        Provider provider = providers.next();

                        for (int i = 0; i < 4; i++) {
                            TableRow row = new TableRow(activity);
                            TextView textView = (TextView)activity.getLayoutInflater().inflate(R.layout.titles_table_row, null);
                            textView.setText("hi there");
                            row.addView(textView);
                            tableLayout.addView(row);
                        }
                    }
                }
            });

            client.close();

            return null;
        }
    }

    private static class LoadSegmentsTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];

            final Activity activity = (Activity)args[0];
            final TableLayout tableLayout = (TableLayout)args[1];

            MyHttpClient client = new MyHttpClient();

            ProviderFactoryImpl factory = new ProviderFactoryImpl(client);

            final Iterator<Provider> providers = factory.getProviders().values().iterator();

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (providers.hasNext()) {
                        Provider provider = providers.next();

                        for (int i = 0; i < 4; i++) {
                            TableRow row = new TableRow(activity);
                            TextView textView = (TextView)activity.getLayoutInflater().inflate(R.layout.segments_table_row, null);
                            textView.setText("hi there");
                            row.addView(textView);
                            tableLayout.addView(row);
                        }
                    }
                }
            });

            client.close();

            return null;
        }
    }

    private static class StartVideoTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];

            Activity activity = (Activity)args[0];
            final VideoFragment fragment = (VideoFragment)args[1];
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
                                fragment.setPlayingSegment(segment);
//                                videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                                videoView.setVideoURI(Uri.parse(segment.getMediaURL().toString()));
//                                videoView.seekTo(30000);
                                if (fragment.getIsVisible())
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
