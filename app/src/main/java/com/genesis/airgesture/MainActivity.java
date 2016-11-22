package com.genesis.airgesture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;

public class MainActivity extends AppCompatActivity implements CameraGestureSensor.Listener {

    private static final String TAG = "MainActivity";
    CameraGestureSensor mGestureSensor = null;
    private JavaCameraView mCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mGestureSensor = new CameraGestureSensor(MainActivity.this);
                    CameraGestureSensor.loadLibrary();
                    mGestureSensor.addGestureListener(MainActivity.this);
                    mGestureSensor.start(mCamera);
                }
                break;
                default:
                    Log.i(TAG, "Some other result than success");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrieveControls();
        loadOpenCV();
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
        if (mGestureSensor == null)
            mGestureSensor = new CameraGestureSensor(MainActivity.this);
        mGestureSensor.start(mCamera);
    }

    protected void retrieveControls()
    {
        mCamera = (JavaCameraView)findViewById(R.id.camera);
    }

    @Override
    public void onGestureUp(CameraGestureSensor caller, long gestureLength) {
        Log.i(TAG, "Up");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Hand Motion Up", Toast.LENGTH_SHORT).show();
            }
        });    }

    @Override
    public void onGestureDown(CameraGestureSensor caller, long gestureLength) {
        Log.i(TAG, "Down");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Hand Motion Down", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
        Log.i(TAG, "Left");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Hand Motion Left", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
        Log.i(TAG, "RIght");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Hand Motion Right", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
