package com.arriwe.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import com.arriwe.wayndr.Activities;

/**
 * Created by anand jain on 28/01/17.
 */
public class GetCityLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context context;
    private static final String TAG = "GetCityLocationService";
    public final static String LATEST_LOC = "LATEST_LOC";
    public static Location LATEST_LOC_OBJ = null;
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


    public GetCityLocationService(){
    }
    public GetCityLocationService(Context context){
        super();
        this.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

     //   getCurrentLatLng();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Log.d("context","start");
        getLocation();
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d("context","onconnext");
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("hello",location.getLatitude()+" "+location.getLongitude());

        Toast.makeText(mContext, location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_SHORT).show();


    }
}