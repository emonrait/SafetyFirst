package com.raihan.safetyfirst;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorListener {
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private SensorManager mSensorMgr;
    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    private long mLastTime;
    //private OnShakeListener mShakeListener;
    private Context mContext;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;


    @Override
    public void onSensorChanged(int i, float[] floats) {

    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }
}
