package com.arriwe.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sancsvision.arriwe.R;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Eight;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lenovo on 12/10/2016.
 */

public class TaggedListAdapterNew extends BaseAdapter {

    private Eight context;
    private final JSONObject listValues;
    LatLng zoomVal = null;

    private static String TAG = "Adpter";

    int groupPosition = 0;
    int childPosition = 0;

    //Constructor to initialize values
    public TaggedListAdapterNew(Eight context, JSONObject jsonObject) {

        this.context        = context;
        this.listValues     = jsonObject;
    }



    @Override
    public int getCount() {
        int cnt = 0;
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("arrived_journey");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("cancelled_journey");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("myjourney");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("tagging");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("travelling_with");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("join_group");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("join_group");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("requested_location");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("received_location");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("requestedfor_location");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }
        try {
            JSONArray arriwe_journey = listValues.getJSONArray("shared_location");
            cnt+=arriwe_journey.length();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }

        return cnt;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap map = null;
        try {
            map = getRowDetailsASection(groupPosition);
            JSONArray childArr = (JSONArray) map.get(context.getString(R.string.key_activity_details));

            //My Journey
            if(groupPosition == 2) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_your_travel, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                String placeName = jsonObject.getString("end_title");
                String attributedPlaceName = "<font color='#0070C0'>"+placeName+"</font>";
                String timeStarted = jsonObject.getString("start_time");
                String time =  Utils.calculateTimeDiffFromNow(timeStarted);
                String timeRemaning =  null;
                timeRemaning = jsonObject.getString("remaining_time");
                String progressStr = String.valueOf(jsonObject.get("progress_percent"));
                double progressPercent = 1.0f;
                if(progressStr != null && progressStr.length() > 0 && !(progressStr.equals("null"))) {
                    progressPercent = Double.parseDouble(progressStr);
                }

//                Bitmap travellerImg = Utils.getBitmapFromBase64(jsonObject.getString(context.getString(R.string.key_traveller_pic)));
                ImageView crossImgView = (ImageView) convertView.findViewById(R.id.imageView_cross);
                ImageView refreshImgView = (ImageView) convertView.findViewById(R.id.imageView_refresh);
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

                refreshImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            context.refreshBtnClicked();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });

                JSONArray taggedPersons = jsonObject.getJSONArray("tagger");
                StringBuilder allTaggedString = new StringBuilder();
                allTaggedString.append(taggedPersons.length()+" tagged.");

//                for (int i=0;i<taggedPersons.length();i++){
//                    JSONObject obj = taggedPersons.getJSONObject(i);
//                    allTaggedString.append(obj.get("name"));
//                    if(taggedPersons.length() - 1 > i)
//                    allTaggedString.append(", ");
//
//                }
                //Travelling text
                TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                if(timeRemaning.equals("null")){
                    lblListHeader.setText("You are away from");
                }
                else {
                    lblListHeader.setText(Html.fromHtml("<strong>You</strong> are " + timeRemaning + " away from"));
                }

                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));


                TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_strated_time);
                timeSinceStarted.setText("Started "+time +" ago");

                TextView taggedPersonsTV = (TextView) convertView.findViewById(R.id.textView_tagged_persons);
                taggedPersonsTV.setText(allTaggedString);

                //my profile image
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_photo");
                RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                taggedImgView.setImageUrl(completeURl);
                progressBar.setProgress((int)progressPercent);

            }
            //Friend's journey for which I am tagged
            else if(groupPosition == 3) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_tagged, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                String placeName = jsonObject.getString("travelling_title");
                String attributedPlaceName = "<font color='#0070C0'>"+placeName+"</font>";
                String remaningTime = jsonObject.getString("remaining_time");
                String currAddress = jsonObject.getString("current_title");
                String startTime = jsonObject.getString("start_time");
                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);
                double progressPercent = Double.parseDouble(String.valueOf(jsonObject.get("progress_percent")));

//                Bitmap travellerImg = Utils.getBitmapFromBase64(jsonObject.getString(context.getString(R.string.key_traveller_pic)));

                TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView tvTravellingLoc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                TextView tvcurrLoc = (TextView) convertView.findViewById(R.id.textView_curr_loc);
                TextView tvstartedTime = (TextView) convertView.findViewById(R.id.textView_started_time);
                ImageView refreshImgView = (ImageView) convertView.findViewById(R.id.imageView11);
                ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_tagged);

                lblListHeader.setText(Html.fromHtml("<strong>"+travellerName+"</strong> is "+remaningTime +" away from" ));
                tvTravellingLoc.setText(Html.fromHtml(attributedPlaceName));
                tvcurrLoc.setText(currAddress);
                tvstartedTime.setText(timeSinceStarted+" since started");
                progressBar.setProgress((int)progressPercent);

                //my profile image
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_photo");
                RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                taggedImgView.setImageUrl(completeURl);

                refreshImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            context.refreshBtnClicked();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });

                ImageView crossBtn = (ImageView) convertView.findViewById(R.id.imageView4);
                crossBtn.setOnClickListener(new View.OnClickListener() {
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

            //travelling with code to come here
            else if(groupPosition == 4){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_travelling_with, null);
                }

                JSONArray taggedPersons = null;
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                JSONArray travellingPersons = jsonObject.getJSONArray("traveller");
                String taggedPersonStr = null;

                if(!(jsonObject.get("tagged_people").equals(null))){
                    taggedPersons = jsonObject.getJSONArray("tagged_people");
                    for (int i=0;i< taggedPersons.length();i++){
                        JSONObject obj = taggedPersons.getJSONObject(i);
                        if(taggedPersonStr == null){
                            taggedPersonStr = obj.getString("name");
                        }
                        else {
                            taggedPersonStr = taggedPersonStr + "," + obj.getString("name");
                        }
                        if(i > 2){
                            int restPplCount = taggedPersons.length() - 2;
                            taggedPersonStr = taggedPersonStr + String.valueOf(restPplCount);
                            break;
                        }

                    }
                }
                ArrayList<String> taggedPersonImgArray = new ArrayList();
                ArrayList<String>  travellingWithPersonsNameList = new ArrayList();
                //add your own pic as first
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String myNo = prefs.getString(context.getResources().getString(R.string.key_reg_no), "");

                ImageView crossBtn = (ImageView) convertView.findViewById(R.id.button_cancel_trup);
                crossBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            context.cancelTripBtnAction(jsonObject.getString("tripid"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                for(int i=0;i<travellingPersons.length();i++){
                    JSONObject obj = travellingPersons.getJSONObject(i);
                    if(obj.getString("status").equals("Accepted")) {
                        taggedPersonImgArray.add(obj.getString("traveller_pic"));
                        //set you instead of name if its ur no
                        if(obj.getString("traveller_mob").equals(myNo))
                            travellingWithPersonsNameList.add("<strong>You</strong>");
                        else
                            travellingWithPersonsNameList.add("<strong> "+obj.getString("traveller_name")+"</strong>");
                    }
                }

                GridView gridView = (GridView) convertView.findViewById(R.id.gridView_travelling_with);
                gridView.setAdapter(new RoundImageGridItem(context,taggedPersonImgArray));

                String travellingLoc = jsonObject.getString("group_dest_title");
                String attributedPlaceName = "<font color='#0070C0'>"+travellingLoc+"</font>";
                String startTime = jsonObject.getString("start_time");
                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                TextView travellersName = (TextView) convertView.findViewById(R.id.textView_travellers_name);
                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_to);
                TextView sinceStartedTV = (TextView) convertView.findViewById(R.id.textView_since_started);
                TextView taggedPersonsList = (TextView) convertView.findViewById(R.id.textView_tagged_ppl);


                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));
                sinceStartedTV.setText("Started "+timeSinceStarted+" ago");

                String strToSet = null;
                if(travellingWithPersonsNameList.size() == 1){
                    strToSet = travellingWithPersonsNameList.get(0);
                }

                else if(travellingWithPersonsNameList.size() == 2){
                    strToSet = travellingWithPersonsNameList.get(0)+", "+travellingWithPersonsNameList.get(1);
                }
                else if(travellingWithPersonsNameList.size() == 3){
                    strToSet = travellingWithPersonsNameList.get(0)+", "+travellingWithPersonsNameList.get(1)+", "+travellingWithPersonsNameList.get(2);
                }
                else{
                    strToSet = travellingWithPersonsNameList.get(0)+", "+travellingWithPersonsNameList.get(1)+", "+travellingWithPersonsNameList.get(2)+ "and "+(travellingWithPersonsNameList.size()-3)+"others";
                }
                travellersName.setText(Html.fromHtml(strToSet+" are travelling to"));
                if(taggedPersonStr == null){
                    taggedPersonsList.setText("0 tagged.");
                }
                else{
                    taggedPersonsList.setText(taggedPersonStr+" tagged.");
                }
            }
            //cancelled journey view
            else if(groupPosition == 1){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_cancelled, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
//                final JSONObject jsonObject = (((JSONArray)(childArr.getJSONArray(0))).getJSONObject(childPosition));
                String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                String attributedPlaceName = "<font color='#0070C0'>"+placeName+"</font>";
                String cancelledTime = jsonObject.getString("cancel_time");
                String time =  Utils.calculateTimeDiffFromNow(cancelledTime);
//                String timeRemaning =  null;
//                timeRemaning = jsonObject.getString("remaining_time");
                TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged_cancel);
                lblListHeader.setText(Html.fromHtml("<strong>"+travellerName+"</strong> cancelled a trip to "));

                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));

                TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_started_time);
                timeSinceStarted.setText(time+" ago");

                //my profile image
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_photo");
                RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                taggedImgView.setImageUrl(completeURl);

                ImageView crossImgView = (ImageView) convertView.findViewById(R.id.imageView9);
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
            else if(groupPosition == 5){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_confirmation, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                TextView invitedTime = (TextView) convertView.findViewById(R.id.textView_strated_time);
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
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("group_owner_pic");
                String startTime = jsonObject.getString("date_time_stamp");
                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);
                String place = jsonObject.getString("group_title");
                String attributedPlaceName = "<font color='#0070C0'>"+place+"</font>";

                invitersNameTV.setText(Html.fromHtml("<strong>"+jsonObject.getString("group_owner_name")+"</strong> invited you to come to"));
                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));
                invitedTime.setText(timeSinceStarted+" ago");
                profileImgView.setImageUrl(completeURl);
            }

            //arriwed
            else if(groupPosition == 0) {
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_arriwed, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
                String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                String attributedPlaceName = "<font color='#FFFFFF'>"+placeName+"</font>";
                String cancelledTime = jsonObject.getString("cancel_time");
                String time =  Utils.calculateTimeDiffFromNow(cancelledTime);
//                String timeRemaning =  null;
//                timeRemaning = jsonObject.getString("remaining_time");
                TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
                lblListHeader.setText(Html.fromHtml("<strong>"+travellerName+"</strong> arriwed at "));

                TextView travellingLocTV = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
                travellingLocTV.setText(Html.fromHtml(attributedPlaceName));

                TextView timeSinceStarted = (TextView) convertView.findViewById(R.id.textView_started_time);
                timeSinceStarted.setText(time+" ago");

                //my profile image
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_photo");
                RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                taggedImgView.setImageUrl(completeURl);

                Double lat = jsonObject.getDouble("travelling_lat");
                Double lon = jsonObject.getDouble("travelling_long");
                //show nearby friends in case of arriwe .
                context.initFragment(lat,lon);

                ImageView crossImgView = (ImageView) convertView.findViewById(R.id.imageView8);
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
            else if(groupPosition == 6){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_location_requested, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView invitedTime = (TextView) convertView.findViewById(R.id.textView_strated_time);
                RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                Button btnAccept = (Button) convertView.findViewById(R.id.button_share_loc);
                Button btndecline = (Button) convertView.findViewById(R.id.button_decline_loc_req);

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
                            context.declineLocShareBtnClicked(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_photo");
//                String startTime = jsonObject.getString("date_time_stamp");
//                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                invitersNameTV.setText(Html.fromHtml("<strong>"+jsonObject.getString("traveller_name")+"</strong> has requested for your location"));
//                invitedTime.setText(timeSinceStarted+" ago");
                profileImgView.setImageUrl(completeURl);
            }
            //recieved a location
            else if(groupPosition == 7){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item_location_recieved, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_loc_shared);
                TextView locTV = (TextView) convertView.findViewById(R.id.textView_going_loc);

                String placeName = jsonObject.getString(context.getString(R.string.key_travelling_loc));
                String attributedPlaceName = "<font color='#FFFFFF'>"+placeName+"</font>";
                locTV.setText(Html.fromHtml(attributedPlaceName));

//                TextView invitedTime = (TextView) convertView.findViewById(R.id.textView_strated_time);
                RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
                Button btnAccept = (Button) convertView.findViewById(R.id.button_create_trip);
                Button btndecline = (Button) convertView.findViewById(R.id.button_ignore);

//                final MapFragment mapFragment = (MapFragment)  context.getFragmentManager()
//                        .findFragmentById(R.id.fragment2);
//                mapFragment.getMapAsync(this);

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

                btndecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            context.ignoreBtnClicked(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("travelrequest_photo");

//                String startTime = jsonObject.getString("date_time_stamp");
//                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                invitersNameTV.setText(Html.fromHtml("<strong>"+jsonObject.getString("travelrequest_name")+"</strong> has shared a location"));
//                invitedTime.setText(timeSinceStarted+" ago");
                profileImgView.setImageUrl(completeURl);
//                Double lat = jsonObject.getDouble("travelling_lat");
//                Double lon = jsonObject.getDouble("travelling_lon");
//                this.zoomVal = new LatLng(lat,lon);
            }
            //requested for a location
            else if(groupPosition == 8){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_loc_req_status, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView reqLocTextTV = (TextView) convertView.findViewById(R.id.textView_req_for_loc);

//                TextView invitedTime = (TextView) convertView.findViewById(R.id.textView_strated_time);
                RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
//                Button btnAccept = (Button) convertView.findViewById(R.id.button_create_trip);
                ImageView btndecline = (ImageView) convertView.findViewById(R.id.button_ignore);

//                btnAccept.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            context.createTripFromSharedLocation(jsonObject);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
                btndecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            context.ignoreBtnClicked(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_photo");
//                String startTime = jsonObject.getString("date_time_stamp");
//                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                invitersNameTV.setText(Html.fromHtml("<strong>You</strong>"));
                reqLocTextTV.setText("have requested for "+jsonObject.getString("travelrequest_name") +"'s location");

//                invitedTime.setText(timeSinceStarted+" ago");
                profileImgView.setImageUrl(completeURl);
            }
            //shared a location
            else if(groupPosition == 9){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_loc_shared_status, null);
                }
                final JSONObject jsonObject = childArr.getJSONObject(childPosition);
                TextView invitersNameTV = (TextView) convertView.findViewById(R.id.textView_tagged);
                TextView reqLocTextTV = (TextView) convertView.findViewById(R.id.textView_req_for_loc);

//                TextView invitedTime = (TextView) convertView.findViewById(R.id.textView_strated_time);
                RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
//                Button btnAccept = (Button) convertView.findViewById(R.id.button_create_trip);
                ImageView btndecline = (ImageView) convertView.findViewById(R.id.button_ignore);

//                btnAccept.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            context.createTripFromSharedLocation(jsonObject);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
                btndecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            context.ignoreBtnClicked(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("travelrequest_photo");
//                String startTime = jsonObject.getString("date_time_stamp");
//                String timeSinceStarted =  Utils.calculateTimeDiffFromNow(startTime);

                invitersNameTV.setText(Html.fromHtml("<strong>You</strong>"));
                reqLocTextTV.setText("have shared your location");

//                invitedTime.setText(timeSinceStarted+" ago");
                profileImgView.setImageUrl(completeURl);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }
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
        switch (section){

            case 0: {
                if(this.listValues.getString("arrived_journey").equals("null") ){
                    arriwed = false;
                }

                JSONArray jsonArray = this.listValues.getJSONArray("arrived_journey");
                finalObj = jsonArray;
                count = jsonArray.length();
                arriwed = (jsonArray.length() > 0) ? true:false;
            }
            break;

            case 1: {
                if(this.listValues.getString("cancelled_journey").equals("null") ){
                    cancelled = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("cancelled_journey");
                finalObj = jsonArray;
                count = jsonArray.length();
                cancelled = (jsonArray.length() > 0) ? true:false;
            }
            break;

            case 2: {
                //my travel will always be only 1
                if(this.listValues.getString("myjourney").equals("null") ){
                    myJourneyStatus = false;
                    break;

                }
                JSONArray jsonArray = this.listValues.getJSONArray("myjourney");
                finalObj = jsonArray;
                count = 1;
                myJourneyStatus = (jsonArray.length() > 0) ? true:false;
            }
            break;

            case 3: {
                if(this.listValues.getString("tagging").equals("null") ){
                    taggingStatus = false;
                }

                JSONArray jsonArray = this.listValues.getJSONArray("tagging");
                JSONArray innerArray = (JSONArray) jsonArray.get(0);
                finalObj = innerArray;
                count = innerArray.length();
                taggingStatus = (innerArray.length() > 0) ? true:false;
            }
            break;

            case 4: {
                if(this.listValues.getString("travelling_with").equals("null") ){
                    travelWith = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("travelling_with");
                finalObj = jsonArray;
                count = jsonArray.length();
                travelWith = (jsonArray.length() > 0) ? true:false;

            }
            break;


            case 5: {
                if(this.listValues.getString("join_group").equals("null") ){
                    joinGroup = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("join_group");
                finalObj = jsonArray;
                count = jsonArray.length();
                joinGroup = (jsonArray.length() > 0) ? true:false;
            }
            break;

            case 6: {
                if(this.listValues.getString("requested_location").equals("null") ){
                    reqLoc = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("requested_location");
                finalObj = jsonArray;
                count = jsonArray.length();
                reqLoc = (jsonArray.length() > 0) ? true:false;
            }
            break;

            case 7: {
                if(this.listValues.getString("received_location").equals("null") ){
                    rcvLoc = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("received_location");
                finalObj = jsonArray;
                count = jsonArray.length();
                rcvLoc = (jsonArray.length() > 0) ? true:false;
            }
            break;
            case 8: {
                if(this.listValues.getString("requestedfor_location").equals("null") ){
                    reqForLoc = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("requestedfor_location");
                finalObj = jsonArray;
                count = jsonArray.length();
                reqForLoc = (jsonArray.length() > 0) ? true:false;
            }
            break;
            case 9: {
                if(this.listValues.getString("shared_location").equals("null") ){
                    sharedLoc = false;
                }
                JSONArray jsonArray = this.listValues.getJSONArray("shared_location");
                finalObj = jsonArray;
                count = jsonArray.length();
                reqForLoc = (jsonArray.length() > 0) ? true:false;
            }
            break;

        }


        if(!myJourneyStatus && !taggingStatus && !travelWith && !cancelled && !joinGroup && !arriwed && !reqLoc && !rcvLoc && !reqForLoc && !sharedLoc){
            context.showNoActivitiesLabel(1);
        }
        else{
            context.showNoActivitiesLabel(0);
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put(context.getString(R.string.key_activity_count),count);
        map.put(context.getString(R.string.key_activity_details),finalObj);
        return  map;
    }
}
