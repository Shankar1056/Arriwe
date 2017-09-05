package com.arriwe.wayndr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sancsvision.arriwe.R;
import com.arriwe.cropper.CropImage;
import com.arriwe.cropper.CropImageActivity;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class SetUpProfile extends Activity  {

    RoundedImageView pickImgBtn;
    Button submitBtn;
    EditText name;
    EditText number;
    ProgressDialog dialog = null;

    private Uri outputFileUri;
    Bitmap finalBitmap = null;
    private  static  final  String TAG = "SetUpProfile.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }

*/        setContentView(R.layout.activity_set_up_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        name = (EditText) findViewById(R.id.textView_mob);
        number = (EditText) findViewById(R.id.textViewNo);
        pickImgBtn = (RoundedImageView) findViewById(R.id.pickImageBtn);
        pickImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
        submitBtn = (Button) findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submitBtnAction();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

//Image picker dialog

    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Wayndr" + File.separator);
        root.mkdirs();
        final String fname = Utils.getUniqueImageName();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("path", String.valueOf(outputFileUri));
        editor.commit();
        Log.e("SetUpProfile","Uri is "+ outputFileUri);


        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, Constants.YOUR_SELECT_PICTURE_REQUEST_CODE);
    }


    //Callbacks
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if(requestCode == Constants.CROPPED_PIC_REQUEST_CODE){
                CropImage.ActivityResult result = (CropImage.ActivityResult) data.getExtras().get(CropImage.CROP_IMAGE_EXTRA_RESULT);
                Uri selectedImageUri = result == null ? null : result.getUri();
                Bitmap bitmap = null;
                Log.d("SetUpProfile","Uri cropped is "+outputFileUri);
                bitmap = getBitmap(selectedImageUri);
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                pickImgBtn.setImageBitmap(bitmap);
                finalBitmap = bitmap;
            }
            else if (requestCode == Constants.YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String value = prefs.getString("path", "error");
                    selectedImageUri = Uri.parse(value);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                Intent i = new Intent(SetUpProfile.this,CropImageActivity.class);
                i.putExtra("ImageURI", selectedImageUri.toString());
                startActivityForResult(i,Constants.CROPPED_PIC_REQUEST_CODE);

            }
        }
    }

    private Bitmap getBitmap(Uri u) {

        Uri uri = u;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 60000; // around 300kb
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("Test", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
//                int height = b.getHeight();
//                int width = b.getWidth();
                int height = 340;
                int width = 340;

                Log.d("Test", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("Test", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("Test", e.getMessage(), e);
            return null;
        }
    }


    private void submitBtnAction() throws JSONException, UnsupportedEncodingException {
        HashMap map = Utils.validateRegForm(finalBitmap, String.valueOf(name.getText()), String.valueOf(number.getText()));
        Boolean res = (Boolean) map.get(Constants.k_VALIDATION_RES);
//          Boolean res = true;
        String reason = (String)map.get(Constants.k_FAIL_REASON);
        if(res){
            callApiForSignUp(String.valueOf(name.getText()), String.valueOf(number.getText()), finalBitmap);
        } else{
            Toast.makeText(this,reason,Toast.LENGTH_LONG).show();
        }

    }


    //APICalls
    void callApiForSignUp(String name,String phoneNo,Bitmap image) throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.err_no_network),Toast.LENGTH_LONG).show();
            return;
        }
        dialog= Utils.showProgressDialog(this,"");
        String base64Img = Utils.getBase64String(image);
        String requestString = "name="+ URLEncoder.encode(name, "utf-8")+"&mobileNo="+URLEncoder.encode(phoneNo, "utf-8")+"&photo="+URLEncoder.encode(base64Img, "utf-8");
        NetworkEngine engine = new NetworkEngine();
        engine.signUp(requestString,this);
    }

    /*
        API Callbacks
     */
    public  void signUpResponse(NetworkDataModel model) throws JSONException {
        Log.e(TAG,"signUpResponse"+model.responseData.toString());
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        if(model.responseFailed){
            Toast.makeText(this,model.error,Toast.LENGTH_SHORT);
            return;
        }
        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        String res = map.get(getResources().getString(R.string.key_result)).toString();
        if(res.equals("Failure")){
            Toast.makeText(this,map.get("msg").toString(),Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = map.get(getResources().getString(R.string.key_otp)).toString();

        Intent i = new Intent(SetUpProfile.this, com.arriwe.wayndr.VerifyNo.class);
        i.putExtra(Constants.k_NAME,String.valueOf(name.getText()));
        i.putExtra(Constants.k_MOB_NO, String.valueOf(number.getText()));
        i.putExtra(Constants.k_BITMAP_IMG, finalBitmap);
        i.putExtra(getResources().getString(R.string.key_otp), otp);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//        }
//        else{
            startActivity(i);
//        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        0).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}