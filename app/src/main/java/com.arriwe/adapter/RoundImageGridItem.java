package com.arriwe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sancsvision.arriwe.R;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Abhi1 on 25/08/15.
 */
public class RoundImageGridItem extends BaseAdapter {

    private Context context;
    private final ArrayList<String> gridValues;

    //Constructor to initialize values
    public RoundImageGridItem(Context context, ArrayList<String> gridValues) {

        this.context        = context;
        this.gridValues     = gridValues;
    }

    @Override
    public int getCount() {

        // Number of times getView method call depends upon gridValues.length
        return gridValues.size();
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

        // LayoutInflator to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.grid_item_round_img, null);

            String base64ImgString = this.gridValues.get(position);

            RoundedImageView imageView = (RoundedImageView) gridView
                    .findViewById(R.id.imageView_grid_activity);

            String completeURl = Constants.DEV_IMG_BASE_URL+this.gridValues.get(position);
//            Bitmap bitmap = Utils.getBitmapFromBase64(base64ImgString);
            imageView.setImageUrl(completeURl);
        }
        else {

            gridView = (View) convertView;
        }

        return gridView;
    }
}