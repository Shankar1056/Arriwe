package com.arriwe.wayndr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sancsvision.arriwe.R;
import com.arriwe.cropper.*;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class Six extends Activity {

    private Uri outputFileUri;
    Bitmap finalBitmap = null;

    CircleImageView circleImageView;
    Bitmap bitmap;
    EditText textViewNo;
    ProgressDialog dialog = null;
    EditText textView_mob;
    TextView btnlogin;
    private  static  final  String TAG = "Six.java";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.six);
        circleImageView = (CircleImageView) findViewById(R.id.pro_six);
        textView_mob = (EditText)findViewById(R.id.textView_mob);
        btnlogin=(TextView)findViewById(R.id.next_six);

        textViewNo = (EditText)findViewById(R.id.textViewNo);

        textViewNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    try {
                        submitBtnAction();
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });



        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            textViewNo.setText(getIntent().getStringExtra("mobileNo"));
            bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
            textView_mob.setText(getIntent().getStringExtra("name"));


        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleImageView.setTransitionName(getString(R.string.image_animation));
        }
        circleImageView.setImageBitmap(bitmap);
        animation();
    }
    void animation(){
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImageIntent();

            }
        });


    }
    public void go(View view){
        /*startActivity(new Intent(Six.this,Seven.class));
        this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);*/
        try {
            submitBtnAction();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void submitBtnAction() throws JSONException, UnsupportedEncodingException {
        HashMap map = Utils.validateRegForm(bitmap, String.valueOf(textView_mob.getText()), String.valueOf(textViewNo.getText()));
        Boolean res = (Boolean) map.get(Constants.k_VALIDATION_RES);
//          Boolean res = true;
        String reason = (String)map.get(Constants.k_FAIL_REASON);
        if(res){
            callApiForSignUp(String.valueOf(textView_mob.getText()), String.valueOf(textViewNo.getText()), bitmap);
        } else{
            Toast.makeText(this,reason,Toast.LENGTH_LONG).show();
        }
    }
    void callApiForSignUp(String name,String phoneNo,Bitmap image) throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.err_no_network),Toast.LENGTH_LONG).show();
            return;
        }
        dialog= Utils.showProgressDialog(this,"");
        String base64Img = Utils.getBase64String(image);

        String requestString = "name="+ URLEncoder.encode(name, "utf-8")+"&mobileNo="+URLEncoder.encode(phoneNo, "utf-8")+"&photo="+URLEncoder.encode(base64Img, "utf-8");

        Log.e("requestString:",requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.signUp(requestString,this);
    }
    public  void signUpResponse(NetworkDataModel model) throws JSONException {
        Log.i(TAG,"signUpResponse"+model.responseData.toString());
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        if(model.responseFailed){
            Toast.makeText(this,model.error,Toast.LENGTH_SHORT);
            return;
        }

        Log.e("Response signup:",model.responseData.toString());
        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        String res = map.get(getResources().getString(R.string.key_result)).toString();
        if(res.equals("Failure")){
            Toast.makeText(this,map.get("msg").toString(),Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = map.get(getResources().getString(R.string.key_otp)).toString();

        Intent i = new Intent(Six.this,Seven.class);
        i.putExtra(Constants.k_NAME,String.valueOf(textView_mob.getText()));
        i.putExtra(Constants.k_MOB_NO, String.valueOf(textViewNo.getText()));
        i.putExtra(Constants.k_BITMAP_IMG, bitmap);
        i.putExtra(getResources().getString(R.string.key_otp), otp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, circleImageView, getString(R.string.image_animation));
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }

        overridePendingTransition(R.anim.enter, R.anim.exit);


        // startActivity(i);

    }

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
        editor.apply();
        Log.e("SetUpProfile", "Uri is " + outputFileUri);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if(requestCode == Constants.CROPPED_PIC_REQUEST_CODE){
                com.arriwe.cropper.CropImage.ActivityResult result = (com.arriwe.cropper.CropImage.ActivityResult) data.getExtras().get(com.arriwe.cropper.CropImage.CROP_IMAGE_EXTRA_RESULT);
                Uri selectedImageUri = result == null ? null : result.getUri();
                Bitmap bitmap = null;
                Log.d("SetUpProfile","Uri cropped is "+outputFileUri);
                bitmap = getBitmap(selectedImageUri);
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                circleImageView.setImageBitmap(bitmap);
                finalBitmap = bitmap;
                this.bitmap=bitmap;
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
                Bitmap bitmap = null;
                Log.d("SetUpProfile","Uri selected is "+outputFileUri);

                Intent i = new Intent(Six.this,CropImageActivity.class);
                i.putExtra("ImageURI", selectedImageUri.toString());
                startActivityForResult(i,Constants.CROPPED_PIC_REQUEST_CODE);


//                bitmap = getBitmap(selectedImageUri);
////                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
//                circleImageView.setImageBitmap(bitmap);
//                finalBitmap = bitmap;
//                this.bitmap=bitmap;
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
                int height = 240;
                int width = 240;

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
}