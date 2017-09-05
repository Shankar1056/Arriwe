package com.arriwe.wayndr;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.sancsvision.arriwe.R;
import com.arriwe.cropper.CropImageView;


public class CropImage extends Activity {

    CropImageView  cropImageView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        Uri myUri = Uri.parse(getIntent().getExtras().getString("ImageURI"));


//        cropImageView = (CropImageView) findViewById(R.id.imageView_cropper);
//        cropImageView.setImageURI(myUri);
//        cropImageView.setGuidelines(1);
    }
}
