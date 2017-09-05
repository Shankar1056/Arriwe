package com.arriwe.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import static com.sancsvision.arriwe.R.id.textView_strated_time;
import static com.sancsvision.arriwe.R.id.textView_travelling_loc;



/**
 * Created by Abhi1 on 25/08/15.
 * This is main adapter for activities screen.
 */
public class TaggedListAdapter extends BaseExpandableListAdapter implements OnMapReadyCallback {

    List<MyDatatype> list;
    private Eight context;
    private Activities act_context;
    private final JSONObject listValues;
    LatLng zoomVal = null;
    GoogleMap googleMap;


    //Constructor to initialize values
    public TaggedListAdapter(Eight context, JSONObject jsonObject) {

        this.context = context;
        this.listValues = jsonObject;

    }

    public TaggedListAdapter(Activities context, JSONObject jsonObject) {

        act_context = context;
        this.listValues = jsonObject;

    }

    @Override
    public int getGroupCount() {
        return 10;
    }

    @Override
    public int getChildTypeCount() {
        return 10;
    }

    public int getAdapterSize(){
        return listValues.length();
    }


    @Override
    public int getChildType(int groupPosition, int childPosition) {
        int pos = 0;
        switch (groupPosition) {
            case 0:
                pos = 0;
                break;
            case 1:
                pos = 1;
                break;
            case 2:
                pos = 2;
                break;
            case 3:
                pos = 3;
                break;
            case 4:
                pos = 4;
                break;
            case 5:
                pos = 5;
                break;

            case 6:
                pos = 6;
                break;

            case 7:
                pos = 7;
                break;

            case 8:
                pos = 8;
                break;
            case 9:
                pos = 9;
                break;
        }

        return pos;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        Log.e("TaggedListAdapter", "Child count is " + groupPosition);
        int count = 0;

        try {
            HashMap map = getRowDetailsASection(groupPosition);
            count = (int) map.get(context.getString(R.string.key_activity_count));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }


    @Override
    public Object getGroup(int groupPosition) {

        HashMap map = null;
        try {
            map = getRowDetailsASection(groupPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map.get(context.getString(R.string.key_activity_details));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        JSONObject jsonObject = null;
        try {

            HashMap map = getRowDetailsASection(groupPosition);
            JSONArray childArr = (JSONArray) map.get(context.getString(R.string.key_activity_details));
            jsonObject = childArr.getJSONObject(childPosition);

//            Log.e("...................:",jsonObject.toString());

        } catch (JSONException e) {

            e.printStackTrace();

        }
        return jsonObject;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.i("TaggedListAdapter", "getGroupView " + groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_group_item, null);
        }
        convertView.setClickable(false);
        convertView.setFocusable(false);

//        TextView lblListHeader = (TextView) convertView
//                .findViewById(R.id.lblListHeader);
//        lblListHeader.setText("Testing123");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        HashMap map = null;
        try {
            map = getRowDetailsASection(groupPosition);

            list=new ArrayList<MyDatatype>();

            JSONArray childArr2 = (JSONArray) map.get(context.getString(R.string.key_activity_details));

            JSONArray childArr=getSortedList(childArr2);
//            for (int i=0;i<childArr.length();i++)
//            {
//                MyDatatype myDatatype=new MyDatatype();
//
//            }

//
//
            Log.e("Sorted Position:"+groupPosition+",Child:"+childPosition,childArr.toString());
//            Log.e("groupPosition>>>>>>>.",groupPosition+"");
//



            //My Journey
            if (groupPosition == 2) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.recent, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);

                Log.e("JSON:",jsonObject.toString());
                String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                String placeName = travellerName;
//                String placeName = jsonObject.getString("end_title");
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
                //    ImageView refreshImgView = (ImageView) convertView.findViewById(R.id.imageView_refresh);
                SmartImageView mapSnapchat = (SmartImageView) convertView.findViewById(R.id.map);


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
                attributedPlaceName=taggedPersons.getJSONObject(0).getString("name");

//                for (int i=0;i<taggedPersons.length();i++){
//                    JSONObject obj = taggedPersons.getJSONObject(i);
//                    allTaggedString.append(obj.get("name"));
//                    if(taggedPersons.length() - 1 > i)
//                    allTaggedString.append(", ");
//
//                }
                //Travelling text
                TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                if (timeRemaning.equals("null")) {
                    lblListHeader.setText("You are away from");
                } else {
                    lblListHeader.setText(Html.fromHtml("<strong>You</strong> are " + timeRemaning + " away from"));
                }

                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));


                TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_strated_time);
                timeSinceStarted.setText(time);

                Date today = new Date();
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy  hh:mm:ss aa");
                String date  = DATE_FORMAT.format(today);



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
                //String mapScreenshotUrl = "http://maps.googleapis.com/maps/api/staticmap?center=" + latLng.latitude + "," + latLng.longitude + "&zoom=13&size=" + width + "x" + height + "&path=color:000000|weight:5|" + start_latlng + "+|" + end_latlng + "&markers=color:blue%7Clabel:S%7C" + end_latlng + "&markers=color:red%7Clabel:D%7C" + start_latlng;

                String string=jsonObject.getString("total_distance").replace(" ","").replace("km","").replace(",","");

                if (string.contains("."))
                {
                    int iend = string.indexOf("."); //this finds the first occurrence of "."


                    if (iend != -1)
                        string= string.substring(0 , iend);


                }


                int dist=Integer.parseInt(string);

                int mapZoomLevel=0;

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

                    mapZoomLevel = 14;
                }


                String mapScreenshotUrl = "http://maps.googleapis.com/maps/api/staticmap?center=" + latLng.latitude + "," + latLng.longitude + "&zoom="+mapZoomLevel+"&size=" + width + "x" + height + "&path=color:000000|weight:5|" + start_latlng + "+|" + end_latlng + "&markers=color:blue%7Clabel:S%7C" + end_latlng + "&markers=color:red%7Clabel:D%7C" + start_latlng;

                Log.e("Map URL:",mapScreenshotUrl);

                Log.e("jsonObject>>>>"+groupPosition,jsonObject.toString());
//                mapSnapchat.setImageUrl(mapScreenshotUrl);

                Picasso.with(context)
                        .load(mapScreenshotUrl)
                        .into(mapSnapchat);


                RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
//                taggedImgView.setImageUrl(completeURl);

                Picasso.with(context)
                        .load(completeURl)
                        .into(taggedImgView);

                progressBar.setProgress((int) progressPercent);
                progressBar.getIndeterminateDrawable().setColorFilter(BLUE, PorterDuff.Mode.MULTIPLY);

                showPrograss(Integer.parseInt(jsonObject.getString("tripid")), (int) progressPercent, lblListHeader.getText().toString().replace("from",""), travellingLocTV.getText().toString(), taggedImgView);
            }
            //Friend's journey for which I am tagged
            else if (groupPosition == 3) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.recent, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);


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
                //    ImageView refreshImgView = (ImageView) convertView.findViewById(R.id.imageView_refresh);
                SmartImageView mapSnapchat = (SmartImageView) convertView.findViewById(R.id.map);


                ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                crossImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            String myNo = prefs.getString(context.getResources().getString(R.string.key_user_id), "");
                            context.cancelTripTravellerAction(jsonObject.getString("tripid"),myNo,travellerPhone);
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
                    lblListHeader.setText(Html.fromHtml("<strong>" + travellerName + "</strong> is " + timeRemaning + " away from"));
                }

                TextView travellingLocTV = (TextView) convertView.findViewById(textView_travelling_loc);
//                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));

                JSONObject jsonObject1=taggedPersons.getJSONObject(0);

                travellingLocTV.setText(Html.fromHtml(jsonObject1.getString("name")));

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

                String string=jsonObject.getString("total_distance").replace(" ","").replace("km","").replace(",","");

                if (string.contains("."))
                {
                    int iend = string.indexOf("."); //this finds the first occurrence of "."


                    if (iend != -1)
                        string= string.substring(0 , iend);


                }


                int dist=Integer.parseInt(string);
                int mapZoomLevel=0;

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

                    mapZoomLevel = 14;
                }





                String mapScreenshotUrl = "http://maps.googleapis.com/maps/api/staticmap?center=" + latLng.latitude + "," + latLng.longitude + "&zoom="+mapZoomLevel+"&size=" + width + "x" + height + "&path=color:000000|weight:5|" + start_latlng + "+|" + end_latlng + "&markers=color:blue%7Clabel:S%7C" + end_latlng + "&markers=color:red%7Clabel:D%7C" + start_latlng;

                //Log.d("imagespath", mapScreenshotUrl);
//                mapSnapchat.setImageUrl(mapScreenshotUrl);
                Picasso.with(context)
                        .load(mapScreenshotUrl)
                        .into(mapSnapchat);

                RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
//                taggedImgView.setImageUrl(completeURl);
                Picasso.with(context)
                        .load(completeURl)
                        .into(taggedImgView);

                progressBar.setProgress((int) progressPercent);
                //progressBar.setProgress(50);
                progressBar.getIndeterminateDrawable().setColorFilter(BLUE, PorterDuff.Mode.MULTIPLY);

                showPrograss(Integer.parseInt(jsonObject.getString("tripid")), (int) progressPercent, lblListHeader.getText().toString().replace("from",""), travellingLocTV.getText().toString(), taggedImgView);
            }

            //travelling with code to come here
//            if (groupPosition == 4) {
//                if (convertView == null) {
//                    LayoutInflater infalInflater = (LayoutInflater) this.context
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    convertView = infalInflater.inflate(R.layout.eleven, null);
//                }
//                JSONArray taggedPersons = null;
//                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
//                JSONArray travellingPersons = jsonObject.getJSONArray("traveller");
//                //SmartImageView group_map = (SmartImageView) convertView.findViewById(R.id.map);
//                String taggedPersonStr = null;
//
//                RoundedImageView imageView_tagged = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
////                imageView_tagged.setImageUrl(Constants.DEV_IMG_BASE_URL + jsonObject.getString("group_owner_pic"));
//
//                Picasso.with(context)
//                        .load(Constants.DEV_IMG_BASE_URL + jsonObject.getString("group_owner_pic"))
//                        .into(imageView_tagged);
//
//                DisplayMetrics displaymetrics = new DisplayMetrics();
//                ((Activity) context).getWindowManager()
//                        .getDefaultDisplay()
//                        .getMetrics(displaymetrics);
//                int height = displaymetrics.heightPixels;
//                int width = displaymetrics.widthPixels;
//
//                height = (height * 20) / 100;
//                ArrayList<String> taggedPersonImgArray = new ArrayList<>();
//                ArrayList<String> travellingWithPersonsNameList = new ArrayList<>();
//                //add your own pic as first
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                final String myNo = prefs.getString(context.getResources().getString(R.string.key_reg_no), "");
//
//                LinearLayout crossBtn = (LinearLayout) convertView.findViewById(R.id.button_cancel_trup);
//                SmartImageView smartImageView = (SmartImageView) convertView.findViewById(R.id.map);
//                smartImageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            Intent intent = new Intent(context, GroupTravelDetail.class);
//                            intent.putExtra("group_details", jsonObject.toString());
//                            intent.putExtra("grp_travel_loc", jsonObject.get("group_dest_title").toString());
//                            intent.putExtra("grp_destination_address", jsonObject.get("group_travelling_to").toString());
//                            intent.putExtra("group_dest_lat", jsonObject.get("group_dest_lat").toString());
//                            intent.putExtra("group_dest_lon", jsonObject.get("group_dest_lon").toString());
//                            intent.putExtra("group_invited_nos", jsonObject.get("group_invited_count").toString());
//                            intent.putExtra("trip_id", jsonObject.get("tripid").toString());
//
//                            context.startActivity(intent);
//                            context.overridePendingTransition(R.anim.slide_up, R.anim.stay);
//                        } catch (Exception e) {
//                            Log.e("Error", e.toString());
//                        }
//
//
//                    }
//                });
//
//
//                crossBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            if(jsonObject.getString("group_owner_mob").equals(myNo)){
//                                context.cancelTripBtnAction(jsonObject.getString("tripid"));
//                            }
//                            else {
//                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                                String myuser_id = prefs.getString(context.getResources().getString(R.string.key_user_id), "");
//                                context.cancelTripTrggerAction(jsonObject.getString("tripid"),myuser_id,jsonObject.getString("group_owner_mob"));
//                            }
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//
//                for (int i = 0; i < travellingPersons.length(); i++) {
//                    JSONObject obj = travellingPersons.getJSONObject(i);
//                    if (obj.getString("status").equals("Accepted")) {
//                        taggedPersonImgArray.add(obj.getString("traveller_pic"));
//                        //set you instead of name if its ur no
//                        if (obj.getString("traveller_mob").equals(myNo))
//                            travellingWithPersonsNameList.add("<strong>You</strong>");
//                        else
//                            travellingWithPersonsNameList.add("<strong> " + obj.getString("traveller_name") + "</strong>");
//                    }
//                }
//
//                String travellingLoc = jsonObject.getString("group_dest_title");
//                String attributedPlaceName = "<font color='#0070C0'>" + travellingLoc + "</font>";
//                String startTime = jsonObject.getString("start_time");
//                String timeSinceStarted = Utils.calculateTimeDiffFromNow(startTime);
//
//                TextView travellersName = (TextView) convertView.findViewById(R.id.textView_travellers_name);
//                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_to);
//                TextView sinceStartedTV = (TextView) convertView.findViewById(R.id.textView_since_started);
//
//                JSONArray jsonArray = jsonObject.getJSONArray("traveller");
//                // TextView taggedPersonsList = (TextView) convertView.findViewById(R.id.textView_tagged_ppl);
//                LinearLayout ll_added_pic = (LinearLayout) convertView.findViewById(R.id.ll_added_pic);
//
//                ll_added_pic.removeAllViews();
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                    if(jsonObject1.getString("status").equalsIgnoreCase("Accepted")){
//                        String completeURl = Constants.DEV_IMG_BASE_URL + jsonObject1.getString("traveller_pic");
//                        RoundedImageView roundedImageView = new RoundedImageView(context);
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);
//                        layoutParams.setMargins(0, 0, 20, 0);
//                        roundedImageView.setLayoutParams(layoutParams);
//
//                        if (jsonObject1.getString("traveller_mob").equals(myNo)) {
//                            String start_latlng = jsonObject.getString("group_dest_lat") + "," + jsonObject.getString("group_dest_lon");
//                            String end_latlng = jsonObject1.getString("current_lat") + "," + jsonObject1.getString("current_lon");
//
//                            LatLng latLng = midPoint(Double.parseDouble(jsonObject.getString("group_dest_lat")), Double.parseDouble(jsonObject.getString("group_dest_lon")), Double.parseDouble(jsonObject1.getString("current_lat")), Double.parseDouble(jsonObject1.getString("current_lon")));
//
//                            String mapScreenshotUrl = "http://maps.googleapis.com/maps/api/staticmap?center=" + latLng.latitude + "," + latLng.longitude + "&zoom=13&size=" + width + "x" + height + "&path=color:000000|weight:5|" + start_latlng + "+|" + end_latlng + "&markers=color:blue%7Clabel:S%7C" + end_latlng + "&markers=color:red%7Clabel:D%7C" + start_latlng;
//
////                            smartImageView.setImageUrl(mapScreenshotUrl);
//                            Picasso.with(context)
//                                    .load(mapScreenshotUrl)
//                                    .into(smartImageView);
//
//
//                        }
//
////                        roundedImageView.setImageUrl(completeURl);
//                        Picasso.with(context)
//                                .load(completeURl)
//                                .into(roundedImageView);
//
//                        ll_added_pic.addView(roundedImageView);
//                    }
//
//                }
//
//
//                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));
//                sinceStartedTV.setText(timeSinceStarted);
//
//                 /*else if (travellingWithPersonsNameList.size() == 2) {
//                     strToSet = travellingWithPersonsNameList.get(0) + ", " + travellingWithPersonsNameList.get(1);  + ", " + travellingWithPersonsNameList.get(1)
//                 }*/
//                String strToSet = null;
//                if (travellingWithPersonsNameList.size() == 1) {
//                    strToSet = travellingWithPersonsNameList.get(0);
//                }
//                    /*else if(travellingWithPersonsNameList.size() == 3){
//                        strToSet = travellingWithPersonsNameList.get(0)+", "+travellingWithPersonsNameList.get(1)+", "+travellingWithPersonsNameList.get(2);
//                    }*/
//                else {
//                    //    strToSet = travellingWithPersonsNameList.get(0)+", "+travellingWithPersonsNameList.get(1)+", "+travellingWithPersonsNameList.get(2)+ "and "+(travellingWithPersonsNameList.size()-3)+"others";
//                    strToSet = travellingWithPersonsNameList.get(0) + " and " + (travellingWithPersonsNameList.size() - 1) + " others";
//                }
//                travellersName.setText(Html.fromHtml(strToSet + " are travelling to"));
//               /* if(taggedPersonStr == null){
//                    taggedPersonsList.setText("0 tagged.");
//                }
//                else{
//                    taggedPersonsList.setText(taggedPersonStr+" tagged.");
//                }
//*/
//            }
            //cancelled journey view
            else if (groupPosition == 1) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.cancel_layout, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
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
                Picasso.with(context)
                        .load(completeURl)
                        .into(taggedImgView);

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
            // join group confirmation view
            else if (groupPosition == 5) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.comfirmtagperson, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
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
//                profileImgView.setImageUrl(completeURl);
                Picasso.with(context)
                        .load(completeURl)
                        .into(profileImgView);

            }

            //arriwed
            else if (groupPosition == 0) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.arriwe_layout, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
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
//                taggedImgView.setImageUrl(completeURl);
                Picasso.with(context)
                        .load(completeURl)
                        .into(taggedImgView);

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
            //asked to share your location
            else if (groupPosition == 6) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.item_location_requested, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView invitedTime = (TextView) convertView.findViewById(textView_strated_time);
                RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                TextView textView_travelling_loc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                Button btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                Button btndecline = (Button) convertView.findViewById(R.id.button_decline);
                LinearLayout imageView_cross = (LinearLayout) convertView.findViewById(R.id.imageView_cross);

                TextView textView_strated_time = (TextView) convertView.findViewById(R.id.textView_strated_time);

                textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));

                SmartImageView mapSnapchat = (SmartImageView) convertView.findViewById(R.id.map);

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

                final LatLng currLatLng = new LatLng(currLat, currLong);
                String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + currLat + "," + currLong + "&sensor=false";
//                mapSnapchat.setImageUrl(url_for_snap);
                Picasso.with(context)
                        .load(url_for_snap)
                        .into(mapSnapchat);

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
                        .load(path)
                        .into(profileImgView);


                // profileImgView.setImageUrl(completeURl);
            }
            //recieved a location
            else if (groupPosition == 7) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.item_received_location, null);// list_item_location_recieved
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView locTV = (TextView) convertView.findViewById(textView_travelling_loc);
                SmartImageView recivedSmart = (SmartImageView) convertView.findViewById(R.id.map);
                LinearLayout imageView_cross = (LinearLayout) convertView.findViewById(R.id.imageView_cross);

                String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + jsonObject.getString("latitude") + "," + jsonObject.getString("longtitude") + "&sensor=false";
//                recivedSmart.setImageUrl(url_for_snap);

                Picasso.with(context)
                        .load(url_for_snap)
                        .into(recivedSmart);

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
                            context.createTripFromSharedLocation(jsonObject);
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
                        .load(path)
                        .into(profileImgView);



            }
            //requested for a location
            else if (groupPosition == 8) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.request_locationfromothers, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView reqLocTextTV = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);

                RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                LinearLayout btndecline = (LinearLayout) convertView.findViewById(R.id.imageView9);
                TextView textView_started_time = (TextView) convertView.findViewById(R.id.textView_started_time);
                TextView textView_travelling_loc = (TextView)convertView.findViewById(R.id.textView_travelling_loc);

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

//                try {
//                    Bitmap b = Utils.getBitmapFromPath(context, path);
//                    profileImgView.setImageBitmap(b);
//                    pro_frag_three1.setImageBitmap(b);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

                textView_started_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));
//                pro_frag_three10.setImageUrl(completeURl);

                Picasso.with(context)
                        .load(completeURl)
                        .into(pro_frag_three10);

            }
            //shared a location
            else if (groupPosition == 9) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_loc_shared, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView reqLocTextTV = (TextView) convertView.findViewById(textView_travelling_loc);
                SmartImageView shared_map = (SmartImageView) convertView.findViewById(R.id.map);
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

                String currLat = jsonObject.getString("latitude");
                String currLong = jsonObject.getString("longtitude");
                String url_for_snap = "http://maps.googleapis.com/maps/api/staticmap?zoom=18&size=512x512&markers=size:mid|color:red|" + currLat + "," + currLong + "&sensor=false";
//                shared_map.setImageUrl(url_for_snap);

                Picasso.with(context)
                        .load(url_for_snap)
                        .into(shared_map);

                textView_strated_time.setText(Utils.calculateTimeDiffFromNowdifferentFormat(jsonObject.getString("time")));
                // profileImgView.setImageUrl(completeURl);
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        if (groupPosition == 2 || groupPosition == 4 || groupPosition == 3) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    /**
     * Custom implementations
     */

    private HashMap getRowDetailsASection(int section) throws JSONException {


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

        JSONArray finalObj = null;
        switch (section) {

            case 0: {
                if (this.listValues.getString("arrived_journey").equals("null") || this.listValues.getString("arrived_journey") == null) {
                    arriwed = false;
                    break;
                }

                JSONArray jsonArray = this.listValues.getJSONArray("arrived_journey");
                finalObj = jsonArray;
                count = jsonArray.length();
                arriwed = (jsonArray.length() > 0) ? true : false;
            }
            break;

            case 1: {
                if (this.listValues.getString("cancelled_journey").equals("null") || this.listValues.getString("cancelled_journey") == null) {
                    cancelled = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("cancelled_journey");
                finalObj = jsonArray;
                count = jsonArray.length();
                cancelled = (jsonArray.length() > 0) ? true : false;
            }
            break;

            case 2: {
                //my travel will always be only 1
                if (this.listValues.getString("myjourney").equals("null") || this.listValues.getString("myjourney") == null) {
                    myJourneyStatus = false;
                    break;

                }
                JSONArray jsonArray = this.listValues.getJSONArray("myjourney");
                finalObj = jsonArray;
                count = 1;
                myJourneyStatus = (jsonArray.length() > 0) ? true : false;
            }
            break;

            case 3: {
                if (this.listValues.getString("tagging").equals("null") || this.listValues.getString("tagging") == null) {
                    taggingStatus = false;
                    break;
                }

                JSONArray jsonArray = this.listValues.getJSONArray("tagging");
                //   if(jsonArray.length()==1){
                // JSONObject innerArray = (JSONObject) jsonArray.get(0);
                finalObj = jsonArray;
                count = jsonArray.length();
                taggingStatus = (jsonArray.length() > 0) ? true : false;
                // taggingStatus = true;//(innerArray.length() > 0) ? true : false;
                //    }
                /*else {
                    JSONArray innerArray = (JSONArray) jsonArray.get(0);
                    finalObj = innerArray;
                    count = innerArray.length();
                    taggingStatus = (innerArray.length() > 0) ? true : false;
                }*/


            }
            break;

            case 4: {
                if (this.listValues.getString("travelling_with").equals("null") || this.listValues.getString("travelling_with") == null) {
                    travelWith = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("travelling_with");
                finalObj = jsonArray;
                count = jsonArray.length();
                travelWith = (jsonArray.length() > 0) ? true : false;

            }
            break;


            case 5: {
                if (this.listValues.getString("join_group").equals("null") || this.listValues.getString("join_group") == null) {
                    joinGroup = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("join_group");
                finalObj = jsonArray;
                count = jsonArray.length();
                joinGroup = (jsonArray.length() > 0) ? true : false;
            }
            break;

            case 6: {
                //requested_location
                if (this.listValues.getString("receieverequestuser").equals("null") || this.listValues.getString("receieverequestuser") == null) {
                    reqLoc = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("receieverequestuser");
                finalObj = jsonArray;
                count = jsonArray.length();
                reqLoc = (jsonArray.length() > 0) ? true : false;
            }
            break;

            case 7: {

                //received_location
                //received_location
                if (this.listValues.getString("receieve_location").equals("null") || this.listValues.getString("receieve_location") == null) {
                    rcvLoc = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("receieve_location");
                finalObj = jsonArray;
                count = jsonArray.length();
                rcvLoc = (jsonArray.length() > 0) ? true : false;
            }
            break;
            case 8: {
                if (this.listValues.getString("requestuser").equals("null") || this.listValues.getString("requestuser") == null) {
                    reqForLoc = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("requestuser");
                finalObj = jsonArray;
                count = jsonArray.length();
                reqForLoc = (jsonArray.length() > 0) ? true : false;
            }
            break;
            case 9: {
                if (this.listValues.getString("shareuser").equals("null") || this.listValues.getString("shareuser") == null) {
                    sharedLoc = false;
                    break;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("shareuser");
                finalObj = jsonArray;
                count = jsonArray.length();
                reqForLoc = (jsonArray.length() > 0) ? true : false;
            }
            break;

        }


        if (!myJourneyStatus && !taggingStatus && !travelWith && !cancelled && !joinGroup && !arriwed && !reqLoc && !rcvLoc && !reqForLoc && !sharedLoc) {
            context.showNoActivitiesLabel(1);
        } else {
            context.showNoActivitiesLabel(0);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(context.getString(R.string.key_activity_count), count);
        map.put(context.getString(R.string.key_activity_details), finalObj);
        return map;
    }

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

      //  mBuilder.setProgress(100, progressPercent, false);
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

//    public  JSONArray getSortedList(JSONArray array) throws JSONException {
//        List<JSONObject> list = new ArrayList<JSONObject>();
//        for (int i = 0; i < array.length(); i++) {
//            list.add(array.getJSONObject(i));
//        }
//        Collections.sort(list, new SortBasedOnMessageId());
//
//        JSONArray resultArray = new JSONArray(list);
//
//        return resultArray;
//    }
//
//    public class SortBasedOnMessageId implements Comparator<JSONObject> {
//        /*
//        * (non-Javadoc)
//        *
//        * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
//        * lhs- 1st message in the form of json object. rhs- 2nd message in the form
//        * of json object.
//        */
//        @Override
//        public int compare(JSONObject lhs, JSONObject rhs) {
//            try {
//                return lhs.getInt("tripid") < rhs.getInt("tripid") ? 1 : (lhs
//                        .getInt("tripid") > rhs.getInt("tripid") ? -1 : 0);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return 0;
//
//        }
//    }


    public  JSONArray getSortedList(JSONArray array) throws JSONException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }
        Collections.sort(list, new SortBasedOnMessageId());

        JSONArray resultArray = new JSONArray(list);

        return resultArray;
    }

    public class SortBasedOnMessageId implements Comparator<JSONObject> {
        /*
        * (non-Javadoc)
        *
        * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
        * lhs- 1st message in the form of json object. rhs- 2nd message in the form
        * of json object.
        */
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

//class ViewHolder {
//    public TextView travellerName;
//    public TextView travellingl;
//}