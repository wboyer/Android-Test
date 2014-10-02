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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bill_boyer.media.catalog.Provider;
import com.bill_boyer.media.catalog.Title;
import com.bill_boyer.media.catalog.Segment;
import com.bill_boyer.media.catalog.impl.ProviderFactoryImpl;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

        ListView providersListView = (ListView)mActivity.findViewById(R.id.providers_list_view);

        ArrayList<String> test = new ArrayList<String>();
        test.add("a");
        test.add("b");
        test.add("c");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.providers_table_row, test);

        providersListView.setAdapter(arrayAdapter);

        new LoadProvidersTask().execute(new Object[]{mActivity, (TableLayout)mActivity.findViewById(R.id.providers_table_view)});

        new LoadTitlesTask().execute(new Object[]{mActivity, (TableLayout)mActivity.findViewById(R.id.titles_table_view)});

        new LoadSegmentsTask().execute(new Object[]{mActivity, (TableLayout)mActivity.findViewById(R.id.segments_table_view)});
    }

    public void setIsVisible(boolean isVisible)
    {
        if (mVideoPlayer != null)
            mVideoPlayer.setIsVisible(isVisible);
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

            final ArrayList<Provider> providers = new ArrayList<Provider>(factory.getProviders().values());

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    ListView providersListView = (ListView) activity.findViewById(R.id.providers_list_view);

                    ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(activity, R.layout.providers_table_row, providers.toArray())  ;

                    providersListView.setAdapter(arrayAdapter);
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

            MyHttpClient client = new MyHttpClient();

            ProviderFactoryImpl factory = new ProviderFactoryImpl(client);

            final ArrayList<Provider> providers = new ArrayList<Provider>(factory.getProviders().values());

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    ListView titlesListView = (ListView) activity.findViewById(R.id.titles_list_view);

                    ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(activity, R.layout.titles_table_row, providers.toArray())  ;

                    titlesListView.setAdapter(arrayAdapter);
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
