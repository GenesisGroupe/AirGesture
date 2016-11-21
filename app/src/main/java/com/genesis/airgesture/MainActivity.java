package com.genesis.airgesture;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);


        AsyncTask<Void, Void, ArrayList<PhotoPost>> task = new AsyncTask<Void, Void, ArrayList<PhotoPost>>() {
            @Override
            protected ArrayList<PhotoPost> doInBackground(Void... params) {
                // Create a new client
                JumblrClient client = new JumblrClient(consumer_key, consumer_secret);
                client.setToken(token, token_secret);

                // Write the user's name
                User user = client.user();
                System.out.println(user.getName());

                Blog blog = client.blogInfo("theweatherlab.tumblr.com");
                System.out.println("\t" + blog.getTitle());
                Log.d("AirGesture", "numbers of posts : " + blog.getPostCount());

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

            }
        };

        task.execute();


    }


    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera != null) {
            try {
                camera.reconnect();
            }
            catch(IOException e) {
                e.printStackTrace();;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();;
        if (camera != null) {
            camera.release();;
        }
    }


    private void setupCamera() {
        try {
        int cameraId = findFrontFacingCamera();

        camera = Camera.open(cameraId);
        Camera.Parameters camParams = camera.getParameters();

// Find a preview size that is at least the size of our IMAGE_SIZE
        Camera.Size previewSize = camParams.getSupportedPreviewSizes().get(0);
        for (Camera.Size size : camParams.getSupportedPreviewSizes()) {
            if (size.width >= IMAGE_SIZE && size.height >= IMAGE_SIZE) {
                previewSize = size;
                break;
            }
        }
        camParams.setPreviewSize(previewSize.width, previewSize.height);

// Try to find the closest picture size to match the preview size.
        Camera.Size pictureSize = camParams.getSupportedPictureSizes().get(0);
        for (Camera.Size size : camParams.getSupportedPictureSizes()) {
            if (size.width == previewSize.width && size.height == previewSize.height) {
                pictureSize = size;
                break;
            }
        }
        camParams.setPictureSize(pictureSize.width, pictureSize.height);
        }
        catch (RuntimeException e){
            Toast.makeText(getBaseContext(), "Impossible d'allumer la caméra", Toast.LENGTH_LONG).show();
            Log.e("AirGesture", "error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int findFrontFacingCamera() {

        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        int cameraId = -1;
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
               break;
            }
        }
        return cameraId;
    }

    */

}
