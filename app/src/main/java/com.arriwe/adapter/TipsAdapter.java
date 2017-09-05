package com.arriwe.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.arriwe.wayndr.TipsFirst;
import com.sancsvision.arriwe.R;


/**
 * Created by Abhi1 on 28/05/15.
 */
public class TipsAdapter extends PagerAdapter {

    TipsFirst mContext;
    LayoutInflater mLayoutInflater;
    //images to come here
    public int[] contentArray = {R.layout.tips_second,R.layout.tips_second};

    public TipsAdapter(Context context) {
        mContext = (TipsFirst)context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return contentArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(contentArray[position], container, false);
        ((ViewPager)container).addView(itemView, 0);
        container.addView(itemView);
//        if(position == 1){
//            LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.tip_second);
//            Button button = (Button) layout.findViewById(R.id.button_skip);
//          //  mContext.init(button);
//        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
