package com.arriwe.wayndr.util;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Eight;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sancsvision.arriwe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class ShareLocation extends Activity implements com.google.android.gms.maps.OnMapReadyCallback, LocationListener,GoogleMap.OnMapClickListener,View.OnClickListener {

    private static String TAG = "ShareLocation.java";

    GoogleMap googleMap;
    TextView sendLocBtn;
    ProgressDialog dialog = null;
    ImageView imageView_cross;
    int pos;
    private String mapscreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten);

        sendLocBtn = (TextView) findViewById(R.id.button2);
        imageView_cross = (ImageView)findViewById(R.id.imageView_cross);
        imageView_cross.setOnClickListener(this);
        sendLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callShareLocationApi();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        initMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.checkGPSStatus(this);
    }

    void initMap() {
//        final RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragment);
        googleMap = mapFragment.getMap();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        mapFragment.getMapAsync(this);
    }


    void callShareLocationApi() throws JSONException, UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.err_no_network),Toast.LENGTH_LONG).show();
            return;
        }
        dialog= Utils.showProgressDialog(this,"");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("mobileNo",mobNO);
//        jsonObject.put("guid",guid);
//        jsonObject.put("status",status);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String mobNo = prefs.getString(getString(R.string.key_reg_no), "");
        String reqLocId = getIntent().getStringExtra("loc_id");
        String fromMobNo = getIntent().getStringExtra("reciever_mobile_no");
        if(reqLocId == null){
            reqLocId = "0";
        }

        if(fromMobNo == null){
            fromMobNo = "0";
        }

        String lat = null;
        String lon = null;
        if (LocationService.LATEST_LOC_OBJ != null) {
            Location loc = LocationService.LATEST_LOC_OBJ;
            lat = String.valueOf(loc.getLatitude());
            lon = String.valueOf(loc.getLongitude());


            Log.e("lat>>>>>>.",lat);
            Log.e("lon>>>>>>.",lon);


        }
        String requestString = null;
        try {
             requestString = "mobileNo=" + URLEncoder.encode(mobNo, "utf-8") + "&request_location_id=" + URLEncoder.encode(reqLocId, "utf-8") + "&lat=" + URLEncoder.encode(lat, "utf-8") + "&lon=" + URLEncoder.encode(lon, "utf-8") + "&fromMobileNo=" + URLEncoder.encode(fromMobNo, "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.i(TAG, "Json obj to be processed is "+requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.shareYourLocation(requestString, this);
    }
    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap map = googleMap;

        String lat = "null";
        String lon = "null";
     // map.setMyLocationEnabled(true);
        if (LocationService.LATEST_LOC_OBJ != null) {
            Location loc = LocationService.LATEST_LOC_OBJ;
            lat = String.valueOf(loc.getLatitude());
            lon = String.valueOf(loc.getLongitude());
        }
        float currLat = 0;
        float currLong = 0;


        if (!(lat.equals("null"))) {
            currLat = Float.parseFloat(lat);
        }
        if (!(lon.equals("null"))) {
            currLong = Float.parseFloat(lon);
        }

        final LatLng currLatLng = new LatLng(currLat, currLong);

//            googleMap.addMarker(new MarkerOptions()
//                    .position(currLatLng));
            final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currLatLng, 16);
            googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    //move the marker to top of screen
                }

                @Override
                public void onCancel() {

                }
            });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                capturescreen(map);

            }
        },2000);


        }

    /***
     * API Callbacks
     * @param map
     */

    private void capturescreen(GoogleMap map) {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {

            @Override
            public void onSnapshotReady(Bitmap snapshot)
            {
                // TODO Auto-generated method stub
                Bitmap bitmap = snapshot;

                OutputStream fout = null;

                String filePath = System.currentTimeMillis() + ".jpeg";

                try
                {
                    fout = openFileOutput(filePath,
                            MODE_WORLD_READABLE);

                    // Write the string to the file
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                    fout.flush();
                    fout.close();
                }
                catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "FileNotFoundException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "IOException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }

               // openShareImageDialog(filePath);
                 mapscreenshot = encodeToBase64(snapshot, Bitmap.CompressFormat.JPEG, 100);
            }
        };

        map.snapshot(callback);
    }
    public void openShareImageDialog(String filePath)
    {
        File file = this.getFileStreamPath(filePath);

        if(!filePath.equals(""))
        {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    return;
                }
                final ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            //DialogUtilities.showOkDialogWithText(this, R.string.shareImageFailed);
        }
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public  void locationSharedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        if(model.responseFailed){
            Toast.makeText(this,model.error,Toast.LENGTH_SHORT);
            return;
        }
        JSONObject obj = new JSONObject(model.responseData.toString());
        String res = obj.getString("result");

        //if(res.equals(getString(R.string.key_Success))){
          //  Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_LONG).show();
        Toast.makeText(this,"Location Shared",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ShareLocation.this,Eight.class);
            intent.putExtra("setfragment","three");
            startActivity(intent);
            //startActivity(new Intent(ShareLocation.this, Eight.class));

            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        /*}
        else{
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ShareLocation.this, Eight.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();

        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_cross:
                Intent intent = new Intent(ShareLocation.this, Eight.class);
                intent.putExtra("setfragment","first");
               // intent.putExtra("pos",Eight.pos);
                startActivity(intent);
               // overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}