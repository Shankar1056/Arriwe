package com.arriwe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sancsvision.arriwe.R;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Abhi1 on 16/08/15.
 */
public class ContactsListAdapter extends ArrayAdapter {
    private static final String TAG = "ContactsListAdapter";
    private  int layoutResourceId;
    private  Context context;
    private  ArrayList<JSONObject> dataArray;

    /**
     * Constructor
     *
     * @param context       Context
     * @param resource      Layout resource
     * @param data     Data array model
     */
    public ContactsListAdapter(Context context, int resource, ArrayList<JSONObject> data) {
        super(context, resource);
        this.dataArray = data;
        this.layoutResourceId = resource;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.dataArray.size();
    }

    @Override
    public  JSONObject getItem(int position){
        JSONObject obj = null;
        try {
            obj = this.dataArray.get(position);
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
            JSONObject jsonObject = this.dataArray.get(position);
            String name = jsonObject.getString("name");
            String mobile = jsonObject.getString("mobile");

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.textView_name);
            lblListHeader.setText(name);


            TextView lblListMob = (TextView) convertView.findViewById(R.id.textView_number);
            lblListMob.setText(mobile);

            RoundedImageView profileImgView = (RoundedImageView) convertView.findViewById(R.id.imageView_profile);
            String completeURl = Constants.DEV_IMG_BASE_URL+jsonObject.getString("photo");
            profileImgView.setImageUrl(completeURl);


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  convertView;
    }

}