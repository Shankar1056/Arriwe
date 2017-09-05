package com.arriwe.wayndr.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.arriwe.cropper.CropImage;
import com.arriwe.cropper.CropImageActivity;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.AboutUs;
import com.arriwe.wayndr.CustomRequest;
import com.arriwe.wayndr.Eight;
import com.arriwe.wayndr.LaunchActivity;
import com.arriwe.wayndr.PrivacyPolicy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sancsvision.arriwe.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class Settings extends Activity implements View.OnClickListener {

    private static final String TAG = "Settings.java";

    private Uri outputFileUri;
    Bitmap finalBitmap = null;
    RoundedImageView taggedImgView;
    EditText nameTV;
    EditText mobNOTV;
    ProgressDialog dialog = null;
    TextView updateBtn = null;
//    TextView button_delete = null;
TextView button_InActive = null;
    TextView button_logout = null;

    TextView about_us = null;
    TextView my_current_location = null;
    TextView privacy_terms = null;
    ImageView button_cancel_trup;
    ImageView name_edt;
    SwitchCompat switch_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        initUI();

        updateBtn = (TextView) findViewById(R.id.button_update);
        privacy_terms = (TextView) findViewById(R.id.privacy_terms);
        about_us = (TextView) findViewById(R.id.about_us);
//        button_delete = (TextView) findViewById(R.id.button_delete);
        button_InActive= (TextView) findViewById(R.id.button_InActive);
        button_logout = (TextView) findViewById(R.id.button_logout);
        button_cancel_trup = (ImageView) findViewById(R.id.button_cancel_trup);
        name_edt = (ImageView) findViewById(R.id.name_edt);
        updateBtn.setOnClickListener(this);
//        button_delete.setOnClickListener(this);

        button_InActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InActiveAccount();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    logoutAccount();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        button_cancel_trup.setOnClickListener(this);
        name_edt.setOnClickListener(this);
        about_us.setOnClickListener(this);
        privacy_terms.setOnClickListener(this);

        //my_current_location.setText();
        nameTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    nameTV.setCursorVisible(false);
                    View view = Settings.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });


    }

    void initUI() {
        taggedImgView = (RoundedImageView) findViewById(R.id.pickImageBtn);
        nameTV = (EditText) findViewById(R.id.textView_name_bold);
        mobNOTV = (EditText) findViewById(R.id.textView_mob);
        switch_location = (SwitchCompat) findViewById(R.id.switch_location);
        my_current_location = (TextView) findViewById(R.id.my_current_location);

        taggedImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String regNo = prefs.getString(getString(R.string.key_reg_no), "");
        String name = prefs.getString(getString(R.string.key_name), "");
        String city_status = prefs.getString(getString(R.string.key_city_status), "");
        if (city_status.equals("") || city_status.equals("on")) {
            switch_location.setChecked(true);
            my_current_location.setText(LocationService.LATEST_LOC_CITY);
        } else {
            switch_location.setChecked(false);
            my_current_location.setText("unknown");
        }

        switch_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    my_current_location.setText(LocationService.LATEST_LOC_CITY);
                else
                    my_current_location.setText("unknown");

                changeStatus(b);
            }
        });
        Log.d("name", name);
        int index = 0;

        int start = index;
        int length = 3;
            /*String replaceStr = name.substring(start, start + length);
            name = name.replace( replaceStr,"");
            name = name.replace(",","");
             */
        nameTV.setText(name);
        nameTV.setSelection(nameTV.getText().length());
        mobNOTV.setText(regNo);
        String path = prefs.getString(getResources().getString(R.string.path_profile_pic), "");


        Log.e("path:", path);
        Picasso.with(this)
                .load(path)
                .into(taggedImgView);

//        try {
////            Bitmap b= Utils.getBitmapFromPath(this, path);
//
//            Picasso.with(this)
//                    .load(path)
//                    .into(taggedImgView);
//
////            taggedImgView.setImageBitmap(b);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        Picasso.with(Settings.this)
                .load(path)
                .into(taggedImgView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        finalBitmap = ((BitmapDrawable) taggedImgView.getDrawable()).getBitmap();
                    }

                    @Override
                    public void onError() {

                    }
                });
        if (((BitmapDrawable) taggedImgView.getDrawable()) != null) {
            finalBitmap = ((BitmapDrawable) taggedImgView.getDrawable()).getBitmap();
        }


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


    //Callbacks
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.CROPPED_PIC_REQUEST_CODE) {
                CropImage.ActivityResult result = (CropImage.ActivityResult) data.getExtras().get(CropImage.CROP_IMAGE_EXTRA_RESULT);
                Uri selectedImageUri = result == null ? null : result.getUri();
                Bitmap bitmap = null;
                Log.d("SetUpProfile", "Uri cropped is " + outputFileUri);
                bitmap = getBitmap(selectedImageUri);
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                taggedImgView.setImageBitmap(bitmap);
                finalBitmap = bitmap;
            } else if (requestCode == Constants.YOUR_SELECT_PICTURE_REQUEST_CODE) {
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


                Intent i = new Intent(Settings.this, CropImageActivity.class);
                i.putExtra("ImageURI", selectedImageUri.toString());
                startActivityForResult(i, Constants.CROPPED_PIC_REQUEST_CODE);
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

    private void submitBtnAction() throws JSONException {
        HashMap map = Utils.validateRegForm(finalBitmap, String.valueOf(nameTV.getText()), String.valueOf(mobNOTV.getText()));
        Boolean res = (Boolean) map.get(Constants.k_VALIDATION_RES);
//          Boolean res = true;
        String reason = (String) map.get(Constants.k_FAIL_REASON);
        if (res) {
            callApiForSignUp(String.valueOf(nameTV.getText()), String.valueOf(mobNOTV.getText()), finalBitmap);
        } else {
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
        }

    }

    private void deleteAccount() throws JSONException {

        Log.e("Mobile:", String.valueOf(mobNOTV.getText()) + "");
        callApiForDeleteAccoun(String.valueOf(mobNOTV.getText()));
    }


    private void InActiveAccount() throws JSONException {

        Log.e("Mobile:", String.valueOf(mobNOTV.getText()) + "");
        callApiForInActiveAccount(String.valueOf(mobNOTV.getText()));
    }

    // APICalls
    private void logoutAccount() throws JSONException {

        Log.e("Mobile:", String.valueOf(mobNOTV.getText()) + "");
        callApiForLogoutAccoun(String.valueOf(mobNOTV.getText()));
    }


    void callApiForInActiveAccount(final String phoneNo) throws JSONException {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");

        Log.e("url:", Constants.DEV_BASE_URL + "user-status-change");
        Ion.with(Settings.this)
                .load(Constants.DEV_BASE_URL + "user-status-change")
                .setBodyParameter(getResources().getString(R.string.key_mob_no), phoneNo)
                .setBodyParameter("status", "inactive")
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                dialog.dismiss();

                Log.e("phoneNo:", phoneNo + "");

                Log.e("result>>>>",result);
                if (e != null) {
                    e.printStackTrace();
                }
                if (result != null) {
                    try {
                        Log.d(TAG, result);
                        JSONObject jsonObject = new JSONObject(result);
                        logoutUserResponce(jsonObject);
                    } catch (Exception e1) {
                        Log.e(TAG, e1.toString());
                    }
                  }
            }
        });
    }


    void callApiForLogoutAccoun(final String phoneNo) throws JSONException {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(Settings.this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logout Successfull", Toast.LENGTH_SHORT).show();

    }


    void callApiForDeleteAccoun(final String phoneNo) throws JSONException {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");
        /*JSONObject jsonObject = new JSONObject();
        jsonObject.put(getResources().getString(R.string.key_mob_no),phoneNo);
        Log.i(TAG, "Json delete"+jsonObject.toString());
       // Json delete{"mobileNo":"9587655086"}
        NetworkEngine engine = new NetworkEngine();
        engine.deleteAccount(jsonObject.toString(),this);*/

        Log.e("url:", Constants.DEV_BASE_URL + Constants.Api_Delete_Acccount);
        Ion.with(Settings.this)
                .load(Constants.DEV_BASE_URL + Constants.Api_Delete_Acccount)
                .setBodyParameter(getResources().getString(R.string.key_mob_no), phoneNo)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                dialog.dismiss();

                Log.e("phoneNo:", phoneNo + "");
                if (e != null) {
                    e.printStackTrace();
                }
                if (result != null) {
                    try {
                        Log.d(TAG, result);
                        JSONObject jsonObject = new JSONObject(result);
                        deleteUserResponce(jsonObject);
                    } catch (Exception e1) {
                        Log.e(TAG, e1.toString());
                    }
                }
            }
        });
    }


    //APICalls
    void callApiForSignUp(String name, String phoneNo, Bitmap image) throws JSONException {

        Log.e("name:", name);
        Log.e("mobileNo:", phoneNo);
        Log.e("photo:", image.toString());

        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("mobileNo", phoneNo);
        jsonObject.put("photo", Utils.getBase64String(image));
//        Log.e(TAG, "Json obj to be processed is "+jsonObject.toString());
//        NetworkEngine engine = new NetworkEngine();
//        engine.updateProfile(jsonObject.toString(), this);


        final RequestQueue requestQueue = Volley.newRequestQueue(Settings.this);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("mobileNo", phoneNo);
        params.put("photo", Utils.getBase64String(image));

        Log.e("URL:", "http://35.163.45.85/index.php?r=api/register");
        Log.e("Passed Parameters:", params.toString());

        CustomRequest jsObjRequest2 = new CustomRequest(Request.Method.POST, "http://35.163.45.85/index.php?r=api/update-profile", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jObj) {

                try {

                    Log.e("volly update service>>>", jObj.toString());

                    String status = jObj.getString("result");
                    if (status.equals("Success")) {


                        Toast.makeText(Settings.this, "Updated Successully", Toast.LENGTH_SHORT).show();


                        final RequestQueue requestQueue22 = Volley.newRequestQueue(Settings.this);

                        final Map<String, String> params = new HashMap<String, String>();

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);

                        params.put("mobileNo", prefs.getString(getString(R.string.key_reg_no),""));

                        CustomRequest jsObjRequest2 = new CustomRequest(Request.Method.POST, "http://35.163.45.85/index.php?r=api/phone-info", params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jObj) {

                                try {


                                    Log.e("after update>>>>>>>", jObj.toString());
                                    String status = jObj.getString("status");
                                    if (status.equals("Success")) {

                                        final JSONObject subObject = jObj.getJSONObject("data");
                                        String completeURl = Constants.DEV_IMG_BASE_URL + subObject.getString("profile_pic");
                                        Log.e("completeURl:", completeURl);

                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                                        SharedPreferences.Editor editor2 = prefs.edit();
                                        editor2.putString(getString(R.string.path_profile_pic), completeURl);
                                        editor2.putString("name", subObject.getString("name"));
                                        editor2.putString(getString(R.string.key_reg_no), subObject.getString("mobile"));
                                        editor2.putString(getString(R.string.key_user_id), subObject.getString("user_id"));
                                        editor2.putBoolean(getResources().getString(R.string.key_is_user_logged_in), true);
                                        editor2.apply();

                                        Log.e("User ID:-------------", subObject.getString("user_id"));
                                        dialog.dismiss();

                                    } else {
                                        dialog.dismiss();

                                        Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    dialog.dismiss();
                                    Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                dialog.dismiss();
                                Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
                                Log.e("volly error:", error.toString());

                            }
                        });

                        requestQueue22.add(jsObjRequest2);


                    } else {

                        dialog.dismiss();
                        Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("volly error:", error.toString());

            }
        });

        requestQueue.add(jsObjRequest2);


    }

    /*
        API Callbacks for delete profile
    */

    public void logoutUserResponce(JSONObject jsonObject) throws JSONException {
        /*Log.i(TAG,"profile "+model.requestData.toString());
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        if(model.responseFailed){
            Toast.makeText(this,model.error,Toast.LENGTH_SHORT).show();
            return;
        }*/
        Log.e("jsonObject:", jsonObject.toString());
        String result = jsonObject.getString("result");
        String msg = jsonObject.getString("msg");
        if (!result.equalsIgnoreCase("Failure")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(Settings.this, LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    public void deleteUserResponce(JSONObject jsonObject) throws JSONException {
        /*Log.i(TAG,"profile "+model.requestData.toString());
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        if(model.responseFailed){
            Toast.makeText(this,model.error,Toast.LENGTH_SHORT).show();
            return;
        }*/
        Log.e("jsonObject:", jsonObject.toString());
        String result = jsonObject.getString("result");
        String msg = jsonObject.getString("msg");
        if (!result.equalsIgnoreCase("Failure")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(Settings.this, LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    /*
        API Callbacks for update profile
     */
    public void signUpResponse(NetworkDataModel model) throws JSONException {
        Log.e(TAG, "update profile " + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e("model:>>>>>>>...", model.toString());

        Utils.saveBitmapToCache(this, finalBitmap);
        //mark user as logged in
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.key_name), nameTV.getText().toString());

        editor.apply();
        finish();
        Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.button_delete:
//                try {
//                    deleteAccount();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;


            case R.id.button_update:
                try {
                    submitBtnAction();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_cancel_trup:
                onBackPressed();
                break;
            case R.id.name_edt:
                nameTV.setFocusable(true);
                nameTV.setCursorVisible(true);
                nameTV.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(nameTV, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.about_us:
                Intent intent = new Intent(Settings.this, AboutUs.class);
                startActivity(intent);
                break;
            case R.id.privacy_terms:
               /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://spoonwebs.com/spoon/privacy-policy.html"));
                startActivity(browserIntent);*/
                Intent intentprivacy = new Intent(Settings.this, PrivacyPolicy.class);
                startActivity(intentprivacy);
                break;

        }
    }

    private void changeStatus(boolean status) {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");
        String msg = "";
        if (status)
            msg = "on";
        else
            msg = "off";
        final String finalMsg = msg;
        Ion.with(Settings.this)
                .load(Constants.DEV_BASE_URL + Constants.Api_chnage_city_status)
                .setBodyParameter("mobile", String.valueOf(mobNOTV.getText()))
                .setBodyParameter("status", msg)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                dialog.dismiss();
                if (e != null) {
                    Log.e(TAG, e.toString());
                }
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
                            //  Toast.makeText(Settings.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(getString(R.string.key_city_status), finalMsg);
                            editor.apply();
                        } else {
                            Toast.makeText(Settings.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        Log.e(TAG, e1.toString());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Settings.this, Eight.class);
        intent.putExtra("setfragment", "three");
        startActivity(intent);
        finish();
        // overridePendingTransition(R.anim.slide_down, R.anim.stay);
    }
}
