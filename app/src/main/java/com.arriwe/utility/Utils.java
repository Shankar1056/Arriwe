package com.arriwe.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.arriwe.networkmanagers.NetworkEngine;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sancsvision.arriwe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Abhi1 on 10/08/15.
 */
public class Utils{

    public  static String TAG = "Utils.java";

    public static String getUniqueImageName(){
        //will generate a random num
        //between 15-10000
        Random r = new Random();
        int num = r.nextInt(10000 - 15) + 15;
        String fileName = "img_"+num+".png";
        return  fileName;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static String getBase64String(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return  encoded;
    }

    public  static Bitmap getBitmapFromBase64(String base64string){
        byte[] imageAsBytes = Base64.decode(base64string.getBytes(), Base64.DEFAULT);
//        ImageView image = (ImageView)this.findViewById(R.id.ImageView);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return  bitmap;
    }

    public static Dialog LoadingSpinner(Context mContext){
        Dialog pd = new Dialog(mContext, android.R.style.Theme_Black);
        View view = LayoutInflater.from(mContext).inflate(R.layout.aux_progress_spinner, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);

        pd.setContentView(view);
        pd.setCancelable(false);
        return pd;
    }


    public  static  ProgressDialog  showProgressDialog(Context context,String string){


        ProgressDialog progDialog = ProgressDialog.show(context, null, null, false, true);
        progDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progDialog.setContentView(R.layout.progressbar);
        progDialog.setCancelable(false);


//        ProgressDialog progress = new ProgressDialog(context, R.style.CustomProgressDialog);
//        progress.getWindow().setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams params = progress.getWindow().getAttributes();
////        params.y = 150;
//        progress.getWindow().setAttributes(params);
//
//
////        if(string.length() == 0)
////            progress.setMessage("Please wait while the request is processing...");
////        else
//            progress.setMessage("");
//        progress.setIndeterminate(true);
//        progress.setCancelable(false);
//        progress.show();
        return progDialog;
    }

    public static HashMap<String,Object> jsonToMap(String t) throws JSONException {

        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }

        System.out.println("json : " + jObject);
        System.out.println("map : " + map);

        return  map;
    }

    public  static void saveBitmapToCache(Context context,Bitmap bitmap){
        // Create a path pointing to the system-recommended cache dir for the app, with sub-dir named
        // as path_profile_pic
        File cacheDir = new   File(context.getCacheDir(), context.getResources().getString(R.string.path_profile_pic));
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        // Create a path in that dir for a file, named by the default hash of the url
        File cacheFile = new File(cacheDir, "userProfilePic.png");
        Log.i("", "Caching image at path " + cacheFile.getAbsolutePath());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getResources().getString(R.string.path_profile_pic), String.valueOf(cacheFile.getAbsolutePath()));
        editor.commit();

        try {
            // Create a file at the file path, and open it for writing obtaining the output stream
            cacheFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(cacheFile);
            // Write the bitmap to the output stream (and thus the file) in PNG format (lossless compression)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            // Flush and close the output stream
            fos.flush();
            fos.close();
        } catch (Exception e) {
            // Log anything that might go wrong with IO to file
            Log.e("", "Error when saving image to cache. ", e);
        }
    }

    public  static Bitmap getBitmapFromPath(Context context,String fromPath) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(fromPath);
        Bitmap local = BitmapFactory.decodeStream(fis);
        Log.d("", "Found bitmap is " + local);
        return  local;
    }

    public  static String getBase64StringFromPath(Context context,String fromPath){
        Bitmap bm = BitmapFactory.decodeFile(fromPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //compress at max
        bm.compress(Bitmap.CompressFormat.JPEG, 30, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return  encodedImage;

    }

    public static ArrayList<String> getContactsFromPhone(Context context){
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        ArrayList<String> list = new ArrayList<String>();
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            list.add(phoneNumber);
            Log.d(TAG,"Name is "+ name + " Phone no is"+ phoneNumber);
//            Toast.makeText(context, name, Toast.LENGTH_LONG).show();
        }
        phones.close();
        return  list;
    }
    public static HashMap<String,String> getContactsFromPhoneHashmap(Context context){
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        HashMap<String,String> list = new HashMap<>();
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phoneNumber.contains("+91"))
                    phoneNumber = phoneNumber.replace("+91","");
            list.put(phoneNumber,name);
            Log.d(TAG,"Name is "+ name + " Phone no is"+ phoneNumber);
//            Toast.makeText(context, name, Toast.LENGTH_LONG).show();
        }
        phones.close();
        return  list;
    }

    public static void pushLocations(String mobNo,String lat,String lon,Context context,String city) throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(context)){
//            Toast.makeText(context, context.getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }

        String requestString = "mobileNo="+ URLEncoder.encode(mobNo, "utf-8")+"&lat="+URLEncoder.encode(lat, "utf-8")+"&lon="+URLEncoder.encode(lon, "utf-8")+"&city="+URLEncoder.encode(city, "utf-8");

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(context.getResources().getString(R.string.key_mob_no),mobNo);
//        jsonObject.put(context.getResources().getString(R.string.key_lat),lat);
//        jsonObject.put(context.getResources().getString(R.string.key_long),lon);
//        Toast.makeText(context,"Pushing location",Toast.LENGTH_SHORT).show();
        NetworkEngine engine = new NetworkEngine();
        engine.pushUsersLocationToServer(requestString, context);


    }
    public static void pushCityLocations(String mobNo,String lat,String lon,Context context) throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(context)){
//            Toast.makeText(context, context.getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }

        String requestString = "mobileNo="+ URLEncoder.encode(mobNo, "utf-8")+"&lat="+URLEncoder.encode(lat, "utf-8")+"&lon="+URLEncoder.encode(lon, "utf-8");

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(context.getResources().getString(R.string.key_mob_no),mobNo);
//        jsonObject.put(context.getResources().getString(R.string.key_lat),lat);
//        jsonObject.put(context.getResources().getString(R.string.key_long),lon);
//        Toast.makeText(context,"Pushing location",Toast.LENGTH_SHORT).show();
        NetworkEngine engine = new NetworkEngine();
        engine.pushUsersLocationToServer(requestString, context);
    }

    public static void markTripAsArriwed(String reqString,Context context) throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(context)){
            Toast.makeText(context, context.getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(context,"Marking as arrwied",Toast.LENGTH_SHORT).show();
        NetworkEngine engine = new NetworkEngine();
        engine.markTripAsArriwed(reqString, context);
    }
    //Validators
    public static HashMap validateRegForm(Bitmap b, String name,String number){
        Boolean result = true;
        String errReason = Constants.VALIDATION_SUCCESS;
        HashMap map = new HashMap();
        if((b == null) || b.getByteCount() == 0){
            result = false;
            errReason = Constants.INVALID_IMG;
        }
        else if(name == null || !isValidName(name)){
            result = false;
            errReason = Constants.INVALID_NAME;
        }
        else if(number == null || !isValidPhoneNo(number)){
            result = false;
            errReason = Constants.INVALID_NO;
        }

        map.put(Constants.k_VALIDATION_RES, result);
        map.put(Constants.k_FAIL_REASON, errReason);

        return  map;
    }

    static Boolean isValidPhoneNo(String phoneNo){
        Boolean res = true;
        //keeping 12 digit for mobile no including country code
        String str = "^\\d{1,12}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(phoneNo);
        res = m.find();
        return  res;
    }

    static Boolean isValidName(String name){
        Boolean res = true;
        //keeping min 3 char and max 40 for name
        String str = "^[a-zA-Z' ']{3,40}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(name);
        res = m.find();
        return  res;
    }

    public static void logLargeString(String str) {

        if(str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else
            Log.i(TAG, str);
    }

    public  static  void pushGCMToken(String token,Context context) throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(context)){
//            Toast.makeText(context, context.getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("gcm code",token);
//        jsonObject.put(getResources().getString(R.string.key_mob_no),phoneNo);
//        jsonObject.put(getResources().getString(R.string.key_profile_photo), Utils.getBase64String(image));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mobNO =  prefs.getString(context.getString(R.string.key_reg_no), "");
        String deviceId = Utils.getDeviceId(context);
        String requestString = "mobileNo="+ URLEncoder.encode(mobNO, "utf-8")+"&token="+URLEncoder.encode(token, "utf-8")+"&device="+URLEncoder.encode(deviceId, "utf-8")+"&udid="+URLEncoder.encode("0", "utf-8");

        Log.i(TAG, "Json obj to be processed in sendRegistrationToServer "+requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.pushGCMTokenToServer(requestString, context);
    }

    public  static String calculateTimeDiffFromNow(String time) throws ParseException {
//        time = "04/03/2016 05:10:10";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setLenient(false);

//        SimpleDateFormat currDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        Date prevTime = sdf.parse(time);
        long prevMillisecond = prevTime.getTime();
//        long pm = prevMillisecond + 3600000;
        long diff = date.getTime() - prevMillisecond;
        String hms = String.format("%02d hr :%02d min :%02d sec", TimeUnit.MILLISECONDS.toHours(diff),
                TimeUnit.MILLISECONDS.toMinutes(diff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
                TimeUnit.MILLISECONDS.toSeconds(diff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));

        String valToReturn;


        //show only minutes
        if(diff <= 1000*60*60){
            long val = diff/(1000*60);
            valToReturn = val+" Min";
        }
        //show only seconds
        else if(diff <= 1000*60){
            long val = diff/1000;
            valToReturn = val+" Sec";
        }

        //show only hours
        else{
            long val = diff/(1000*60*60);
            valToReturn = val+" Hours";
        }

        Log.i("Utils.java", "Time diff is " + hms);
        return valToReturn;

    }
    public  static String calculateTimeDiffFromNowdifferentFormat(String time) throws ParseException {
//        time = "04/03/2016 05:10:10";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sdf.setLenient(false);

//        SimpleDateFormat currDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        Date prevTime = sdf.parse(time);
        long prevMillisecond = prevTime.getTime();
//        long pm = prevMillisecond + 3600000;
        long diff = date.getTime() - prevMillisecond;
        String hms = String.format("%02d hr :%02d min :%02d sec", TimeUnit.MILLISECONDS.toHours(diff),
                TimeUnit.MILLISECONDS.toMinutes(diff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
                TimeUnit.MILLISECONDS.toSeconds(diff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));

        String valToReturn;


        //show only minutes
        if(diff <= 1000*60*60){
            long val = diff/(1000*60);
            valToReturn = val+" Min";
        }
        //show only seconds
        else if(diff <= 1000*60){
            long val = diff/1000;
            valToReturn = val+" Sec";
        }

        //show only hours
        else{
            long val = diff/(1000*60*60);
            valToReturn = val+" Hours";
        }

        Log.i("Utils.java", "Time diff is " + hms);
        return valToReturn;

    }

    static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public  static void addStringToSharedPref(String key,String value,Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public  static String getStringForKey(String key,Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return  prefs.getString(key,"");
    }


    public static String changeDateTimeFormat(String existingDateTime){
        SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = null;
        try
        {
            date = form.parse(existingDateTime);
        }
        catch (ParseException e)
        {

            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("MMMMMMM dd,yyyy HH:mm");
        String newDateStr = postFormater.format(date);
        return  newDateStr;
    }


    public static void checkGPSStatus(Context context){
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
          //  buildAlertMessageNoGps(context);
        }
    }

    public static Boolean gpsStatus(Context context){
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return false;
        }
        return  true;
    }

    private static void buildAlertMessageNoGps(final Context context) {

//        ContextThemeWrapper ctw = new ContextThemeWrapper(context, THEME_HOLO_LIGHT);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        final  Context con = context;
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public   static Bitmap cropBitmap(Bitmap bitmap){
//        Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.xyz);
        Bitmap bmp = null;
        bmp=Bitmap.createBitmap(bitmap, 0,0,140, 140);

        return  bmp;
    }

    public static HashMap reverseGeocode(double lat,double lon, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        HashMap map = new HashMap();
        map.put("address",address+" "+city);
        map.put("name",knownName);
        map.put("city",city);
        return  map;

    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth() , bitmap.getHeight() );

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static void drawPolylines( GoogleMap googleMap,ArrayList<LatLng>latLngArrayList){
//        Polyline line = googleMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(12.9716, 77.5946), new LatLng(18.5204, 73.8567))
//                .width(25)
//                .color(Color.YELLOW));
//

        Polyline line = googleMap.addPolyline(new PolylineOptions().addAll(latLngArrayList)
                .width(10)
                .color(Color.BLUE));
    }
}