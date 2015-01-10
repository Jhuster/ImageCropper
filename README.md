ImageCropper
=========
A custom image cropper library for Android

Features
=========
- Support moving/scale the crop window freely by finger
- Support set a fixed crop window size
- Support set the max crop window size
- Support set a fixed crop window's width/height aspect
- Support rotate the image when cropping
- Easy to integrate to your app

ScreenShot
=========
![Screenshot](https://raw.githubusercontent.com/Jhuster/ImageCropper/master/ImageCropper.jpg)

Building 
=========
- It's a android library project with eclipse
- You should import the project into the eclipse with your main project
- Config the Library Reference to ImageCropper in the main project's properties

Usage
=========
Declare the CropImageActivity in the main project's AndroidManifest.xml
```xml
<activity android:name="com.ticktick.imagecropper.CropImageActivity"/>
```

Declare the write_external_storage permission in the main project's AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

Call these methods to run CropImage Activity
```java
//1. Using the CropIntent to help build the start intent
private void startCropImage() {

    // Create a CropIntent
    CropIntent intent = new CropIntent(); 
    
    // Set the source image filepath/URL and output filepath/URL (Required)
    intent.setImagePath("/sdcard/source.jpg");
    intent.setOutputPath("/sdcard/cropped.jpg");
    
    // Set a fixed crop window size (Optional) 
    intent.setOutputSize(640,480);

    // set the max crop window size (Optional) 
    intent.setMaxOutputSize(800,600);

    // Set a fixed crop window's width/height aspect (Optional) 
    intent.setAspect(3,2);
    
    // start ImageCropper activity with certain request code and listen for result
    startActivityForResult(intent.getIntent(this), REQUEST_CODE_CROP_PICTURE);
}
//2. Create the intent by manual
private void startCropImage() {

    // Create explicit intent
    Intent intent = new Intent(this, CropImageActivity.class);
        
    // Set the source image filepath/URL and output filepath/URL (Required)
    intent.setData(Uri.fromFile(new File("/sdcard/source.jpg")));
    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File("/sdcard/cropped.jpg")));
    
    // Set a fixed crop window size (Optional) 
    intent.putExtra("outputX",640);
    intent.putExtra("outputY",480);

    // set the max crop window size (Optional) 
    intent.putExtra("maxOutputX",800);
    intent.putExtra("maxOutputY",600);

    // Set a fixed crop window's width/height aspect (Optional) 
    intent.putExtra("aspectX",3);
    intent.putExtra("aspectY",2);
    
    // start ImageCropper activity with certain request code and listen for result
    startActivityForResult(intent, REQUEST_CODE_CROP_PICTURE);
}
```

Waiting for result
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode != RESULT_OK) {
        return;
    }

    if (requestCode == REQUEST_CODE_CROP_PICTURE ) {
        Uri croppedUri = data.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);	
        InputStream in = null;
	try {
            in = getContentResolver().openInputStream(croppedUri);
            Bitmap b = BitmapFactory.decodeStream(in);
            mImageView.setImageBitmap(b);
        } 
	catch (FileNotFoundException e) {
            e.printStackTrace();
        }     
    }
    super.onActivityResult(requestCode, resultCode, data);
}
```

Contact
----------
Email：lujun.hust@gmail.com


