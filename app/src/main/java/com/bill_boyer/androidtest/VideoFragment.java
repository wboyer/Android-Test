package com.bill_boyer.androidtest;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.bill_boyer.media.catalog.Provider;
import com.bill_boyer.media.catalog.Title;
import com.bill_boyer.media.catalog.Segment;
import com.bill_boyer.media.catalog.impl.ProviderFactoryImpl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Iterator;

public class VideoFragment extends Fragment
{
    public static VideoFragment newInstance()
    {
        System.out.println("VideoFragment constructed");

        return new VideoFragment();
    }

    public VideoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        System.out.println("VideoFragment started");

        Activity activity = getActivity();
        VideoView videoView = (VideoView)activity.findViewById(R.id.section_video);

        new StartVideoTask().execute(new Object[] {activity, videoView});
    }

    private static class StartVideoTask extends AsyncTask<Object[], Void, Void>
    {
        @Override
        protected Void doInBackground(Object[]... objects)
        {
            Object[] args = objects[0];

            Activity activity = (Activity)args[0];
            final VideoView view = (VideoView)args[1];

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
                                view.setVideoURI(Uri.parse(segment.getMediaURL().toString()));
                                view.start();
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
