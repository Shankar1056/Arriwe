package com.arriwe.wayndr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.arriwe.ServiceHandler;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sancsvision.arriwe.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.arriwe.utility.Constants.mobileNo;


public class LoginActivity extends Activity {
    ProgressDialog dialog = null;
    EditText mobileno;
    ServiceHandler serviceHandler;
    TextView login;
    String url = "http://35.163.45.85/index.php?r=api/phone-info";
    String otpurl = "http://35.163.45.85/index.php?r=api/login";

    String[] mPermission = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WAKE_LOCK, android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.READ_SMS};

    int REQUEST_CODE_PERMISSION = 2;

    String username = "", usermobileNo = "";
    Bitmap userBitmap;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        View rootView = inflater.inflate(R.layout.fragment_two, container, false);


        ActivityCompat.requestPermissions(this,
                mPermission, REQUEST_CODE_PERMISSION);


        context = LoginActivity.this;
        serviceHandler = new ServiceHandler();
        mobileno = (EditText) findViewById(R.id.textViewNo);
        login = (TextView) findViewById(R.id.next_six);


        mobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (mobileno.getText().toString().length() == 10) {

                        mobileNo = mobileno.getText().toString();
                        dialog = Utils.showProgressDialog(LoginActivity.this, "");
                        loginuser(mobileno.getText().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error", e.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mobileno.getText().toString().length() == 10) {
                    loginuser(mobileno.getText().toString());
                } else {
                    Toast.makeText(LoginActivity.this, "Enter valid mobile", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void loginuser(final String mobile) {

        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        final Map<String, String> params = new HashMap<String, String>();

        params.put("mobileNo", mobile);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("response custome:", response.toString());


                try {

                    String status = response.getString("status");
                    if (status.equals("Success")) {

                        final JSONObject subObject = response.getJSONObject("data");
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(getString(R.string.key_reg_no), mobileno.getText().toString());
                        editor.putString(getString(R.string.key_name), subObject.getString("name"));
                        String completeURl = Constants.DEV_IMG_BASE_URL + subObject.getString("profile_pic");
                        Log.e("completeURl:", completeURl);
                        editor.putString(getString(R.string.path_profile_pic), completeURl);
//                        editor.putString(getString(R.string.key_user_id), subObject.getString("user_id"));
                        editor.putString(getString(R.string.push_location), "notdone");
                        editor.apply();

                        try {

                            if (subObject.getString("verified").equals("no")) {
                                Picasso.with(LoginActivity.this)
                                        .load(completeURl)
                                        .into(new Target() {
                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                // loaded bitmap is here (bitmap)
                                                Log.e("Bitmap:", bitmap.toString());

                                                dialog.dismiss();
                                                Constants.islogin = false;
                                                Intent i = new Intent(LoginActivity.this, Six.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                i.putExtra("BitmapImage", bitmap);
                                                try {
                                                    i.putExtra("name", subObject.getString("name"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                i.putExtra("mobileNo", mobileno.getText().toString());
                                                startActivity(i);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });


                            }


                        } catch (Exception e) {
                            Log.e("Error", e.toString());


                            if (subObject.getString("profile_status").equals("inactive")) {
                                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(LoginActivity.this);
                                View mView = layoutInflaterAndroid.inflate(R.layout.activate_account, null);
                                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(LoginActivity.this);


                                alertDialogBuilderUserInput
                                        .setMessage("Your Account is Not Active,You have to activate your account to continue")
                                        .setCancelable(false)
                                        .setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                // ToDo get user input here
                                                if (!Utils.isNetworkConnected(LoginActivity.this)) {
                                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                dialog = Utils.showProgressDialog(LoginActivity.this, "");

                                                Log.e("url:", Constants.DEV_BASE_URL + "user-status-change");
                                                try {
                                                    Ion.with(LoginActivity.this)
                                                            .load(Constants.DEV_BASE_URL + "user-status-change")
                                                            .setBodyParameter(getResources().getString(R.string.key_mob_no), subObject.getString("mobile_no"))
                                                            .setBodyParameter("status", "active")
                                                            .asString().setCallback(new FutureCallback<String>() {
                                                        @Override
                                                        public void onCompleted(Exception e, String result) {
                                                            dialog.dismiss();

                                                            if (e != null) {
                                                                e.printStackTrace();
                                                            }
                                                            if (result != null) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(result);
                                                                    Log.e(" result result>>>>", result);
                                                                    if (jsonObject.getString("result").equals("Success")) {

                                                                        Toast.makeText(LoginActivity.this, "Activated", Toast.LENGTH_SHORT).show();
                                                                        CustomRequest jsObjRequest2 = new CustomRequest(Request.Method.POST, otpurl, params, new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                try {
                                                                                    Log.e("Otp:>>>>>>>>>>>>>>>>>..", response.getString("otp"));
                                                                                    String status = response.getString("status");
                                                                                    if (status.equals("Success")) {

                                                                                        dialog.dismiss();
                                                                                        Constants.islogin = true;
                                                                                        Intent i = new Intent(LoginActivity.this, Seven.class);
                                                                                        i.putExtra("otp", response.getString("otp"));
                                                                                        i.putExtra(Constants.k_MOB_NO, mobileno.getText().toString());
                                                                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                        startActivity(i);
                                                                                        overridePendingTransition(R.anim.enter, R.anim.exit);

                                                                                    } else {

                                                                                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                } catch (JSONException e) {

                                                                                    Log.e("JSONException:", e.toString());
                                                                                    e.printStackTrace();
                                                                                }


                                                                            }
                                                                        }, new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {

                                                                                Log.e("volly error:", error.toString());

                                                                            }
                                                                        });

                                                                        requestQueue.add(jsObjRequest2);

                                                                    }


                                                                } catch (JSONException e1) {
                                                                    e1.printStackTrace();
                                                                }

                                                            }


                                                        }
                                                    });
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }

                                            }
                                        })

                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogBox, int id) {
                                                        dialogBox.cancel();
                                                        dialog.dismiss();
                                                    }
                                                });

                                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                alertDialogAndroid.show();
                            } else {
                                CustomRequest jsObjRequest2 = new CustomRequest(Request.Method.POST, otpurl, params, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Log.e("Otp:>>>>>>>>>>>>>>>>>..", response.getString("otp"));
                                            String status = response.getString("status");
                                            if (status.equals("Success")) {

                                                dialog.dismiss();
                                                Constants.islogin = true;
                                                Intent i = new Intent(LoginActivity.this, Seven.class);
                                                i.putExtra("otp", response.getString("otp"));
                                                i.putExtra(Constants.k_MOB_NO, mobileno.getText().toString());
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);

                                            } else {

                                                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {

                                            Log.e("JSONException:", e.toString());
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.e("volly error:", error.toString());

                                    }
                                });

                                requestQueue.add(jsObjRequest2);

                            }


                        }


                    } else {

                        Constants.islogin = false;

                        Intent i = new Intent(LoginActivity.this, Six.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("mobileNo", mobileno.getText().toString());
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }


                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsObjRequest);


    }


    public Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            // Success Stuff heres
        }
    }


    private void submitBtnAction(Bitmap bitmap, String name, String mobileNo) throws JSONException, UnsupportedEncodingException {
        HashMap map = Utils.validateRegForm(bitmap, name, mobileNo);
        Boolean res = (Boolean) map.get(Constants.k_VALIDATION_RES);
//          Boolean res = true;
        String reason = (String) map.get(Constants.k_FAIL_REASON);
        if (res) {
            callApiForSignUp(name, mobileNo, bitmap);

        } else {
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
        }
    }

    void callApiForSignUp(String name, String phoneNo, Bitmap image) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");
        String base64Img = Utils.getBase64String(image);
        String requestString = "name=" + URLEncoder.encode(name, "utf-8") + "&mobileNo=" + URLEncoder.encode(phoneNo, "utf-8") + "&photo=" + URLEncoder.encode(base64Img, "utf-8");

        Log.e("requestString:", requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.signUp(requestString, this);
    }

    public void signUpResponse(NetworkDataModel model) throws JSONException {

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT);
            return;
        }

        Log.e("Response Login:", model.responseData.toString());
        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        String res = map.get(getResources().getString(R.string.key_result)).toString();
        if (res.equals("Failure")) {
            Toast.makeText(this, map.get("msg").toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        String otp = map.get(getResources().getString(R.string.key_otp)).toString();

        Log.e("OTP>>>>>>>>>>>>>>>>>>>", otp);

        Intent i = new Intent(LoginActivity.this, Seven.class);
        i.putExtra(Constants.k_NAME, username);
        i.putExtra(Constants.k_MOB_NO, usermobileNo);
        i.putExtra(Constants.k_BITMAP_IMG, userBitmap);
        i.putExtra(getResources().getString(R.string.key_otp), otp);

        startActivity(i);


        overridePendingTransition(R.anim.enter, R.anim.exit);


        // startActivity(i);

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















