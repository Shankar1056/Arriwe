package com.arriwe.wayndr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.Model.FavPlaceModel;
import com.sancsvision.arriwe.R;
import com.arriwe.adapter.FavPlacesListAdapter;
import com.arriwe.adapter.PlaceArrayAdapter;
import com.arriwe.database.DBUtility;
import com.arriwe.database.FrequentlyVisitedLoc;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class WhereRUGoing extends Activity implements com.google.android.gms.maps.OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    String TAG = "WhereRUGoing";
    int CONTACT_PICKER_RESULT = 1234;
    private GoogleApiClient mGoogleApiClient;
    AutoCompleteTextView textView = null;
    Location lastKnownLocation;
    PlaceArrayAdapter autoCompleteAdapter;
    ArrayList<String> placeArr;
    Place selectedPlace = null;
    EditText dialogTextBox;
    Toast toast;
    ImageView settingImgView = null;
    Button addToFav = null;
    Button skipBtn = null;
    Button reqALoc = null;
    ProgressDialog dialog = null;
    LocationRequest mLocationRequest;
    RelativeLayout bottomLayout = null;
    MyReceiver myReceiver = null;
    FrameLayout noFavFrameLayout = null;
    ListView favPlaceLV;
    TextView yourFavPlaceHeading;
    TextView noFavPlaceTV;
    MapFragment mapFragment;
    RelativeLayout profilePicLayout;
    RelativeLayout nextLayout = null;
    Button cancelBtn = null;
    Button nextBtn = null;


//    RelativeLayout reqLocationLayout;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final long MIN_DISPLACMENT = 1;

//    private static final String[] placeArr = new MutableString[] {
//            "IndiraNagar", "CV Raman Nagar", "JP Nagar", "MGRoad", "Spain"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
// finally change the color
            window.setStatusBarColor(this.getResources().getColor(R.color.off_white));
        }

        setContentView(R.layout.activity_where_rugoing);
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
        textView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);
        textView.setThreshold(2);
        textView.setDropDownVerticalOffset(15);

        yourFavPlaceHeading = (TextView) findViewById(R.id.textView3);
        noFavPlaceTV = (TextView) findViewById(R.id.editText2);
        favPlaceLV = (ListView) findViewById(R.id.listView2);
//        searchBtn = (Button) findViewById(R.id.button_search);
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onSearchBtnAction();
//            }
//        });

        addToFav = (Button) findViewById(R.id.button_add_to_fav);
        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addToFav.isSelected()){
//                    addToFav.setSelected(false);
//                    ResourcesCompat.getDrawable(getResources(), R.drawable.add_to_fav, null);
                }
                else{
                    showCustomDialog();
                }
            }
        });

        skipBtn = (Button) findViewById(R.id.button_skip_to_activities);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WhereRUGoing.this,Activities.class);
                startActivity(i);
            }
        });

        reqALoc = (Button) findViewById(R.id.btn_req_loc);
        reqALoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactPicker();
            }
        });

        bottomLayout = (RelativeLayout) findViewById(R.id.relativeLayout4);
        nextLayout = (RelativeLayout) findViewById(R.id.go_next_screen_layout);
        noFavFrameLayout = (FrameLayout) findViewById(R.id.no_fav_layout);
        settingImgView = (ImageView) findViewById(R.id.setting_imgview);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_layout_fragment);
        mapFragment.getView().setVisibility(View.INVISIBLE);
        profilePicLayout = (RelativeLayout) findViewById(R.id.relativeLayout5);

        profilePicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WhereRUGoing.this, Settings.class);
                startActivity(i);
            }
        });

        cancelBtn = (Button) findViewById(R.id.button_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(WhereRUGoing.this, "call", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        nextBtn = (Button) findViewById(R.id.button_move_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchBtnAction();
            }
        });

        initAdapter();
        initGrid();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String path = prefs.getString(getResources().getString(R.string.path_profile_pic), "");
        try {
            Bitmap b= Utils.getBitmapFromPath(this, path);
            settingImgView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gcmToken = prefs.getString(getString(R.string.gcm_token), "");
        Log.i(TAG,"GCM token is "+gcmToken);
        if(gcmToken.length() > 0){
            try {
                Utils.pushGCMToken(gcmToken,this);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    //initialise
    void initAdapter() {
        autoCompleteAdapter = new PlaceArrayAdapter(this, R.layout.autocomplete_cell, null);
        textView.setAdapter(autoCompleteAdapter);
        textView.setDropDownBackgroundDrawable(getResources().getDrawable(R.color.gray));
        textView.setOnItemClickListener(mAutocompleteClickListener);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    selectedPlace = null;
//                    reqLocationLayout.setVisibility(View.VISIBLE);
                }
                else {
                    favPlaceLV.setAlpha(0);
                    yourFavPlaceHeading.setAlpha(0);
                    noFavPlaceTV.setAlpha(0);
                    bottomLayout.setAlpha(0);
//                    reqLocationLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void initMap() {
        mapFragment.getView().setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(this);
    }

    void initGrid(){

        DBUtility utility = new DBUtility(this);
        ArrayList<FrequentlyVisitedLoc> places = utility.getFrequentlyVisitedPlaces();
        ArrayList<FavPlaceModel> model = new ArrayList<FavPlaceModel>();

        for(FrequentlyVisitedLoc obj:places){
            Log.i(TAG,"Location saved as fav is "+obj.place_name);
            LatLng latLng = new LatLng(obj.latitude,obj.longitude);
            FavPlaceModel m = new FavPlaceModel(obj.place_name,latLng,obj.address,obj.user_saved_name);
            model.add(m);
        }

        final FavPlacesListAdapter adapter = new FavPlacesListAdapter(this,R.layout.fav_list_cell,model);
        favPlaceLV.setAdapter(adapter);
        favPlaceLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                favLocationBtnClicked(position, adapter.getItem(position));
            }
        });

        favPlaceLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog alertDialog;
//                LayoutInflater inflater = WhereRUGoing.this.getLayoutInflater();
//                View layout=inflater.inflate(R.layout.alert_input,null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WhereRUGoing.this, AlertDialog.THEME_HOLO_LIGHT);
                alertDialogBuilder.setTitle(getResources().getString(R.string.delete_fav_confirmation));
//                alertDialogBuilder.setView(layout);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(WhereRUGoing.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                        deleteAFavLoc(adapter.getItem(position));
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        favPlaceLV.clearFocus();
                        favPlaceLV.clearChoices();
                        favPlaceLV.requestLayout();

                    }
                });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        if(model.size() == 0){
            noFavFrameLayout.setAlpha(1);

            return;
        }
        noFavFrameLayout.setAlpha(0);

    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dialog = Utils.showProgressDialog(WhereRUGoing.this,getString(R.string.str_fetching_loc_details));
            final PlaceArrayAdapter.PlaceAutocomplete item = autoCompleteAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            String attributedAddress = "<h4>"+item.address+"</h4>";
//            textView.setText(item.name+"\n"+ Html.fromHtml(attributedAddress));
            textView.setText(Html.fromHtml("<medium><font color='#0070C0'>" + item.name + "</font></medium><br><small><font color='#595959'>" + item.address + "</small></font>"));
            Log.i(TAG, "Selected: " + item.name);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(LocationService.googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);
//            reqLocationLayout.setVisibility(View.INVISIBLE);
//            favPlaceLV.setAlpha(1);
//            yourFavPlaceHeading.setAlpha(1);
            noFavPlaceTV.setAlpha(1);
            textView.setSelection(0);
            addToFav.setVisibility(View.VISIBLE);
            nextLayout.setVisibility(View.VISIBLE);
            reqALoc.setVisibility(View.INVISIBLE);
            hideKeyboard();
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if(dialog != null){
                dialog.dismiss();
            }
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
//                Toast.makeText(WhereRUGoing.this, Constants.ERROR_GETTING_PLACE,Toast.LENGTH_SHORT).show();
                showAToast(Constants.ERROR_GETTING_PLACE.toString());
                return;
            }
            // Selecting the first object buffer.
            selectedPlace = places.get(0);
            CharSequence attributions = places.getAttributions();
            Log.i(TAG,"Lat long of selected place is "+selectedPlace.getLatLng());
            //load map once place is selected
            initMap();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

//        Utils.checkGPSStatus(this);
    //    startService(new Intent(this, LocationService.class));
        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.LATEST_LOC);
        registerReceiver(myReceiver, intentFilter);
        autoOnGPS();
//        startLocationUpdates();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        resetUIToInitialState();
        unregisterReceiver(myReceiver);
//        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        //currently disabling for background
//        stopService(new Intent(this,LocationService.class));
//        unregisterReceiver(myReceiver);
        Log.e(TAG, "onPause");
        super.onPause();
//        if(mGoogleApiClient.isConnected())
////            stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
//        stopService(new Intent(this, LocationService.class));
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//            Log.d(TAG, "Location update resumed .....................");
//        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        hideKeyboard();
        if(bottomLayout.getAlpha() == 0) {
            resetUIToInitialState();
        }
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    void  resetUIToInitialState(){
        favPlaceLV.setAlpha(1);
        yourFavPlaceHeading.setAlpha(1);
        noFavPlaceTV.setAlpha(1);
        bottomLayout.setAlpha(1);
        mapFragment.getView().setVisibility(View.INVISIBLE);
        textView.setText("");
        textView.setFocusable(true);
        addToFav.setVisibility(View.INVISIBLE);
        reqALoc.setVisibility(View.VISIBLE);
        nextLayout.setVisibility(View.INVISIBLE);

//        reqLocationLayout.setVisibility(View.VISIBLE);

    }
    //Callback from adapter

    public void favLocationBtnClicked(int position,FavPlaceModel modelObj){
        hideKeyboard();
        Log.i(TAG, "Clicked position is " + modelObj.address);
        //case for adding new location
            Intent i = new Intent(WhereRUGoing.this,MainActivity.class);
            i.putExtra((getResources().getString(R.string.key_freq_visit_place)),modelObj.placeName);
            i.putExtra((getResources().getString(R.string.key_freq_visit_lat)), modelObj.latLng.latitude);
            i.putExtra((getResources().getString(R.string.key_freq_visit_long)), modelObj.latLng.longitude);
            i.putExtra((getResources().getString(R.string.key_freq_visit_address)), modelObj.address);
            i.putExtra((getResources().getString(R.string.key_curr_lat)), lastKnownLocation.getLatitude());
            i.putExtra((getResources().getString(R.string.key_curr_long)), lastKnownLocation.getLongitude());
            i.putExtra((getResources().getString(R.string.key_tagged_title)), modelObj.placeName);
                i.putExtra("from_shared_loc", false);


        startActivity(i);
    }

    //Custom Implementation
    void showCustomDialog(){
        final AlertDialog alertDialog;
        LayoutInflater inflater=WhereRUGoing.this.getLayoutInflater();
        View layout=inflater.inflate(R.layout.alert_input,null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle(getResources().getString(R.string.title_add_frequent_place));
        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(WhereRUGoing.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                addToFav.setSelected(true);
                ResourcesCompat.getDrawable(getResources(), R.drawable.added_to_fav, null);
                addAFavLocation();
                }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
       alertDialog = alertDialogBuilder.create();
        alertDialog.show();
     //   dialogTextBox = (EditText) alertDialog.findViewById(R.id.editText);
    }

    void onSearchBtnAction(){
        if(selectedPlace == null){
            Toast.makeText(this,getResources().getString(R.string.err_invalid_location_selected),Toast.LENGTH_SHORT).show();
        }
       else {
            //reusing the callback class
            //position will be 0
            FavPlaceModel obj = new FavPlaceModel(selectedPlace.getName().toString(),selectedPlace.getLatLng(),selectedPlace.getAddress().toString(),"");
            favLocationBtnClicked(0,obj);
        }
    }
    //insert the new tagged location to
    //database
    void addAFavLocation(){
        if(selectedPlace == null){
            Toast.makeText(this,getResources().getString(R.string.err_invalid_location_selected),Toast.LENGTH_SHORT).show();
        }
        else if(dialogTextBox != null && dialogTextBox.getText().toString().length() == 0){
            Toast.makeText(this,getResources().getString(R.string.err_invalid_fav_name),Toast.LENGTH_SHORT).show();
        }
        else{
            LatLng latLng = selectedPlace.getLatLng();
            FrequentlyVisitedLoc loc = new FrequentlyVisitedLoc();
            loc.user_saved_name = dialogTextBox.getText().toString();
            loc.place_name = selectedPlace.getName().toString();
            loc.latitude = latLng.latitude;
            loc.longitude = latLng.longitude;
            loc.address = selectedPlace.getAddress().toString();
            DBUtility utility = new DBUtility(this);
            Boolean res = utility.insertFavLocation(loc);
            initGrid();
            if(res == true) {
                Toast.makeText(this, getResources().getString(R.string.str_fav_loc_added), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, getResources().getString(R.string.str_fav_loc_added), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void deleteAFavLoc(FavPlaceModel model){
        DBUtility utility = new DBUtility(this);
        utility.deleteALoc(model);
        initGrid();
    }
    public void showAToast (String st){ //"Toast toast" is declared in the class
        try{ toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(this, st, Toast.LENGTH_LONG);
        }
        toast.show();  //finally display it
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        String placeName = getIntent().getStringExtra((getResources().getString(R.string.key_freq_visit_place)));
//        Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
//        Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
//
//        final LatLng latLng = new LatLng(lat, longitude);
//        Log.i(TAG, "Lat long for annotatiing is " + lat + "," + longitude);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(selectedPlace.getLatLng()).title(selectedPlace.getName().toString()));
        final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(selectedPlace.getLatLng(), 15);
        googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {

            }
        });
    }

    //Contact Picker

    void openContactPicker(){
//        Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
//        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//        startActivityForResult(pickContactIntent, CONTACT_PICKER_RESULT);

        Intent i = new Intent(this,ContactsActivity.class);
        startActivityForResult(i, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 111) {
            if (requestCode == 101) {
                String phoneNo = data.getStringExtra("mobNO");
                try {
                    callApiToRequestLocation(phoneNo);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void callApiToRequestLocation(String toPhoneNo) throws UnsupportedEncodingException {
        if(!Utils.isNetworkConnected(this)){
            Toast.makeText(this,getResources().getString(R.string.err_no_network),Toast.LENGTH_LONG).show();
            return;
        }
        dialog= Utils.showProgressDialog(this,"");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mobileNo = prefs.getString(getString(R.string.key_reg_no), "");

        String requestString = "mobileNo=" + URLEncoder.encode(mobileNo, "utf-8") + "&tomobileNo=" + URLEncoder.encode(String.valueOf(toPhoneNo), "utf-8");

        Log.i(TAG, "Sending location request callApiToRequestLocation " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.requestForLocation(requestString, this);
    }

    /***
     * API Callbacks
     */
    public void locationRequestedResponse(NetworkDataModel model) throws JSONException {
//       Utils.logLargeString("usersActivitiesResponse" + model.responseData.toString());
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
        if(jObject.get("result").equals("Failure")){
            Toast.makeText(this, jObject.getString("msg"), Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Toast.makeText(this, jObject.getString("msg"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub


            Location latestLoc = arg1.getParcelableExtra(getString(R.string.key_loc_changed));
            lastKnownLocation = latestLoc;
            if(lastKnownLocation!=null) {
                LatLng l = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                LatLngBounds bounds = LatLngBounds.builder().include(l).build();

                autoCompleteAdapter.setGoogleApiClient(LocationService.googleApiClient, bounds);
//                startLocationUpdates();

                Log.i(TAG, "Google Places API connected. " + lastKnownLocation);
            }
            else{
                Toast.makeText(WhereRUGoing.this,getString(R.string.str_unable_to_locate),Toast.LENGTH_LONG).show();
            }
        }

    }

    void autoOnGPS(){
        GoogleApiClient googleApiClient = null;

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(WhereRUGoing.this)
                    .addOnConnectionFailedListener(WhereRUGoing.this).build();
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
                                status.startResolutionForResult(WhereRUGoing.this, 1000);
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

//    private void turnGPSOff() {
//        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//        intent.putExtra("enabled", false);
//        this.sendBroadcast(intent);
//    }
}