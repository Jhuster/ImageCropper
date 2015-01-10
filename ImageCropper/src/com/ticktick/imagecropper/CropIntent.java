/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, ticktick <lujun.hust@gmail.com>
 *  http://ticktick.blog.51cto.com/
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    CropIntent.java
 *  @brief   Builder for Image Cropper Intent
 *  
 *  @version 1.0     
 *  @author  ticktick
 *  @date    2015/01/09    
 */
package com.ticktick.imagecropper;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class CropIntent {
	
    public static final String ASPECT_X   = "aspectX";
    public static final String ASPECT_Y   = "aspectY";
    public static final String OUTPUT_X   = "outputX";
    public static final String OUTPUT_Y   = "outputY"; 
    public static final String MAX_OUTPUT_X  = "maxOutputX";
    public static final String MAX_OUTPUT_Y  = "maxOutputY"; 
    
    private Intent mCropIntent = new Intent();
    
    public void setImagePath( String filepath ) {
    	setImagePath(Uri.fromFile(new File(filepath)));
    }
    
    public void setImagePath( Uri uri ) {
    	mCropIntent.setData(uri);
    }
    
    public void setOutputPath( String filepath ) {
    	setOutputPath(Uri.fromFile(new File(filepath)));
    }
    
    public void setOutputPath( Uri uri) {
    	mCropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    }
    
    public void setAspect( int x, int y ) {
    	mCropIntent.putExtra(ASPECT_X, x);
    	mCropIntent.putExtra(ASPECT_Y, y);
    }
    
    public void setOutputSize( int x, int y ) {
    	mCropIntent.putExtra(OUTPUT_X, x);
    	mCropIntent.putExtra(OUTPUT_Y, y);
    }
    
    public void setMaxOutputSize( int x, int y ) {
    	mCropIntent.putExtra(MAX_OUTPUT_X, x);
    	mCropIntent.putExtra(MAX_OUTPUT_Y, y);
    }
        
    public Intent getIntent(Context context) {
    	mCropIntent.setClass(context,CropImageActivity.class);
    	return mCropIntent;
    }    
}
