package com.arriwe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arriwe.Model.FavPlaceModel;
import com.sancsvision.arriwe.R;

import java.util.ArrayList;

/**
 * Created by Abhi1 on 16/08/15.
 */
public class FavPlacesListAdapter
        extends ArrayAdapter {
    private static final String TAG = "FavPlacesListAdapter";
    private  int layoutResourceId;
    private  Context context;
    private  ArrayList<FavPlaceModel> dataArray;

    /**
     * Constructor
     *
     * @param context       Context
     * @param resource      Layout resource
     * @param data     Data array model
     */
    public FavPlacesListAdapter(Context context, int resource, ArrayList<FavPlaceModel> data) {
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
    public  FavPlaceModel getItem(int position){
        FavPlaceModel obj = null;
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
            FavPlaceModel jsonObject = this.dataArray.get(position);
            String placeName = jsonObject.userSavedName;
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.textview_fav_list);
            lblListHeader.setText(placeName);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  convertView;
    }

}