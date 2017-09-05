package com.arriwe.wayndr;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.sancsvision.arriwe.R;
import com.arriwe.adapter.TaggedListAdapter;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.util.ShareLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class Activities extends Activity {

    private static String TAG = "Activities.java";
    ProgressDialog dialog = null;
    GridView gridView = null;
    ExpandableListView listView = null;
    ProgressDialog progressDialog = null;
    String acceptDeclineStr = null;
    ImageButton backBtn = null;
    ImageButton refreshBtn = null;
    Activity context  = null;
    Timer timer = null;
    Boolean activityInProgress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        context = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        backBtn = (ImageButton) findViewById(R.id.imageButton_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshBtn = (ImageButton) findViewById(R.id.refreshButton);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    refreshBtnClicked();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
// finally change the color
            window.setStatusBarColor(this.getResources().getColor(R.color.text_color_sky_blue_status_bar));
        }
//        try {
//            getUserActivities();
//            refreshTimer();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        refreshTimer();
        if (Utils.isNetworkConnected(Activities.this)) {
            try {
                getUserActivities(false);

                refreshTimer();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            startService(new Intent(this, LocationService.class));
            LocationService.activityContext = this;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshTimer();
        if (Utils.isNetworkConnected(Activities.this)) {
            try {
                getUserActivities(false);
                refreshTimer();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        //currently disabling for background
//        stopService(new Intent(this, LocationService.class));
        Log.e(TAG, "onPause");
        super.onPause();
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
//        stopService(new Intent(this, LocationService.class));
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    //API Calls
    void getUserActivities(Boolean isRefreshing) throws JSONException, UnsupportedEncodingException {
        if(activityInProgress == true) return;

        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            activityInProgress = false;
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = prefs.getString(getString(R.string.key_user_id), "");
        //dont show progress dialog
        //for refresh feature
        if(!isRefreshing)
            dialog = Utils.showProgressDialog(context, "");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(getResources().getString(R.string.key_tagged_by), regNo);
//        Log.i(TAG, "Json obj to be processed is in getUserActivities " + jsonObject.toString());
        String requestString = "userid="+ URLEncoder.encode(userId, "utf-8");
        activityInProgress = true;
        if (Utils.isNetworkConnected(Activities.this)) {
            NetworkEngine engine = new NetworkEngine();
            engine.getUserActivities(requestString, this);
        }

    }

    void callApiForAcceptOrDecline(String status,String guid) throws JSONException, UnsupportedEncodingException {
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
        String userId = prefs.getString(getString(R.string.key_user_id), "");

        String requestString = "group_id="+ URLEncoder.encode(guid, "utf-8")+"&user_id="+ URLEncoder.encode(userId, "utf-8")+"&status="+ URLEncoder.encode(status, "utf-8");

        Log.i(TAG, "Json obj to be processed is "+requestString);
        if (Utils.isNetworkConnected(Activities.this)) {
            NetworkEngine engine = new NetworkEngine();
            engine.updateStatus(requestString, this);
        }
    }

    void callApiToDeclineLocReq(JSONObject jsonObject) throws JSONException, UnsupportedEncodingException {
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
        String reqLocId = jsonObject.getString("request_location_id");

        String requestString = "mobileNo="+ URLEncoder.encode(mobNo, "utf-8")+"&request_location_id="+ URLEncoder.encode(reqLocId, "utf-8");

        Log.i(TAG, "Json obj to be processed is "+requestString);
        if (Utils.isNetworkConnected(Activities.this)) {
            NetworkEngine engine = new NetworkEngine();
            engine.declineLocationReq(requestString, this);
        }
    }

    void callApiToDeleteLocRecieved(JSONObject jsonObject) throws JSONException, UnsupportedEncodingException {
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
        String reqLocId = jsonObject.getString("request_location_id");

        String requestString = "mobileNo="+ URLEncoder.encode(mobNo, "utf-8")+"&request_location_id="+ URLEncoder.encode(reqLocId, "utf-8");

        Log.i(TAG, "Json obj to be processed is "+requestString);
        if (Utils.isNetworkConnected(Activities.this)) {
            NetworkEngine engine = new NetworkEngine();
            engine.deleteRecievedLoc(requestString, this);
        }
    }


    /***
     * API Callbacks
     */
    public void usersActivitiesResponse(NetworkDataModel model) throws JSONException {
//       Utils.logLargeString("usersActivitiesResponse" + model.responseData.toString());
        activityInProgress = false;
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (model.responseData == null) {
            Toast.makeText(this, getString(R.string.str_no_activities), Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jObject = new JSONObject(model.responseData.toString());
        //initializeListView(jObject);

        android.os.Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("KEY", jObject.toString());
        msg.setData(b);
        handler.sendMessage(msg);
    }

    /***
     * Setup the data in gridview
     * once we get the matching contacts
     * from server
     */
//    void  initializeTravellingWithGridView(ArrayList<JSONObject> list){
//
//        gridView = (GridView) findViewById(R.id.gridView_travel_with);
//        gridView.setAdapter(new TravellingWithGridAdapter(this, list));
//        gridView.setNumColumns(list.size());
//    }

    private final Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            Bundle b = msg.getData();
            try {
                initializeListView(new JSONObject(b.getString("KEY")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    void initializeListView(final JSONObject jsonObject) {
        listView = (ExpandableListView) findViewById(R.id.listView);
        final TaggedListAdapter expandableAdapeter = new TaggedListAdapter(this, jsonObject);
        listView.setAdapter(expandableAdapeter);
        for (int i = 10; i > 0; i--) {
            listView.expandGroup(i);
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //My Journey
                if (groupPosition == 2) {
                    Log.d(TAG, "Data on click " + listView.getItemAtPosition(groupPosition));
                    try {
//                        JSONArray jsonArray = (JSONArray) listView.getItemAtPosition(groupPosition);
                        JSONObject obj = (JSONObject) expandableAdapeter.getChild(groupPosition, childPosition);
                        Log.i(TAG, "Group " + obj);

                        HashMap map = new HashMap();
                        map.put(getString(R.string.key_traveller_name), obj.get(getString(R.string.key_traveller_name)).toString());
                        map.put(getString(R.string.key_travelling_loc), obj.get(getString(R.string.key_end_loc)).toString());
                        map.put(getString(R.string.key_curr_address), obj.get(getString(R.string.key_curr_address)).toString());
                        map.put(getString(R.string.key_curr_lat), obj.get("current_lat").toString());
                        map.put(getString(R.string.key_curr_long), obj.get("current_lon").toString());
                        map.put(getString(R.string.start_lat), obj.get(getString(R.string.start_lat)).toString());
                        map.put(getString(R.string.start_long), obj.get("start_long").toString());
                        map.put(getString(R.string.key_traveller_pic), obj.get(getString(R.string.key_traveller_pic)).toString());
                        map.put(getString(R.string.key_distance_travelled), obj.get(getString(R.string.key_travelled_distance)).toString());
                        map.put(getString(R.string.key_distance_left), obj.get(getString(R.string.key_remaning_distance)).toString());
                        map.put(getString(R.string.key_time_taken), obj.get(getString(R.string.key_time_travelled)).toString());
                        map.put(getString(R.string.key_time_left), obj.get(getString(R.string.key_time_remaning)).toString());
                        map.put("group_dest_lat", obj.get("end_lat").toString());
                        map.put("group_dest_lon", obj.get("end_long").toString());
                        map.put("trip_id", obj.get("tripid").toString());


                        Intent intent = new Intent(Activities.this, IndividualTravelDetail.class);
                        intent.putExtra("data", map);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                } else if (groupPosition == 4) {
//                    try {
//                        JSONObject obj = (JSONObject) expandableAdapeter.getChild(groupPosition, childPosition);
////                        JSONArray travvelingPersonsDetails = obj.getJSONArray("travellingWith");
//                        Intent intent = new Intent(Activities.this, GroupTravelDetail.class);
//                        intent.putExtra("group_details", obj.toString());
//                        intent.putExtra("grp_travel_loc", obj.get("group_dest_title").toString());
//                        intent.putExtra("grp_destination_address", obj.get("group_travelling_to").toString());
//                        intent.putExtra("group_dest_lat", obj.get("group_dest_lat").toString());
//                        intent.putExtra("group_dest_lon", obj.get("group_dest_lon").toString());
//                        intent.putExtra("group_invited_nos", obj.get("group_invited_count").toString());
//                        intent.putExtra("trip_id", obj.get("tripid").toString());
//
//                        startActivity(intent);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
                    //when you are tagged by someone
                }
                else if(groupPosition == 3) {
                    try {
//                        JSONArray jsonArray = (JSONArray) listView.getItemAtPosition(groupPosition);
                        JSONObject obj = (JSONObject) expandableAdapeter.getChild(groupPosition, childPosition);
                        Log.i(TAG, "Tagged activity details " + obj);

                        HashMap map = new HashMap();
                        map.put(getString(R.string.key_traveller_name), obj.get(getString(R.string.key_traveller_name)).toString());
                        map.put(getString(R.string.key_travelling_loc), obj.get(getString(R.string.key_travelling_loc)).toString());
                        map.put(getString(R.string.key_curr_address), obj.get(getString(R.string.key_curr_address)).toString());
                        map.put(getString(R.string.key_curr_lat), obj.get("current_lat").toString());
                        map.put(getString(R.string.key_curr_long), obj.get("current_lon").toString());
                        map.put(getString(R.string.start_lat), obj.get(getString(R.string.start_lat)).toString());
                        map.put(getString(R.string.start_long), obj.get("start_long").toString());
                        map.put(getString(R.string.key_traveller_pic), obj.get(getString(R.string.key_traveller_pic)).toString());
                        map.put(getString(R.string.key_distance_travelled), obj.get(getString(R.string.key_travelled_distance)).toString());
                        map.put(getString(R.string.key_distance_left), obj.get(getString(R.string.key_remaning_distance)).toString());
                        map.put(getString(R.string.key_time_taken), obj.get(getString(R.string.key_time_travelled)).toString());
                        map.put(getString(R.string.key_time_left), obj.get(getString(R.string.key_time_remaning)).toString());
                        map.put("group_dest_lat", obj.get("travelling_lat").toString());
                        map.put("group_dest_lon", obj.get("travelling_long").toString());
                        map.put("trip_id", obj.get("tripid").toString());




                        Intent intent = new Intent(Activities.this, IndividualTravelDetail.class);
                        intent.putExtra("data", map);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });
    }

    /***
     * List buttons callback
     */

    public void acceptBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiForAcceptOrDecline("A", obj.getString("group_id"));
        acceptDeclineStr = "A";
    }


    public void declineBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiForAcceptOrDecline("C", obj.getString("group_id"));
        acceptDeclineStr = "C";

    }

    public void crossBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiForAcceptOrDecline("C", obj.getString("group_id"));
        acceptDeclineStr = "O";
    }

    public void refreshBtnClicked() throws JSONException, UnsupportedEncodingException {
        getUserActivities(false);
    }

    public void shareLocBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        if(Utils.gpsStatus(this) == false){
            Utils.checkGPSStatus(this);
            return;
        }
        String reqLocId = obj.getString("request_location_id");
        Intent intent = new Intent(Activities.this, ShareLocation.class);
        intent.putExtra("loc_id", reqLocId);
        startActivity(intent);
    }

    public void createTripFromSharedLocation(JSONObject obj) throws JSONException, IOException {
//        String currLat = String.valueOf(LocationService.LATEST_LOC_OBJ.getLatitude());
//        String currLon  = String.valueOf(LocationService.LATEST_LOC_OBJ.getLongitude());
        String placeName = "";
        HashMap map = Utils.reverseGeocode(LocationService.LATEST_LOC_OBJ.getLatitude(),LocationService.LATEST_LOC_OBJ.getLongitude(),this);
        if(map.get("name") != null){
            placeName = map.get("name").toString();
        }
        Intent i = new Intent(Activities.this,MainActivity.class);
        i.putExtra((getResources().getString(R.string.key_freq_visit_place)),placeName);
        i.putExtra((getResources().getString(R.string.key_freq_visit_lat)),Double.parseDouble(obj.getString("travelling_lat")));
        i.putExtra((getResources().getString(R.string.key_freq_visit_long)),Double.parseDouble(obj.getString("travelling_lon")));
        i.putExtra((getResources().getString(R.string.key_freq_visit_address)),map.get("address").toString());
        i.putExtra((getResources().getString(R.string.key_curr_lat)),LocationService.LATEST_LOC_OBJ.getLatitude());
        i.putExtra((getResources().getString(R.string.key_curr_long)),LocationService.LATEST_LOC_OBJ.getLongitude());
        i.putExtra((getResources().getString(R.string.key_tagged_title)), placeName);
        i.putExtra("from_shared_loc", true);
        i.putExtra("tag_person_no", obj.getString("travelrequest_phone"));
        startActivity(i);
        ignoreBtnClicked(obj);

    }

    public void ignoreBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiToDeleteLocRecieved(obj);
    }


    public void declineLocShareBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiToDeclineLocReq(obj);
    }

    public void acceptDeclineResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
//        Log.i(TAG,"acceptDeclineResponse"+model.responseData.toString());
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
        if(res.equals(getString(R.string.key_Success))){
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_LONG).show();
            getUserActivities(false);
        }
        else{
//            finish();
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public void clearBtnClicked(String tripId) throws JSONException, UnsupportedEncodingException {
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
        String userId = prefs.getString(getString(R.string.key_user_id), "");

        String requestString = "tripid="+ URLEncoder.encode(tripId, "utf-8")+"&userid="+URLEncoder.encode(userId, "utf-8");

        Log.i(TAG, "Json obj to be processed is " + requestString);
        if (Utils.isNetworkConnected(Activities.this)) {
            NetworkEngine engine = new NetworkEngine();
            engine.clearTrip(requestString, this);
        }
    }


    public  void cancelTripBtnAction(String tripId) throws UnsupportedEncodingException {
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
        String userId = prefs.getString(getString(R.string.key_user_id), "");

//        String requestString = "tripid="+ URLEncoder.encode(tripId, "utf-8");
        String requestString = "tripid="+ URLEncoder.encode(tripId, "utf-8")+"&userid="+URLEncoder.encode(userId, "utf-8");

        Log.i(TAG, "Json obj to be processed is "+requestString);
        if (Utils.isNetworkConnected(Activities.this)) {
            NetworkEngine engine = new NetworkEngine();
            engine.cancelATrip(requestString, this);
        }
    }

    public void clearTripResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
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
        if(res.equals(getString(R.string.key_Success))){
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_LONG).show();
            getUserActivities(false);
        }
        else{
//            finish();
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
            getUserActivities(false);


        }
    }

    public void cancelTripResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
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
        if(res.equals(getString(R.string.key_Success))){
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_LONG).show();
            getUserActivities(false);
//            String tripId = Utils.getStringForKey(mContext.getString(R.string.key_active_trip_id),mContext);
            Utils.addStringToSharedPref(getString(R.string.key_has_active_trip), "false", this);
            Utils.addStringToSharedPref(getString(R.string.key_active_trip_id), "", this);
        }
        else{
//            finish();
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public  void locationRequestRejectedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
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

        if(res.equals(getString(R.string.key_Success))){
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_LONG).show();
            getUserActivities(false);
        }
        else{
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public  void recievedLocRejectedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
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

        if(res.equals(getString(R.string.key_Success))){
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_LONG).show();
            getUserActivities(false);
        }
        else{
//            finish();
            Toast.makeText(this,obj.getString("msg"),Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }


    public void showNoActivitiesLabel(int alpha){
        TextView notActivities = (TextView) findViewById(R.id.textView13);
        notActivities.setAlpha(alpha);
    }

    public void initFragment(Double lat,Double longitude){
        Fragment f = getFragmentManager().findFragmentByTag("NearbyFriendsFragment");
        if(f == null) {

            // get an instance of FragmentTransaction from your Activity
            FragmentManager fragmentManager = this.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //add a fragment
//        Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
//        Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
            String placeName = getIntent().getStringExtra(getResources().getString(R.string.key_freq_visit_place));

            NearByFriendsFragment myFragment = new NearByFriendsFragment(lat, longitude, 1, placeName);
            fragmentTransaction.add(R.id.layout_placeholder, myFragment, "NearbyFriendsFragment");
//        fragmentTransaction.replace(R.id.layout_placeholder,myFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    public void refreshTimer(){
        if(timer == null) {
            timer = new Timer();
        }
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run () {

             context.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         getUserActivities(true);
                     } catch (JSONException e) {
                         e.printStackTrace();
                     } catch (UnsupportedEncodingException e) {
                         e.printStackTrace();
                     }
                 }
             });
            }
        };

        timer.schedule (hourlyTask, 0l, (1000*60)/5);
    }
}