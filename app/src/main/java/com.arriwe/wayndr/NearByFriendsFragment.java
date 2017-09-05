package com.arriwe.wayndr;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sancsvision.arriwe.R;
import com.arriwe.adapter.ContactsGridAdapter;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

//import com.arriwe.networkmanagers.NetworkDataModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByFriendsFragment extends Fragment {

    private static String TAG = "NearByFriendsFragment.java";
    GridView gridView = null;
    TextView nearbyFriendsTV = null;
    ProgressDialog dialog = null;
    double latDestination;
    double lonDestination;
    int notificationReq;
    String locName;
    ImageView crossButton;


    public NearByFriendsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NearByFriendsFragment(double travellingToLat, double travellingToLon, int notificationReq , String locName) {
        // Required empty public constructor
        this.latDestination = travellingToLat;
        this.lonDestination = travellingToLon;
        this.notificationReq = notificationReq;
        this.locName = locName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getFragmentManager().beginTransaction().hide(this).commit();

        nearbyFriendsTV = (TextView) getView().findViewById(R.id.textView14);
        nearbyFriendsTV.setText("Friends who are at "+this.locName + ".");
        crossButton = (ImageView) getView().findViewById(R.id.imageView_cancel_nearby_activity_btn);
        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("NearbyFriendsFragment")).commit();
            }
        });

        try {
            initContacts();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    void initContacts() throws JSONException, UnsupportedEncodingException {
        ArrayList<String> contactNos = Utils.getContactsFromPhone(getActivity());
        callApiToGetNearbyContacts(contactNos);
    }

    /***
     * API Calls
     */

    @SuppressLint("LongLogTag")
    void callApiToGetNearbyContacts(ArrayList<String> phoneNOs) throws JSONException, UnsupportedEncodingException {
        NearByFriendsFragment fragment = this;

        if (!Utils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
//        dialog = Utils.showProgressDialog(getActivity(), "Loading your contacts");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String registeredPhoneNo = prefs.getString(getString(R.string.key_reg_no), "");

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(getResources().getString(R.string.key_phonenos), contactsJsonArray);
//        String requestString = "phoneNos=" + URLEncoder.encode(String.valueOf(phoneNOs), "utf-8") + "&to_lat="+ URLEncoder.encode(String.valueOf(lat);
        String requestString = "phoneNos=" + URLEncoder.encode(String.valueOf(phoneNOs), "utf-8") + "&phoneNo=" + URLEncoder.encode(String.valueOf(registeredPhoneNo), "utf-8") + "&lat=" + URLEncoder.encode(String.valueOf(this.latDestination), "utf-8") + "&long=" + URLEncoder.encode(String.valueOf(this.lonDestination), "utf-8") + "&notify=" + URLEncoder.encode(String.valueOf(this.notificationReq), "utf-8");

        Log.i(TAG, "Json obj to be processed is in registered contacts " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.getnearbyFriends(requestString, fragment);
    }

    /***
     * API Callbacks
     */
    @SuppressLint("LongLogTag")
    public void nearbyContactsResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Log.i(TAG, "nearbyContactsResponse" + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            return;
        }

        JSONObject jObject = new JSONObject(model.responseData.toString());

        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        if (map.containsKey("result")) {
            if ((map.get("result").equals("Fail"))) {
                //handle api fail case here
                return;
            }
        }
        JSONArray jsonArray = null;
        if(jObject.has("data")) {
            jsonArray = jObject.getJSONArray("data");

            ArrayList arrayList = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(jsonArray.getJSONObject(i));
                Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
            }
            initializeGridView(arrayList);
            getFragmentManager().beginTransaction().show(this).commit();

        }
        else {
//            Fragment f = getFragmentManager().findFragmentByTag("NearbyFriendsFragment");
//            if(f != null) {
                try {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("NearbyFriendsFragment")).commit();
                }
                catch (Exception e){}
//            }
        }
    }

    void initializeGridView(ArrayList<JSONObject> list) {
        gridView = (GridView) getView().findViewById(R.id.gridView_nearby_friends);
        gridView.setAdapter(new ContactsGridAdapter(getActivity(), list));
    }
}
