package com.genesis.airgesture;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
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
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        setupCamera();
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


    public void setupCamera() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    0);


            return;
        }
        cameraPreview.getHolder().addCallback(surfaceHolderCallback);
        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
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
        try {
            camera.setPreviewDisplay(cameraPreview.getHolder());
            camera.startPreview();
        }
        catch(IOException e){
            e.printStackTrace();

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "La permission camera n'est pas accordée", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            setupCamera();
        }
    }

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                camera.setPreviewDisplay(cameraPreview.getHolder());
                camera.startPreview();
            } catch (IOException e) {
                Log.e("CAMERA SOURCE", e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
            }
        }
    };


    private Bitmap processImage(byte[] data) throws IOException {
        // Determine the width/height of the image
        int width = camera.getParameters().getPictureSize().width;
        int height = camera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, IMAGE_SIZE, IMAGE_SIZE, true);
        cropped.recycle();

        return scaledBitmap;
    }


}
