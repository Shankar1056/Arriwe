package com.arriwe.utility;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.wayndr.Activities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sancsvision.arriwe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import com.arriwe.networkmanagers.NetworkDataModel;

/**
 * Created by abhinandans on 16/09/15.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationService";
    public final static String LATEST_LOC = "LATEST_LOC";
    public static Location LATEST_LOC_OBJ = null;
    public static String LATEST_LOC_CITY = "";
    public static Activities activityContext = null;
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 3;
    //displacment is given priority over
    //intervals
    private static final long MIN_DISPLACMENT = 5;
    //min distance to mark a destination as arriwed in meters
    private static final long MIN_DIST_FOR_ARRIWED = 50;
    private Context mContext;
    private LocationRequest locationRequest;
    public static GoogleApiClient googleApiClient;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private boolean firsttime = true;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        /*mContext = this;
        getLocation();*/
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mContext = this;
        if (Utils.isNetworkConnected(mContext)) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    getLocation();
                }
            });
            t.start();
        }

        // showPrograss();

    }



  

    private void getLocation() {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);
//            locationRequest.setSmallestDisplacement(MIN_DISPLACMENT);
            fusedLocationProviderApi = LocationServices.FusedLocationApi;
        }
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d(TAG, "Location onConnected ..............: ");
//  Location location = fusedLocationProviderApi.getLastLocation(googleApiClient);
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

        fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }


    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "Location update started ..............: ");
//        Toast.makeText(mContext, "Location :" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    
        if (firsttime)
        {
            doOperation(location);
        }
        scheduler.scheduleAtFixedRate(new Runnable() {
        
            public void run() {
               // Log.d(TAG, "shankar....: ");
    
                doOperation(location);
            
            }
        }, 0, 5, TimeUnit.MINUTES);
    
    
      
            /*final Runnable beeper = new Runnable() {
                public void run() {
                    System.out.println("beep"); }
            };
            final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 0, 5, TimeUnit.MINUTES);
            scheduler.schedule(new Runnable() {
                public void run() { beeperHandle.cancel(true); }
            }, 5, TimeUnit.MINUTES);*/
        
        
        
        
        
      
    }
    
    private void doOperation(final Location location) {
    
        if (Utils.isNetworkConnected(mContext)) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction(LATEST_LOC);
                    intent.putExtra(mContext.getString(R.string.key_loc_changed), location);
                    sendBroadcast(intent);
                }
            });
            t.start();
        }
    
        LocationService.LATEST_LOC_OBJ = location;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String regNo = prefs.getString(getString(R.string.key_reg_no), "");
    
        String res = Utils.getStringForKey(mContext.getString(R.string.key_has_active_trip), mContext);
        String res_push = Utils.getStringForKey(mContext.getString(R.string.push_location), mContext);
        HashMap map = null;
        try {
            if (Utils.isNetworkConnected(mContext)) {
                map = Utils.reverseGeocode(location.getLatitude(), location.getLongitude(), getApplicationContext());
                if (map.get("city") != null) {
                    LATEST_LOC_CITY = map.get("city").toString();
                    sendUserCurrentLocation(map.get("city").toString(), regNo);
                }
            
                if (res_push.equals("notdone")) {
                    if (map.get("city") != null) {
                        Utils.pushLocations(regNo, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), getApplicationContext(), map.get("city").toString());
                    } else {
                        Utils.pushLocations(regNo, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), getApplicationContext(), "");
                    }
                    Utils.addStringToSharedPref(getString(R.string.push_location), "done", this);
                    Utils.addStringToSharedPref(getString(R.string.push_last_location_lat), String.valueOf(location.getLatitude()), this);
                    Utils.addStringToSharedPref(getString(R.string.push_last_location_lng), String.valueOf(location.getLongitude()), this);
                
                }
            }
        
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        //  Toast.makeText(mContext,"Pushing",Toast.LENGTH_SHORT).show();
        // dont push location if there is not
        //active trip
        if (res.equals("true")) {
            try {
                Double destinationLat = Double.valueOf(Utils.getStringForKey(mContext.getString(R.string.key_active_trip_lat), mContext));
                Double destinationLong = Double.valueOf(Utils.getStringForKey(mContext.getString(R.string.key_active_trip_lon), mContext));
            
                Location targetLocation = new Location("");//provider name is unecessary
                targetLocation.setLatitude(destinationLat);//your coords of course
                targetLocation.setLongitude(destinationLong);
            
                Float dist = location.distanceTo(targetLocation);
                if (dist > MIN_DIST_FOR_ARRIWED) {
                    String res_last_lat = Utils.getStringForKey(mContext.getString(R.string.push_last_location_lat), mContext);
                    String res_last_lng = Utils.getStringForKey(mContext.getString(R.string.push_last_location_lng), mContext);
                    if (!res_last_lat.equals(String.valueOf(location.getLatitude())) || !res_last_lng.equals(String.valueOf(location.getLatitude()))) {
                        if (map != null && map.get("city") != null) {
                            Utils.pushLocations(regNo, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), getApplicationContext(), map.get("city").toString());
                        } else {
                            Utils.pushLocations(regNo, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), getApplicationContext(), "");
                        }
                        Utils.addStringToSharedPref(getString(R.string.push_last_location_lat), String.valueOf(location.getLatitude()), this);
                        Utils.addStringToSharedPref(getString(R.string.push_last_location_lng), String.valueOf(location.getLongitude()), this);
                    }
                } else {
                    String tripId = Utils.getStringForKey(mContext.getString(R.string.key_active_trip_id), mContext);
                    if (tripId != null && tripId.length() > 0) {
                        String requestString = "tripid=" + URLEncoder.encode(tripId, "utf-8");
                        Utils.markTripAsArriwed(requestString, mContext);
                        Toast.makeText(this, "You have arriwed at your destination", Toast.LENGTH_LONG).show();
                    
                        // dismiss the activity when user arrived
                        if (activityContext != null) {
                            activityContext.finish();
                            activityContext = null;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        
        firsttime = false;
        
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult arg0) {

    }

    public void sendUserCurrentLocation(final String location, String regNo) {
        Ion.with(mContext)
                .load(Constants.DEV_BASE_URL + Constants.Api_send_User_Location)
                .setBodyParameter("mobileNo", regNo)
                .setBodyParameter("city", location)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if (e != null) {
                    Log.e(TAG, e.toString());
                }
                if (result != null) {
                    try {
                        Log.d("mynew", result);
                        Log.d("location", location);
                        //{"result":"Success","msg":"User location updated successfully."}
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
                            //  Toast.makeText(mContext, result+"", Toast.LENGTH_SHORT).show();
                        } else {
                            // Toast.makeText(mContext, result+"", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e1) {
                        Log.e(TAG, e1.toString());
                    }
                }
            }
        });
    }

    public void onTripArriwed(NetworkDataModel model) throws JSONException {
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (model.responseData == null) {
            Toast.makeText(this, getString(R.string.str_no_activities), Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jObject = new JSONObject(model.responseData.toString());
        // mark as arriwed
        Utils.addStringToSharedPref(getString(R.string.key_has_active_trip), "false", this);
        Utils.addStringToSharedPref(getString(R.string.key_active_trip_id), "", this);
        Toast.makeText(this, "You have arrived to your destination", Toast.LENGTH_SHORT).show();

//        activityContext.initFragment();
    }
}