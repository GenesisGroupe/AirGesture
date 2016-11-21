package com.genesis.airgesture;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import com.genesis.airgesture.adapters.PostAdapter;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private static int IMAGE_SIZE = 1024;
    public static  String consumer_key = "KLwOfrGQ80ZBauTPmLYbjoHAnrAwFJAjfs8Z0QjlO6qf5WpBAA";
    public static  String consumer_secret = "cCTqMq8lY69jLLvNIY6n5IWFQb8nD2hbEJA7AG8DdPQGfekgNW";
    public static  String token = "BmUdLjlMriPaA8fFRYkMiMj9cUWy58NjQ6IZGvp5YXMH1Cv73Z";
    public static  String token_secret = "8dMatYVgfiOBdwf2pHD0ma0wh4szkB9oL90zBBZmPPvVE0OyYe";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SurfaceView cameraPreview;

    private Camera camera;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);

        mHandler = new Handler();

        AsyncTask<Void, Void, ArrayList<PhotoPost>> task = new AsyncTask<Void, Void, ArrayList<PhotoPost>>() {
            @Override
            protected ArrayList<PhotoPost> doInBackground(Void... params) {
                // Create a new client
                JumblrClient client = new JumblrClient(consumer_key, consumer_secret);
                client.setToken(token, token_secret);

                Blog blog = client.blogInfo("theweatherlab.tumblr.com");
                ArrayList<PhotoPost> finalArray = new ArrayList<>();
                for (Post post : blog.posts()) {
                    if (post.getClass().equals(PhotoPost.class)){
                        finalArray.add((PhotoPost)post);
                    }
                }

                return finalArray;
            }

            @Override
            protected void onPostExecute(ArrayList<PhotoPost> result) {
                PostAdapter adapter = new PostAdapter(result);
                //MainActivity.this.gvPosts.setAdapter(adapter);
                // specify an adapter (see also next example)
                mRecyclerView.setAdapter(adapter);
                //mHandler.post(scrollRunnable);
            }
        };

        task.execute();


    }


    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            mRecyclerView.scrollBy(0, 100);
            mHandler.postDelayed(this, 100);
        }
    };


}
