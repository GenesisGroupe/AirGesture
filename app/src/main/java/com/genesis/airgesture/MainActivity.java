package com.genesis.airgesture;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.genesis.airgesture.adapters.PostAdapter;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.util.ArrayList;
import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;


public class MainActivity extends Activity implements CameraGestureSensor.Listener{

    private static int LENGTH_CAP = 500;
    private static int IMAGE_SIZE = 1024;
    private static int SCROLL_OFFSET = 10;

    public static  String consumer_key = "KLwOfrGQ80ZBauTPmLYbjoHAnrAwFJAjfs8Z0QjlO6qf5WpBAA";
    public static  String consumer_secret = "cCTqMq8lY69jLLvNIY6n5IWFQb8nD2hbEJA7AG8DdPQGfekgNW";
    public static  String token = "BmUdLjlMriPaA8fFRYkMiMj9cUWy58NjQ6IZGvp5YXMH1Cv73Z";
    public static  String token_secret = "8dMatYVgfiOBdwf2pHD0ma0wh4szkB9oL90zBBZmPPvVE0OyYe";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    //private SurfaceView cameraPreview;


    private Handler mHandler;
    private Camera camera;
    private int currentIndex = 0;

    private static final String TAG = "MainActivity";
    CameraGestureSensor mGestureSensor = null;
    private JavaCameraView mCameraPreview = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                0);


                        return;
                    }


                    CameraGestureSensor.loadLibrary();

                }
                break;
                default:
                    loadOpenCV();
                    Log.i(TAG, "Some other result than success");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                0);

        loadOpenCV();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        //cameraPreview = (SurfaceView) findViewById(R.id.camera);
        mCameraPreview = (JavaCameraView) findViewById(R.id.camera_preview);
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

        mGestureSensor = new CameraGestureSensor(MainActivity.this);
        mGestureSensor.addGestureListener(MainActivity.this);
        mGestureSensor.start(mCameraPreview);

        /*
        //mCameraPreview.getHolder().addCallback(surfaceHolderCallback);
        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
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
            camera.setPreviewDisplay(mCameraPreview.getHolder());
            camera.startPreview();
        }
        catch(IOException e){
            e.printStackTrace();

        }

        */
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

    /*
    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


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

*/
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



    protected void loadOpenCV()
    {

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        }
        else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    public void onPause()
    {
        super .onPause();
        if (mGestureSensor != null)
            mGestureSensor.stop();
    }
    @Override
    public void onResume()
    {
        super .onResume();
        setupCamera();
    }


    @Override
    public void onGestureUp(CameraGestureSensor caller, final long gestureLength) {
        Log.i(TAG, "Up");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "gestureLength = " + gestureLength);
                scrollByOffset(SCROLL_OFFSET, (int)gestureLength);
            }
        });
    }

    @Override
    public void onGestureDown(CameraGestureSensor caller, final long gestureLength) {
        Log.i(TAG, "Down");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "gestureLength = " + gestureLength);
                scrollByOffset(-SCROLL_OFFSET, (int)gestureLength);
            }
        });

    }

    @Override
    public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
        Log.i(TAG, "Left");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setBackgroundColor(1);

            }
        });
    }

    @Override
    public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
        Log.i(TAG, "RIght");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setBackgroundColor(-1);
            }
        });
    }

    private void setBackgroundColor(int direction) {
        int[] rainbow = MainActivity.this.getResources().getIntArray(R.array.backgroundColors);
        currentIndex += direction;

        Log.d(TAG, "rainbow.length : " + rainbow.length  + " current index : " + currentIndex);
        if (currentIndex < 0) {
            currentIndex = rainbow.length -1;
        }
        if (currentIndex > rainbow.length -1) {
            currentIndex = 0;
        }
        int index = currentIndex % rainbow.length;

        mRecyclerView.setBackgroundColor(rainbow[index]);
    }

    private void scrollByOffset(int offset, int gestureLength) {
        int length = (LENGTH_CAP - gestureLength);
        if (length <1) {
            length = 1;
        }
        mRecyclerView.smoothScrollBy(0, (offset*length));
    }
}
