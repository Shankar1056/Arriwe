package com.arriwe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arriwe.Model.Contact;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;
import com.arriwe.wayndr.Nine;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.sancsvision.arriwe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * Created by Abhi1 on 25/08/15.
 */
public class ContactsGridAdapter extends BaseAdapter {
    public List<Contact> arrayList = new ArrayList<>();

    LayoutInflater inflater;
    private Context context;
    private ArrayList<JSONObject> gridValues;
    private ArrayList<JSONObject> gridValuesSearch = new ArrayList<>();
    String myNo = "";
    SharedPreferences prefs;
    String TAG = "ContactsGridAdapter";
    List<String> tag_postion = new ArrayList<>();
    List<String> already_added = new ArrayList<>();
    int sys = 1;

    //Constructor to initialize values
    public ContactsGridAdapter(Context context, ArrayList<JSONObject> gridValues,int sys,List<String> already_added) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context        = context;
        this.gridValues     = gridValues;
        this.sys = sys;
        this.already_added = already_added;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        myNo = prefs.getString(context.getResources().getString(R.string.key_reg_no), "");
        gridValuesSearch.addAll(gridValues);
    }
    public ContactsGridAdapter(Context context, ArrayList<JSONObject> gridValues) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context        = context;
        this.gridValues     = gridValues;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        myNo = prefs.getString(context.getResources().getString(R.string.key_reg_no), "");

        Log.e("Contacts before:",gridValues.toString());

        for (int i = 0; i < gridValues.size(); i++) {
            JSONObject jsonObject = gridValues.get(i);
            try {
                if(!jsonObject.getString("mobile").equals(myNo)){
                    Contact contact = new Contact(jsonObject.getString("name"), jsonObject.getString("mobile"), jsonObject.getString("photo"), jsonObject.getString("city"));
                    arrayList.add(contact);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Collections.sort(arrayList, new Comparator<Contact>() {
            public int compare(Contact v1, Contact v2) {

                return v1.getContactName().toLowerCase().compareToIgnoreCase(v2.getContactName().toLowerCase());
            }
        });

        gridValues.clear();
        for (int i=0;i<arrayList.size();i++)
        {
            try {

                JSONObject item = new JSONObject();
                item.put("name", arrayList.get(i).getContactName());
                item.put("mobile", arrayList.get(i).getContactNumber());
                item.put("photo", arrayList.get(i).getContactPhoto());
                item.put("city", arrayList.get(i).getContactLocation());

                gridValues.add(item);

            }catch (Exception e)
            {
                Log.e("Error:",e.toString());
            }


        }

        Log.e("Contacts After:",gridValues.toString());
        gridValuesSearch.addAll(gridValues);
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

    @SuppressLint("NewApi")
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.main_activity_grid_item,null);
            holder = new Holder();
            holder.taggedView = (RelativeLayout) convertView.findViewById(R.id.layout_hover);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.imageButton_gridview);
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView);
            holder.imgView = (ImageView) convertView.findViewById(R.id.imageView_tag_travel);
            holder.textView = (TextView) convertView.findViewById(R.id.textViewName_gridview);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        holder.taggedView.setContentDescription(String.valueOf(position));
        // set value into textview
        JSONObject object = this.gridValues.get(position);

        try {
            if(!object.getString("mobile").equalsIgnoreCase(myNo)){
                //      try {
                if(sys==0){
                    if(already_added.contains(object.getString("mobile")))
                        object.put(context.getString(R.string.key_is_added_for_travel), true);
                }

                String name = object.getString("name");
                name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
                holder.textView.setText(name);
    /*    } catch (JSONException e) {
            e.printStackTrace();
        }
*/
                //      try {
                String completeURl = Constants.DEV_IMG_BASE_URL+object.getString("photo");
                holder.imageView.setImageUrl(completeURl);
       /* }*//* catch (JSONException e) {
            e.printStackTrace();
        }*//*
*/

                Boolean taggedRes = false;
                Boolean travelWithRes = false;
                if(object.has(context.getString(R.string.key_is_tagged))) {
                    taggedRes = object.getBoolean(context.getString(R.string.key_is_tagged));
                }
                if(object.has(context.getString(R.string.key_is_added_for_travel))) {
                    travelWithRes = object.getBoolean(context.getString(R.string.key_is_added_for_travel));
                }

                if(taggedRes){
                    holder.taggedView.setVisibility(View.VISIBLE);
                    if(position== Nine.pos)
                        YoYo.with(Techniques.BounceIn).duration(500).playOn(holder.taggedView);
                    holder.taggedView.setAlpha(0.95f);
                    holder.imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.tag_contacts));
                    holder.imgView.setVisibility(View.VISIBLE);
                    holder.imageView2.setAlpha(1f);
                }


                if(travelWithRes){
                    holder.taggedView.setVisibility(View.VISIBLE);
                    holder.taggedView.setAlpha(0.9f);

//                    ImageView imgViewMid = (ImageView) gridView.findViewById(R.id.imageView_up);
////                    imgViewMid.setBackground(context.getResources().getDrawable(R.drawable.round_tag));
//                    imgViewMid.setBackgroundResource(R.drawable.round_tag);
//
                    holder.imgView.setVisibility(View.VISIBLE);
                    holder.imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.add_contacts_green));
                }
                else {
                    if(!taggedRes)
                        holder.taggedView.setVisibility(View.INVISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //}
        /*else {

            gridView = (View) convertView;
        }*/

        return convertView;
    }

    // Filter Class
    public void filter(String charText) {
        try {
            charText = charText.toLowerCase(Locale.getDefault());
            gridValues.clear();
            if (charText.length() == 0) {
                gridValues.addAll(gridValuesSearch);
            } else {
                for (JSONObject wp : gridValuesSearch) {
                    if (wp.getString("name").toLowerCase(Locale.getDefault())
                            .contains(charText) || wp.getString("mobile").toLowerCase(Locale.getDefault()).contains(charText)) {
                        gridValues.add(wp);
                    }

                }
            }
            notifyDataSetChanged();
        }
        catch (Exception e){
            Log.e(TAG,e.toString());
        }

    }

    static class Holder{
        RelativeLayout taggedView;
        ImageView imgView,imageView2;
        RoundedImageView imageView;
        TextView textView;
    }


}

/*
if(taggedRes == true){
    RelativeLayout taggedView = (RelativeLayout) gridView.findViewById(R.id.layout_hover);
    taggedView.setVisibility(View.VISIBLE);
    ImageView imgView = (ImageView) gridView.findViewById(R.id.imageView_tag_travel);

    YoYo.with(Techniques.BounceIn).duration(500).playOn(taggedView);
    taggedView.setAlpha(0.9f);
    imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.tag_contacts));
    imgView.setAlpha(1f);
}

*/