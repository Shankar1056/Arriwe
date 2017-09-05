package com.arriwe.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arriwe.fragment.ThreeFragment;
import com.arriwe.utility.ClsGeneral;
import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Activities;
import com.arriwe.wayndr.Eight;
import com.arriwe.wayndr.GCMListener;
import com.arriwe.wayndr.GMapV2Direction;
import com.arriwe.wayndr.MyDatatype;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.image.SmartImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.graphics.Color.BLUE;
import static com.arriwe.utility.StaticContacts.convertDpToPixel;
import static com.sancsvision.arriwe.R.id.textView_strated_time;
import static com.sancsvision.arriwe.R.id.textView_travelling_loc;


/**
 * Created by Abhi1 on 25/08/15.
 * This is main adapter for activities screen.
 */
public class ListAdapter extends BaseAdapter implements OnMapReadyCallback {

    List<MyDatatype> list;
    private Eight context;
    private Activities act_context;
    // private final JSONObject listValues;
    LatLng zoomVal = null;
    GoogleMap googleMap;
    HashMap map = null;
    JSONArray childArr;
    private Bitmap bmp;
    private ThreeFragment threeFragment;

    // public ListAdapter(Eight context, JSONObject jsonObject) {
    public ListAdapter(Eight context, JSONArray childArr, ThreeFragment threeFragment) {

        this.context = context;
        // this.listValues = jsonObject;
        this.childArr = childArr;
        this.threeFragment = threeFragment;


    }

    public void sendalldata(Eight context, JSONArray childArr, ThreeFragment threeFragment) {
        this.context = context;
        this.childArr = childArr;
        this.threeFragment = threeFragment;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        try {
            return childArr.length();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        try {

            //My Journey
            if (childArr.getJSONObject(position).getString("activity_type").equals("myjourney"))

            {
                try {


//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.recent, null);
//                }

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.recent, null);
                    final JSONObject jsonObject = childArr.getJSONObject(position);

                    Log.e("JSON:", jsonObject.toString());
                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    // String placeName = travellerName;
                    String placeName = jsonObject.getString("end_title");
                    String attributedPlaceName = "<font color='#0070C0'>" + placeName + "</font>";
                    String timeStarted = jsonObject.getString("start_time");


                    String time = Utils.calculateTimeDiffFromNow(timeStarted);
                    //   Log.d("time",time);
                    String timeRemaning = null;


                    //  initMap();
                    timeRemaning = jsonObject.getString("remaining_time");
                    String progressStr = String.valueOf(jsonObject.get("progress_percent"));
                    double progressPercent = 1.0f;
                    if (progressStr != null && progressStr.length() > 0 && !(progressStr.equals("null"))) {
                        progressPercent = Double.parseDouble(progressStr);
                    }

                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    LinearLayout parentlayout = (LinearLayout) convertView.findViewById(R.id.parentlayout);

                    ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    parentlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.sendpos(position, "full");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    JSONArray taggedPersons = jsonObject.getJSONArray("tagger");
                    StringBuilder allTaggedString = new StringBuilder();
                    allTaggedString.append(taggedPersons.length() + " tagged.");
                    try {
                        attributedPlaceName = taggedPersons.getJSONObject(0).getString("end_loc");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//
                    //Travelling text
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                    if (timeRemaning.equals("null")) {
                        lblListHeader.setText("You are away from");
                    } else {
                        lblListHeader.setText(Html.fromHtml("<strong>You</strong> are " + timeRemaning + " away from "));
                    }

                    TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                    travellingLocTV.setText(Html.fromHtml(attributedPlaceName));


                    TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_strated_time);
                    timeSinceStarted.setText(time);

                    Date today = new Date();
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy  hh:mm:ss aa");
                    String date = DATE_FORMAT.format(today);


                    // TextView taggedPersonsTV = (TextView) convertView.findViewById(R.id.textView_tagged_persons);
                    // taggedPersonsTV.setText(allTaggedString);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity) context).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;


                    String string = jsonObject.getString("total_distance").replace(" ", "").replace("km", "").replace(",", "");

                    if (string.contains(".")) {
                        int iend = string.indexOf("."); //this finds the first occurrence of "."


                        if (iend != -1)
                            string = string.substring(0, iend);


                    }

                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);
                    progressBar.setProgress((int) progressPercent);
                    progressBar.getIndeterminateDrawable().setColorFilter(BLUE, PorterDuff.Mode.MULTIPLY);

                    showPrograss(Integer.parseInt(jsonObject.getString("tripid")), (int) progressPercent, lblListHeader.getText().toString().replace("from", ""), travellingLocTV.getText().toString(), taggedImgView);
                    try {
                        JSONArray jsonArray = childArr.optJSONObject(position).getJSONArray("tagger");
                        LinearLayout ll_tagger = (LinearLayout) convertView.findViewById(R.id.ll_tagger);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jO = jsonArray.getJSONObject(i);

                            LinearLayout linearLayout = new LinearLayout(context);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER;
                            layoutParams.setMargins(50, 0, 0, 0);
                            linearLayout.setLayoutParams(layoutParams);

                            RoundedImageView circleImageView = new RoundedImageView(context);
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(convertDpToPixel(20, context), convertDpToPixel(20, context));
                            layoutParams1.setMargins(10, 10, 10, 10);
                            layoutParams1.gravity = Gravity.CENTER;
                            circleImageView.setLayoutParams(layoutParams1);
                            circleImageView.setImageUrl(Constants.DEV_IMG_BASE_URL + jO.getString("profile_image"));

                            linearLayout.addView(circleImageView);

                            ll_tagger.addView(linearLayout);
                        }
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }

                    Button button_accept = (Button) convertView.findViewById(R.id.button_accept);
                    button_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.openlivemap(jsonObject.get("start_lat").toString(), jsonObject.get("start_long").toString(), jsonObject.get("end_lat").toString(), jsonObject.get("end_long").toString());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
/*

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.recent, null);
                    final JSONObject jsonObject = childArr.getJSONObject(position);

                    Log.e("JSON:", jsonObject.toString());
                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    String placeName = jsonObject.getString("end_title");
                    String attributedPlaceName = "<font color='#0070C0'>" + placeName + "</font>";
                    String timeStarted = jsonObject.getString("start_time");


                    String time = Utils.calculateTimeDiffFromNow(timeStarted);
                    String timeRemaning = null;
                    timeRemaning = jsonObject.getString("remaining_time");
                    String progressStr = String.valueOf(jsonObject.get("progress_percent"));
                    double progressPercent = 1.0f;
                    if (progressStr != null && progressStr.length() > 0 && !(progressStr.equals("null"))) {
                        progressPercent = Double.parseDouble(progressStr);
                    }
                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    JSONArray taggedPersons = jsonObject.getJSONArray("tagger");
                    StringBuilder allTaggedString = new StringBuilder();
                    allTaggedString.append(taggedPersons.length() + " tagged.");
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                    if (timeRemaning.equals("null")) {
                        lblListHeader.setText("You are away from");
                    } else {
                        lblListHeader.setText(Html.fromHtml("<strong>You</strong> are " + timeRemaning + " away from "));
                    }
                    TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                    travellingLocTV.setText(Html.fromHtml(placeName));
                    TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_strated_time);
                    timeSinceStarted.setText(time);
                    Date today = new Date();
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy  hh:mm:ss aa");
                    String date = DATE_FORMAT.format(today);
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity) context).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;

                    height = (height * 20) / 100;

                    String start_latlng = jsonObject.getString("end_lat") + "," + jsonObject.getString("end_long");
                    String end_latlng = jsonObject.getString("start_lat") + "," + jsonObject.getString("start_long");

                    LatLng latLng = midPoint(Double.parseDouble(jsonObject.getString("end_lat")), Double.parseDouble(jsonObject.getString("end_long")), Double.parseDouble(jsonObject.getString("start_lat")), Double.parseDouble(jsonObject.getString("start_long")));
                    String string = jsonObject.getString("total_distance").replace(" ", "").replace("km", "").replace(",", "");
                    if (string.contains(".")) {
                        int iend = string.indexOf("."); //this finds the first occurrence of "."
                        if (iend != -1)
                            string = string.substring(0, iend);
                    }
                    int dist = 0;
                    if (!string.equals("")) {
                        dist = Integer.parseInt(string);
                    }
                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);
                    progressBar.setProgress((int) progressPercent);
                    progressBar.getIndeterminateDrawable().setColorFilter(BLUE, PorterDuff.Mode.MULTIPLY);

                    showPrograss(Integer.parseInt(jsonObject.getString("tripid")), (int) progressPercent, lblListHeader.getText().toString().replace("from", ""), travellingLocTV.getText().toString(), taggedImgView);
                */
                }
            } else if (childArr.getJSONObject(position).getString("activity_type").equals("tagging")) {
                try {

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.recent, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);


                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    final String travellerPhone = jsonObject.getString(context.getString(R.string.key_traveller_phone));
                    String placeName = jsonObject.getString("end_title");
                    String attributedPlaceName = "<font color='#0070C0'>" + placeName + "</font>";
                    String timeStarted = jsonObject.getString("start_time");
                    String time = Utils.calculateTimeDiffFromNow(timeStarted);
                    //   Log.d("time",time);
                    String timeRemaning = null;


                    //  initMap();
                    timeRemaning = jsonObject.getString("remaining_time");
                    String progressStr = String.valueOf(jsonObject.get("progress_percent"));
                    double progressPercent = 1.0f;
                    if (progressStr != null && progressStr.length() > 0 && !(progressStr.equals("null"))) {
                        progressPercent = Double.parseDouble(progressStr);
                    }

//                Bitmap travellerImg = Utils.getBitmapFromBase64(jsonObject.getString(context.getString(R.string.key_traveller_pic)));
                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    LinearLayout directionlayout = (LinearLayout) convertView.findViewById(R.id.directionlayout);
                    //    ImageView refreshImgView = (ImageView) convertView.findViewById(R.id.imageView_refresh);

                    directionlayout.setVisibility(View.GONE);
                    LinearLayout parentlayout = (LinearLayout) convertView.findViewById(R.id.parentlayout);
                    ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                    try {
                        JSONArray jsonArray = childArr.optJSONObject(position).getJSONArray("tagger");
                        LinearLayout ll_tagger = (LinearLayout) convertView.findViewById(R.id.ll_tagger);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jO = jsonArray.getJSONObject(i);

                            LinearLayout linearLayout = new LinearLayout(context);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER;
                            layoutParams.setMargins(50, 0, 0, 0);
                            linearLayout.setLayoutParams(layoutParams);

                            RoundedImageView circleImageView = new RoundedImageView(context);
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(convertDpToPixel(20, context), convertDpToPixel(20, context));
                            layoutParams1.setMargins(10, 10, 10, 10);
                            layoutParams1.gravity = Gravity.CENTER;
                            circleImageView.setLayoutParams(layoutParams1);
                            circleImageView.setImageUrl(Constants.DEV_IMG_BASE_URL + jO.getString("profile_image"));

                            linearLayout.addView(circleImageView);

                            ll_tagger.addView(linearLayout);
                        }
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }


                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                String myNo = prefs.getString(context.getResources().getString(R.string.key_user_id), "");
                                context.cancelTripTravellerAction(jsonObject.getString("tripid"), myNo, travellerPhone);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    parentlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.sendpos(position, "full");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    JSONArray taggedPersons = jsonObject.getJSONArray("tagger");
                    StringBuilder allTaggedString = new StringBuilder();
                    allTaggedString.append(taggedPersons.length() + " tagged.");
                    //Travelling text
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                    if (timeRemaning.equals("null")) {
                        lblListHeader.setText(travellerName + " is away from ");
                    } else {
                        lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> is " + timeRemaning + " away from "));
                    }

                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    travellingLocTV.setText(Html.fromHtml(attributedPlaceName));

//                    JSONObject jsonObject1=taggedPersons.getJSONObject(0);
//
//                    travellingLocTV.setText(Html.fromHtml(jsonObject1.getString("name")));

                    TextView timeSinceStarted = (TextView) convertView.findViewById(textView_strated_time);
                    timeSinceStarted.setText(time);


                    // TextView taggedPersonsTV = (TextView) convertView.findViewById(R.id.textView_tagged_persons);
                    // taggedPersonsTV.setText(allTaggedString);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity) context).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;

                    height = (height * 20) / 100;

                    String start_latlng = jsonObject.getString("end_lat") + "," + jsonObject.getString("end_long");
                    String end_latlng = jsonObject.getString("start_lat") + "," + jsonObject.getString("start_long");

                    LatLng latLng = midPoint(Double.parseDouble(jsonObject.getString("end_lat")), Double.parseDouble(jsonObject.getString("end_long")), Double.parseDouble(jsonObject.getString("start_lat")), Double.parseDouble(jsonObject.getString("start_long")));
                    // https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x300&path=color:000000|weight:5|40.737102,-73.990318|40.755823,-73.986397&markers=color:blue%7Clabel:S%7C40.737102,-73.990318&markers=color:red%7Clabel:C%7C40.755823,-73.986397

                    String string = jsonObject.getString("total_distance").replace(" ", "").replace("km", "").replace(",", "");

                    if (string.contains(".")) {
                        int iend = string.indexOf("."); //this finds the first occurrence of "."


                        if (iend != -1)
                            string = string.substring(0, iend);


                    }

                    int dist = 0;
                    if (!string.equals("")) {
                        dist = Integer.parseInt(string);
                    }
                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);
                    progressBar.setProgress((int) progressPercent);
                    //progressBar.setProgress(50);
                    progressBar.getIndeterminateDrawable().setColorFilter(BLUE, PorterDuff.Mode.MULTIPLY);

                    showPrograss(Integer.parseInt(jsonObject.getString("tripid")), (int) progressPercent, lblListHeader.getText().toString().replace("from", ""), travellingLocTV.getText().toString(), taggedImgView);
                } catch (Exception e) {

//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.recent, null);
//                }
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.recent, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);


                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    final String travellerPhone = jsonObject.getString(context.getString(R.string.key_traveller_phone));
                    String placeName = jsonObject.getString("end_title");
                    String attributedPlaceName = "<font color='#0070C0'>" + placeName + "</font>";
                    String timeStarted = jsonObject.getString("start_time");
                    String time = Utils.calculateTimeDiffFromNow(timeStarted);
                    //   Log.d("time",time);
                    String timeRemaning = null;


                    //  initMap();
                    timeRemaning = jsonObject.getString("remaining_time");
                    String progressStr = String.valueOf(jsonObject.get("progress_percent"));
                    double progressPercent = 1.0f;
                    if (progressStr != null && progressStr.length() > 0 && !(progressStr.equals("null"))) {
                        progressPercent = Double.parseDouble(progressStr);
                    }

                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView_cross);


                    ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                String myNo = prefs.getString(context.getResources().getString(R.string.key_user_id), "");
                                context.cancelTripTravellerAction(jsonObject.getString("tripid"), myNo, travellerPhone);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    JSONArray taggedPersons = jsonObject.getJSONArray("tagger");
                    StringBuilder allTaggedString = new StringBuilder();
                    allTaggedString.append(taggedPersons.length() + " tagged.");
                    //Travelling text
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                    if (timeRemaning.equals("null")) {
                        lblListHeader.setText(travellerName + " is away from");
                    } else {
                        lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> is " + timeRemaning + " away from "));
                    }

                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    travellingLocTV.setText(Html.fromHtml(attributedPlaceName));

//                    JSONObject jsonObject1=taggedPersons.getJSONObject(0);
//
//                    travellingLocTV.setText(Html.fromHtml(jsonObject1.getString("name")));

                    TextView timeSinceStarted = (TextView) convertView.findViewById(textView_strated_time);
                    timeSinceStarted.setText(time);


                    // TextView taggedPersonsTV = (TextView) convertView.findViewById(R.id.textView_tagged_persons);
                    // taggedPersonsTV.setText(allTaggedString);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    ((Activity) context).getWindowManager()
                            .getDefaultDisplay()
                            .getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;

                    height = (height * 20) / 100;

                    String start_latlng = jsonObject.getString("end_lat") + "," + jsonObject.getString("end_long");
                    String end_latlng = jsonObject.getString("start_lat") + "," + jsonObject.getString("start_long");

                    LatLng latLng = midPoint(Double.parseDouble(jsonObject.getString("end_lat")), Double.parseDouble(jsonObject.getString("end_long")), Double.parseDouble(jsonObject.getString("start_lat")), Double.parseDouble(jsonObject.getString("start_long")));
                    // https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x300&path=color:000000|weight:5|40.737102,-73.990318|40.755823,-73.986397&markers=color:blue%7Clabel:S%7C40.737102,-73.990318&markers=color:red%7Clabel:C%7C40.755823,-73.986397

                    String string = jsonObject.getString("total_distance").replace(" ", "").replace("km", "").replace(",", "");

                    if (string.contains(".")) {
                        int iend = string.indexOf("."); //this finds the first occurrence of "."


                        if (iend != -1)
                            string = string.substring(0, iend);


                    }

                    int dist = 0;
                    if (!string.equals("")) {
                        dist = Integer.parseInt(string);
                    }
                    int mapZoomLevel = 0;

                    if (dist > 2 && dist <= 5) {

                        mapZoomLevel = 12;
                    } else if (dist > 5 && dist <= 10) {

                        mapZoomLevel = 11;
                    } else if (dist > 10 && dist <= 20) {

                        mapZoomLevel = 11;
                    } else if (dist > 20 && dist <= 40) {

                        mapZoomLevel = 10;
                    } else if (dist > 40 && dist < 100) {

                        mapZoomLevel = 9;
                    } else if (dist > 100 && dist < 200) {

                        mapZoomLevel = 8;
                    } else if (dist > 200 && dist < 400) {

                        mapZoomLevel = 7;
                    } else if (dist > 400 && dist < 700) {

                        mapZoomLevel = 7;
                    } else if (dist > 700 && dist < 1000) {

                        mapZoomLevel = 6;
                    } else if (dist > 1000) {

                        mapZoomLevel = 5;
                    } else {

                        mapZoomLevel = 12;
                    }


                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);
                    progressBar.setProgress((int) progressPercent);
                    //progressBar.setProgress(50);
                    progressBar.getIndeterminateDrawable().setColorFilter(BLUE, PorterDuff.Mode.MULTIPLY);

                    showPrograss(Integer.parseInt(jsonObject.getString("tripid")), (int) progressPercent, lblListHeader.getText().toString().replace("from", ""), travellingLocTV.getText().toString(), taggedImgView);
                }
            } else if (childArr.getJSONObject(position).getString("activity_type").equals("cancelled_journey")) {
                try {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.cancel_layout, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                    String attributedPlaceName = "<font color='#0070C0'>" + placeName + "</font>";
                    String cancelledTime = jsonObject.getString("cancel_time");
                    String time = Utils.calculateTimeDiffFromNow(cancelledTime);
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);
                    lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> cancelled the trip to "));

                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    travellingLocTV.setText(placeName);

                    TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_started_time);
                    timeSinceStarted.setText(time);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);

                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView9);
                    LinearLayout parentcancel = (LinearLayout) convertView.findViewById(R.id.parentcancel);
                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.clearBtnClicked(jsonObject.getString("tripid"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    parentcancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("travelling_lat"), jsonObject.getString("travelling_long"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (Exception e) {
//
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.cancel_layout, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                    String attributedPlaceName = "<font color='#0070C0'>" + placeName + "</font>";
                    String cancelledTime = jsonObject.getString("cancel_time");
                    String time = Utils.calculateTimeDiffFromNow(cancelledTime);
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);
                    lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> cancelled the trip to "));

                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    travellingLocTV.setText(placeName);

                    TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_started_time);
                    timeSinceStarted.setText(time);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);

                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView9);
                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.clearBtnClicked(jsonObject.getString("tripid"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
            // join group confirmation view
            else if (childArr.getJSONObject(position).getString("activity_type").equals("join_group")) {
                try {

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.comfirmtagperson, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    TextView invitedTime = (TextView) convertView.findViewById(textView_strated_time);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                    Button btndecline = (Button) convertView.findViewById(R.id.button_decline);

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.acceptBtnClicked(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.declineBtnClicked(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("group_owner_pic");
                    String startTime = jsonObject.getString("date_time_stamp");
                    String timeSinceStarted = Utils.calculateTimeDiffFromNow(startTime);
                    String place = jsonObject.getString("group_title");
                    String attributedPlaceName = "<font color='#0070C0'>" + place + "</font>";

                    invitersNameTV.setText(Html.fromHtml("<strong>" + jsonObject.getString("group_owner_name") + "</strong> wants you to come to"));
                    travellingLocTV.setText(Html.fromHtml(attributedPlaceName));
                    invitedTime.setText(timeSinceStarted);
                    profileImgView.setImageUrl(completeURl);
                } catch (Exception e) {
//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.comfirmtagperson, null);
//                }

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.comfirmtagperson, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    TextView invitedTime = (TextView) convertView.findViewById(textView_strated_time);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                    Button btndecline = (Button) convertView.findViewById(R.id.button_decline);

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.acceptBtnClicked(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.declineBtnClicked(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("group_owner_pic");
                    String startTime = jsonObject.getString("date_time_stamp");
                    String timeSinceStarted = Utils.calculateTimeDiffFromNow(startTime);
                    String place = jsonObject.getString("group_title");
                    String attributedPlaceName = "<font color='#0070C0'>" + place + "</font>";

                    invitersNameTV.setText(Html.fromHtml("<strong>" + jsonObject.getString("group_owner_name") + "</strong> wants you to come to"));
                    travellingLocTV.setText(Html.fromHtml(attributedPlaceName));
                    invitedTime.setText(timeSinceStarted);
                    profileImgView.setImageUrl(completeURl);
                }
            }

            //arriwed
            else if (childArr.getJSONObject(position).getString("activity_type").equals("arrived_journey")) {
                try {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.arriwe_layout, null);

//
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                    String attributedPlaceName = "<font color='#FFFFFF'>" + placeName + "</font>";
                    String cancelledTime = jsonObject.getString("cancel_time");
                    String time = Utils.calculateTimeDiffFromNow(cancelledTime);
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);
                    lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> arriwed at "));

                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    travellingLocTV.setText(placeName);

                    TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_started_time);
                    timeSinceStarted.setText(time);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);

                    Double lat = jsonObject.getDouble("travelling_lat");
                    Double lon = jsonObject.getDouble("travelling_long");
                    //show nearby friends in case of arriwe .
                    //    context.initFragment(lat, lon);

                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView9);
                    LinearLayout parentarrived = (LinearLayout) convertView.findViewById(R.id.parentarrived);
                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.clearBtnClicked(jsonObject.getString("tripid"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    parentarrived.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("travelling_lat"), jsonObject.getString("travelling_long"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.arriwe_layout, null);

//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.arriwe_layout, null);
//                }
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                    String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                    String attributedPlaceName = "<font color='#FFFFFF'>" + placeName + "</font>";
                    String cancelledTime = jsonObject.getString("cancel_time");
                    String time = Utils.calculateTimeDiffFromNow(cancelledTime);
                    TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);
                    lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> arriwed at "));

                    TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    travellingLocTV.setText(placeName);

                    TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_started_time);
                    timeSinceStarted.setText(time);

                    //my profile image
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("traveller_photo");
                    RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    taggedImgView.setImageUrl(completeURl);

                    Double lat = jsonObject.getDouble("travelling_lat");
                    Double lon = jsonObject.getDouble("travelling_long");
                    //show nearby friends in case of arriwe .
                    //    context.initFragment(lat, lon);

                    LinearLayout crossImgView = (LinearLayout) convertView.findViewById(R.id.imageView9);
                    crossImgView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.clearBtnClicked(jsonObject.getString("tripid"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
            //asked to share your location
            else if (childArr.getJSONObject(position).getString("activity_type").equals("receieverequestuser"))

            {
                try {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.item_location_requested, null);

                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView invitedTime = (TextView) convertView.findViewById(textView_strated_time);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    TextView textView_travelling_loc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                    Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                    Button btndecline = (Button) convertView.findViewById(R.id.button_decline);
                    LinearLayout imageView_cross = (LinearLayout) convertView.findViewById(R.id.imageView_cross);

                    TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);

                    textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));


                    String lat = "null";
                    String lon = "null";
                    if (LocationService.LATEST_LOC_OBJ != null) {
                        Location loc = LocationService.LATEST_LOC_OBJ;
                        lat = String.valueOf(loc.getLatitude());
                        lon = String.valueOf(loc.getLongitude());
                    }
                    float currLat = 0;
                    float currLong = 0;


                    if (!(lat.equals("null"))) {
                        currLat = Float.parseFloat(lat);
                    }
                    if (!(lon.equals("null"))) {
                        currLong = Float.parseFloat(lon);
                    }


                    imageView_cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                    try {
                        String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
                        recivedSmart.setImageUrl(url_for_snap);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    recivedSmart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("latitude"), jsonObject.getString("longtitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.shareLocBtnClicked(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
//                String startTime = jsonObject.getString("date_time_stamp");
//                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                    invitersNameTV.setText(Html.fromHtml("<strong>" + jsonObject.getString("name") + "</strong>"));
                    textView_travelling_loc.setText("has requested for your location");
                    //has requested for your location
//                invitedTime.setText(timeSinceStarted+" ago");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");

                    Picasso.with(context)
                            .load(completeURl)
                            .into(profileImgView);


                    // profileImgView.setImageUrl(completeURl);
                } catch (Exception e) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.item_location_requested, null);

//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.item_location_requested, null);
//                }
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView invitedTime = (TextView) convertView.findViewById(textView_strated_time);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    TextView textView_travelling_loc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                    Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                    Button btndecline = (Button) convertView.findViewById(R.id.button_decline);
                    LinearLayout imageView_cross = (LinearLayout) convertView.findViewById(R.id.imageView_cross);

                    TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);

                    textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));

                    SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                    try {
                        String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
                        recivedSmart.setImageUrl(url_for_snap);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    recivedSmart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("latitude"), jsonObject.getString("longtitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    String lat = "null";
                    String lon = "null";
                    if (LocationService.LATEST_LOC_OBJ != null) {
                        Location loc = LocationService.LATEST_LOC_OBJ;
                        lat = String.valueOf(loc.getLatitude());
                        lon = String.valueOf(loc.getLongitude());
                    }
                    float currLat = 0;
                    float currLong = 0;


                    if (!(lat.equals("null"))) {
                        currLat = Float.parseFloat(lat);
                    }
                    if (!(lon.equals("null"))) {
                        currLong = Float.parseFloat(lon);
                    }


                    imageView_cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.shareLocBtnClicked(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
//                String startTime = jsonObject.getString("date_time_stamp");
//                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                    invitersNameTV.setText(Html.fromHtml("<strong>" + jsonObject.getString("name") + "</strong>"));
                    textView_travelling_loc.setText("has requested for your location");
                    //has requested for your location
//                invitedTime.setText(timeSinceStarted+" ago");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");

                    Picasso.with(context)
                            .load(completeURl)
                            .into(profileImgView);


                    // profileImgView.setImageUrl(completeURl);
                }
            }
            //recieved a location

            else if (childArr.getJSONObject(position).getString("activity_type").equals("receieve_location")) {
                try {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.item_received_location, null);

//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.item_received_location, null);// list_item_location_recieved
//                }
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView locTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                    LinearLayout imageView_cross = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    try {


                        String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
                        recivedSmart.setImageUrl(url_for_snap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    recivedSmart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("latitude"), jsonObject.getString("longtitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    imageView_cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    final Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (btnAccept.getText().toString().equalsIgnoreCase("SAVE CONTACT TO CONTINUE")) {
                                    ClsGeneral.setPreferences(context, "refrescontact", "yes");
                                    addnewcontact(jsonObject.getString("mobile"));
                                } else {
                                    ArrayList<String> contactNos = Utils.getContactsFromPhone(context);
                                    String numbers = null;
                                    for (int i = 0; i < contactNos.size(); i++) {
                                        if (contactNos.get(i).length() > 9) {
                                            String text = contactNos.get(i).replace(" ", "");
                                            numbers = text.length() <= 10 ? text : text.substring(text.length() - 10);
                                            //substring = contactNos.get(i).substring(Math.max(contactNos.get(i).length() - 10, 0));
                                            if (numbers.equalsIgnoreCase(jsonObject.getString("mobile"))) {
                                                context.createTripFromSharedLocation(jsonObject);
                                                return;
                                            }
                                        }
                                        if (contactNos.size() == i + 1) {
                                            btnAccept.setText("SAVE CONTACT TO CONTINUE");

                                        }
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));

                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
                    invitersNameTV.setText(Html.fromHtml("<strong>" + jsonObject.getString("name") + "</strong> "));
                    locTV.setText("sent a location");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");
                    Picasso.with(context)
                            .load(completeURl)
                            .into(profileImgView);


                } catch (Exception e) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.item_received_location, null);

//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.item_received_location, null);// list_item_location_recieved
//                }
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView locTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                    LinearLayout imageView_cross = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    try {
                        String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
                        recivedSmart.setImageUrl(url_for_snap);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    recivedSmart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("latitude"), jsonObject.getString("longtitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    imageView_cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                    btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                ArrayList<String> contactNos = Utils.getContactsFromPhone(context);
                                String substring = null;
                                for (int i = 0; i < contactNos.size(); i++) {
                                    if (contactNos.get(i).length() > 9) {
                                        substring = contactNos.get(i).substring(Math.max(contactNos.get(i).length() - 10, 0));
                                        if (substring.equalsIgnoreCase(jsonObject.getString("mobile"))) {
                                            context.createTripFromSharedLocation(jsonObject);
                                            return;
                                        }
                                    }
                                    if (contactNos.size() == i) {
                                        addnewcontact(jsonObject.getString("mobile"));
                                    }

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));

                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
                    invitersNameTV.setText(Html.fromHtml("<strong>" + jsonObject.getString("name") + "</strong> "));
                    locTV.setText("sent a location");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");
                    Picasso.with(context)
                            .load(completeURl)
                            .into(profileImgView);


                }
            }
            //requested for a location
            else if (childArr.getJSONObject(position).getString("activity_type").equals("requestuser")) {
                try {

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.request_locationfromothers, null);

//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.request_locationfromothers, null);
//                }
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView reqLocTextTV = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);

                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    LinearLayout btndecline = (LinearLayout) convertView.findViewById(R.id.imageView9);
                    TextView textView_started_time = (TextView) convertView.findViewById(R.id.textView_started_time);
                    TextView textView_travelling_loc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);

                    RoundedImageView pro_frag_three10 = (RoundedImageView) convertView.findViewById(R.id.pro_frag_three10);
                    RoundedImageView pro_frag_three1 = (RoundedImageView) convertView.findViewById(R.id.pro_frag_three1);

                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.clearBtnClicked(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
                    reqLocTextTV.setText(Html.fromHtml("<strong>You</strong> requested location from"));
                    textView_travelling_loc.setText(Html.fromHtml("<strong>" + jsonObject.getString("name") + "</strong>"));
                    //  profileImgView.setImageUrl(completeURl);


                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");

//                Log.e("path:",path);
                    Picasso.with(context)
                            .load(path)
                            .into(profileImgView);
                    Picasso.with(context)
                            .load(path)
                            .into(pro_frag_three1);

//
                    textView_started_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));
                    pro_frag_three10.setImageUrl(completeURl);
                } catch (Exception e) {

                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.request_locationfromothers, null);

//
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView reqLocTextTV = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);

                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    LinearLayout btndecline = (LinearLayout) convertView.findViewById(R.id.imageView9);
                    TextView textView_started_time = (TextView) convertView.findViewById(R.id.textView_started_time);
                    TextView textView_travelling_loc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);

                    RoundedImageView pro_frag_three10 = (RoundedImageView) convertView.findViewById(R.id.pro_frag_three10);
                    RoundedImageView pro_frag_three1 = (RoundedImageView) convertView.findViewById(R.id.pro_frag_three1);

                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.clearBtnClicked(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
                    reqLocTextTV.setText(Html.fromHtml("<strong>You</strong> requested location from"));
                    textView_travelling_loc.setText(Html.fromHtml("<strong>" + jsonObject.getString("name") + "</strong>"));
                    //  profileImgView.setImageUrl(completeURl);


                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");

//                Log.e("path:",path);
                    Picasso.with(context)
                            .load(path)
                            .into(profileImgView);
                    Picasso.with(context)
                            .load(path)
                            .into(pro_frag_three1);


                    textView_started_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));
                    pro_frag_three10.setImageUrl(completeURl);
                }
            }
            //shared a location
            else if (childArr.getJSONObject(position).getString("activity_type").equals("shareuser")) {
                try {
//
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_loc_shared, null);
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView reqLocTextTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    LinearLayout btndecline = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);

                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                                // childArr.remove(position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");
//                Log.e("path:",path);
                    Picasso.with(context)
                            .load(path)
                            .into(profileImgView);


                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
                    invitersNameTV.setText(Html.fromHtml("<strong>" + "You" + "</strong> sent your location to"));

                    // himmat blue m

                    //you photo view reciver photo

                    String text = jsonObject.getString("name");
                    reqLocTextTV.setText(Html.fromHtml("<strong>" + text + "</strong>"));


                    textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));
                    // profileImgView.setImageUrl(completeURl);
                    SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                    try {
                        String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
                        recivedSmart.setImageUrl(url_for_snap);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    recivedSmart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                threeFragment.sendpos(position, jsonObject.getString("latitude"), jsonObject.getString("longtitude"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (Exception e) {
//
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_loc_shared, null);
                    final JSONObject jsonObject = childArr.getJSONObject(position);
                    TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                    TextView reqLocTextTV = (TextView) convertView.findViewById(textView_travelling_loc);
                    RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                    LinearLayout btndecline = (LinearLayout) convertView.findViewById(R.id.imageView_cross);
                    TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);

                    btndecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String path = sharedPreferences.getString(context.getResources().getString(R.string.path_profile_pic), "");
//                Log.e("path:",path);
                    Picasso.with(context)
                            .load(path)
                            .into(profileImgView);


                    String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject.getString("pic");
                    invitersNameTV.setText(Html.fromHtml("<strong>" + "You" + "</strong> sent your location to"));

                    // himmat blue m

                    //you photo view reciver photo

                    String text = jsonObject.getString("name");
                    reqLocTextTV.setText(Html.fromHtml(text));

                    textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));
                    // profileImgView.setImageUrl(completeURl);
                    try {
                        SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                        String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
                        recivedSmart.setImageUrl(url_for_snap);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.eleven, null);
        }
        return convertView;


    }

    private void addnewcontact(String mobile) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.PHONE, mobile);

        context.startActivity(intent);
    }


    public static LatLng midPoint(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        Log.d("radioue", Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
        //print out in degrees

    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    /**
     * Custom implementations
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void showPrograss(int trip_id, int progressPercent, String s, String description, RoundedImageView roundedImageView) {
        //  roundedImageView.setId(R.id.titleId);
        NotificationManager mNotifyManager;

        NotificationCompat.Builder mBuilder;
        int id = 10;

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new android.support.v7.app.NotificationCompat.Builder(context);
        mBuilder.setContentTitle(s)
                .setContentText("traveling to " + description)
                .setSmallIcon(GCMListener.getNotificationIcon())
                .setLargeIcon(roundedImageView.getDrawingCache());

    //    mBuilder.setProgress(100, progressPercent, false);
        mNotifyManager.notify(trip_id, mBuilder.build());
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
                    //              Polyline polylin = mapObj.addPolyline(rectLine);md.getDurationText(doc);
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
                    //            mapObj.animateCamera(cameraUpdate);
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

    public JSONArray getSortedList(JSONArray array) throws JSONException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }
        Collections.sort(list, new SortBasedOnMessageId());

        JSONArray resultArray = new JSONArray(list);

        return resultArray;
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

}

