package com.arriwe.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arriwe.Model.FavPlaceModel;
import com.sancsvision.arriwe.R;

import java.util.ArrayList;



/**
 * Created by Nishu on 01-11-2016.
 */
public class FavouriteAdapter extends BaseAdapter {

    LayoutInflater innflater;
    Context context;
    String[] titles,details;
    ArrayList<FavPlaceModel> data;
    int res;
    public FavouriteAdapter(Context context, String[] titles, String[] details, int res) {
        this.context = context;
        this.titles = titles;
        this.details = details;
        this.res = res;
        innflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public FavouriteAdapter(Context context, int resource, ArrayList<FavPlaceModel> data) {
        this.context = context;
        this.res = resource;
        this.data = data;
        for(int i=0;i<data.size();i++){
            Log.d("size",data.get(i).userSavedName);
        }

        innflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public FavPlaceModel getItem(int i) {
        FavPlaceModel obj = null;
        try {
            obj = this.data.get(i);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  obj;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if(view == null){
            view = innflater.inflate(R.layout.row,null);
            holder = new Holder();
            holder.title = (TextView) view.findViewById(R.id.a);
            holder.detail = (TextView) view.findViewById(R.id.b);
            view.setTag(holder);
        }
        else {
            holder = (Holder) view.getTag();
        }

        try {
            FavPlaceModel jsonObject = this.data.get(i);
            String placeName = data.get(i).getUserSavedName();//jsonObject.userSavedName;
            String savedName = data.get(i).getPlaceName();
            holder.title.setText(placeName);
            holder.detail.setText(savedName);
        }
        catch (Exception e){
            e.printStackTrace();
        }

       /* v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, view.getId()+"", Toast.LENGTH_SHORT).show();
            }
        });*/
        return view;
    }

    static class Holder{
        TextView title,detail;
    }

}
