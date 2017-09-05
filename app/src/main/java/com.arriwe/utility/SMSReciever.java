package com.arriwe.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import com.arriwe.wayndr.Seven;


/**
 * Created by abhinandans on 25/07/2016.
 */
public class SMSReciever extends BroadcastReceiver {
    private static final String TAG = "SMSReciever";
    public static Seven verifyNoContext = null;
    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;

    @Override
    public void onReceive(Context context, Intent intent) {
        //  Toast.makeText(context, "all hua", Toast.LENGTH_SHORT).show();
        Bundle bundle = null;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        currentSMS = getIncomingMessage(aObject, bundle);

                        String senderNo = currentSMS.getDisplayOriginatingAddress();

                        if (!senderNo.equalsIgnoreCase(Constants.SMS_ORIGIN) && !senderNo.equalsIgnoreCase(Constants.SMS_ORIGIN1) && !senderNo.equalsIgnoreCase(Constants.SMS_ORIGIN2))
                            return;

                        message = currentSMS.getDisplayMessageBody();

                        message = message.replace("- ", "-");

                        Log.e("message:", message);
                        String code = getVerificationCode(message, context);
                        try {
                            Log.e("from Reciever:", code);
                            Log.e("code length:", code.length() + "");

                            verifyNoContext.callApiForNoVerification(code);


                        } catch (JSONException e) {
                            Log.e("SMSReciver", e.toString());
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            Log.e("SMSRecivermain", e.toString());
                            e.printStackTrace();
                        }
//                        Toast.makeText(OtpActivity.this, "senderNum: " + senderNo + " :\n message: " + message, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "String message " + code);


                    }
                    this.abortBroadcast();
                    // End of loop
                }
            }
        } // bundle null
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message, Context context) {
        //Toast.makeText(context, "getvarification", Toast.LENGTH_SHORT).show();
        String code = null;
        int index = message.indexOf("-");

        if (index != -1) {
            int start = index + 1;
            int length = 6;

            code = message.substring(start, start + length);

            return code;
        }
        return code;
    }
}
