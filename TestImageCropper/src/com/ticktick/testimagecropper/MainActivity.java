package com.ticktick.testimagecropper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.ticktick.imagecropper.CropImageActivity;
import com.ticktick.imagecropper.CropIntent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_PICK_IMAGE = 0x1;
    public static final int REQUEST_CODE_IMAGE_CROPPER  = 0x2;
    public static final String CROPPED_IMAGE_FILEPATH = "/sdcard/test.jpg";    
    private ImageView mImageView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	mImageView = (ImageView)findViewById(R.id.CroppedImageView);
    }

    public void onClickButton(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_PICK_IMAGE);
    }

    public void startCropImage( Uri uri ) {
    	Intent intent = new Intent(this,CropImageActivity.class);
    	intent.setData(uri);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(CROPPED_IMAGE_FILEPATH)));
    	//intent.putExtra("aspectX",2);
    	//intent.putExtra("aspectY",1);
    	//intent.putExtra("outputX",320);
    	//intent.putExtra("outputY",240);
    	//intent.putExtra("maxOutputX",640);
    	//intent.putExtra("maxOutputX",480);
    	startActivityForResult(intent, REQUEST_CODE_IMAGE_CROPPER);
    }
    
    public void startCropImageByCropIntent( Uri uri ) {    	
    	CropIntent intent = new CropIntent();
    	intent.setImagePath(uri);
    	intent.setOutputPath(CROPPED_IMAGE_FILEPATH);
    	//intent.setAspect(2, 1);
    	//intent.setOutputSize(480,320);
    	//intent.setMaxOutputSize(480,320);
        startActivityForResult(intent.getIntent(this), REQUEST_CODE_IMAGE_CROPPER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if( requestCode == REQUEST_CODE_PICK_IMAGE ) {
            startCropImage(data.getData());
        }
        else if( requestCode == REQUEST_CODE_IMAGE_CROPPER ) {
        	
            Uri croppedUri = data.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
	
            InputStream in = null;
            try {
		in = getContentResolver().openInputStream(croppedUri);
		Bitmap b = BitmapFactory.decodeStream(in);
		mImageView.setImageBitmap(b);
		Toast.makeText(this,"Crop success，saved at"+CROPPED_IMAGE_FILEPATH,Toast.LENGTH_LONG).show();
            } 
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }        	
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
