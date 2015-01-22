/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, ticktick <lujun.hust@gmail.com>
 *  https://github.com/Jhuster/ImageCropper
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    CropImageView.java
 *  @brief   Draw ImageView and CropWindow
 *  
 *  @version 1.0     
 *  @author  ticktick
 *  @date    2015/01/09    
 */
package com.ticktick.imagecropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ticktick.imagecropper.CropImageActivity.CropParam;
import com.ticktick.imagecropper.TouchEventDetector.TouchEventListener;

public class CropImageView extends View implements TouchEventListener {
    
    private static final float CROP_WINDOW_PAINTER_WIDTH = 3.0f;
    private static final float OUTSIDE_WINDOW_PAINTER_WIDTH = 1.0f;
    private static final float DRAG_ICONS_RADIUS = 10.0f;
	
    private Paint mCropPainter;
    private Paint mOutsidePainter;
    
    private Bitmap mOriginBitmap;
    private RotateBitmap mCropBitmap;    
    private Matrix mMarix = new Matrix();
    
    private CropParam mCropParam;
    private CropWindow mCropWindow;
    private boolean mIsCropParamChanged = true;    

    private float mScaleRate = (float)1.0;
    private TouchEventDetector mTouchEventDetector = new TouchEventDetector();
    
    private Drawable[] mDragDrawables = { getResources().getDrawable(R.drawable.ic_crop_drag_x), 
            getResources().getDrawable(R.drawable.ic_crop_drag_y),
            getResources().getDrawable(R.drawable.ic_crop_drag_x),
            getResources().getDrawable(R.drawable.ic_crop_drag_y)};    

    public CropImageView(Context context) {
        super(context);    
        createPainter();
    }
    
    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createPainter();
    }    
    
    public void destroy() {
    	if( mCropBitmap != null && !mCropBitmap.getBitmap().isRecycled() ) {
            mCropBitmap.recycle();
            mCropBitmap = null;
    	}
    	if( mOriginBitmap !=null && !mOriginBitmap.isRecycled() ) {
            mOriginBitmap.recycle();
            mOriginBitmap = null;
    	}
    }
    
    private void createPainter() { 
    	
        mCropPainter = new Paint();
        mCropPainter.setAntiAlias(true);
        mCropPainter.setStyle(Style.STROKE);
        mCropPainter.setStrokeWidth(CROP_WINDOW_PAINTER_WIDTH);
        mCropPainter.setColor(Color.YELLOW);                    
        
        mOutsidePainter = new Paint();
        mOutsidePainter.setAntiAlias(true);
        mOutsidePainter.setStyle(Style.FILL);
        mOutsidePainter.setARGB(125, 50, 50, 50);
        mOutsidePainter.setStrokeWidth(OUTSIDE_WINDOW_PAINTER_WIDTH);
    }
    
    public void initialize( Bitmap bitmap ) {
    	initialize(bitmap,0,new CropParam());
    }
    
    public void initialize( Bitmap bitmap, CropParam param ) {
    	initialize(bitmap,0,param);
    }
    
    public void initialize( Bitmap bitmap, int degrees ) {
    	initialize(bitmap,degrees,new CropParam());
    }    
    
    public void initialize( Bitmap bitmap, int degrees, CropParam param ) {
    	mCropParam = param;
    	mOriginBitmap = bitmap;    	
    	replace(bitmap,degrees);
    }

    public Bitmap getCropBitmap() {
    	if( mCropBitmap != null ) {
            return mCropBitmap.getBitmap();
    	}
    	return null;
    }
    
    public void rotate() {
    	if( mCropBitmap != null ) {
            mCropBitmap.setRotation(mCropBitmap.getRotation()+90);
            mIsCropParamChanged = true;
            invalidate();
    	}    	
    }
       
    public void crop() {
    	
    	if( mCropBitmap != null ) {
    		
            float cropWidth  = mCropWindow.width()/mScaleRate;
            float cropHeight = mCropWindow.height()/mScaleRate;
        	
            Rect cropRect = mCropWindow.getWindowRect(mScaleRate); 
            RectF dstRect = new RectF(0, 0,cropWidth,cropHeight);
            
            Matrix cropMatrix = new Matrix();
            cropMatrix.setRectToRect(new RectF(cropRect), dstRect, Matrix.ScaleToFit.FILL);
            cropMatrix.preConcat(mCropBitmap.getRotateMatrix());
                        
            Bitmap cropped = Bitmap.createBitmap((int)cropWidth,(int)cropHeight,Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(cropped);
            canvas.drawBitmap(mCropBitmap.getBitmap(),cropMatrix, null);
            
            replace(cropped,0);
    	}    	
    }
    
    public void reset() {
    	if( mCropBitmap == null ) {
            return;
    	}
    	replace(mOriginBitmap,0);
    }
    
    private void replace( Bitmap bitmap, int degrees ) {
    	if( mCropBitmap != null && mCropBitmap.getBitmap() != mOriginBitmap ) {
            mCropBitmap.recycle();
    	}
    	mCropBitmap = new RotateBitmap(bitmap,degrees);
    	mIsCropParamChanged = true;
    	invalidate();
    }
    
    private void calculateCropParams( RotateBitmap bitmap ) {

    	mScaleRate = Math.min((float)getWidth()/bitmap.getWidth(),(float)getHeight()/bitmap.getHeight());
    	    	
    	float offsetX = (getWidth()-bitmap.getWidth()*mScaleRate)/2;
    	float offsetY = (getHeight()-bitmap.getHeight()*mScaleRate)/2;
    	
    	mMarix.reset();
    	mMarix.postConcat(bitmap.getRotateMatrix());
    	mMarix.postScale(mScaleRate,mScaleRate);
    	mMarix.postTranslate(offsetX,offsetY);
    	
    	RectF border = new RectF(offsetX,offsetY,offsetX+bitmap.getWidth()*mScaleRate,offsetY+bitmap.getHeight()*mScaleRate );    
    	
    	CropParam param = new CropParam();
    	param.mAspectX = mCropParam.mAspectX;
    	param.mAspectY = mCropParam.mAspectY;    	
    	param.mOutputX = (int)(mCropParam.mOutputX*mScaleRate);
    	param.mOutputY = (int)(mCropParam.mOutputY*mScaleRate);
    	param.mMaxOutputX = (int)(mCropParam.mMaxOutputX*mScaleRate);
    	param.mMaxOutputY = (int)(mCropParam.mMaxOutputY*mScaleRate);
    	
    	mCropWindow = new CropWindow(border,param);
    	
    	mTouchEventDetector.setTouchEventListener(this);    	
    }
    
    private void drawOutsideCropArea( Canvas canvas ) {
    	RectF[] rects = mCropWindow.getOutWindowRects();
    	for( RectF rect : rects ) {
            canvas.drawRect(rect,mOutsidePainter);
    	}        
    }
    
    private void drawDragIcons(Canvas canvas) {       
    	Point[] points = mCropWindow.getDragPoints();
    	for( int i=0; i<points.length; i++ ) {
            mDragDrawables[i].setBounds((int)(points[i].x-DRAG_ICONS_RADIUS),(int)(points[i].y-DRAG_ICONS_RADIUS), (int)(points[i].x+DRAG_ICONS_RADIUS),(int)(points[i].y+DRAG_ICONS_RADIUS));
            mDragDrawables[i].draw(canvas); 				
    	}    	
    }  

    @Override
    protected void onDraw(Canvas canvas) {                         	
        canvas.save();       
        if( mCropBitmap != null ) {
            if( mIsCropParamChanged ) {
            	calculateCropParams(mCropBitmap);
            	mIsCropParamChanged = false;
            }               
            canvas.drawBitmap(mCropBitmap.getBitmap(), mMarix, mCropPainter);       
            canvas.drawRect(mCropWindow.getWindowRectF(),mCropPainter);
            drawOutsideCropArea(canvas);
            drawDragIcons(canvas);
        }                      
        canvas.restore();        
        super.onDraw(canvas);
    }
    
    public boolean onTouchEvent(MotionEvent event) {
    	if( mCropBitmap != null ) {
            return mTouchEventDetector.onTouchEvent(event);	
    	}    	
    	return true;
    }
        
    @Override
    public void onTouchMoved(float srcX, float srcY, float deltaX, float deltaY) {		
	mCropWindow.onTouchMoved(deltaX, deltaY);
	invalidate();				
    }

    @Override
    public void onTouchDown(float x, float y) {
	mCropWindow.onTouchDown(x, y);
    }

    @Override
    public void onTouchUp(float x, float y) {
	mCropWindow.onTouchUp();
    }   
}
