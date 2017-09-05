//package com.arriwe.wayndr;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.location.Location;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.preference.PreferenceManager;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomSheetBehavior;
//import android.text.Editable;
//import android.text.Html;
//import android.text.InputFilter;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.arriwe.adapter.ContactsGridAdapter;
//import com.arriwe.networkmanagers.NetworkDataModel;
//import com.arriwe.networkmanagers.NetworkEngine;
//import com.arriwe.utility.Constants;
//import com.arriwe.utility.LocationService;
//import com.arriwe.utility.RoundedImageView;
//import com.arriwe.utility.Utils;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.koushikdutta.async.future.FutureCallback;
//import com.koushikdutta.ion.Ion;
//import com.sancsvision.arriwe.R;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.w3c.dom.Document;
//
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import static com.arriwe.networkmanagers.NetworkEngine.TAG;
//import static com.arriwe.utility.StaticContacts.contactArrayList;
//import static com.arriwe.utility.StaticContacts.convertDpToPixel;
//import static com.arriwe.wayndr.Nine.pos;
//public class GroupTravelDetail extends Activity implements  OnMapReadyCallback,GoogleMap.OnMapClickListener, View.OnClickListener {
//
//    ListView listView;
//    JSONArray travellersDetail = null;
//    String trip_id = null;
//    GoogleMap mapObj;
//    TextView travellingLocTV = null;
//    TextView travellingLocAddress = null;
//    TextView invitedNos = null;
//    TextView groupCreateByTV = null;
//    Boolean isExpanded = false;
//    private  Bitmap bmp;
//    int recursiveIndex = 0;
//    TextView accept,pending,declined,textView_distancetravelled,textView_timetaken,textView_distanceleft;
//    ImageView add_more_people;
//
//    LinearLayout ll_tagger,cross;
//    RelativeLayout edt_search;
//    View view_search;
//    GridView gridview_contacts;
//    RelativeLayout ll_bottemsheet;
//    ArrayList<JSONObject> arrayList = new ArrayList<>();
//    TextView noContactsTextView;
//    ProgressDialog dialog = null;
//    ContactsGridAdapter contactsGridAdapter;
//    EditText username_input;
//    LinearLayout ll_main;
//    TextView textView_place_name;
//    int accepts_status = 0,pending_status = 0,declined_status = 0;
//    SharedPreferences prefs;
//    View view;
//    // BottomSheetBehavior variable
//    private BottomSheetBehavior bottomSheetBehavior;
//    private List<String> already_added_person = new ArrayList<>();
//    private List<String> already_added_person_fix = new ArrayList<>();
//    private  ArrayList<String> travellingWithArrayToSend;
//    private TextView txt_done_add_remove;
//    private TextView directions;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.group_travel);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        initMap();
//        //   travellingLocTV = (TextView) findViewById(R.id.textView2);
//        travellingLocAddress = (TextView) findViewById(R.id.dest_address);
//        directions = (TextView)findViewById(R.id.directions);
//        textView_place_name = (TextView) findViewById(R.id.textView_place_name);
//        view = findViewById(R.id.view);
//        txt_done_add_remove = (TextView)findViewById(R.id.txt_done_add_remove);
//        invitedNos = (TextView) findViewById(R.id.textView_invited_nos);
//        groupCreateByTV = (TextView) findViewById(R.id.textView_group_created_by);
//        username_input = (EditText)findViewById(R.id.username_input);
//        noContactsTextView = (TextView)findViewById(R.id.textView_no_contacts);
//        accept = (TextView)findViewById(R.id.accept);
//        pending = (TextView)findViewById(R.id.pending);
//        declined = (TextView)findViewById(R.id.declined);
//        add_more_people = (ImageView)findViewById(R.id.add_more_people);
//        ll_tagger = (LinearLayout)findViewById(R.id.ll_tagger);
//        textView_distancetravelled = (TextView)findViewById(R.id.textView_distancetravelled);
//        textView_timetaken = (TextView)findViewById(R.id.textView_timetaken);
//        textView_distanceleft = (TextView)findViewById(R.id.textView_distanceleft);
//        ll_main = (LinearLayout)findViewById(R.id.ll_main);
//        cross = (LinearLayout)findViewById(R.id.cross);
//        edt_search = (RelativeLayout)findViewById(R.id.edt_search);
//        view_search = findViewById(R.id.view_search);
//        gridview_contacts = (GridView)findViewById(R.id.gridview_contacts);
//        ll_bottemsheet = (RelativeLayout)findViewById(R.id.ll_bottemsheet);
//
//        add_more_people.setOnClickListener(this);
//        pending.setOnClickListener(this);
//        declined.setOnClickListener(this);
//        accept.setOnClickListener(this);
//        cross.setOnClickListener(this);
//        txt_done_add_remove.setOnClickListener(this);
//        directions.setOnClickListener(this);
//
//
//        username_input.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                try {
//                    String text = username_input.getText().toString().toLowerCase(Locale.getDefault());
//                    contactsGridAdapter.filter(text);
//                }
//                catch (Exception e){
//                    Log.e(TAG,e.toString());
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        String detailStr = getIntent().getStringExtra("group_details");
//        final String travellingLoc = getIntent().getStringExtra("grp_travel_loc");
//        textView_place_name.setText(travellingLoc);
//
//        // travellingLocTV.setText(travellingLoc);
//        travellingLocAddress.setText(getIntent().getStringExtra("grp_destination_address"));
//
//        JSONObject groupDetailsObj;
//        try {
//            groupDetailsObj = new JSONObject(detailStr);
//            String taggedBy = prefs.getString(getString(R.string.key_reg_no), "");
//
//            if(!groupDetailsObj.getString("group_owner_mob").equals(taggedBy)){
//                add_more_people.setVisibility(View.GONE);
//                view.setVisibility(View.GONE);
//            }
//            else {
//                /*add_more_people.setVisibility(View.VISIBLE);
//                view.setVisibility(View.VISIBLE);*/
//                add_more_people.setVisibility(View.GONE);
//                view.setVisibility(View.GONE);
//            }
//
//
//
//
//
//
//            groupCreateByTV.setText(Html.fromHtml("<strong>"+groupDetailsObj.getString("group_owner_name")+"</strong> created the journey"));
//            invitedNos.setText(getIntent().getStringExtra("group_invited_nos")+" contacts invited");
//            travellersDetail = groupDetailsObj.getJSONArray("traveller");
//            trip_id = groupDetailsObj.getString("tripid");
//
//            for(int i=0;i<travellersDetail.length();i++){
//                JSONObject jsonObject = travellersDetail.getJSONObject(i);
//                String status = jsonObject.getString("status");
//                already_added_person.add(jsonObject.getString("traveller_mob"));
//                already_added_person_fix.add(jsonObject.getString("traveller_mob"));
//                if(status.equalsIgnoreCase("Accepted")){
//                    accepts_status++;
//                }
//                else if(status.equalsIgnoreCase("Requested")){
//                    pending_status++;
//                }
//                else {
//                    declined_status++;
//                }
//            }
//
//            accept.setText(accepts_status+" ACCEPTED");
//            pending.setText(pending_status+" PENDING");
//            declined.setText(declined_status+" DECLINED");
//            accept.setTextColor(Color.parseColor("#1d80c7"));
//
//            showPerson(1);
//
//
//
//            textView_distancetravelled.setText(groupDetailsObj.getString("travelled_distance"));
//            // textView_timetaken.setText(groupDetailsObj.getString());
//            // textView_distanceleft.setText(groupDetailsObj.getString(""));
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            //    initContacts();
//            if(contactArrayList.size() < 1){
//                initContacts();
//            }
//            else {
//                arrayList.clear();
//                arrayList = new ArrayList<>();
//                arrayList.addAll(contactArrayList);
//                initializeGridView(arrayList);
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        /*listView = (ListView) findViewById(R.id.listView_group_travel);
//        listView.setAdapter(new GroupTravelListArrayAdapter(this,R.layout.list_item_tagged,travellersDetail));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                JSONObject obj = null;
//                try {
//                    obj = travellersDetail.getJSONObject(position);
//                    HashMap map = new HashMap();
//                    map.put(getString(R.string.key_traveller_name), obj.get(getString(R.string.key_traveller_name)).toString());
//                    map.put(getString(R.string.key_travelling_loc), travellingLoc);
//                    map.put(getString(R.string.key_curr_address), obj.get("current_address").toString());
//                    map.put(getString(R.string.key_curr_lat), obj.get("current_lat").toString());
//                    map.put(getString(R.string.key_curr_long), obj.get("current_lon").toString());
//                    map.put(getString(R.string.start_lat), "0");
//                    map.put(getString(R.string.start_long), "0");
//                    map.put(getString(R.string.key_traveller_pic), obj.get("traveller_pic").toString());
//                    map.put(getString(R.string.key_distance_travelled), obj.get("travelled_distance").toString());
//                    map.put(getString(R.string.key_distance_left), obj.get("remaining_distance").toString());
//                    map.put(getString(R.string.key_time_taken), obj.get("travelled_time").toString());
//                    map.put(getString(R.string.key_time_left), obj.get("remaining_time").toString());
//                    map.put("group_dest_lat", getIntent().getStringExtra("group_dest_lat"));
//                    map.put("group_dest_lon", getIntent().getStringExtra("group_dest_lon"));
//                    map.put("trip_id", getIntent().getStringExtra("trip_id"));
//
//
//
//                    Intent intent = new Intent(GroupTravelDetail.this, IndividualTravelDetail.class);
//                    intent.putExtra("data", map);
//                    startActivity(intent);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });*/
//
//    }
//    private void showPerson(int show_status){
//        try {
//            for(int i=0;i<travellersDetail.length();i++){
//                JSONObject jsonObject = travellersDetail.getJSONObject(i);
//                if(show_status==1){
//                    if(jsonObject.getString("status").equalsIgnoreCase("Accepted")){
//                        LinearLayout linearLayout = new LinearLayout(this);
//                        linearLayout.setOrientation(LinearLayout.VERTICAL);
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams.gravity = Gravity.CENTER;
//                        layoutParams.setMargins(45,0,0,0);
//
//                        linearLayout.setLayoutParams(layoutParams);
//
//                        RoundedImageView circleImageView = new RoundedImageView(this);
//                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(convertDpToPixel(40,this),convertDpToPixel(40,this));
//                        layoutParams1.setMargins(10,10,10,10);
//                        layoutParams1.gravity = Gravity.CENTER;
//                        circleImageView.setLayoutParams(layoutParams1);
//                        circleImageView.setImageUrl(Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_pic"));
//
//                        TextView textView = new TextView(this);
//                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams2.gravity = Gravity.CENTER;
//                        layoutParams2.setMargins(0,10,0,0);
//                        textView.setLayoutParams(layoutParams2);
//                        textView.setAllCaps(true);
//                        textView.setTextSize(12.0f);
//                        textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
//                        textView.setMaxLines(1);
//                        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                        textView.setText(jsonObject.getString("traveller_name"));
//
//                        TextView textViewremaing = new TextView(this);
//                        LinearLayout.LayoutParams layoutParamsreming = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParamsreming.gravity = Gravity.CENTER;
//                        layoutParamsreming.setMargins(0,10,0,0);
//                        textViewremaing.setLayoutParams(layoutParamsreming);
//                        textViewremaing.setTextSize(10.0f);
//                        textViewremaing.setText(jsonObject.getString("remaining_time"));
//                        textViewremaing.setVisibility(View.VISIBLE);
//
//                        linearLayout.addView(circleImageView);
//                        linearLayout.addView(textView);
//                        linearLayout.addView(textViewremaing);
//
//                        ll_tagger.addView(linearLayout);
//                    }
//                }
//                else if(show_status==2){
//                    if(jsonObject.getString("status").equalsIgnoreCase("Requested")){
//                        LinearLayout linearLayout = new LinearLayout(this);
//                        linearLayout.setOrientation(LinearLayout.VERTICAL);
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams.gravity = Gravity.CENTER;
//                        layoutParams.setMargins(45,0,0,0);
//                        linearLayout.setLayoutParams(layoutParams);
//
//                        RoundedImageView circleImageView = new RoundedImageView(this);
//                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(convertDpToPixel(40,this),convertDpToPixel(40,this));
//                        layoutParams1.setMargins(10,10,10,10);
//                        layoutParams1.gravity = Gravity.CENTER;
//                        circleImageView.setLayoutParams(layoutParams1);
//                        circleImageView.setImageUrl(Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_pic"));
//
//                        TextView textView = new TextView(this);
//                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams2.gravity = Gravity.CENTER;
//                        layoutParams2.setMargins(0,10,0,0);
//                        textView.setLayoutParams(layoutParams2);
//                        textView.setAllCaps(true);
//                        textView.setTextSize(12.0f);
//                        textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
//                        textView.setMaxLines(1);
//                        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                        textView.setText(jsonObject.getString("traveller_name"));
//
//                        TextView textViewremaing = new TextView(this);
//                        LinearLayout.LayoutParams layoutParamsreming = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParamsreming.gravity = Gravity.CENTER;
//                        layoutParamsreming.setMargins(0,10,0,0);
//                        textViewremaing.setLayoutParams(layoutParamsreming);
//                        textViewremaing.setTextSize(10.0f);
//                        textViewremaing.setText(jsonObject.getString("remaining_time"));
//                        textViewremaing.setVisibility(View.GONE);
//
//                        linearLayout.addView(circleImageView);
//                        linearLayout.addView(textView);
//                        linearLayout.addView(textViewremaing);
//
//                        ll_tagger.addView(linearLayout);
//                    }
//                }
//                else {
//                    if(!jsonObject.getString("status").equalsIgnoreCase("Requested") && !jsonObject.getString("status").equalsIgnoreCase("Accepted")){
//                        LinearLayout linearLayout = new LinearLayout(this);
//                        linearLayout.setOrientation(LinearLayout.VERTICAL);
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams.gravity = Gravity.CENTER;
//                        layoutParams.setMargins(45,0,0,0);
//                        linearLayout.setLayoutParams(layoutParams);
//
//                        RoundedImageView circleImageView = new RoundedImageView(this);
//                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(convertDpToPixel(40,this),convertDpToPixel(40,this));
//                        layoutParams1.setMargins(10,10,10,10);
//                        layoutParams1.gravity = Gravity.CENTER;
//                        circleImageView.setLayoutParams(layoutParams1);
//                        circleImageView.setImageUrl(Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_pic"));
//
//                        TextView textView = new TextView(this);
//                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams2.gravity = Gravity.CENTER;
//                        layoutParams2.setMargins(0,10,0,0);
//                        textView.setLayoutParams(layoutParams2);
//                        textView.setAllCaps(true);
//                        textView.setTextSize(12.0f);
//                        textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
//                        textView.setMaxLines(1);
//                        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                        textView.setText(jsonObject.getString("traveller_name"));
//
//                        TextView textViewremaing = new TextView(this);
//                        LinearLayout.LayoutParams layoutParamsreming = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParamsreming.gravity = Gravity.CENTER;
//                        layoutParamsreming.setMargins(0,10,0,0);
//                        textViewremaing.setLayoutParams(layoutParamsreming);
//                        textViewremaing.setTextSize(10.0f);
//                        textViewremaing.setText(jsonObject.getString("remaining_time"));
//
//                        linearLayout.addView(circleImageView);
//                        linearLayout.addView(textView);
//                        linearLayout.addView(textViewremaing);
//
//
//                        ll_tagger.addView(linearLayout);
//                    }
//                }
//
//            }
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    void initContacts() throws JSONException, UnsupportedEncodingException {
//        ArrayList<String> contactNos = Utils.getContactsFromPhone(this);
//        callApiToGetRegisteredContacts(contactNos);
//    }
//    void callApiToGetRegisteredContacts(ArrayList<String> phoneNOs) throws JSONException, UnsupportedEncodingException {
//
//
//        if (!Utils.isNetworkConnected(this)) {
//            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
//            return;
//        }
//        dialog = Utils.showProgressDialog(this, "Loading your contacts");
//        String requestString = "phoneNos=" + URLEncoder.encode(String.valueOf(phoneNOs), "utf-8");
//        NetworkEngine engine = new NetworkEngine();
//        engine.getUsersUsingWayndr(requestString, this);
//    }
//    /***
//     * API Callbacks
//     */
//    public void registeredContactsResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
//        Log.i(TAG, "registeredContactsResponse" + model.responseData.toString());
//        if (dialog != null) {
//            dialog.dismiss();
//            dialog = null;
//        }
//        if (model.responseFailed) {
//            noContactsTextView.setAlpha(1);
//            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        JSONObject jObject = new JSONObject(model.responseData.toString());
//
//        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
//        if (map.containsKey("result")) {
//            if ((map.get("result").equals("Fail"))) {
//                noContactsTextView.setAlpha(1);
//                return;
//            }
//        }
//        JSONArray jsonArray = jObject.getJSONArray("data");
//
//        arrayList = new ArrayList<JSONObject>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            arrayList.add(jsonArray.getJSONObject(i));
//            Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
//        }
//        initializeGridView(arrayList);
//        // push location
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String regNo = prefs.getString(getString(R.string.key_reg_no), "");
//        Location loc = LocationService.LATEST_LOC_OBJ;
//
//
//    }
//    private void tagOrAddContactToTravelWith(int type, int position) throws JSONException {
//        //get selected user
//        JSONObject jsonObject = arrayList.get(position);
//
//
//        if (type == 1) {
//            //if that person is already added from travel
//            //and then click again, unadd him
//            Boolean travellingStatus = false;
//            if (jsonObject.has(getResources().getString(R.string.key_is_added_for_travel))) {
//                travellingStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_added_for_travel));
//            }
//            //if user try to tag make sure first its removed from adding
//            if(travellingStatus) {
//                jsonObject.put(getString(R.string.key_is_added_for_travel), false);
//
//                //already_added_person.remove(position);
//            }
//            /*else {
//                Boolean taggedStatus = false;
//                if (jsonObject.has(getResources().getString(R.string.key_is_tagged))) {
//                    taggedStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_tagged));
//                }
//                jsonObject.put(getString(R.string.key_is_tagged), !taggedStatus);
//            }*/
//
//        } else {
//            Boolean travellingStatus = false;
//            if (jsonObject.has(getResources().getString(R.string.key_is_added_for_travel))) {
//                travellingStatus = jsonObject.getBoolean(getResources().getString(R.string.key_is_added_for_travel));
//            }
//
//            //if user try to add make sure tagging is set to false
//            jsonObject.put(getString(R.string.key_is_tagged), false);
//            jsonObject.put(getString(R.string.key_is_added_for_travel), !travellingStatus);
//        }
//
//        arrayList.set(position, jsonObject);
//        //initializeGridView(arrayList);
//        contactsGridAdapter.notifyDataSetChanged();
//        gridview_contacts.invalidateViews();
//        // calculateTagAndAddCount();
//    }
//    void initializeGridView(ArrayList<JSONObject> list) {
//        contactsGridAdapter = new ContactsGridAdapter(GroupTravelDetail.this,list,0,already_added_person);
//        gridview_contacts.setAdapter(contactsGridAdapter);
//        gridview_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "Item long pressed");
//                try {
//                    tagOrAddContactToTravelWith(2, position);
//                    pos = position;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                return true;
//            }
//        });
//
//        //tag a person
//        gridview_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "Clicked to tag");
//                try {
//                    tagOrAddContactToTravelWith(1, position);
//
//                    JSONObject jsonObject = arrayList.get(position);
//                    try {
//                        if(already_added_person.contains(jsonObject.getString("mobile"))){
//                            already_added_person.remove(jsonObject.getString("mobile"));
//                        }
//
//                    }
//                    catch (Exception e){
//                        Log.e(TAG,e.toString());
//                    }
//
//                    //  pos = position;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//
//    void initMap() {
//
//        final RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
//        final  MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        final  MapFragment mapFragmentbig = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map_big);
//        mapFragment.getMapAsync(this);
//        mapFragmentbig.getMapAsync(this);
//        mapFragmentbig.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                ll_main.setVisibility(View.VISIBLE);
//            }
//        });
//        mapFragment.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                ll_main.setVisibility(View.GONE);
//                /*if(!isExpanded) {
//                    ll_main.setVisibility(View.GONE);
//                    isExpanded = true;
//                }
//                else{
//                    ll_main.setVisibility(View.VISIBLE);
//             //       backgroundLayout.bringToFront();
//                    isExpanded = false;
//                }*/
//            }
//        });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//
////     googleMap.setOnMapClickListener(GroupTravelDetail.this);
//        //add destination marker first
//        mapObj = googleMap;
//        LatLng destLatLng = null;
//        double lat;
//        double lon;
//        String place ;
//        String travellerImgUrl;
//
//     //   final GoogleMap map = googleMap;
//
//        lat =  Double.valueOf(getIntent().getStringExtra("group_dest_lat"));
//        lon =  Double.valueOf(getIntent().getStringExtra("group_dest_lon"));
//        place =getIntent().getStringExtra("grp_travel_loc");
//
//        destLatLng = new LatLng(lat, lon);
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(destLatLng)
//                .title("Destination:-" + place));
//        LatLng userLatLng = null;
//
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String mob = prefs.getString(getString(R.string.key_reg_no), "");
//
//
//        for (int i =0;i<travellersDetail.length() ;i ++){
//            try {
//                JSONObject obj = travellersDetail.getJSONObject(i);
////                final String userProfilePic = obj.getString("traveller_pic");
//                String status = obj.getString("status");
//                if(status.equalsIgnoreCase("Accepted")){
//
//                    String pic = obj.getString("traveller_pic");
//                    final String userProfilePic =  pic;//.replace("resources/user", "resources/user/50x50");
//                    String latStr;
//                    String lonStr;
////                if(LocationService.LATEST_LOC_OBJ != null){
////                    Location loc = LocationService.LATEST_LOC_OBJ;
////                    latStr = String.valueOf(loc.getLatitude());
////                    lonStr = String.valueOf(loc.getLongitude());
////                }
////                else {
//                    latStr = obj.getString("current_lat");
//                    lonStr = obj.getString("current_lon");
//                    if(latStr.equals("null") || lonStr.equals("null"))
//                        continue;
////                }
//             /*   if(latStr.equals("null")){
//                    latStr = "12.9545163";
//                }
//                if(lonStr.equals("null")){
//                    lonStr = "77.3500498";
//                }*/
//                    double usersLat =  Double.valueOf(latStr);
//                    double usersLon = Double.valueOf(lonStr);
//
//                    String usersPlace = (obj.get("current_title")).toString();
//                    userLatLng = new LatLng(usersLat, usersLon);
//
//
//
//                    if(obj.getString("traveller_mob").equals(mob)){
//                        route(userLatLng,destLatLng,GMapV2Direction.MODE_DRIVING);
//                    }
//                    setMapMarker(userProfilePic,mapObj,userLatLng);
//                }
//
//
//
//
//
//
////                googleMap.addMarker(new MarkerOptions()
////                        .position(userLatLng)
////                        .title(usersPlace));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if(destLatLng != null) {
//            //Zoom to show current lat and long
//            //if that is unavailable,show on destination
//            LatLng zoomVal = userLatLng;
//            if(zoomVal == null || (zoomVal.equals("null"))){
//                zoomVal = destLatLng;
//            }
//
//            final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(zoomVal, 20);
//            googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
//                @Override
//                public void onFinish() {
//                    //move the marker to top of screen
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            });
//        }
//
//    }
//
//    @Override
//    public void onMapClick(LatLng latLng) {
//
//    }
//
//    protected void route(final LatLng sourcePosition, final LatLng destPosition, String mode) {
//        final Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//                try {
//                    Document doc = (Document) msg.obj;
//                    GMapV2Direction md = new GMapV2Direction();
//                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
//                    PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.parseColor("#0070c0"));
//
//                    for (int i = 0; i < directionPoint.size(); i++) {
//                        rectLine.add(directionPoint.get(i));
//                    }
//                    Polyline polylin = mapObj.addPolyline(rectLine);md.getDurationText(doc);
//                    int DISTANCE_BETWEEN = 0;
//                    int CAMERA_ZOOM = 0;
//
//                    if(null!=rectLine) {
//                        // mMap.addPolyline(polyLineOptions);
//                        float totalDistance = 0;
//                        for(int k = 1; k < rectLine.getPoints().size(); k++) {
//                            Location currLocation = new Location("this");
//                            currLocation.setLatitude(rectLine.getPoints().get(k).latitude);
//                            currLocation.setLongitude(rectLine.getPoints().get(k).longitude);
//                            Location lastLocation = new Location("that");
//                            lastLocation.setLatitude(rectLine.getPoints().get(k-1).latitude);
//                            lastLocation.setLongitude(rectLine.getPoints().get(k-1).longitude);
//                            totalDistance += lastLocation.distanceTo(currLocation);
//                        }
//                        DISTANCE_BETWEEN= (int) (totalDistance/1000);
//                    }
//                    LatLng x = new LatLng((destPosition.latitude + sourcePosition.latitude) / 2, (destPosition.longitude + sourcePosition.longitude) / 2);
//                    //   mMap.resetMinMaxZoomPreference();
//                    if (DISTANCE_BETWEEN < 30) {
//                        CAMERA_ZOOM = 12;
//                    } else if (DISTANCE_BETWEEN > 30 && DISTANCE_BETWEEN < 60) {
//                        CAMERA_ZOOM = 6;
//                    } else {
//                        CAMERA_ZOOM = 4;
//                    }
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(x, CAMERA_ZOOM);
//                    mapObj.animateCamera(cameraUpdate);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            ;
//        };
//
//        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
//    }
//    public class GMapV2DirectionAsyncTask extends AsyncTask<String, Void, Document> {
//
//        private final  String TAG = GMapV2Direction.GMapV2DirectionAsyncTask.class.getSimpleName();
//        private Handler handler;
//        private LatLng start, end;
//        private String mode;
//
//        public GMapV2DirectionAsyncTask(Handler handler, LatLng start, LatLng end, String mode) {
//            this.start = start;
//            this.end = end;
//            this.mode = mode;
//            this.handler = handler;
//        }
//
//        @Override
//        protected Document doInBackground(String... params) {
//
//            String url = "http://maps.googleapis.com/maps/api/directions/xml?"
//                    + "origin=" + start.latitude + "," + start.longitude
//                    + "&destination=" + end.latitude + "," + end.longitude
//                    + "&sensor=false&units=metric&mode=" + mode;
//            Log.d("url", url);
//            try {
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpContext localContext = new BasicHttpContext();
//                HttpPost httpPost = new HttpPost(url);
//                HttpResponse response = httpClient.execute(httpPost, localContext);
//                InputStream in = response.getEntity().getContent();
//                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//                        .newDocumentBuilder();
//                Document doc = builder.parse(in);
//                return doc;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Document result) {
//            if (result != null) {
//                Log.d(TAG, "---- GMapV2DirectionAsyncTask OK ----");
//                Message message = new Message();
//                message.obj = result;
//                handler.dispatchMessage(message);
//            } else {
//                Log.d(TAG, "---- GMapV2DirectionAsyncTask ERROR ----");
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//    }
//
//
//    void setMapMarker(final String userProfilePic, final GoogleMap map ,final LatLng userLatLng){
//        Thread thread = new Thread(new Runnable(){
//            @Override
//            public void run(){
//                URL url ;
//                try {
//                    url = new URL(Constants.DEV_IMG_BASE_URL+userProfilePic);
////                    url = new URL("http://arriwe.tglobalsolutions.com/resources/user/50x50/profile_8553902950_32f39633bd2bcbc5edef1b110ed633b6.jpg");
//                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = null;
//                        if(bmp != null) {
////                            bitmap = Utils.getCroppedBitmap(bmp);
//                            bitmap = Utils.getCroppedBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
//                            map.addMarker(new MarkerOptions()
//                                    .position(userLatLng)
//                                    .title("Current Position")
//                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
//                        }
//                        else{
//                            map.addMarker(new MarkerOptions()
//                                    .position(userLatLng)
//                                    .title("Current Position"));
//                        }
//                    }
//                });
//            }
//        });
//        thread.start();
//
//    }
//
//
//    void setMapMarkerRecursivly(int index, GoogleMap googleMap) throws JSONException {
//        final GoogleMap map = googleMap;
//
//        if(index < travellersDetail.length()) {
//            recursiveIndex = index;
//            JSONObject obj = travellersDetail.getJSONObject(index);
//            String pic = obj.getString("traveller_pic");
//            final String userProfilePic =  pic.replace("resources/user", "resources/user/50x50");
//            String latStr;
//            String lonStr;
////            if(LocationService.LATEST_LOC_OBJ != null){
////                Location loc = LocationService.LATEST_LOC_OBJ;
////                latStr = String.valueOf(loc.getLatitude());
////                lonStr = String.valueOf(loc.getLongitude());
////            }
////            else {
//            latStr = obj.getString("current_lat");
//            lonStr = obj.getString("current_lon");
////            }
//            double usersLat =  Double.valueOf(latStr);
//            double usersLon = Double.valueOf(lonStr);
//
//            String usersPlace = (obj.get("current_title")).toString();
//            final LatLng userLatLng = new LatLng(usersLat, usersLon);
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    URL url;
//                    try {
//                        url = new URL(Constants.DEV_IMG_BASE_URL + userProfilePic);
////                    url = new URL("http://arriwe.tglobalsolutions.com/resources/user/50x50/profile_8553902950_32f39633bd2bcbc5edef1b110ed633b6.jpg");
//                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Bitmap bitmap = null;
//                            if (bmp != null) {
////                                    bitmap = Utils.cropBitmap(bmp);
//                                map.addMarker(new MarkerOptions()
//                                        .position(userLatLng)
//                                        .title("Current Position")
//                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
//                            } else {
//                                map.addMarker(new MarkerOptions()
//                                        .position(userLatLng)
//                                        .title("Current Position"));
//                            }
//                            try {
//                                recursiveIndex = recursiveIndex + 1;
//                                setMapMarkerRecursivly(recursiveIndex,map);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            });
//            thread.start();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.accept:
//                accept.setTextColor(Color.parseColor("#1d80c7"));
//                pending.setTextColor(Color.parseColor("#a6a6a6"));
//                declined.setTextColor(Color.parseColor("#a6a6a6"));
//
//                ll_tagger.removeAllViews();
//
//                showPerson(1);
//                break;
//            case R.id.pending:
//                accept.setTextColor(Color.parseColor("#a6a6a6"));
//                pending.setTextColor(Color.parseColor("#1d80c7"));
//                declined.setTextColor(Color.parseColor("#a6a6a6"));
//                ll_tagger.removeAllViews();
//                showPerson(2);
//                break;
//            case R.id.declined:
//                accept.setTextColor(Color.parseColor("#a6a6a6"));
//                pending.setTextColor(Color.parseColor("#a6a6a6"));
//                declined.setTextColor(Color.parseColor("#1d80c7"));
//                ll_tagger.removeAllViews();
//                showPerson(3);
//                break;
//            case R.id.add_more_people:
//                ll_bottemsheet.setVisibility(View.VISIBLE);
//                //BottomSheetBehavior sheetBehavior =  BottomSheetBehavior.from(R.layout.bottem_shee)
//                bottomSheetBehavior = BottomSheetBehavior.from(ll_bottemsheet);
//                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                    @Override
//                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//                    }
//
//                    @Override
//                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//                    }
//                });
//                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//                } else {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                    ll_bottemsheet.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.cross:
//                onBackPressed();
//                break;
//            case R.id.txt_done_add_remove:
//                try {
//                    recreateGroup();
//                }
//                catch (JSONException | UnsupportedEncodingException e){
//                    Log.e(TAG,e.toString());
//                }
//
//                break;
//            case R.id.directions:
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?daddr="+String.valueOf(getIntent().getStringExtra("group_dest_lat"))+","+String.valueOf(getIntent().getStringExtra("group_dest_lon"))));
//                startActivity(intent);
//                break;
//        }
//    }
//
//
//    private void recreateGroup()  throws JSONException, UnsupportedEncodingException{
//        travellingWithArrayToSend = new ArrayList<String>();
//        try {
//            for (JSONObject object : arrayList) {
//                if (object.has(getString(R.string.key_is_added_for_travel))) {
//                    if (object.getBoolean(getString(R.string.key_is_added_for_travel))) {
//                        travellingWithArrayToSend.add(object.getString(getString(R.string.key_mob)));
//                    }
//                }
//            }
//        } catch (Exception e) {
//        }
//        finally {
//            String traveller_name = "";
//            for(int i=0;i<travellingWithArrayToSend.size();i++){
//                Log.d("traving",travellingWithArrayToSend.get(i));
//                if(i==0)
//                    traveller_name = traveller_name+travellingWithArrayToSend.get(i);
//                else
//                    traveller_name = traveller_name+","+travellingWithArrayToSend.get(i);
//            }
//
//
//            if (travellingWithArrayToSend.size() > 0) {
//                //create request
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//                Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
//                Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
//                Double latOrigin = getIntent().getDoubleExtra((getResources().getString(R.string.key_curr_lat)), 0.0);
//                Double longOrigin = getIntent().getDoubleExtra((getResources().getString(R.string.key_curr_long)), 0.0);
//                String placeName = getIntent().getStringExtra(getResources().getString(R.string.key_tagged_title));
//
//                String taggedBy = prefs.getString(getString(R.string.key_user_id), "");
//                String taggedByNumber = prefs.getString(getString(R.string.key_reg_no), "");
//            //    traveller_name = traveller_name+taggedByNumber;
//                String requestString = "taggedby=" + URLEncoder.encode(taggedBy, "utf-8") + "&trip_id=" +
//                        URLEncoder.encode(trip_id, "utf-8")+ "&travelwith=" +
//                        URLEncoder.encode(String.valueOf(travellingWithArrayToSend), "utf-8");
//
//                Log.i(TAG, "Sending taaging details from callApiToTagOrAddToTravel " + requestString);
//                dialog = Utils.showProgressDialog(this, "");
///*
//                NetworkEngine engine = new NetworkEngine();
//                engine.sendTagAndTravelDetails(requestString, this);
//*/
//
//                traveller_name = traveller_name+","+taggedByNumber;
//                updateTravalDetaails(taggedBy,trip_id,traveller_name);
//
//            }
//        }
//    }
//
//    private void updateTravalDetaails(final String taggedBy, final String trip_id, String traveller_name){
//        Ion.with(GroupTravelDetail.this)
//                .load(Constants.DEV_BASE_URL+Constants.Api_update_group_traval)
//                .setBodyParameter("user_id",taggedBy)
//                .setBodyParameter("trip_id",trip_id)
//                .setBodyParameter("travelwith",traveller_name)
//                .asString().setCallback(new FutureCallback<String>() {
//            @Override
//            public void onCompleted(Exception e, String result) {
//
//                if(e!=null){
//                    cancelDialog();
//                    Log.e(TAG,e.toString());
//                }
//                if(result!=null){
//                    Log.e("result",result);
//                    try {
//                        cancelDialog();
//                        Toast.makeText(GroupTravelDetail.this, "Group Details Updated Successfully", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(GroupTravelDetail.this,Eight.class);
//                        intent.putExtra("setfragment","three");
//                        startActivity(intent);
//                        finish();
//                       /* String delete_user_mobile = "";
//                        for(int i=0;i<already_added_person_fix.size();i++){
//                            if(!travellingWithArrayToSend.contains(already_added_person_fix.get(i)))
//                                delete_user_mobile = delete_user_mobile+already_added_person_fix.get(i)+",";
//
//                        }
//
//                        String substring = delete_user_mobile.substring(Math.max(delete_user_mobile.length() - 1, 0));
//                        if(substring.equals(",")){
//                            delete_user_mobile = delete_user_mobile.substring(0, delete_user_mobile.lastIndexOf(" ")) + "";
//                        }
//
//
//                        if(delete_user_mobile.equals("")){
//                            cancelDialog();
//                            Toast.makeText(GroupTravelDetail.this, "Group Details Updated Successfully", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(GroupTravelDetail.this,Eight.class);
//                            intent.putExtra("setfragment","three");
//                            startActivity(intent);
//                            finish();
//                        }
//                        else {
//                            deleteTravalDetails(taggedBy,trip_id,delete_user_mobile);
//                        }*/
//
//
//                    }
//                    catch (Exception e1){
//                      cancelDialog();
//                        Log.e(TAG,e1.toString());
//                    }
//                }
//            }
//        });
//    }
//
//    private void deleteTravalDetails(String taggedBy,String trip_id,String delete_user_mobile){
//        Ion.with(GroupTravelDetail.this)
//                .load(Constants.DEV_BASE_URL+Constants.Api_delete_group_traval)
//                .setBodyParameter("user_id",taggedBy)
//                .setBodyParameter("travelwith",delete_user_mobile)
//                .setBodyParameter("trip_id",trip_id)
//                .asString().setCallback(new FutureCallback<String>() {
//            @Override
//            public void onCompleted(Exception e, String result) {
//
//                cancelDialog();
//                if(e!=null){
//                    Log.e(TAG,e.toString());
//                }
//                if(result!=null){
//                    try {
//                        JSONObject jsonObject = new JSONObject(result);
//                        if(jsonObject.getString("result").equalsIgnoreCase("Success")){
//                            Toast.makeText(GroupTravelDetail.this, "Group Details Updated Successfully", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(GroupTravelDetail.this,Eight.class);
//                            intent.putExtra("setfragment","three");
//                            startActivity(intent);
//                            finish();
//                        }
//                        else {
//                            Toast.makeText(GroupTravelDetail.this, getString(R.string.failed_to_tag_travel), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    catch (Exception e1){
//                        Log.e(TAG,e1.toString());
//                    }
//                }
//            }
//        });
//    }
//
//
//    private void cancelDialog(){
//        if(dialog!=null){
//            dialog.dismiss();
//            dialog = null;
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(GroupTravelDetail.this,Eight.class);
//        intent.putExtra("setfragment","three");
//        startActivity(intent);
//        finish();
//       // overridePendingTransition(R.anim.slide_down, R.anim.stay);
//    }
//}
