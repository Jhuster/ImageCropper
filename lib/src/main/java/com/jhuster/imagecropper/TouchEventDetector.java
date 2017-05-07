/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, Jhuster <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/ImageCropper
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    TouchEventDetector.java
 *  @brief   Detect the touch event
 *  
 *  @version 1.0     
 *  @author  Jhuster
 *  @date    2015/01/09    
 */
package com.jhuster.imagecropper;

import android.graphics.PointF;
import android.view.MotionEvent;

public class TouchEventDetector {

    private static final float DETECT_THRESHOLD = (float) 0.05;

    private PointF mPoint = new PointF(0, 0);
    private TouchEventListener mTouchEventListener;
    private boolean mIsDetectStarted = false;

    public interface TouchEventListener {
        void onTouchDown(float x, float y);
        void onTouchUp(float x, float y);
        void onTouchMoved(float srcX, float srcY, float deltaX, float deltaY);
    }

    public void setTouchEventListener(TouchEventListener listener) {
        mTouchEventListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchEventListener == null || event.getPointerCount() != 1) {
            mIsDetectStarted = false;
            return false;
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchEventListener.onTouchDown(event.getX(), event.getY());
            mIsDetectStarted = true;
        } else if (action == MotionEvent.ACTION_UP) {
            mTouchEventListener.onTouchUp(event.getX(), event.getY());
            mIsDetectStarted = false;
        } else if (mIsDetectStarted && action == MotionEvent.ACTION_MOVE) {
            if (Math.abs(mPoint.x - event.getX()) > DETECT_THRESHOLD || Math.abs(mPoint.y - event.getY()) > DETECT_THRESHOLD) {
                mTouchEventListener.onTouchMoved(mPoint.x, mPoint.y, event.getX() - mPoint.x, event.getY() - mPoint.y);
            }
        }

        mPoint.set(event.getX(), event.getY());

        return true;
    }
}
