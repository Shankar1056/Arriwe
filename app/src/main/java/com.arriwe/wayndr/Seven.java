package com.arriwe.wayndr;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.IndeterminateDrawable;
import com.arriwe.utility.SMSReciever;
import com.arriwe.utility.Utils;
import com.sancsvision.arriwe.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class Seven extends AppCompatActivity {

    public static Context context;
    CircleImageView circleImageView;
    public static Bitmap bitmap;
    TextView phoneNOTextView;
    EditText textView_mobno;
    private static final String TAG = "Seven.java";
    ProgressDialog dialog = null;
    TextView vefry_number;
    Timer timer;
    ImageView imageView;
    IndeterminateDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SMSReciever.verifyNoContext = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_seven);
        context = this;


        Log.e("In Seven Activity", "*************************");

        imageView = (ImageView) findViewById(R.id.img_indtr);
        drawable = IndeterminateDrawable.newInstance(this);
        startLoader();

        circleImageView = (CircleImageView) findViewById(R.id.rounded_imgview);
        phoneNOTextView = (TextView) findViewById(R.id.mob_no_textview);
        textView_mobno = (EditText) findViewById(R.id.textView_mobno);
        vefry_number = (TextView) findViewById(R.id.vefry_number);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bitmap = (Bitmap) (getIntent().getParcelableExtra(Constants.k_BITMAP_IMG));
            phoneNOTextView.setText((String) getIntent().getStringExtra(Constants.k_MOB_NO));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleImageView.setTransitionName(getString(R.string.image_animation));
        }
        if (bitmap != null) {
            circleImageView.setImageBitmap(bitmap);
           // imageView.setVisibility(View.GONE);
        } else {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String path = prefs.getString(getResources().getString(R.string.path_profile_pic), "");

            Picasso.with(this)
                    .load(path)
                    .placeholder(R.drawable.user_new)
                    .into(circleImageView);
           // imageView.setVisibility(View.GONE);

        }


        startAnimation();


        textView_mobno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (textView_mobno.getText().toString().equals((String) getIntent().getStringExtra(getResources().getString(R.string.key_otp)))) {

                        Log.e("API passed Otp:",textView_mobno.getText().toString());
                        callApiForNoVerification(textView_mobno.getText().toString());
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
        // animation();

        
    }

    private void startAnimation() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        //  myTextBox.setText("my text");
                        if (vefry_number.getText().toString().equals("VERIFYING YOUR NUMBER..")) {
                            vefry_number.setText("VERIFYING YOUR NUMBER  ");
                        } else if (vefry_number.getText().toString().equals("VERIFYING YOUR NUMBER. ")) {
                            vefry_number.setText("VERIFYING YOUR NUMBER..");
                        } else if (vefry_number.getText().toString().equals("VERIFYING YOUR NUMBER..")) {
                            vefry_number.setText("VERIFYING YOUR NUMBER  ");
                        } else if (vefry_number.getText().toString().equals("VERIFYING YOUR NUMBER  ")) {
                            vefry_number.setText("VERIFYING YOUR NUMBER..");
                        }
                    }
                });

            }
        }, 0, 500);

    }

    void startLoader() {
        imageView.setImageDrawable(drawable);
        drawable.start();
    }

    void stopLoader() {
        drawable.stop();
        imageView.setVisibility(View.GONE);
    }

    void animation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    circleImageView.setColorFilter(0xFFffc000, PorterDuff.Mode.MULTIPLY);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            circleImageView.clearColorFilter();
                            // startActivity(new Intent(context, Eight.class));
                        }
                    }, 100);
                }
            });
        } else {
            circleImageView.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //    startActivity(new Intent(context, Eight.class));
                        }
                    }, 100);

                    // Pass all events through to the host view.
                    return false;
                }
            });

        }
    }

    public void otpResponse(NetworkDataModel model) throws JSONException {
        Log.e(TAG, "otpResponse" + model.responseData.toString());

        JSONObject jsonObject = new JSONObject(model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }


        Log.e("result seven*****************:", model.requestData.toString());

        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
//        String res = map.get(getResources().getString(R.string.key_result)).toString();
        String res;
        try
        {
            res = jsonObject.getString("status");
        }
        catch (Exception e)
        {
            res = jsonObject.getString("result");
        }

        Log.e("result seven:", res);
        if (res.equals(getResources().getString(R.string.key_Success))) {


            final RequestQueue requestQueue = Volley.newRequestQueue(Seven.this);

            final Map<String, String> params = new HashMap<String, String>();

            params.put("mobileNo",Constants.mobileNo);

            CustomRequest jsObjRequest2 = new CustomRequest(Request.Method.POST, "http://35.163.45.85/index.php?r=api/phone-info", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jObj) {

                    try {



                        String status = jObj.getString("status");
                        if (status.equals("Success")) {

                            final JSONObject subObject = jObj.getJSONObject("data");
                            String completeURl = Constants.DEV_IMG_BASE_URL + subObject.getString("profile_pic");
                            Log.e("completeURl:", completeURl);

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Seven.this);
                            SharedPreferences.Editor editor2 = prefs.edit();
                            editor2.putString(getString(R.string.path_profile_pic), completeURl);
                            editor2.putString("name", subObject.getString("name"));
                            editor2.putString(getString(R.string.key_reg_no), Constants.mobileNo);
                            editor2.putString(getString(R.string.key_user_id), subObject.getString("user_id"));
                            editor2.putBoolean(getResources().getString(R.string.key_is_user_logged_in), true);
                            editor2.apply();

                            Log.e("User ID:-------------",subObject.getString("user_id"));

                            Intent i = new Intent(Seven.this, Eight.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();

                        } else {

                            Toast.makeText(Seven.this, "Error", Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
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





        } else {
            Toast.makeText(this, getResources().getString(R.string.str_otp_not_matching), Toast.LENGTH_SHORT).show();

        }
    }

    public void callApiForNoVerification(String otp) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");
//        String requestString = "mobileNo=" + URLEncoder.encode("9016030889", "utf-8") + "&otp=" + URLEncoder.encode(otp, "utf-8");

        String requestString;
        String otpurl;
        if (Constants.islogin) {
            otpurl = "login";
            requestString = "mobileNo=" + URLEncoder.encode(phoneNOTextView.getText().toString(), "utf-8") + "&otp_code="+URLEncoder.encode(otp, "utf-8");
            Log.e(TAG, "Json obj to be processed is in verifyNo " + requestString);
        } else {
            otpurl = "otpconfirm";
            requestString = "mobileNo=" + URLEncoder.encode(phoneNOTextView.getText().toString(), "utf-8") + "&otp=" + URLEncoder.encode(otp, "utf-8");
            Log.e(TAG, "Json obj to be processed is in verifyNo " + requestString);
        }

        NetworkEngine engine = new NetworkEngine();
        engine.verifyNo(requestString, this, otpurl);
    }
}
