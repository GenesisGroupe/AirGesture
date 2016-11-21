package com.genesis.airgesture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;

public class MainActivity extends AppCompatActivity implements CameraGestureSensor.Listener {

    CameraGestureSensor mGestureSensor = new CameraGestureSensor(this);
    private boolean mOpenCVInitiated;
    /**
     * OpenCV library initialization.
     */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mOpenCVInitiated = true;
                    CameraGestureSensor.loadLibrary();
                    mGestureSensor.start();     // your main gesture sensor object

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this, mLoaderCallback);
        mGestureSensor.addGestureListener(this);
    }

    @Override
    public void onGestureUp(CameraGestureSensor caller, long gestureLength) {
        Toast.makeText(this, "UP", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGestureDown(CameraGestureSensor caller, long gestureLength) {
        Toast.makeText(this, "DOWN", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
        Toast.makeText(this, "LEFT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
        Toast.makeText(this, "RIGHT", Toast.LENGTH_SHORT).show();
    }
}
