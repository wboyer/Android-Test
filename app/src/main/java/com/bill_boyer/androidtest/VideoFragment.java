package com.bill_boyer.androidtest;

import android.app.Activity;
import android.app.Fragment;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bill_boyer.media.catalog.HttpClient;
import com.bill_boyer.media.catalog.Provider;
import com.bill_boyer.media.catalog.Title;
import com.bill_boyer.media.catalog.Segment;
import com.bill_boyer.media.catalog.impl.ProviderFactoryImpl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment
{
    VideoPlayer mVideoPlayer;
    MyHttpClient mHttpClient;

    private static final String LOG = "VideoFragment";

    public static VideoFragment newInstance()
    {
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

        mVideoPlayer = new VideoPlayer(getActivity());
        mHttpClient = new MyHttpClient();

        new LoadProvidersTask().execute(new Object[]{this, mHttpClient});
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mHttpClient.close();
        mVideoPlayer.onStop();
    }

    public void setIsVisible(boolean isVisible)
    {
        if (mVideoPlayer != null)
            mVideoPlayer.setIsVisible(isVisible);
    }

    private List<Provider> loadProviders(HttpClient httpClient)
    {
        ProviderFactoryImpl factory = new ProviderFactoryImpl(httpClient);
        final List<Provider> providers = new ArrayList<Provider>(factory.getProviders().values());

        final Activity activity = getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView)activity.findViewById(R.id.providers_list_view);
                final Playlist playlist = new Playlist(activity, R.layout.playlist_row, providers);
                listView.setAdapter(playlist);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id)
                    {
                        selectProvider(playlist, position, providers.get(position));
                    }
                });

                selectProvider(playlist, 0, providers.get(0));
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
            VideoFragment fragment = (VideoFragment)args[0];
            HttpClient httpClient = (HttpClient)args[1];

            fragment.loadProviders(httpClient);

            return null;
        }
    }

    public void selectProvider(Playlist playlist, int position, Provider provider)
    {
        playlist.selectItem(position);
        new LoadTitlesTask().execute(new Object[]{this, provider});
    }

    private List<Title> loadTitles(final Provider provider)
    {
        final List<Title> titles = provider.getLatestTitles(0, 1);

        final Activity activity = getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView)activity.findViewById(R.id.titles_list_view);
                final Playlist playlist = new Playlist(activity, R.layout.playlist_row, titles);
                listView.setAdapter(playlist);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectTitle((Playlist)parent.getAdapter(), position, titles.get(position));
                    }
                });

                selectTitle(playlist, 0, titles.get(0));
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
            VideoFragment fragment = (VideoFragment)args[0];
            Provider provider = (Provider)args[1];

            fragment.loadTitles(provider);

            return null;
        }
    }

    public void selectTitle(Playlist playlist, int position, Title title)
    {
        playlist.selectItem(position);
        new LoadSegmentsTask().execute(new Object[]{this, title});
    }

    private void loadSegments(final Title title)
    {
        final List<Segment> segments = title.getSegments();

        final Activity activity = getActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView)activity.findViewById(R.id.segments_list_view);
                final Playlist playlist = new Playlist(activity, R.layout.playlist_row, segments);
                listView.setAdapter(playlist);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectSegment(playlist, position, segments.get(position));
                    }
                });

                selectSegment(playlist, 0, segments.get(0));
            }
        });
    }

    private static class LoadSegmentsTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];
            final VideoFragment fragment = (VideoFragment)args[0];
            final Title title = (Title)args[1];

            fragment.loadSegments(title);

            return null;
        }
    }

    public void selectSegment(Playlist playlist, int position, Segment segment)
    {
        playlist.selectItem(position);
        mVideoPlayer.play(segment);
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
