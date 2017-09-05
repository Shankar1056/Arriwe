package com.arriwe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.arriwe.Model.FavPlaceModel;
import com.sancsvision.arriwe.R;
import com.arriwe.wayndr.WhereRUGoing;

import java.util.ArrayList;

/**
 * Created by Abhi1 on 17/08/15.
 */

public class FavPlaceGridAdapter extends BaseAdapter {
    ArrayList<FavPlaceModel> favPlaceList;
    Context mContext;

    public FavPlaceGridAdapter(Context context,ArrayList<FavPlaceModel> data){
        super();
        mContext = context;
        favPlaceList = data;
    }
    @Override
    public int getCount() {
        return favPlaceList.size();
    }

    @Override
    public Object getItem(int position) {
        return favPlaceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        String title = favPlaceList.get(position).placeName;
        Button btn = new Button(mContext);
        btn.setText(title);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        btn.setTextColor(mContext.getResources().getColor(R.color.white));
        Drawable d = mContext.getResources().getDrawable(R.drawable.rounded_rect);
        btn.setBackground(d);
        Resources r = Resources.getSystem();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());

        btn.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WhereRUGoing obj= (WhereRUGoing)mContext;
                obj.favLocationBtnClicked(position,favPlaceList.get(position));
            }
        });
        return btn;
    }
}

