package com.arriwe.wayndr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sancsvision.arriwe.R;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Eight;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class VerifyNo extends Activity {

    RoundedImageView pickImgBtn = null;
    TextView nameTextView, phoneNOTextView;
    EditText verificationCodeEditText = null;
    Button verifyBtn = null;
    ProgressDialog dialog = null;
    Bitmap porifleImg;
    private static final String TAG = "VerifyNo.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // SMSReciever.verifyNoContext = this;
//        // inside your activity (if you did not enable transitions in your theme)
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setExitTransition(new Explode());
//        }

        setContentView(R.layout.activity_verify_no);

        pickImgBtn = (RoundedImageView) findViewById(R.id.rounded_imgview);
        nameTextView = (TextView) findViewById(R.id.name_textview);
        phoneNOTextView = (TextView) findViewById(R.id.mob_no_textview);
        verificationCodeEditText = (EditText) findViewById(R.id.textView_mobno);
        verifyBtn = (Button) findViewById(R.id.submit_button);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verifyBtnClicked();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent intent = getIntent();
        porifleImg = (Bitmap) (intent.getParcelableExtra(Constants.k_BITMAP_IMG));
        pickImgBtn.setImageBitmap(porifleImg);
        nameTextView.setText("Hi " + (String) intent.getStringExtra(Constants.k_NAME) + ",");
        phoneNOTextView.setText((String) intent.getStringExtra(Constants.k_MOB_NO));
    }

    //ButtonActions

    void verifyBtnClicked() throws JSONException, UnsupportedEncodingException {
        if (String.valueOf(verificationCodeEditText.getText()).length() < 4) {
            Toast.makeText(this, Constants.INVALID_VER_CODE, Toast.LENGTH_SHORT).show();
            return;
        }
        callApiForNoVerification(verificationCodeEditText.getText().toString());
    }

    //APICalls
    public void callApiForNoVerification(String otp) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "");


        String otpurl,requestString;
        if(Constants.islogin)
        {
            otpurl="login";
            requestString = "mobileNo=" + URLEncoder.encode(phoneNOTextView.getText().toString(), "utf-8") + "&otp_code=" + URLEncoder.encode(otp, "utf-8");
            Log.e(TAG, "Json obj to be processed is in verifyNo " + requestString);
        }
        else
        {
            otpurl="otpconfirm";
            requestString = "mobileNo=" + URLEncoder.encode(phoneNOTextView.getText().toString(), "utf-8") + "&otp=" + URLEncoder.encode(otp, "utf-8");
            Log.e(TAG, "Json obj to be processed is in verifyNo " + requestString);
        }


        NetworkEngine engine = new NetworkEngine();
        engine.verifyNo(requestString, this,otpurl);
    }

    /*****
     * API Callbacks
     *****/
    public void otpResponse(NetworkDataModel model) throws JSONException {
        Log.i(TAG, "otpResponse" + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        String res = map.get(getResources().getString(R.string.key_result)).toString();
        if (res.equals(getResources().getString(R.string.key_Success))) {
            Utils.saveBitmapToCache(this, porifleImg);
            Intent i = new Intent(VerifyNo.this, Eight.class);
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//            } else {
            startActivity(i);
//            }

            //mark user as logged in
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getResources().getString(R.string.key_is_user_logged_in), true);
            editor.putString(getString(R.string.key_reg_no), phoneNOTextView.getText().toString());
            editor.putString(getString(R.string.key_name), nameTextView.getText().toString());
            editor.putString(getString(R.string.key_user_id), map.get("id").toString());

            editor.apply();

        } else {
            Toast.makeText(this, getResources().getString(R.string.str_otp_not_matching), Toast.LENGTH_SHORT).show();

        }
    }

}
