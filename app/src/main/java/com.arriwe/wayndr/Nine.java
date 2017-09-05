package com.arriwe.wayndr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.Model.FavPlaceModel;
import com.arriwe.adapter.ContactsGridAdapter;
import com.arriwe.database.DBUtility;
import com.arriwe.database.FrequentlyVisitedLoc;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.ClsGeneral;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nineoldandroids.animation.Animator;
import com.sancsvision.arriwe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.arriwe.utility.StaticContacts.contactArrayList;

/**
 * Created by Lenovo on 12/3/2016.
 */

public class Nine extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {

    AlertDialog alertDialog;
    private GoogleMap mMap;
    public static int pos = 0;
    private LikeButton img_add_to_fav;
    Double lat,Lng;
    String name,address;
    EditText dialogTextBox;
    boolean fav_status = false;
    String user_saved_name = "";
    TextView textView_place_name,txt_address,txt_distance,txt_time,confirm_location,noContactsTextView;
    ImageView cross,img_back;
    ProgressDialog dialog = null;
    ArrayList<JSONObject> arrayList = new ArrayList<>();
    EditText username_input;
    RelativeLayout related_user,relative_layout_place,tipe_ralative,comfirm_location,tagTravelCountLayout;
    TextView tagCountTextView,addCountTextView;
    ImageView tickButton;
    TextView tvtickdone;
    LinearLayout donelayout;


    String TAG = "Nine.java";
    int tagCount = 0;
    int addCount = 0;
    String saved_name="";
    TextView textView4,dot,textView6;
    String myNo = "";
    SharedPreferences prefs;

    ContactsGridAdapter contactsGridAdapter;

    GridView gridview_contacts;

    double currLat = 0;
    double currLong = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nine);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            lat = b.getDouble("lat");
            Lng = b.getDouble("long");
            name = b.getString("place");
            address = b.getString("address");
            fav_status = b.getBoolean("fav_status");
            saved_name = b.getString("saved_name");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(Nine.this);
        myNo = prefs.getString(getResources().getString(R.string.key_reg_no), "");

        img_add_to_fav  = (LikeButton) findViewById(R.id.img_add_to_fav);
        textView_place_name = (TextView)findViewById(R.id.textView_place_name);
        txt_address = (TextView)findViewById(R.id.txt_address);
        txt_distance = (TextView)findViewById(R.id.txt_distance);
        txt_time = (TextView)findViewById(R.id.txt_time);
        confirm_location = (TextView)findViewById(R.id.confirm_location);
        cross = (ImageView)findViewById(R.id.cross);
        gridview_contacts = (GridView)findViewById(R.id.gridview_contacts);
        username_input = (EditText)findViewById(R.id.username_input);
        related_user = (RelativeLayout)findViewById(R.id.related_user);
        relative_layout_place = (RelativeLayout)findViewById(R.id.relative_layout_place);
        tipe_ralative = (RelativeLayout)findViewById(R.id.tipe_ralative);
        comfirm_location = (RelativeLayout)findViewById(R.id.comfirm_location);
        tagTravelCountLayout = (RelativeLayout) findViewById(R.id.tag_travel_layout);
        tagCountTextView = (TextView) findViewById(R.id.textView_tagged_count);
        addCountTextView = (TextView) findViewById(R.id.textView_add);
        noContactsTextView = (TextView) findViewById(R.id.textView_no_contacts);
        tickButton = (ImageView) findViewById(R.id.imageButton_tick);
        tvtickdone=(TextView)findViewById(R.id.tvtickdone);
        donelayout=(LinearLayout) findViewById(R.id.donelayout);
        img_back = (ImageView)findViewById(R.id.img_back);
        textView4 = (TextView)findViewById(R.id.textView4);
        //dot = (TextView)findViewById(R.id.dot);
        textView6 = (TextView)findViewById(R.id.textView6);
        related_user.setVisibility(View.GONE);
        tipe_ralative.setVisibility(View.GONE);
        relative_layout_place.setVisibility(View.VISIBLE);
        comfirm_location.setVisibility(View.VISIBLE);


        img_add_to_fav.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // img_add_to_fav.setLiked(true);
                        showCustomDialog(R.style.DialogAnimation_2);
                        //buildDialog(R.style.DialogAnimation_2,"Animated Dialog");
                    }
                },800);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                FavPlaceModel loc = new FavPlaceModel(name,new LatLng(lat,Lng),address,saved_name);
                deleteAFavLoc(loc);
            }
        });


        tickButton.setOnClickListener(this);
        tvtickdone.setOnClickListener(this);
        img_back.setOnClickListener(this);
        if(fav_status){
          //  img_add_to_fav.setImageResource(R.drawable.added_to_fav);
            img_add_to_fav.setLiked(true);
        }
        else {
           // img_add_to_fav.setImageResource(R.drawable.add_to_fav);
            img_add_to_fav.setLiked(false);
        }


        username_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String text = username_input.getText().toString().toLowerCase(Locale.getDefault());
                    contactsGridAdapter.filter(text);
                }
                catch (Exception e){
                    Log.e(TAG,e.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirm_location.setOnClickListener(this);
        cross.setOnClickListener(this);

        String clat = "null";
        String clon = "null";
        if (LocationService.LATEST_LOC_OBJ != null) {
            Location loc = LocationService.LATEST_LOC_OBJ;
            clat = String.valueOf(loc.getLatitude());
            clon = String.valueOf(loc.getLongitude());
        }


        if (!(clat.equals("null"))) {
            currLat = Double.parseDouble(clat);
        }
        if (!(clon.equals("null"))) {
            currLong = Double.parseDouble(clon);
        }

        textView_place_name.setText(name);
        txt_address.setText(address);
        
        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_frag);

        mapFragment.getMapAsync(this);


        Log.e("DataTesting: ","/ "+currLat+" / "+currLong+" / "+lat+" / "+Lng);

        findDistance(currLat,currLong,lat,Lng);
	    

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

    private void findDistance(double currLat,double currLong,double lat,double Lng){
      //  Log.d("urlof","http://maps.googleapis.com/maps/api/directions/json?origin="+currLat+","+currLong+"&destination="+lat+","+Lng+"&sensor=false");
        final ProgressDialog dialog = new ProgressDialog(Nine.this);
        dialog.setMessage("Calculating distance and time...");
        dialog.setCancelable(false);
        dialog.show();
        Ion.with(Nine.this)
                .load("http://maps.googleapis.com/maps/api/directions/json?origin="+currLat+","+currLong+"&destination="+lat+","+Lng+"&sensor=false")
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                dialog.dismiss();
                if(e!=null){
                    Log.e(TAG,e.toString());
                }
                if(result!=null){
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray routeArray = json.getJSONArray("routes");
                        JSONObject routes = routeArray.getJSONObject(0);

                        JSONArray newTempARr = routes.getJSONArray("legs");
                        JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                        JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                        JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                        Log.i("Diatance :", distOb.getString("text"));
                        Log.i("Time :", timeOb.getString("text"));

                        txt_time.setText(timeOb.getString("text"));
                        txt_distance.setText(distOb.getString("text"));

                    }
                    catch (Exception e1){
                        Log.e(TAG,e1.toString());
                    }

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle b = getIntent().getExtras();
        LatLng sydney = new LatLng(lat,Lng);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(sydney).title(name));
        final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(sydney, 15);
        googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {

            }
        });
    }
    void showCustomDialog(int animationSource){
        View layout=Nine.this.getLayoutInflater().inflate(R.layout.alert_input,null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle("Save this location as ?").setCancelable(false);
        alertDialogBuilder.setView(layout);
        // editText = (EditText) alertDialog.findViewById(R.id.edt);


        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                img_add_to_fav.setLiked(false);

            }
        });
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(dialogTextBox.getText().toString().equals(""))
                    img_add_to_fav.setLiked(false);
                else
                    addAFavLocation();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = animationSource;
        alertDialog.show();
        dialogTextBox = (EditText)alertDialog.findViewById(R.id.edit_dialog);
    }

    void addAFavLocation(){
        if(name == null){
            Toast.makeText(this,getResources().getString(R.string.err_invalid_location_selected),Toast.LENGTH_SHORT).show();
        }
        else if(dialogTextBox != null && dialogTextBox.getText().toString().length() == 0){
            Toast.makeText(this,getResources().getString(R.string.err_invalid_fav_name),Toast.LENGTH_SHORT).show();
        }
        else{
            LatLng latLng = new LatLng(lat,Lng);
            FrequentlyVisitedLoc loc = new FrequentlyVisitedLoc();
            saved_name = dialogTextBox.getText().toString();
            loc.user_saved_name = dialogTextBox.getText().toString();
            loc.place_name = name;
            loc.latitude = latLng.latitude;
            loc.longitude = latLng.longitude;
            loc.address = address;
            DBUtility utility = new DBUtility(this);
            Boolean res = utility.insertFavLocation(loc);
          //  initGrid();
            if(res) {
                Toast.makeText(this, getResources().getString(R.string.str_fav_loc_added), Toast.LENGTH_SHORT).show();
               // img_add_to_fav.setImageResource(R.drawable.added_to_fav);
                fav_status = true;
            }
            else{
                Toast.makeText(this, getResources().getString(R.string.str_fav_loc_added), Toast.LENGTH_SHORT).show();
              //  img_add_to_fav.setImageResource(R.drawable.added_to_fav);
                fav_status = true;
            }
        }
    }
    void deleteAFavLoc(FavPlaceModel model){
        DBUtility utility = new DBUtility(this);
        utility.deleteALoc(model);
        fav_status = false;
     //   img_add_to_fav.setImageResource(R.drawable.add_to_fav);
        Toast.makeText(this, "Successfully Remove", Toast.LENGTH_SHORT).show();

       // initGrid();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_location:

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                final Boolean confirm_again = prefs.getBoolean("confirm_again",true);

                if (confirm_again)
                {

                    final Dialog dialog = new Dialog(Nine.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.one);

                    Button okButton = (Button) dialog.findViewById(R.id.button_nexttipsecond);
                    final CheckBox confirmAgain = (CheckBox) dialog.findViewById(R.id.button_nexttipdontshow);

                    okButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            //Perfome Action
                            if (confirmAgain.isChecked())
                            {
                                SharedPreferences.Editor editor=prefs.edit();
                                editor.putBoolean("confirm_again",false);
                                editor.apply();
                            }

                            arrayList.clear();
                            arrayList = new ArrayList<>();
                            FavPlaceModel obj = new FavPlaceModel(name,new LatLng(lat,Lng),address,"");
                            favLocationBtnClicked(obj);

                            dialog.dismiss();
                        }
                    });


                    dialog.show();
                }
                else
                {
                    arrayList.clear();
                    arrayList = new ArrayList<>();
                    FavPlaceModel obj = new FavPlaceModel(name,new LatLng(lat,Lng),address,"");
                    favLocationBtnClicked(obj);
                }


                break;
            case R.id.cross:
                onBackPressed();
                break;



            case R.id.tvtickdone:
                try {
                    callApiToTagOrAddToTravel();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.imageButton_tick:
                try {
                    callApiToTagOrAddToTravel();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_back:
                related_user.setVisibility(View.GONE);
                related_user.clearAnimation();
                tipe_ralative.clearAnimation();
                relative_layout_place.clearAnimation();
                comfirm_location.clearAnimation();
                tipe_ralative.setVisibility(View.GONE);
                relative_layout_place.setVisibility(View.VISIBLE);
                comfirm_location.setVisibility(View.VISIBLE);

//                Log.d("get",textView_place_name.getText().toString());

                break;
        }
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
        }finally {
            String taggedArray = "",travellingArray = "";
            for(int i=0;i<taggedArrayToSend.size();i++){
                if(i!=taggedArrayToSend.size()-1)
                    taggedArray = taggedArray+taggedArrayToSend.get(i)+",";
                else
                    taggedArray = taggedArray+taggedArrayToSend.get(i);
               // Log.d("tagged",taggedArrayToSend.get(i));
            }
            for(int i=0;i<travellingWithArrayToSend.size();i++){
                if(i!=travellingWithArrayToSend.size()-1)
                    travellingArray = travellingArray+travellingWithArrayToSend.get(i)+",";
                else
                    travellingArray = travellingArray+travellingWithArrayToSend.get(i);
               // Log.d("traving",travellingWithArrayToSend.get(i));
            }
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
                String requestString = "taggedby=" + URLEncoder.encode(taggedBy, "utf-8") + "&to_lat=" +
                        URLEncoder.encode(String.valueOf(lat), "utf-8") + "&to_lon=" +
                        URLEncoder.encode(String.valueOf(longitude), "utf-8") + "&to_address=" +
                        URLEncoder.encode(address, "utf-8") + "&from_lat=" +
                        URLEncoder.encode(String.valueOf(latOrigin), "utf-8") + "&from_lon=" +
                        URLEncoder.encode(String.valueOf(longOrigin), "utf-8") + "&title=" +
                        URLEncoder.encode(String.valueOf(placeName), "utf-8") + "&taggedto=" +
                        URLEncoder.encode(String.valueOf(taggedArray), "utf-8") + "&travelwith=" +
                        URLEncoder.encode(String.valueOf(travellingArray), "utf-8");

                Log.i(TAG, "Sending taaging details from callApiToTagOrAddToTravel " + requestString);
                dialog = Utils.showProgressDialog(this, "");
                NetworkEngine engine = new NetworkEngine();
                engine.sendTagAndTravelDetails(requestString, this);


                //  TaggedWithIon(taggedBy,String.valueOf(lat),String.valueOf(longitude),address,String.valueOf(latOrigin),String.valueOf(longOrigin),String.valueOf(placeName),String.valueOf(taggedArray),String.valueOf(travellingArray));

            }
        }

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
            Intent i = new Intent(Nine.this, Eight.class);
            i.putExtra("setfragment","three");
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
    public void favLocationBtnClicked(FavPlaceModel modelObj){
        Log.i(TAG, "Clicked position is " + modelObj.address);
        //case for adding new location
        /*Intent i = new Intent(Nine.this,MainActivity.class);
        i.putExtra((getResources().getString(R.string.key_freq_visit_place)),modelObj.placeName);
        i.putExtra((getResources().getString(R.string.key_freq_visit_lat)), modelObj.latLng.latitude);
        i.putExtra((getResources().getString(R.string.key_freq_visit_long)), modelObj.latLng.longitude);
        i.putExtra((getResources().getString(R.string.key_freq_visit_address)), modelObj.address);
        i.putExtra((getResources().getString(R.string.key_curr_lat)), currLat);
        i.putExtra((getResources().getString(R.string.key_curr_long)), currLong);
        i.putExtra((getResources().getString(R.string.key_tagged_title)), modelObj.placeName);
        i.putExtra("from_shared_loc", false);
        startActivity(i);*/

        related_user.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInRight).duration(50).interpolate(new AccelerateDecelerateInterpolator()).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                related_user.clearAnimation();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(related_user);
        YoYo.with(Techniques.SlideOutLeft).duration(50).interpolate(new AccelerateDecelerateInterpolator()).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                relative_layout_place.clearAnimation();
                YoYo.with(Techniques.SlideInRight).duration(50).playOn(relative_layout_place);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(relative_layout_place);
        relative_layout_place.setVisibility(View.GONE);
        
        tipe_ralative.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn).duration(1000).interpolate(new AccelerateDecelerateInterpolator()).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tipe_ralative.clearAnimation();
                //YoYo.with(Techniques.SlideInUp).duration(500).playOn(tipe_ralative);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(tipe_ralative);

        YoYo.with(Techniques.FadeOut).duration(1000).interpolate(new AccelerateDecelerateInterpolator()).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                comfirm_location.clearAnimation();
                YoYo.with(Techniques.FadeIn).duration(500).playOn(comfirm_location);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(comfirm_location);
        comfirm_location.setVisibility(View.GONE);

        try {
        //    initContacts();
            String status = ClsGeneral.getPreferences(Nine.this,"refrescontact");
            if(contactArrayList.size() < 1 || status.equalsIgnoreCase("yes")){
                initContacts();
                ClsGeneral.setPreferences(Nine.this,"refrescontact","no");
            }
            else {
                arrayList.clear();
                arrayList = new ArrayList<>();
                arrayList.addAll(contactArrayList);
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
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }
    void initContacts() throws JSONException, UnsupportedEncodingException {
        ArrayList<String> contactNos = Utils.getContactsFromPhone(this);
        callApiToGetRegisteredContacts(contactNos);
    }
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
          engine.getUsersUsingWayndr(requestString, this);
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
        //initializeGridView(arrayList);
        contactsGridAdapter.notifyDataSetChanged();
        gridview_contacts.invalidateViews();
        calculateTagAndAddCount();
    }
    private void tagOrAddContactToTravelWithBack(int type, int position) throws JSONException {
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
            if(travellingStatus) {
                jsonObject.put(getString(R.string.key_is_added_for_travel), false);
            }


        } else {
            Boolean travellingStatus = false;
            if (jsonObject.has(getResources().getString(R.string.key_is_added_for_travel))) {
                travellingStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_added_for_travel));
            }

            //if user try to add make sure tagging is set to false
            jsonObject.put(getString(R.string.key_is_tagged), false);
            if(travellingStatus)
                    jsonObject.put(getString(R.string.key_is_added_for_travel),false);
        }

        arrayList.set(position, jsonObject);
        //initializeGridView(arrayList);
        contactsGridAdapter.notifyDataSetChanged();
        gridview_contacts.invalidateViews();
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
//            if (jsonObject.has(getString(R.string.key_is_added_for_travel))) {
//                Boolean travelWithRes = jsonObject.getBoolean(getString(R.string.key_is_added_for_travel));
//                if (travelWithRes)
//                    addCount++;
//            }
        }
        tagTravelCountLayout.setAlpha(1);
        tagCountTextView.setText("TAGS  . " + tagCount);
        addCountTextView.setText("ADDED " + addCount);
        textView4.setVisibility(View.GONE);
        //dot.setVisibility(View.GONE);
        textView6.setVisibility(View.GONE);
        if (tagCount==0)
            donelayout.setVisibility(View.INVISIBLE);
        else if (tagCount>0)
            donelayout.setVisibility(View.VISIBLE);
    }

    void initializeGridView(ArrayList<JSONObject> list) {

        //gridview_contacts = (GridView) findViewById(R.id.gridview_contacts);

        /*for(int i=0;i<list.size();i++){
            try {
                JSONObject jsonObject = list.get(i);
                jsonObject.put("",false);
                list.set(i,jsonObject);
            }
            catch (Exception e){
                Log.e("Error",e.toString());
            }

        }*/

        contactsGridAdapter = new ContactsGridAdapter(Nine.this,list);

        gridview_contacts.setAdapter(contactsGridAdapter);


        gridview_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Item long pressed");
//                try {
//                    tagOrAddContactToTravelWith(2, position);
//                    pos = position;
//                } catch (JSONException e) {
//                    e.printStackTrace();
               // }

                return true;
            }
        });

        //tag a person
        gridview_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Clicked to tag");
                try {
                    tagOrAddContactToTravelWith(1, position);
                    pos = position;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        for(int i=0;i<arrayList.size();i++){
            try {
                tagOrAddContactToTravelWithBack(1,i);
                tagOrAddContactToTravelWithBack(2,i);
            }
            catch (Exception e){
                Log.e(TAG,e.toString());
            }

        }
       // Intent intent = new Intent(Nine.this,Eight.class);
        //startActivity(intent);
       // overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }
    
    
    
}
