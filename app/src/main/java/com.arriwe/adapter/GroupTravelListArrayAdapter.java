package com.arriwe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sancsvision.arriwe.R;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Abhi1 on 16/08/15.
 */
public class GroupTravelListArrayAdapter
        extends ArrayAdapter {
    private static final String TAG = "GroupTravelListArrayAdapter";
    private  int layoutResourceId;
    private  Context context;
    private  JSONArray dataArray;

    public GroupTravelListArrayAdapter(Context context, int resource, JSONArray dataArray) {
        super(context, resource);
        this.dataArray = dataArray;
        this.layoutResourceId = resource;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.dataArray.length();
    }

    @Override
    public  JSONObject getItem(int position){
        JSONObject obj = null;
        try {
            obj = this.dataArray.getJSONObject(position);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  obj;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.layoutResourceId, parent, false);
        }

        try {
            JSONObject jsonObject = this.dataArray.getJSONObject(position);
            String travellerName = jsonObject.getString(context.getString(R.string.key_traveller_name));
            String currLoc = jsonObject.getString("current_address");
            String timeStarted = jsonObject.getString("start_time");
            String progressStr = String.valueOf(jsonObject.get("progress_percent"));
            String timeRemaning = jsonObject.getString("remaining_time");
            double progressPercent = 1.0f;
            if(progressStr != null && progressStr.length() > 0 && !(progressStr.equals("null"))) {
                progressPercent = Double.parseDouble(progressStr);
            }

//            double progressPercent = Double.parseDouble(String.valueOf(jsonObject.get("progress_percent")));
//            String timeRemaning =  Utils.calculateTimeDiffFromNow(timeStarted);

            //my profile image
            String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("traveller_pic");
            RoundedImageView taggedImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_tagged);
            taggedImgView.setImageUrl(completeURl);

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_tagged);
            lblListHeader.setText(travellerName);

            TextView currLocTV = (TextView) convertView.findViewById(R.id.textView_curr_loc);
            if(currLoc.equals("null")){
                currLocTV.setText("Not available");

            }
            else {
                currLocTV.setText(currLoc);
            }
//
            TextView timeRemaningTV = (TextView) convertView.findViewById(R.id.textView_started_time);
            timeRemaningTV.setText(timeRemaning +" to destination");

            if(timeRemaning.equals("null")){
                timeRemaningTV.setText("Not available");

            }
            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_tagged);
            progressBar.setProgress((int) progressPercent);

            //here it will be used to show the time at which req was accepted
            TextView tvTravellingLoc = (TextView) convertView.findViewById(R.id.textView_travelling_loc);
            tvTravellingLoc.setText("Accepted at " + Utils.changeDateTimeFormat(timeStarted));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  convertView;
    }

}