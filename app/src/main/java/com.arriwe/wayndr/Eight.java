package com.arriwe.wayndr;


import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.fragment.OneFragment;
import com.arriwe.fragment.ThreeFragment;
import com.arriwe.fragment.TwoFragment;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.utility.ClsGeneral;
import com.arriwe.utility.Utils;
import com.arriwe.utility.Utilz;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.sancsvision.arriwe.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

//import com.arriwe.networkmanagers.NetworkDataModel;


public class Eight extends AppCompatActivity implements com.google.android.gms.maps.OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    TextView t;
    ListView mylistview;
    private CustomTabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    //MyReceiver myReceiver = null;
    Location lastKnownLocation;
    String TAG = "Eight.java";

    String setfragment = null;
    Timer timer = null;
    public static GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.eigth);

        registerservice_register();
        savedevicetokentoserver();

    }

    private void savedevicetokentoserver() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mobNo = prefs.getString(getString(R.string.key_reg_no), "");
        String device_token = ClsGeneral.getPreferences(Eight.this, ClsGeneral.DEVICE_TOKEN);
        if (Utils.isNetworkConnected(Eight.this)) {
            GetDescBannerImage getDescBannerImage = new GetDescBannerImage();
            getDescBannerImage.execute("http://35.163.45.85/index.php?r=api/registerfcm", mobNo, device_token);
        }
    }

    private void setupfragment() {
        if (setfragment != null) {
            if (setfragment.equals("first")) {
                viewPager.setCurrentItem(0, true);
            } else {
                viewPager.setCurrentItem(2, true);
            }
        } else {
            viewPager.setCurrentItem(1, true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(getApplicationContext(),Six.class));
    }

    private void setupTabIcons() {

     /*   tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        // unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        //resetUIToInitialState();
//\       unregisterReceiver(myReceiver);
//        mGoogleApiClient.disconnect();
    }

    private void setupViewPager(final ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager()); //create adapter for viewpager
        adapter.addFragment(new OneFragment(), "CONTACTS"); //add fragments to adapter
        adapter.addFragment(new TwoFragment(), "HOME");
        adapter.addFragment(new ThreeFragment(Eight.this), "ACTIVITIES");

        viewPager.setAdapter(adapter); //and finally set adapter to viewpager


        setupfragment();

    }

    public void registeredConResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        /*OneFragment fragment = (OneFragment) getFragmentManager().findFragmentById(R.id.example_fragment);
        fragment.<specific_function_name>();*/

        //Toast.makeText(this, "eigtht", Toast.LENGTH_SHORT).show();

        Fragment activeFragment = adapter.getItem(0);

        // Toast.makeText(this, "calll", Toast.LENGTH_SHORT).show();
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((OneFragment) activeFragment).registeredConResponsel("");
        }
    }

    /***
     * API Callbacks
     */
    public void usersActivitiesResponse(NetworkDataModel model) throws JSONException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //   Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).usersActivitiesResponse(model);
        }

    }

    public void cancelTripBtnAction(String tripId) throws UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            // Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).cancelTripBtnAction(tripId);
        }
    }
    public void sendpos(int position, String fullorsingle) throws JSONException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            // Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).openmapdialo(position,fullorsingle);
        }
    }
    public void openlivemap(String start_lat, String start_lon, String group_dest_lat, String group_dest_lon) throws UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            // Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).openlivemap(start_lat, start_lon, group_dest_lat, group_dest_lon);
        }

    }

    public void cancelTripTravellerAction(String tripid, String userid, String mobile) throws UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).cancelTripTravellerAction(tripid, userid, mobile);//group_owner_mob
        }
    }

    public void cancelTripTrggerAction(String tripid, String userid, String mobile) throws UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).cancelTripTrggerAction(tripid, userid, mobile);//group_owner_mob
        }
    }

    public void cancelTripResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).cancelTripResponse(model);
        }
    }

    public void refreshBtnClicked() throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).refreshBtnClicked();
        }
    }

    public void clearTripResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).clearTripResponse(model);
        }
    }

    public void clearBtnClicked(String tripId) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).clearBtnClicked(tripId);
        }
    }

    /***
     * List buttons callback
     */

    public void acceptBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            // Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).acceptBtnClicked(obj);
        }
    }

    public void acceptDeclineResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            // Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).acceptDeclineResponse(model);
        }
    }

    public void declineBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).declineBtnClicked(obj);
        }
    }

    public void initFragment(Double lat, Double longitude) {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).initFragment(lat, longitude);
        }
    }

    public void shareLocBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).shareLocBtnClicked(obj);
        }
    }

    public void declineLocShareBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //    Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).declineLocShareBtnClicked(obj);
        }
    }

    public void locationRequestRejectedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //   Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).locationRequestRejectedResponse(model);
        }
    }

    public void createTripFromSharedLocation(JSONObject obj) throws JSONException, IOException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).createTripFromSharedLocation(obj);
        }
    }

    public void recievedLocRejectedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).recievedLocRejectedResponse(model);
        }
    }

    public void ignoreBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            //   Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).ignoreBtnClicked(obj);
        }
    }

    public void showNoActivitiesLabel(int alpha) {
        Fragment activeFragment = adapter.getItem(2);
        if (activeFragment == null) {
            // Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((ThreeFragment) activeFragment).showNoActivitiesLabel(alpha);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }




    class ViewPagerAdapter extends FragmentStatePagerAdapter { //Custom adapter class provides fragments required for the view pager.
        private final List<Fragment> mFragmentList = new ArrayList<>(); //create a list of fragments
        private final List<String> mFragmentTitleList = new ArrayList<>(); //create a list of fragment titles

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public String getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            // return null;
        }
    }

    public void requestLocation(View view) {
        Fragment activeFragment = adapter.getItem(0);
        ((OneFragment) activeFragment).requestLocation(view);


    }

    public void sendYourLocation(View view) {
        Fragment actiFragment = adapter.getItem(0);
        ((OneFragment) actiFragment).sendYourLocation(view);

    }

    public void locationRequestedResponse(NetworkDataModel model) throws JSONException {
//       Utils.logLargeString("usersActivitiesResponse" + model.responseData.toString());
        Fragment activeFragment = adapter.getItem(0);

        // Toast.makeText(this, "calll", Toast.LENGTH_SHORT).show();
        if (activeFragment == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            ((OneFragment) activeFragment).locationRequestedResponse(model);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupViewPager(viewPager);  //set viewPager to Eight //Defines the number of tabs by setting appropriate fragment and tab name.
        tabLayout.setupWithViewPager(viewPager); // set viewpager to tablayout in Eight //Assigns the ViewPager to TabLayout
        /*if(setfragment!=null){
            viewPager.setCurrentItem(2,true);
        }
        else {
            viewPager.setCurrentItem(1,true);
        }*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String gcmToken = prefs.getString(getString(R.string.gcm_token), "");
        Log.i(TAG, "GCM token is " + gcmToken);
        if (gcmToken.length() > 0) {
            try {
                if (Utils.isNetworkConnected(Eight.this)) {
                    Utils.pushGCMToken(gcmToken, this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (Utils.isNetworkConnected(Eight.this)) {
            Utils.checkGPSStatus(this);
        }

        //  startLocationUpdates();
        //   mGoogleApiClient.connect();
    }

   /* private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            Location latestLoc = arg1.getParcelableExtra(getString(R.string.key_loc_changed));
            lastKnownLocation = latestLoc;
            if (lastKnownLocation != null) {
                LatLng l = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                LatLngBounds bounds = LatLngBounds.builder().include(l).build();

                //  autoCompleteAdapter.setGoogleApiClient(LocationService.googleApiClient, bounds);
//                startLocationUpdates();

                Log.i(TAG, "Google Places API connected. " + lastKnownLocation);
            } else {
                Toast.makeText(Eight.this, getString(R.string.str_unable_to_locate), Toast.LENGTH_LONG).show();
            }
        }

    }*/

    public void autoOnGPS() {
        if (Utils.isNetworkConnected(Eight.this)) {
            googleApiClient = null;

            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API).addConnectionCallbacks(Eight.this)
                        .addOnConnectionFailedListener(Eight.this).build();
                googleApiClient.connect();
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true); // this is the key ingredient

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                        .checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result
                                .getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can
                                // initialize location
                                // requests here.
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be
                                // fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling
                                    // startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(Eight.this, 1000);
                                } catch (Exception e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have
                                // no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        
    }

    private class GetDescBannerImage extends AsyncTask<String, Void, String> {
        String result1;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            nameValuePairs.add(new BasicNameValuePair("fcmDeviceToken", params[2]));
            nameValuePairs.add(new BasicNameValuePair("mobileNo", params[1]));

            try {
                result1 = Utilz.executeHttpPost(params[0], nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.optString("result");
                    if (result.equalsIgnoreCase("Success")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {


            }

        }


    }

    private void registerservice_register() {
        try {
            setfragment = getIntent().getStringExtra("setfragment");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


        if (Utils.isNetworkConnected(Eight.this)) {
           /* myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LocationService.LATEST_LOC);
            registerReceiver(myReceiver, intentFilter);*/
            autoOnGPS();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager); //get viewpager which we created in activity_main.xml
        tabLayout = (CustomTabLayout) findViewById(R.id.tabs); //get tablayout defined in activity_main.xml

    }

}
