package com.arriwe.wayndr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.image.WebImage;
import com.sancsvision.arriwe.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.arriwe.utility.StaticContacts.convertDpToPixel;

//import com.arriwe.networkmanagers.NetworkDataModel;


public class IndividualTravelDetail extends Activity implements  OnMapReadyCallback,GoogleMap.OnMapClickListener, View.OnClickListener {

    private static String TAG = "I.java";
    private  Bitmap bmp;
    Boolean isExpanded = false;
    ProgressDialog dialog = null;
    GoogleMap googleMap = null;
    HashMap dataMap = null;
    LinearLayout cross;
    TextView directions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.thirteen);
        initUI();
        initMap();
    }

    void  initUI(){
        LinearLayout hoverDetailLayout;
        TextView nameTextView,currentLocTextView;
        TextView timeTaken,distanceTravelled,distanceRemaning,tagged;
        LinearLayout ll_tagger;
        nameTextView = (TextView) findViewById(R.id.name_textview);
        ll_tagger = (LinearLayout)findViewById(R.id.ll_tagger);
        cross = (LinearLayout)findViewById(R.id.cross);
        cross.setOnClickListener(this);

        currentLocTextView = (TextView) findViewById(R.id.curr_loc_tv);
        directions = (TextView)findViewById(R.id.directions);
        directions.setOnClickListener(this);
        timeTaken = (TextView) findViewById(R.id.textView_timetaken);
        distanceRemaning = (TextView) findViewById(R.id.textView_distanceleft);
        distanceTravelled = (TextView) findViewById(R.id.textView_distancetravelled);

        tagged = (TextView)findViewById(R.id.tagged);


        dataMap = (HashMap) getIntent().getSerializableExtra("data");
        String tagger_array =  dataMap.get("tagger").toString();

        Log.e(" share location>>>",tagger_array.toString());
        try {
            JSONArray jsonArray = new JSONArray(tagger_array);
            tagged.setText(jsonArray.length()+" TAGGED");


            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                currentLocTextView.setText(jsonObject1.getString("name").toString());

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.setMargins(40,0,0,0);
                linearLayout.setLayoutParams(layoutParams);

                RoundedImageView circleImageView = new RoundedImageView(this);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(convertDpToPixel(40,this),convertDpToPixel(40,this));
                layoutParams1.setMargins(10,10,10,10);
                layoutParams1.gravity = Gravity.CENTER;
                circleImageView.setLayoutParams(layoutParams1);
                circleImageView.setImageUrl(Constants.DEV_IMG_BASE_URL+jsonObject.getString("profile_image"));

                TextView textView = new TextView(this);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.gravity = Gravity.CENTER;
                layoutParams2.setMargins(0,10,0,0);
                textView.setLayoutParams(layoutParams2);
                textView.setAllCaps(true);
                textView.setTextSize(12.0f);
                textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                textView.setText(jsonObject.getString("name"));

                linearLayout.addView(circleImageView);
                linearLayout.addView(textView);

                ll_tagger.addView(linearLayout);
            }
        }
        catch (Exception e){
                Log.e("error",e.toString());
        }

        nameTextView.setText(Html.fromHtml("<strong>"+dataMap.get(getString(R.string.key_traveller_name)).toString()+"</strong> is travelling to "));
        String loc = dataMap.get(getString(R.string.key_travelling_loc)).toString();
        String attributedPlaceName = "<font color='#0070C0'>"+loc+"</font>";
        if(!(dataMap.get("end_title").toString().equals("null")))
            currentLocTextView.setText(dataMap.get("end_title").toString());
        if(!(dataMap.get(getString(R.string.key_time_left)).toString().equals("null")))
            //timeRemaning.setText(dataMap.get(getString(R.string.key_time_left)).toString());
        if(!(dataMap.get(getString(R.string.key_time_taken)).toString().equals("null")))
            timeTaken.setText(dataMap.get(getString(R.string.key_time_taken)).toString());
            if(!(dataMap.get(getString(R.string.key_distance_left)).toString().equals("null")))
        distanceRemaning.setText(dataMap.get(getString(R.string.key_distance_left)).toString());
        if(!(dataMap.get(getString(R.string.key_distance_travelled)).toString().equals("null")))
            distanceTravelled.setText(dataMap.get(getString(R.string.key_distance_travelled)).toString());

        //my profile image
//        Bitmap travellerImg = Utils.getBitmapFromBase64(dataMap.get(getString(R.string.key_traveller_pic)).toString());
        String completeURl = Constants.DEV_IMG_BASE_URL+dataMap.get(getString(R.string.key_traveller_pic));
      //  RoundedImageView taggedImgView = (RoundedImageView) findViewById(R.id.imageView_profile);
       // taggedImgView.setImageUrl(completeURl);



    }

    void initMap() {
      //  final RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.bottom_layout);


        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(!isExpanded) {
//                    LinearLayout.LayoutParams  params = (LinearLayout.LayoutParams) backgroundLayout.getLayoutParams();
//                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mapFragment.getView().getLayoutParams();
////                    params.weight = (float) 3.0;
////                    params1.weight = (float) 2;
////                    backgroundLayout.setLayoutParams(params);



                    mapFragment.getView().setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//                    mapFragment.getView().bringToFront();
                    isExpanded = true;
                }
                else{
                   // LinearLayout.LayoutParams  params = (LinearLayout.LayoutParams) backgroundLayout.getLayoutParams();
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mapFragment.getView().getLayoutParams();
                //    params.weight = (float)3.5;
                    params1.height = 0;
                    params1.weight = ViewGroup.LayoutParams.MATCH_PARENT;
                    params1.weight = (float)3.5;
                //    backgroundLayout.setLayoutParams(params);
                    mapFragment.getView().setLayoutParams(params1);
                    isExpanded = false;

                }
            }
        });
    }

    ArrayList<LatLng> getPathPoints(){
        ArrayList<LatLng> polylines = new ArrayList<LatLng>();
        LatLng latLng1 = new LatLng(12.9716,77.5946);
        LatLng latLng2 = new LatLng(18.5204,73.8567);

        polylines.add(latLng1);
        polylines.add(latLng2);

        return  polylines;
    }

    protected void route(final LatLng sourcePosition, final LatLng destPosition, String mode) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.parseColor("#0070c0"));

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = googleMap.addPolyline(rectLine);md.getDurationText(doc);
                    int DISTANCE_BETWEEN = 0;
                    int CAMERA_ZOOM = 0;

                    if(null!=rectLine) {
                       // mMap.addPolyline(polyLineOptions);
                        float totalDistance = 0;
                        for(int k = 1; k < rectLine.getPoints().size(); k++) {
                            Location currLocation = new Location("this");
                            currLocation.setLatitude(rectLine.getPoints().get(k).latitude);
                            currLocation.setLongitude(rectLine.getPoints().get(k).longitude);
                            Location lastLocation = new Location("that");
                            lastLocation.setLatitude(rectLine.getPoints().get(k-1).latitude);
                            lastLocation.setLongitude(rectLine.getPoints().get(k-1).longitude);
                            totalDistance += lastLocation.distanceTo(currLocation);
                        }
                       DISTANCE_BETWEEN= (int) (totalDistance/1000);
                    }
                    LatLng x = new LatLng((destPosition.latitude + sourcePosition.latitude) / 2, (destPosition.longitude + sourcePosition.longitude) / 2);
                 //   mMap.resetMinMaxZoomPreference();
                    if (DISTANCE_BETWEEN < 30) {
                        CAMERA_ZOOM = 12;
                    } else if (DISTANCE_BETWEEN > 30 && DISTANCE_BETWEEN < 60) {
                        CAMERA_ZOOM = 6;
                    } else {
                        CAMERA_ZOOM = 4;
                    }
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(x, CAMERA_ZOOM);
                    googleMap.animateCamera(cameraUpdate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };

        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cross:
                onBackPressed();
                break;
            case R.id.directions:
                LatLng start_latlong = new LatLng(Double.parseDouble(dataMap.get("start_lat").toString()),Double.parseDouble(dataMap.get("start_lon").toString()));
                LatLng end_latlng = new LatLng(Double.parseDouble(dataMap.get("group_dest_lat").toString()),Double.parseDouble(dataMap.get("group_dest_lon").toString()));
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+String.valueOf(start_latlong.latitude)+","+String.valueOf(start_latlong.longitude)+"&daddr="+String.valueOf(end_latlng.latitude)+","+String.valueOf(end_latlng.longitude)));
                startActivity(intent);
                break;
        }
    }

    public class GMapV2DirectionAsyncTask extends AsyncTask<String, Void, Document> {

        private final  String TAG = GMapV2Direction.GMapV2DirectionAsyncTask.class.getSimpleName();
        private Handler handler;
        private LatLng start, end;
        private String mode;

        public GMapV2DirectionAsyncTask(Handler handler, LatLng start, LatLng end, String mode) {
            this.start = start;
            this.end = end;
            this.mode = mode;
            this.handler = handler;
        }

        @Override
        protected Document doInBackground(String... params) {

            String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                    + "origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&sensor=false&units=metric&mode=" + mode;
            Log.d("url", url);
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                InputStream in = response.getEntity().getContent();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = builder.parse(in);
                return doc;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document result) {
            if (result != null) {
                Log.d(TAG, "---- GMapV2DirectionAsyncTask OK ----");
                Message message = new Message();
                message.obj = result;
                handler.dispatchMessage(message);
            } else {
                Log.d(TAG, "---- GMapV2DirectionAsyncTask ERROR ----");
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng start_latlong = new LatLng(Double.parseDouble(dataMap.get("start_lat").toString()),Double.parseDouble(dataMap.get("start_lon").toString()));
        LatLng end_latlng = new LatLng(Double.parseDouble(dataMap.get("group_dest_lat").toString()),Double.parseDouble(dataMap.get("group_dest_lon").toString()));
        route(start_latlong,end_latlng,GMapV2Direction.MODE_DRIVING);
         final GoogleMap map = googleMap;
        this.googleMap = googleMap;
        HashMap dataMap = (HashMap) getIntent().getSerializableExtra("data");

     //   ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(3).color(
                Color.RED);

       /* for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }
        Polyline polylin = map.addPolyline(rectLine);*/

        String lat = String.valueOf(dataMap.get(getString(R.string.key_curr_lat)));
        String lon = String.valueOf(dataMap.get(getString(R.string.key_curr_long)));
        String pic = String.valueOf(dataMap.get(getString(R.string.key_traveller_pic)));
        final String userProfilePic =  pic;//.replace("resources/user","resources/user/50x50");


//        if(LocationService.LATEST_LOC_OBJ != null){
//            Location loc = LocationService.LATEST_LOC_OBJ;
//            lat = String.valueOf(loc.getLatitude());
//            lon = String.valueOf(loc.getLongitude());
//        }
        float currLat = 0;
        float currLong = 0;


        if(!(lat.equals("null"))) {
            currLat = Float.parseFloat(lat);
        }
        if(!(lon.equals("null"))){
            currLong = Float.parseFloat(lon);
        }

        float startLat = Float.parseFloat(String.valueOf(dataMap.get(getString(R.string.start_lat))));
        float startLong = Float.parseFloat(String.valueOf(dataMap.get(getString(R.string.start_long))));

        final LatLng currLatLng = new LatLng(currLat, currLong);
        final LatLng destLatLng = new LatLng(startLat, startLong);

        Log.d("staring",currLat+" "+currLong);
        Log.d("ending",startLat+" "+startLong);

        String destLatStr = null;
        String destLonStr = null;

        if((dataMap.containsKey("group_dest_lat")) == true) {
             destLatStr = dataMap.get("group_dest_lat").toString();
        }
        else{
            return;
        }
        if((dataMap.containsKey("group_dest_lon")== true)) {
            destLonStr = dataMap.get("group_dest_lon").toString();
        }

        if(!destLatStr.equals("null") && !destLonStr.equals("null")) {
            double usersLat = Double.valueOf(destLatStr);
            double usersLon = Double.valueOf(destLonStr);
            LatLng latLngDestination = new LatLng(usersLat, usersLon);

            googleMap.addMarker(new MarkerOptions()
                    .position(latLngDestination)
                    .title("Destination"));
            //Zoom to show current lat and long
            //if that is unavailable,show on destination
            LatLng zoomVal = currLatLng;
            if(zoomVal == null || (zoomVal.equals("null"))){
                zoomVal = destLatLng;
            }
            final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(zoomVal, 17);
            googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    //move the marker to top of screen
                }

                @Override
                public void onCancel() {

                }
            });
        }
        WebImage image = new WebImage(userProfilePic);

        Log.i("jj",+ currLat + "," + currLong);
//        googleMap.addMarker(new MarkerOptions()
//                .position(currLatLng)
//                .title("Current Position"));
       /* googleMap.addMarker(new MarkerOptions()
                .position(destLatLng)
                .title("Starting Point"));
*/

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                URL url ;
                try {
                    url = new URL(Constants.DEV_IMG_BASE_URL+userProfilePic);
//                    url = new URL("http://arriwe.tglobalsolutions.com/resources/user/50x50/profile_8553902950_32f39633bd2bcbc5edef1b110ed633b6.jpg");
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bmp != null) {
//                            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
//                            profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 120, false));
                            Bitmap bitmap = Utils.getCroppedBitmap(Bitmap.createScaledBitmap(bmp, 80, 80, false));
                            map.addMarker(new MarkerOptions()
                                    .position(currLatLng)
                                    .title("Current Position")
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        }
                        else{
                            map.addMarker(new MarkerOptions()
                                    .position(currLatLng)
                                    .title("Current Position"));

                        }

                    }
                });
            }
        });
        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // startService(new Intent(this, LocationService.class));
        try {
            callApiToGetUserPoints();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        //currently disabling for background
        stopService(new Intent(this,LocationService.class));
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

    @Override
    public void onMapClick(LatLng latLng) {

    }

    void callApiToGetUserPoints() throws JSONException, UnsupportedEncodingException {
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
        HashMap dataMap = (HashMap) getIntent().getSerializableExtra("data");
        String tripId =  dataMap.get("trip_id").toString();
        String requestString = "tripid="+ URLEncoder.encode(tripId, "utf-8")+"&userid="+URLEncoder.encode(userId, "utf-8");

        Log.i(TAG, "Json obj to be processed is "+requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.getUsersLocationsPoint(requestString, this);
    }

    public void usersLocationPointsResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
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

            JSONArray locArray = obj.getJSONArray("location");
            ArrayList<LatLng> pointObjs = new ArrayList<>();
            for(int i=0;i<locArray.length();i++){
                JSONObject jsonObject = (JSONObject) locArray.get(i);
                Double lat = jsonObject.getDouble("lat");
                Double lon = jsonObject.getDouble("lon");

                LatLng latLng = new LatLng(lat,lon);
                pointObjs.add(latLng);
            }
            Utils.drawPolylines(this.googleMap,pointObjs);


        }
        else{
//            finish();
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(IndividualTravelDetail.this,Eight.class);
        intent.putExtra("setfragment","three");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
