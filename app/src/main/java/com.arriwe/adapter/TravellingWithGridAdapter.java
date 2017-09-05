package com.arriwe.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.arriwe.utility.RoundedImageView;
import com.arriwe.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Abhi1 on 25/08/15.
 */
public class TravellingWithGridAdapter extends BaseAdapter {

    private Context context;
    private final ArrayList<JSONObject> imgUrlArray;

    //Constructor to initialize values
    public TravellingWithGridAdapter(Context context, ArrayList<JSONObject> gridValues) {

        this.context        = context;
        this.imgUrlArray     = gridValues;
    }

    @Override
    public int getCount() {

        // Number of times getView method call depends upon gridValues.length
        return imgUrlArray.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    // Number of times getView method call depends upon gridValues.length

    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject jsonObject = this.imgUrlArray.get(position);
        Resources r = Resources.getSystem();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics());

        RoundedImageView imgView = new RoundedImageView(context,null);
        imgView.setAdjustViewBounds(true);
        imgView.setMaxWidth(px);
        imgView.setMaxHeight(px);
        try {
            Bitmap bitmap = Utils.getBitmapFromBase64(jsonObject.getString("photo"));
            imgView.setImageBitmap(bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        imgView.setBackground(context.getResources().getDrawable(R.drawable.rounded_rect));


        return  imgView;
    }
}