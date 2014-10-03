package com.bill_boyer.androidtest;

import android.app.Activity;
import android.app.Fragment;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bill_boyer.media.catalog.HttpClient;
import com.bill_boyer.media.catalog.Provider;
import com.bill_boyer.media.catalog.Title;
import com.bill_boyer.media.catalog.Segment;
import com.bill_boyer.media.catalog.impl.ProviderFactoryImpl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class VideoFragment extends Fragment
{
    Activity mActivity;
    VideoPlayer mVideoPlayer;

    private static final String LOG = "VideoFragment";

    public static VideoFragment newInstance()
    {
        Log.v(LOG, "VideoFragment constructed");

        return new VideoFragment();
    }

    public VideoFragment() {}

    public boolean getIsVisible()
    {
        return (mVideoPlayer != null) && mVideoPlayer.mIsVisible;
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
        mVideoPlayer = new VideoPlayer(mActivity);

        MyHttpClient httpClient = new MyHttpClient();

        new LoadProvidersTask().execute(new Object[]{mActivity, mVideoPlayer, httpClient});
    }

    public void setIsVisible(boolean isVisible)
    {
        if (mVideoPlayer != null)
            mVideoPlayer.setIsVisible(isVisible);
    }

    private static List<Provider> LoadProviders(final Activity activity, HttpClient httpClient)
    {
        ProviderFactoryImpl factory = new ProviderFactoryImpl(httpClient);
        final List<Provider> providers = new ArrayList<Provider>(factory.getProviders().values());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView providersListView = (ListView) activity.findViewById(R.id.providers_list_view);
                ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(activity, R.layout.providers_table_row, providers.toArray());
                providersListView.setAdapter(arrayAdapter);
            }
        });

        return providers;
    }

    private static class LoadProvidersTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];
            Activity activity = (Activity)args[0];
            VideoPlayer videoPlayer = (VideoPlayer)args[1];
            HttpClient httpClient = (HttpClient)args[2];

            List<Provider> providers = LoadProviders(activity, httpClient);

            List<Title> titles = LoadTitles(activity, (Provider)providers.get(0));

            LoadSegments(activity, videoPlayer, (Title)titles.get(0));

            return null;
        }
    }

    private static List<Title> LoadTitles(final Activity activity, final Provider provider)
    {
        final List titles = provider.getLatestTitles(0, 1);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView titlesListView = (ListView)activity.findViewById(R.id.titles_list_view);
                ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(activity, R.layout.titles_table_row, titles.toArray());
                titlesListView.setAdapter(arrayAdapter);
            }
        });

        return titles;
    }

    private static class LoadTitlesTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];
            Activity activity = (Activity)args[0];
            Provider provider = (Provider)args[1];

            LoadTitles(activity, provider);

            return null;
        }
    }

    private static void LoadSegments(final Activity activity, final VideoPlayer videoPlayer, final Title title)
    {
        final List<Segment> segments = title.getSegments();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView segmentsListView = (ListView)activity.findViewById(R.id.segments_list_view);
                ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(activity, R.layout.segments_table_row, segments.toArray());
                segmentsListView.setAdapter(arrayAdapter);

                segmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id)
                    {
                        Segment segment = segments.get(position);
                        Log.v(LOG, segment.toString());
                        videoPlayer.play(segment);
                    }
                });
            }
        });
    }

    private static class LoadSegmentsTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];
            final Activity activity = (Activity)args[0];
            final VideoPlayer videoPlayer = (VideoPlayer)args[1];
            final Title title = (Title)args[2];

            LoadSegments(activity, videoPlayer, title);

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
