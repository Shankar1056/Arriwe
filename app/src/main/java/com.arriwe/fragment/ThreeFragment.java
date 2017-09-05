package com.arriwe.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.adapter.ListAdapter;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Eight;
import com.arriwe.wayndr.GMapV2Direction;
import com.arriwe.wayndr.MyDatatype;
import com.arriwe.wayndr.NearByFriendsFragment;
import com.arriwe.wayndr.Nine;
import com.arriwe.wayndr.Settings;
import com.arriwe.wayndr.util.ShareLocation;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.loopj.android.image.WebImage;
import com.sancsvision.arriwe.R;
import com.squareup.picasso.Picasso;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.arriwe.utility.StaticContacts.activityArrayList;

public class ThreeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, LocationListener {


    HashMap hashMap = null;
    JSONArray childArr;
    List<MyDatatype> list;

    LocationManager locationManager;
    Location location;
    CircleImageView circleImageView, circleImageView2;
    ImageView curtain, cross, cross2;
    TextView textView;
    RelativeLayout relativeLayout;
    // Animation
    Animation slide_doqwn, slide_up, bounce, slide_from_right, slide_from_left;


    private static String TAG = "Activities.java";
    ProgressDialog dialog = null;
    GridView gridView = null;
    ListView listView = null;
    ListAdapter listAdapter;
    ProgressDialog progressDialog = null;
    String acceptDeclineStr = null;
    ImageButton backBtn = null;
    ImageButton refreshBtn = null;
    Activity context = null;
    Timer timer = null;
    // Boolean activityInProgress = false;
    TextView notActivities;
    Eight eightContext;
    SwipeRefreshLayout expandable_list_swipe;
    View view;
    ImageView username_icon;
    TextView autoCompleteTextView;
    SharedPreferences sharedPreferences;
    private boolean isscreenvisible = false, isloadedfirsttime = false;
    private Bundle savedInstanceState;
    private Bitmap bmp;
    private GoogleMap googleMap;
    private ProgressBar proressbar;


    public ThreeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ThreeFragment(Eight eigthContext) {
        this.eightContext = eigthContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_activities, container, false);
        this.savedInstanceState = savedInstanceState;


        listView = (ListView) view.findViewById(R.id.listView);
        notActivities = (TextView) view.findViewById(R.id.textView13);
        username_icon = (ImageView) view.findViewById(R.id.username_icon);
        autoCompleteTextView = (TextView) view.findViewById(R.id.autoCompleteTextView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String name = sharedPreferences.getString(getActivity().getResources().getString(R.string.key_name), "");
        String path = sharedPreferences.getString(getActivity().getResources().getString(R.string.path_profile_pic), "");
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        autoCompleteTextView.setText(name);

       proressbar = (ProgressBar)view.findViewById(R.id.proressbar);
        if (childArr!=null && childArr.length()>0)
        {
            proressbar.setVisibility(View.GONE);
        }

//        try {
//

//            Bitmap b= Utils.getBitmapFromPath(getActivity(), path);
//
//
//
////            username_icon.setImageBitmap(b);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        Log.e("Eight path:",path);
        Picasso.with(getActivity())
                .load(path)
                .placeholder(R.drawable.user_new)
                .into(username_icon);

        username_icon.setOnClickListener(this);
        context = getActivity();
        expandable_list_swipe = (SwipeRefreshLayout) view.findViewById(R.id.expandable_list_swipe);

        backBtn = (ImageButton) view.findViewById(R.id.imageButton_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
            }
        });

        refreshBtn = (ImageButton) view.findViewById(R.id.refreshButton);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listAdapter = new ListAdapter(eightContext, childArr,ThreeFragment.this);
        listView.setAdapter(listAdapter);

        expandable_list_swipe.setOnRefreshListener(this);


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isscreenvisible = isVisibleToUser;
        if (isscreenvisible) {
            try {
                if (activityArrayList.size()==0)
                getUserActivities(true);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
           // refreshTimer();
        }
    }

    private void refreshTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isscreenvisible)
                                    if (Utils.isNetworkConnected(getActivity())) {
                                        getUserActivities(true);
                                    }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };

// schedule the task to run starting now and then every min...
        timer.schedule(hourlyTask, 10000, (1000 * 60) / 6);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view != null)
            expandable_list_swipe = (SwipeRefreshLayout) view.findViewById(R.id.expandable_list_swipe);
    }

    public void refreshBtnClicked() throws JSONException, UnsupportedEncodingException {
        getUserActivities(false);
    }

    //API Calls
    public void getUserActivities(Boolean isRefreshing) throws JSONException, UnsupportedEncodingException {
        // if (activityInProgress) return;

        if (getContext() != null) {
            if (!Utils.isNetworkConnected(getContext())) {
                Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
                //activityInProgress = false;
                return;
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String userId = prefs.getString(getString(R.string.key_user_id), "");
            Log.e("userId------------", userId);
            String requestString = "userid=" + URLEncoder.encode(userId, "utf-8");
            //  activityInProgress = true;
            NetworkEngine engine = new NetworkEngine();
            engine.getUserActivities(requestString, getContext());
        }


    }

    /***
     * API Callbacks
     */
    public void usersActivitiesResponse(NetworkDataModel model) throws JSONException {
//       Utils.logLargeString("usersActivitiesResponse" + model.responseData.toString());

        // activityInProgress = false;

        if (expandable_list_swipe != null)
            expandable_list_swipe.setRefreshing(false);

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (model.responseData == null) {
            Toast.makeText(getContext(), getString(R.string.str_no_activities), Toast.LENGTH_SHORT).show();
            return;
        }

//        Log.e("My Activities>>>>>>>>>>", model.responseData.toString());
        JSONObject jObject = new JSONObject(model.responseData.toString());

        Log.e("Refreshed.............", model.responseData.toString());

        if (Constants.myPreviousJSON == null) {
            Constants.myPreviousJSON = jObject.toString();
            activityArrayList.clear();
            activityArrayList.add(jObject);
            initializeListView(jObject, true);
        } else if (Constants.myPreviousJSON.equals(jObject.toString())) {
//           Log.e("***Same data***","**********************************") ;
        } else {
            activityArrayList.clear();
            activityArrayList.add(jObject);
            initializeListView(jObject, false);
        }

        proressbar.setVisibility(View.GONE);
    }

    void initializeListView(JSONObject jsonObject, boolean first_time) {

        int pos = 0;



       /* if (isloadedfirsttime)
        {
            listAdapter.notifyDataSetChanged();
        }
        else {
            listAdapter = new ListAdapter(eightContext, jsonObject);
            listView.setAdapter(listAdapter);
            listView.setSelection(pos);
            isloadedfirsttime = true;
        }
*/


        try {
            hashMap = getRowDetailsASection(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        list = new ArrayList<MyDatatype>();

        JSONArray childArr2 = (JSONArray) hashMap.get(context.getString(R.string.key_activity_details));

        try {
            childArr = getSortedList(childArr2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //listAdapter.notifyDataSetChanged();
            listAdapter.sendalldata(eightContext, childArr,ThreeFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                try {
                    Log.e("Position:", position + ">" + childArr.getJSONObject(position).getString("activity_type") + "");
                    if (childArr.getJSONObject(position).getString("activity_type").equals("myjourney")) {
                        try {
                            JSONObject obj = childArr.getJSONObject(position);
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
                            map.put("tagger", obj.getJSONArray("tagger").toString());
                            map.put("end_title", obj.get("end_title").toString());
                            map.put("start_time", obj.get("start_time").toString());


                            Intent intent = new Intent(getContext(), IndividualTravelDetail.class);
                            intent.putExtra("data", map);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            JSONObject obj = childArr.getJSONObject(position);
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
                            map.put("tagger", obj.getJSONArray("tagger").toString());
                            map.put("end_title", obj.get("end_title").toString());
                            map.put("start_time", obj.get("start_time").toString());


                            Intent intent = new Intent(getContext(), IndividualTravelDetail.class);
                            intent.putExtra("data", map);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });*/

    }
    public void sendpos(int position, String travelling_lat, String travelling_long) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mapexpanddialog);
        MapView mapView = (MapView) dialog.findViewById(R.id.map_view);

        MapsInitializer.initialize(getActivity());

        mapView = (MapView) dialog.findViewById(R.id.map_view);
        mapView.onCreate(dialog.onSaveInstanceState());
        mapView.onResume();// needed to get the map to display immediately
        googleMap = mapView.getMap();

        mapView.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                dialog.dismiss();
            }
        });
        if (mapView != null) {
            googleMap = mapView.getMap();
            double usersLat = Double.valueOf(travelling_lat);
            double usersLon = Double.valueOf(travelling_long);
            LatLng latLngDestination = new LatLng(usersLat, usersLon);

            googleMap.addMarker(new MarkerOptions()
                    .position(latLngDestination)
                    .title("Destination"));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngDestination, 17);
            googleMap.animateCamera(cameraUpdate);

        }
        dialog.show();



    }
    public void openmapdialo(int pos, String fullsingle) throws JSONException {


        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mapexpanddialog);
        MapView mapView = (MapView) dialog.findViewById(R.id.map_view);

        MapsInitializer.initialize(getActivity());

        mapView = (MapView) dialog.findViewById(R.id.map_view);
        mapView.onCreate(dialog.onSaveInstanceState());
        mapView.onResume();// needed to get the map to display immediately
        googleMap = mapView.getMap();

        mapView.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                dialog.dismiss();
            }
        });
        JSONObject obj = childArr.getJSONObject(pos);
        if (mapView != null) {
            googleMap = mapView.getMap();

            if (fullsingle.equalsIgnoreCase("full")) {
                showfullmap(obj);
            }
        }
        dialog.show();


    }

    private void showfullmap(JSONObject obj) throws JSONException {
        LatLng start_latlong = new LatLng(Double.parseDouble(obj.get(getString(R.string.start_lat)).toString().toString()), Double.parseDouble(obj.get("start_long").toString()));
        LatLng end_latlng = new LatLng(Double.parseDouble(obj.get("end_lat").toString().toString()), Double.parseDouble(obj.get("end_long").toString().toString()));
        route(start_latlong, end_latlng, GMapV2Direction.MODE_DRIVING);

        PolylineOptions rectLine = new PolylineOptions().width(3).color(
                Color.RED);

        String lat = obj.get("current_lat").toString();
        String lon = obj.get("current_lon").toString();
        String pic = obj.get(getString(R.string.key_traveller_pic)).toString();
        final String userProfilePic = pic;//.replace("resources/user","resources/user/50x50");
        float currLat = 0;
        float currLong = 0;


        if (!(lat.equals("null"))) {
            currLat = Float.parseFloat(lat);
        }
        if (!(lon.equals("null"))) {
            currLong = Float.parseFloat(lon);
        }

        float startLat = Float.parseFloat(obj.get(getString(R.string.start_lat)).toString());
        float startLong = Float.parseFloat(obj.get("start_long").toString());

        final LatLng currLatLng = new LatLng(currLat, currLong);
        final LatLng destLatLng = new LatLng(startLat, startLong);

        Log.d("staring", currLat + " " + currLong);
        Log.d("ending", startLat + " " + startLong);

        String destLatStr = null;
        String destLonStr = null;

        if (obj.get("end_lat").toString().length() > 0) {
            destLatStr = obj.get("end_lat").toString();
        } else {
            return;
        }
        if (obj.get("end_long").toString().length() > 0) {
            destLonStr = obj.get("end_long").toString();
        }

        if (!destLatStr.equals("null") && !destLonStr.equals("null")) {
            double usersLat = Double.valueOf(destLatStr);
            double usersLon = Double.valueOf(destLonStr);
            LatLng latLngDestination = new LatLng(usersLat, usersLon);

            googleMap.addMarker(new MarkerOptions()
                    .position(latLngDestination)
                    .title("Destination"));
            LatLng zoomVal = currLatLng;
            if (zoomVal == null || (zoomVal.equals("null"))) {
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

        Log.i("jj", +currLat + "," + currLong);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(Constants.DEV_IMG_BASE_URL + userProfilePic);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bmp != null) {
//                            Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
//                            profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 120, false));
                            Bitmap bitmap = Utils.getCroppedBitmap(Bitmap.createScaledBitmap(bmp, 80, 80, false));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(currLatLng)
                                    .title("Current Position")
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        } else {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(currLatLng)
                                    .title("Current Position"));

                        }

                    }
                });
            }
        });
        thread.start();

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
                    Polyline polylin = googleMap.addPolyline(rectLine);
                    md.getDurationText(doc);
                    int DISTANCE_BETWEEN = 0;
                    int CAMERA_ZOOM = 0;

                    if (null != rectLine) {
                        // mMap.addPolyline(polyLineOptions);
                        float totalDistance = 0;
                        for (int k = 1; k < rectLine.getPoints().size(); k++) {
                            Location currLocation = new Location("this");
                            currLocation.setLatitude(rectLine.getPoints().get(k).latitude);
                            currLocation.setLongitude(rectLine.getPoints().get(k).longitude);
                            Location lastLocation = new Location("that");
                            lastLocation.setLatitude(rectLine.getPoints().get(k - 1).latitude);
                            lastLocation.setLongitude(rectLine.getPoints().get(k - 1).longitude);
                            totalDistance += lastLocation.distanceTo(currLocation);
                        }
                        DISTANCE_BETWEEN = (int) (totalDistance / 1000);
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



    public class GMapV2DirectionAsyncTask extends AsyncTask<String, Void, Document> {

        private final String TAG = GMapV2Direction.GMapV2DirectionAsyncTask.class.getSimpleName();
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


    public void cancelTripBtnAction(String tripId) throws UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = prefs.getString(getString(R.string.key_user_id), "");

//        String requestString = "tripid="+ URLEncoder.encode(tripId, "utf-8");
        String requestString = "tripid=" + URLEncoder.encode(tripId, "utf-8") + "&userid=" + URLEncoder.encode(userId, "utf-8");

        Log.i(TAG, "Json obj to be processed is " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.cancelATrip(requestString, getContext());
    }

    public void openlivemap(String start_lat, String start_lon, String group_dest_lat, String group_dest_lon) {
        LatLng start_latlong = new LatLng(Double.parseDouble(start_lat), Double.parseDouble(start_lon));
        LatLng end_latlng = new LatLng(Double.parseDouble(group_dest_lat), Double.parseDouble(group_dest_lon));
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + String.valueOf(start_latlong.latitude) + "," + String.valueOf(start_latlong.longitude) + "&daddr=" + String.valueOf(end_latlng.latitude) + "," + String.valueOf(end_latlng.longitude)));
        startActivity(intent);
    }

    public void cancelTripTravellerAction(String tripid, String userid, String mobile) throws UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");

        cancelTripTraveler(tripid, userid, mobile, Constants.Api_cancel_tagger_trip);
    }

    public void cancelTripTrggerAction(String tripid, String userid, String mobile) throws UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");

        cancelTripTraveler(tripid, userid, mobile, Constants.Api_cancel_trivvver_trip);
    }

    public void cancelTripResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT);
            return;
        }
        JSONObject obj = new JSONObject(model.responseData.toString());
        String res = obj.getString("result");
        if (res.equals(getString(R.string.key_Success))) {
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
//            finish();
            // Toast.makeText(getContext(),obj.getString("msg"),Toast.LENGTH_LONG).show();
            //     Toast.makeText(getContext(),"Trip cancelled",Toast.LENGTH_LONG).show();
            getUserActivities(false);
//            String tripId = Utils.getStringForKey(mContext.getString(R.string.key_active_trip_id),mContext);
            Utils.addStringToSharedPref(getString(R.string.key_has_active_trip), "false", getContext());
            Utils.addStringToSharedPref(getString(R.string.key_active_trip_id), "", getContext());
        } else {
//            finish();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public void clearBtnClicked(String tripId) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = prefs.getString(getString(R.string.key_user_id), "");

        String requestString = "tripid=" + URLEncoder.encode(tripId, "utf-8") + "&userid=" + URLEncoder.encode(userId, "utf-8");

        Log.i(TAG, "Json obj to be processed is " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.clearTrip(requestString, getContext());
    }

    public void clearTripResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT);
            return;
        }
        JSONObject obj = new JSONObject(model.responseData.toString());
        String res = obj.getString("result");
        if (res.equals(getString(R.string.key_Success))) {
            // Toast.makeText(getContext(),obj.getString("msg"),Toast.LENGTH_LONG).show();
            getUserActivities(false);
        } else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            getUserActivities(false);
        }
    }

    /***
     * List buttons callback
     */

    public void acceptBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiForAcceptOrDecline("A", obj.getString("group_id"));
        acceptDeclineStr = "A";
    }

    void callApiForAcceptOrDecline(String status, String guid) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = prefs.getString(getString(R.string.key_user_id), "");

        String requestString = "group_id=" + URLEncoder.encode(guid, "utf-8") + "&user_id=" + URLEncoder.encode(userId, "utf-8") + "&status=" + URLEncoder.encode(status, "utf-8");

        Log.i(TAG, "Json obj to be processed is " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.updateStatus(requestString, getContext());
    }

    public void acceptDeclineResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
//        Log.i(TAG,"acceptDeclineResponse"+model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT);
            return;
        }
        JSONObject obj = new JSONObject(model.responseData.toString());
        String res = obj.getString("result");
        if (res.equals(getString(R.string.key_Success))) {
//            finish();
            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
            getUserActivities(false);
        } else {
//            finish();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public void declineBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiForAcceptOrDecline("C", obj.getString("group_id"));
        acceptDeclineStr = "C";
    }

    public void initFragment(final Double lat, final Double longitude) {
        try {
            Fragment f = getFragmentManager().findFragmentByTag("NearbyFriendsFragment");
            if (f == null) {

                // get an instance of FragmentTransaction from your Activity
                FragmentManager fragmentManager = context.getFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //add a fragment
//        Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
//        Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
                final String placeName = context.getIntent().getStringExtra(getResources().getString(R.string.key_freq_visit_place));

                new Handler().post(new Runnable() {
                    public void run() {
                        NearByFriendsFragment myFragment = new NearByFriendsFragment(lat, longitude, 1, placeName);
                        fragmentTransaction.add(R.id.layout_placeholder, myFragment, "NearbyFriendsFragment");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });


            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    public void shareLocBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        if (!Utils.gpsStatus(getContext())) {
            Utils.checkGPSStatus(getContext());
            return;
        }
        // String reqLocId = obj.getString("request_location_id");
        Intent intent = new Intent(getContext(), ShareLocation.class);
        intent.putExtra("reciever_mobile_no", obj.getString("mobile"));
        startActivity(intent);

        cancelTripBtnAction(obj.getString("tripid"));


    }

    public void declineLocShareBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiToDeclineLocReq(obj);
    }

    void callApiToDeclineLocReq(JSONObject jsonObject) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("mobileNo",mobNO);
//        jsonObject.put("guid",guid);
//        jsonObject.put("status",status);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String mobNo = prefs.getString(getString(R.string.key_reg_no), "");
        String reqLocId = jsonObject.getString("request_location_id");

        String requestString = "mobileNo=" + URLEncoder.encode(mobNo, "utf-8") + "&request_location_id=" + URLEncoder.encode(reqLocId, "utf-8");

        Log.i(TAG, "Json obj to be processed is " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.declineLocationReq(requestString, getContext());
    }

    public void locationRequestRejectedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT);
            return;
        }
        JSONObject obj = new JSONObject(model.responseData.toString());
        String res = obj.getString("result");

        if (res.equals(getString(R.string.key_Success))) {
//            finish();
            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
            getUserActivities(false);
        } else {
//            finish();
            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public void createTripFromSharedLocation(JSONObject obj) throws JSONException, IOException {
//        String currLat = String.valueOf(LocationService.LATEST_LOC_OBJ.getLatitude());
//        String currLon  = String.valueOf(LocationService.LATEST_LOC_OBJ.getLongitude());
        String placeName = "";
        //  HashMap map = Utils.reverseGeocode(LocationService.LATEST_LOC_OBJ.getLatitude(),LocationService.LATEST_LOC_OBJ.getLongitude(),getContext());

        Log.e("to nine>.", obj.toString());

        HashMap map = Utils.reverseGeocode(Double.parseDouble(obj.getString("latitude")), Double.parseDouble(obj.getString("longtitude")), getContext());
        if (map.get("name") != null) {
            placeName = map.get("name").toString();
        }
        Intent i = new Intent(getContext(), Nine.class);
        i.putExtra((getResources().getString(R.string.key_freq_visit_place)), obj.getString("name"));
        i.putExtra((getResources().getString(R.string.key_freq_visit_lat)), Double.parseDouble(obj.getString("latitude")));
        i.putExtra((getResources().getString(R.string.key_freq_visit_long)), Double.parseDouble(obj.getString("longtitude")));
        i.putExtra((getResources().getString(R.string.key_freq_visit_address)), map.get("address").toString());


        i.putExtra((getResources().getString(R.string.key_curr_lat)), LocationService.LATEST_LOC_OBJ.getLatitude());
        i.putExtra((getResources().getString(R.string.key_curr_long)), LocationService.LATEST_LOC_OBJ.getLongitude());

        i.putExtra((getResources().getString(R.string.key_tagged_title)), obj.getString("name"));
        i.putExtra("from_shared_loc", true);
        i.putExtra("tag_person_no", obj.getString("mobile"));//obj.getString("travelrequest_phone")
        Bundle b = new Bundle();
        b.putDouble("lat", Double.parseDouble(obj.getString("latitude")));//latitude
        b.putDouble("long", Double.parseDouble(obj.getString("longtitude")));//longitude
        b.putString("place", obj.getString("name"));
        b.putString("address", map.get("address").toString());
        i.putExtras(b);
        startActivity(i);
        cancelTripBtnAction(obj.getString("tripid"));


    }

    public void ignoreBtnClicked(JSONObject obj) throws JSONException, UnsupportedEncodingException {
        callApiToDeleteLocRecieved(obj);
    }

    void callApiToDeleteLocRecieved(JSONObject jsonObject) throws JSONException, UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("mobileNo",mobNO);
//        jsonObject.put("guid",guid);
//        jsonObject.put("status",status);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String mobNo = prefs.getString(getString(R.string.key_reg_no), "");
        String reqLocId = jsonObject.getString("request_location_id");

        String requestString = "mobileNo=" + URLEncoder.encode(mobNo, "utf-8") + "&request_location_id=" + URLEncoder.encode(reqLocId, "utf-8");

        Log.i(TAG, "Json obj to be processed is " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.deleteRecievedLoc(requestString, getContext());
    }

    public void recievedLocRejectedResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT);
            return;
        }
        JSONObject obj = new JSONObject(model.responseData.toString());
        String res = obj.getString("result");

        if (res.equals(getString(R.string.key_Success))) {
            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
            getUserActivities(false);
        } else {
            Toast.makeText(getContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
            getUserActivities(false);

        }
    }

    public void showNoActivitiesLabel(int alpha) {
        notActivities.setAlpha(alpha);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        try {
            getUserActivities(true);
        } catch (JSONException | UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.username_icon:
                Intent i = new Intent(getActivity(), Settings.class);
                startActivity(i);
                context.overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
        }
    }

    private void cancelTripTraveler(String tripid, String userid, String mobile, String api) {
        Ion.with(context)
                .load(Constants.DEV_BASE_URL + api)
                .setBodyParameter("tripid", tripid)
                .setBodyParameter("userid", userid)
                .setBodyParameter("mobile", mobile)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                cancelDialog();
                if (e != null) {
                    Log.e(TAG, e.toString());
                }
                if (result != null) {
                    try {
                        //{"result":"Success","msg":"Trip cancelled successfully."}
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
                            Toast.makeText(getContext(), "Trip cancelled.", Toast.LENGTH_LONG).show();
                            getUserActivities(false);
                            Utils.addStringToSharedPref(getString(R.string.key_has_active_trip), "false", getContext());
                            Utils.addStringToSharedPref(getString(R.string.key_active_trip_id), "", getContext());


                        } else {
                            Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e1) {
                        Log.e(TAG, e1.toString());
                    }
                }

            }
        });
    }

    private void cancelDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public JSONArray getSortedList(JSONArray array) throws JSONException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }
        Collections.sort(list, new SortBasedOnMessageId());

        JSONArray resultArray = new JSONArray(list);

        return resultArray;
    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;


    }

    public class SortBasedOnMessageId implements Comparator<JSONObject> {

        @Override
        public int compare(JSONObject lhs, JSONObject rhs) {
            try {
                return lhs.getInt("tripid") < rhs.getInt("tripid") ? 1 : (lhs
                        .getInt("tripid") > rhs.getInt("tripid") ? -1 : 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;

        }
    }


    private HashMap getRowDetailsASection(JSONObject jsonObject) throws JSONException {


        int count = 0;
        Boolean myJourneyStatus = false;
        Boolean taggingStatus = false;
        Boolean travelWith = false;
        Boolean cancelled = false;
        Boolean joinGroup = false;
        Boolean arriwed = false;
        Boolean reqLoc = false;
        Boolean rcvLoc = false;
        Boolean reqForLoc = false;
        Boolean sharedLoc = false;

        JSONArray finalObj = jsonObject.getJSONArray("activity");


        HashMap<String, Object> map = new HashMap<>();
        map.put(context.getString(R.string.key_activity_count), count);
        map.put(context.getString(R.string.key_activity_details), finalObj);
        return map;
    }


}
