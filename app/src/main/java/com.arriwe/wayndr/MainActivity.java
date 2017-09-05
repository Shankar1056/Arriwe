package com.arriwe.wayndr;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sancsvision.arriwe.R;
import com.arriwe.adapter.ContactsGridAdapter;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class MainActivity extends Activity implements com.google.android.gms.maps.OnMapReadyCallback {

    private static String TAG = "MainActivity.java";
    GoogleMap mapObj;
    TextView addressTextView = null;
    TextView placeNameTextView = null;
    ProgressDialog dialog = null;
    GridView gridView = null;
    ArrayList<JSONObject> arrayList = null;
    ImageButton tickButton = null;
    ImageButton backButtpn = null;
    int tagCount = 0;
    int addCount = 0;
    RelativeLayout tagTravelCountLayout;
    TextView tagCountTextView, addCountTextView, noContactsTextView;
    public final static String LATEST_LOC = "LATEST_LOC";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
        initUI();
    }

    //    initialization
    void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void initUI() {
        tickButton = (ImageButton) findViewById(R.id.imageButton_tick);
        backButtpn = (ImageButton) findViewById(R.id.imageButton_back);
        int layoutID = getResources().getIdentifier("bottom_layout", "id", getPackageName());
        RelativeLayout bottomLayout = (RelativeLayout) findViewById(layoutID);
        addressTextView = (TextView) bottomLayout.findViewById(R.id.text_view_address);
        placeNameTextView = (TextView) findViewById(R.id.textView_place_name);

        addressTextView.setText(getIntent().getStringExtra(getResources().getString(R.string.key_freq_visit_address)));
        placeNameTextView.setText(getIntent().getStringExtra(getResources().getString(R.string.key_freq_visit_place)));

        bottomLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("MainActivity", "Touch +" + mapObj);
                return true;
            }
        });

        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Utils.isNetworkConnected(MainActivity.this)) {
                        callApiToTagOrAddToTravel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        backButtpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tagTravelCountLayout = (RelativeLayout) findViewById(R.id.tag_travel_layout);
        tagCountTextView = (TextView) findViewById(R.id.textView_tagged_count);
        addCountTextView = (TextView) findViewById(R.id.textView_add);
        noContactsTextView = (TextView) findViewById(R.id.textView_no_contacts);

        //initializeGridView(OneFragment.arrayList);
    }

    void initContacts() throws JSONException, UnsupportedEncodingException {
        ArrayList<String> contactNos = Utils.getContactsFromPhone(this);
        callApiToGetRegisteredContacts(contactNos);
    }

    /***
     * Setup the data in gridview
     * once we get the matching contacts
     * from server
     */
    void initializeGridView(ArrayList<JSONObject> list) {

        gridView = (GridView) findViewById(R.id.gridview_contacts);
        gridView.setAdapter(new ContactsGridAdapter(this, list));


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Item long pressed");
                try {
                    tagOrAddContactToTravelWith(2, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

        //tag a person
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Clicked to tag");
                try {
                    tagOrAddContactToTravelWith(1, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void autotag(String num) throws JSONException {

        int position = 0;
        for(int i=0; i < arrayList.size();i++){
             JSONObject jsonObject  = (JSONObject) arrayList.get(i);
            String mobNo = jsonObject.getString("mobile");
            if(mobNo.equals(num)){
//                break;
                position  = i;
            }
        }
        tagOrAddContactToTravelWith(1, position);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapObj = googleMap;
        String placeName = getIntent().getStringExtra((getResources().getString(R.string.key_freq_visit_place)));
        Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
        Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);

        final LatLng latLng = new LatLng(lat, longitude);
        Log.i(TAG, "Lat long for annotatiing is " + lat + "," + longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(placeName));
        final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mapObj.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //move the marker to top of screen
                moveMapToAPosition(latLng);
                //get the contacts once marker is placed
                try {
                    Log.d(TAG, "===== Called finish =====");
                    initContacts();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    void moveMapToAPosition(LatLng latLng) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;

        Point pointInScreen = mapObj.getProjection().toScreenLocation(latLng);
        Point newPoint = new Point();
        newPoint.x = pointInScreen.x;
        int yAxis = (int) (outMetrics.heightPixels / 3);
        newPoint.y = pointInScreen.y + yAxis;

        LatLng newCenterLatLng = mapObj.getProjection().fromScreenLocation(newPoint);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(newCenterLatLng, 15);
        mapObj.animateCamera(yourLocation);
    }

    /***
     * API Calls
     */

    void callApiToGetRegisteredContacts(ArrayList<String> phoneNOs) throws JSONException, UnsupportedEncodingException {
        //for testing
//        ArrayList<String> obj = new ArrayList<String >();
//        obj.add("8951374713");
//        obj.add("9620498754");
//        obj.add("8553902950");
//        obj.add("1234");
//        obj.add("5678");

        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "Loading your contacts");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(getResources().getString(R.string.key_phonenos), contactsJsonArray);
        String requestString = "phoneNos=" + URLEncoder.encode(String.valueOf(phoneNOs), "utf-8");
        Log.i(TAG, "Json obj to be processed is in registered contacts " + requestString);
        NetworkEngine engine = new NetworkEngine();
     //   engine.getUsersUsingWayndr(requestString, this);
    }

        void callApiToTagOrAddToTravel() throws JSONException, UnsupportedEncodingException {
            //get the contacts who are tagged or added

            ArrayList<String> taggedArrayToSend = new ArrayList<String>();
            ArrayList<String> travellingWithArrayToSend = new ArrayList<String>();
            try {
                for (JSONObject object : arrayList) {
                    if (object.has(getString(R.string.key_is_tagged))) {
                        if (object.getBoolean(getString(R.string.key_is_tagged))) {
                            taggedArrayToSend.add(object.getString(getString(R.string.key_mob)));
                        }
                    }
                    if (object.has(getString(R.string.key_is_added_for_travel))) {
                        if (object.getBoolean(getString(R.string.key_is_added_for_travel))) {
                            travellingWithArrayToSend.add(object.getString(getString(R.string.key_mob)));
                        }
                    }
                }
            } catch (Exception e) {
            } finally {
                if (taggedArrayToSend.size() > 0 || (travellingWithArrayToSend.size() > 0)) {
                    //create request
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
                    Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
                    Double latOrigin = getIntent().getDoubleExtra((getResources().getString(R.string.key_curr_lat)), 0.0);
                    Double longOrigin = getIntent().getDoubleExtra((getResources().getString(R.string.key_curr_long)), 0.0);
                    String placeName = getIntent().getStringExtra(getResources().getString(R.string.key_tagged_title));

    //                    JSONArray contactsJsonArray = new JSONArray(taggedArrayToSend);
    //                    JSONArray travelJsonArray = new JSONArray(travellingWithArrayToSend);
    //
    //                    JSONObject jsonObject = new JSONObject();
    //                    jsonObject.put(getString(R.string.key_tagged_by), prefs.getString(getString(R.string.key_reg_no), ""));
    //                    jsonObject.put(getString(R.string.key_tagged_address), addressTextView.getText().toString());
    //                    jsonObject.put(getString(R.string.key_tagged_loc_title), placeName);
    //                    jsonObject.put(getString(R.string.key_lat_dest), lat);
    //                    jsonObject.put(getString(R.string.key_long_dest), longitude);
    //                    jsonObject.put(getString(R.string.key_lat_origin), latOrigin);
    //                    jsonObject.put(getString(R.string.key_long_origin), longOrigin);
    //                    jsonObject.put(getString(R.string.key_tagged_to), contactsJsonArray);
    //                    jsonObject.put(getString(R.string.key_travelling_with), travelJsonArray);

                    String taggedBy = prefs.getString(getString(R.string.key_reg_no), "");
                    String requestString = "taggedby=" +
                            URLEncoder.encode(taggedBy, "utf-8") + "&to_lat=" +
                            URLEncoder.encode(String.valueOf(latOrigin), "utf-8") + "&to_lon=" +
                            URLEncoder.encode(String.valueOf(longOrigin), "utf-8") + "&to_address=" +
                            URLEncoder.encode(addressTextView.getText().toString(), "utf-8") + "&from_lat=" +
                            URLEncoder.encode(String.valueOf(lat), "utf-8") + "&from_lon=" +
                            URLEncoder.encode(String.valueOf(longitude), "utf-8") + "&title=" +
                            URLEncoder.encode(String.valueOf(placeName), "utf-8") + "&taggedto=" +
                            URLEncoder.encode(String.valueOf(taggedArrayToSend), "utf-8") + "&travelwith=" +
                            URLEncoder.encode(String.valueOf(travellingWithArrayToSend), "utf-8");

                    Log.i(TAG, "Sending taaging details from callApiToTagOrAddToTravel " + requestString);
                    dialog = Utils.showProgressDialog(this, "");
                    NetworkEngine engine = new NetworkEngine();
                    engine.sendTagAndTravelDetails(requestString, this);

                }
            }
        }

    /***
     * API Callbacks
     */
    public void registeredContactsResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Log.i(TAG, "registeredContactsResponse" + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            noContactsTextView.setAlpha(1);
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jObject = new JSONObject(model.responseData.toString());

        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        if (map.containsKey("result")) {
            if ((map.get("result").equals("Fail"))) {
                noContactsTextView.setAlpha(1);
                return;
            }
        }
        JSONArray jsonArray = jObject.getJSONArray("data");

        arrayList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getJSONObject(i));
            Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
        }
        initializeGridView(arrayList);
        // push location
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String regNo = prefs.getString(getString(R.string.key_reg_no), "");
        Location  loc = LocationService.LATEST_LOC_OBJ;
//        Toast.makeText(this,"latest loc is "+loc,Toast.LENGTH_LONG).show();
        Utils.pushLocations(regNo, String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()), getApplicationContext(),LocationService.LATEST_LOC_CITY);


        Boolean isCreatedFromSharedLoc = getIntent().getBooleanExtra("from_shared_loc",false);
        if(isCreatedFromSharedLoc) {
            final String mobNoToTag = getIntent().getStringExtra("tag_person_no");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    try {
                        autotag(mobNoToTag);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        }
        //fragment of neearby friends
        initFragment();
    }


    public void taggedTravellingResponse(NetworkDataModel model) throws JSONException {

        Log.i(TAG, "taggedTravellingResponse" + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jObject = new JSONObject(model.responseData.toString());
        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());

        String res = map.get(getResources().getString(R.string.key_result)).toString();
        if (res.equals(getString(R.string.key_Success))) {
            Intent i = new Intent(MainActivity.this, Activities.class);
            startActivity(i);
            Utils.addStringToSharedPref(getString(R.string.key_has_active_trip), "true", this);
            Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
            Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
            String activeTripId = jObject.getString("id");


            Utils.addStringToSharedPref(getString(R.string.key_active_trip_lat), String.valueOf(lat), this);
            Utils.addStringToSharedPref(getString(R.string.key_active_trip_lon), String.valueOf(longitude), this);
            Utils.addStringToSharedPref(getString(R.string.key_active_trip_id),String.valueOf(activeTripId),this);
            finish();
        } else {
            Toast.makeText(this, jObject.getString("msg"), Toast.LENGTH_LONG).show();
        }
    }

    /***
     * @param type     :- Type can be 1 or 2,1 for tagging and 2 for adding to travel
     * @param position :- Position of Contact to be tagged/added
     */
    private void tagOrAddContactToTravelWith(int type, int position) throws JSONException {
        //get selected user
        JSONObject jsonObject = arrayList.get(position);


        if (type == 1) {
            //if that person is already added from travel
            //and then click again, unadd him
            Boolean travellingStatus = false;
            if (jsonObject.has(getResources().getString(R.string.key_is_added_for_travel))) {
                travellingStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_added_for_travel));
            }
            //if user try to tag make sure first its removed from adding
            if(travellingStatus == true) {
                jsonObject.put(getString(R.string.key_is_added_for_travel), false);
            }
            else {
                Boolean taggedStatus = false;
                if (jsonObject.has(getResources().getString(R.string.key_is_tagged))) {
                    taggedStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_tagged));
                }
                jsonObject.put(getString(R.string.key_is_tagged), !taggedStatus);
            }

        } else {
            Boolean travellingStatus = false;
            if (jsonObject.has(getResources().getString(R.string.key_is_added_for_travel))) {
                travellingStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_added_for_travel));
            }

            //if user try to add make sure tagging is set to false
            jsonObject.put(getString(R.string.key_is_tagged), false);
            jsonObject.put(getString(R.string.key_is_added_for_travel), !travellingStatus);
        }

        arrayList.set(position, jsonObject);
        initializeGridView(arrayList);
        gridView.invalidateViews();
        calculateTagAndAddCount();
    }

    void calculateTagAndAddCount() throws JSONException {
        tagCount = 0;
        addCount = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject jsonObject = arrayList.get(i);
            if (jsonObject.has(getString(R.string.key_is_tagged))) {
                Boolean taggedRes = jsonObject.getBoolean(getString(R.string.key_is_tagged));
                if (taggedRes)
                    tagCount++;
            }
            if (jsonObject.has(getString(R.string.key_is_added_for_travel))) {
                Boolean travelWithRes = jsonObject.getBoolean(getString(R.string.key_is_added_for_travel));
                if (travelWithRes)
                    addCount++;
            }
        }
        tagTravelCountLayout.setAlpha(1);
        tagCountTextView.setText("TAGS  . " + tagCount);
        addCountTextView.setText("ADDED " + addCount);


    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        0).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
     //   startService(new Intent(this, LocationService.class));

    }

    @Override
    protected void onPause() {
        //currently disabling for background
//        stopService(new Intent(this, LocationService.class));
        Log.e(TAG, "onPause");
        super.onPause();
//        if(mGoogleApiClient.isConnected())
////            stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    void initFragment(){
        // get an instance of FragmentTransaction from your Activity
        Fragment f = getFragmentManager().findFragmentByTag("NearbyFriendsFragment");
        if(f == null) {
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //add a fragment
            Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
            Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
            String placeName = getIntent().getStringExtra(getResources().getString(R.string.key_freq_visit_place));

            NearByFriendsFragment myFragment = new NearByFriendsFragment(lat, longitude, 0, placeName);
            fragmentTransaction.add(R.id.layout_placeholder, myFragment, "NearbyFriendsFragment");
//        fragmentTransaction.replace(R.id.layout_placeholder,myFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
