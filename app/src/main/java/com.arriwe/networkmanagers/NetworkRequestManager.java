package com.arriwe.networkmanagers;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.arriwe.utility.Constants;
import com.arriwe.wayndr.LoginActivity;
import com.arriwe.wayndr.Six;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Abhi1 on 19/08/15.
 */
public class NetworkRequestManager extends AsyncTask<Object,Void,Object> {
    NetworkAbstract callbackObj;
    ProgressDialog dialog;
    public static  final  String TAG = "NetworkRequestManager";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute called");
//        dialog= Utils.showProgressDialog(Wayndr.getContext());
    }

    @Override
    protected Object doInBackground(Object[] param) {
        Log.d(TAG, "doInBackground called");
        NetworkDataModel model = (NetworkDataModel)param[0];
        URL oURL = null;
        try {
//            String url = URLEncoder.encode(Constants.DEV_BASE_URL,"UTF-8");
//            Log.d(TAG,"UrL is "+url);
            String urlStr = Constants.DEV_BASE_URL+model.apiUrl;

//            Log.e(TAG+"urlStr:",urlStr);
            oURL = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
            con.setRequestMethod(model.getHttpMethodType());
            con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
//            con.setRequestProperty("SOAPAction", urlStr);
            OutputStream reqStream = con.getOutputStream();
            String data = (String)model.requestData;

            reqStream.write(data.getBytes());
            reqStream.flush();reqStream.close();

            InputStream resStream ;
            if(con.getResponseCode()<=400){
                resStream=con.getInputStream();
//                Log.e(TAG, "Successfull Response code "+con.getResponseCode());
//                byte[] byteBuf = new byte[10240];
//                int len = resStream.read(byteBuf);
                BufferedReader rd = new BufferedReader(new InputStreamReader(resStream));
                String line;
                StringBuffer response = new StringBuffer();

                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                model.responseData = response;
                model.responseFailed = false;
//                Log.e(TAG, "Response message:"+response);

            }else{
                  /* error from server */
                resStream = con.getErrorStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(resStream));
                String line;
                StringBuffer errResponse = new StringBuffer();

                while((line = rd.readLine()) != null) {
                    errResponse.append(line);
                    errResponse.append('\r');
                }
                rd.close();
                model.responseFailed = true;
                model.error = errResponse.toString();
//                Log.e(TAG, "Error Response occured"+ errResponse);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        NetworkDataModel model = (NetworkDataModel) o;

        String strToFind = model.apiUrl.replace(Constants.DEV_BASE_URL,"");
        if(model.responseData!=null) {
//            Log.e(TAG, "onPostExecute called " + model.responseData.toString());
            String responseString = model.responseData.toString();
            try {
                notifyCaller(strToFind,model);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    void  notifyCaller(String string,NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        if(string.equals(Constants.Api_Register)) {
            if(model.currentContext instanceof Six)
                callbackObj.onSignUpResponse((NetworkDataModel) model);
           else
                callbackObj.onUpdateProfileResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Verify_No)){
            callbackObj.onOTPVerificationResponse((NetworkDataModel) model);
        }
        else if(string.equals("login")){
            callbackObj.onOTPVerificationResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Wayndr_reg_users)){
            callbackObj.onRegisteredContactResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Tagged_Travel_Users)){
            callbackObj.onTaggedTravellingWithResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Get_User_Activities)){
            callbackObj.onUserActivitesResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Update_Status)){
            callbackObj.onUpdateStatusResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Cancel_Trip)){
            callbackObj.onCancelTripResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Arriwe_Trip)){
            callbackObj.onTripArriwedResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Clear_Trip)){
            callbackObj.onTripClearedResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Req_Loc)){
            callbackObj.onLocationRequestedResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Reject_Loc_Req)){
            callbackObj.onLocationRequestRejectedResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Share_Loc)){
            callbackObj.onLocationSharedResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Reject_Rcv_Loc)){
            callbackObj.onSharedLocDeletedResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Get_Nearby_Contacts)){
            callbackObj.onNearbyFriendsResponse((NetworkDataModel) model);
        }

        else if(string.equals(Constants.Api_Get_Track)){
            callbackObj.onUsersJourneyPointsResponse((NetworkDataModel) model);
        }
        else if(string.equals(Constants.Api_Delete_Acccount)){
            callbackObj.onUserAccountDelete((NetworkDataModel) model);
        }
    }
}

class ParseXML {
    public String parse(InputStream in, String strToMatch) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser, strToMatch);
        } finally {
            in.close();
        }
    }

    private String readFeed(XmlPullParser parser, String strToMatch) throws XmlPullParserException, IOException {
        String resString = null;
        final String ns = null;
        String value = null;
        String name = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    break;
                case XmlPullParser.TEXT:
                    resString = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (name.equals(strToMatch)) {
                        value = resString;
//                        Log.i("TAG", " REsponse is " + value);

                    }
                    break;

                default:
                    Log.i("TAG", "Went in default case " );
                    break;
            }
            eventType = parser.next();
        }
        return value;
    }
}