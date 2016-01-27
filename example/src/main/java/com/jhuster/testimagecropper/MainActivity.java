package com.jhuster.testimagecropper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.jhuster.imagecropper.CropImageActivity;
import com.jhuster.imagecropper.CropIntent;

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

    public static final String CROPPED_IMAGE_FILEPATH = "/sdcard/cropped.jpg";

    private ImageView mImageView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    mImageView = (ImageView)findViewById(R.id.CroppedImageView);
    }

    public void onClickButton(View v) {
        startCropImage();
    }

    private void startCropImage() {

        // Create a CropIntent
        CropIntent intent = new CropIntent();

        // Set the source image filepath/URL and output filepath/URL (Optional)
        //intent.setImagePath("/sdcard/source.jpg");
        intent.setOutputPath(CROPPED_IMAGE_FILEPATH);

        // Set a fixed crop window size (Optional)
        //intent.setOutputSize(640,480);

        // set the max crop window size (Optional)
        //intent.setMaxOutputSize(800,600);

        // Set a fixed crop window's width/height aspect (Optional)
        //intent.setAspect(3,2);

        // start ImageCropper activity with certain request code and listen for result
        startActivityForResult(intent.getIntent(this), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 0) {
            Uri croppedUri = data.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
            try {
                InputStream in = getContentResolver().openInputStream(croppedUri);
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
