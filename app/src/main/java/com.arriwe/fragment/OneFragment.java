package com.arriwe.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.Model.Contact;
import com.sancsvision.arriwe.R;
import com.arriwe.adapter.SearchContactAdapter;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Constants;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Eight;
import com.arriwe.wayndr.util.ShareLocation;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.arriwe.utility.StaticContacts.contactArrayList;


public class  OneFragment extends Fragment {



    public static int position_list = 0;
  //  public static ArrayList<JSONObject> onefrement_list = new ArrayList<>();
    private static String TAG = "OneFragment.java";
    public List<Contact> arrayList = new ArrayList<>();
    ProgressDialog dialog = null;
    ListView listView;
    SearchContactAdapter searchContactAdapter;
    EditText username_input;
    SwipeRefreshLayout contanct_list_swipe;
    TextView textView13;
    String myNo = "";
    SharedPreferences prefs;

    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        listView = (ListView) view.findViewById(R.id.list_search_contacts);
        username_input = (EditText) view.findViewById(R.id.username_input);
        textView13 = (TextView)view.findViewById(R.id.textView13);
        contanct_list_swipe = (SwipeRefreshLayout) view.findViewById(R.id.contanct_list_swipe);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        myNo = prefs.getString(getContext().getResources().getString(R.string.key_reg_no), "");


        contanct_list_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    contanct_list_swipe.setRefreshing(true);
                    initContacts();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

            }
        });

        username_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (username_input.getText().toString().length() == 0) {
                        JSONArray jsonArray = new JSONArray(prefs.getString(getContext().getResources().getString(R.string.key_contanct_number), ""));
                        arrayList.clear();
                        contactArrayList.clear();
                        //   HashMap<String,String> contactsFromPhoneHashmap = Utils.getContactsFromPhoneHashmap(getContext());

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);
                            if(!jsonObject.getString("mobile").equals(myNo))
                            {
                                Contact contact = null;
                                contact = new Contact(jsonObject.getString("name"), jsonObject.getString("mobile"), jsonObject.getString("photo"), jsonObject.getString("city"));
                                arrayList.add(contact);
                                contactArrayList.add(jsonObject);
                            }
                            Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
                        }
                        initializeListView(arrayList, false, "");
                    } else {
                        String text = username_input.getText().toString().toLowerCase(Locale.getDefault());
                        searchContactAdapter.filter(text);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    //initializeListView(arrayList,false,"");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
     /*   searchContactAdapter = new SearchContactAdapter(getContext(),names,numbers,R.layout.search_rowview);
        listView.setAdapter(searchContactAdapter);*/

        try {
            if (prefs.getString(getResources().getString(R.string.key_contanct_number), "").equals("")) {
                contanct_list_swipe.setRefreshing(true);
                initContacts();
            } else {
                JSONArray jsonArray = new JSONArray(prefs.getString(getContext().getResources().getString(R.string.key_contanct_number), ""));
                arrayList.clear();
                contactArrayList.clear();
             //   HashMap<String,String> contactsFromPhoneHashmap = Utils.getContactsFromPhoneHashmap(getContext());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    //jsonObject.getString("city");
                    if(!jsonObject.getString("mobile").equals(myNo)){
                       // String value=(String)contactsFromPhoneHashmap.get(jsonObject.getString("mobile"));
                        Contact contact = null;
                        /*if(value!=null)
                            contact = new Contact(value, jsonObject.getString("mobile"), jsonObject.getString("photo"), jsonObject.getString("city"));*/
                        //else
                            contact = new Contact(jsonObject.getString("name"), jsonObject.getString("mobile"), jsonObject.getString("photo"), jsonObject.getString("city"));

                      //  Contact contact = new Contact(jsonObject.getString("name"), jsonObject.getString("mobile"), jsonObject.getString("photo"), jsonObject.getString("city"));
                        arrayList.add(contact);
                        contactArrayList.add(jsonObject);
                    }
                    Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
                }
                initializeListView(arrayList, false, "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    void initContacts() throws JSONException, UnsupportedEncodingException {
        ArrayList<String> contactNos = Utils.getContactsFromPhone(getContext());


        callApiToGetRegisteredContacts(contactNos);
    }

    void callApiToGetRegisteredContacts(ArrayList<String> phoneNOs) throws JSONException, UnsupportedEncodingException {
        //for testing
//        ArrayList<String> obj = new ArrayList<String >();
//        obj.add("8951374713");
//        obj.add("9620498754");
//        obj.add("8553902950");
//        obj.add("1234");
//        obj.add("5678");

        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }

        //  if(!contanct_list_swipe.isRefreshing())
        // dialog = Utils.showProgressDialog(getContext(), "Loading your contacts");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(getResources().getString(R.string.key_phonenos), contactsJsonArray);

        String phone_con = "";

        for(int i=0;i<phoneNOs.size();i++){
            if(i==0)
                phone_con+=phoneNOs.get(i);
            else
                phone_con+=","+phoneNOs.get(i);
        }phone_con = phone_con.trim();

        String requestString = "phoneNos=" + URLEncoder.encode(String.valueOf(phone_con), "utf-8");
        Log.i(TAG, "Json obj to be processed is in registered contacts " + requestString);
        /*NetworkEngine engine = new NetworkEngine();
        engine.getUsersUsingWayndr(requestString, getActivity());*/

        getUserUsingWayndr(phone_con);
    }
    private void getUserUsingWayndr(String phone_con){
        Ion.with(getContext())
                .load(Constants.DEV_BASE_URL+Constants.Api_Wayndr_reg_users)
                .setBodyParameter("phoneNos",phone_con)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if(e!=null){
                    Log.i(TAG, "registeredContactsResponse" + e.toString());
                }
                if(result!=null){
                    try {
                        registeredConResponsel(result);
                    }
                    catch (Exception e1){
                        //textView13.setAlpha(1);
                        Log.e(TAG,e1.toString());
                    }

                }
            }
        });
    }

    public void registeredConResponsel(String model) throws JSONException, UnsupportedEncodingException {
        //    Toast.makeText(getActivity(), "calll dummy toast", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "registeredContactsResponse" + model);
      /*  if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }*/

        //  contanct_list_swipe.setRefreshing(false);

        if (contanct_list_swipe!=null)
            contanct_list_swipe.setRefreshing(false);
       /* if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT).show();
            return;
        }*/

        JSONObject jObject = new JSONObject(model);

        HashMap<String, Object> map = Utils.jsonToMap(model);
        if (map.containsKey("result")) {
            if ((map.get("result").equals("Fail"))) {
//                noContactsTextView.setAlpha(1);
                return;
            }
        }

        arrayList.clear();
        contactArrayList.clear();
        if(jObject.has("data")){

            JSONArray jsonArray = jObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(!jsonObject.getString("mobile").equals(myNo)){
                    Contact contact = new Contact(jsonObject.getString("name"), jsonObject.getString("mobile"), jsonObject.getString("photo"), jsonObject.getString("city"));
                    arrayList.add(contact);
                    contactArrayList.add(jsonObject);
                }
                
                Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.key_contanct_number), jsonArray.toString());

            editor.apply();
            initializeListView(arrayList, false, "");
        }
        else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.key_contanct_number), "");

            editor.apply();
            initializeListView(arrayList, false, "");
        }

    }

    void initializeListView(final List<Contact> list, Boolean applyFilter, String filterStr) throws JSONException {
        try {
            if(list.size()==0)
                textView13.setAlpha(1);
            else
                textView13.setAlpha(0);
            //here i did sorting
            Collections.sort(arrayList, new Comparator<Contact>() {
                public int compare(Contact v1, Contact v2) {

                    return v1.getContactName().toLowerCase().compareToIgnoreCase(v2.getContactName().toLowerCase());
                }
            });
            searchContactAdapter = new SearchContactAdapter(getActivity(), list, R.layout.search_rowview);
            listView.setAdapter(searchContactAdapter);
            if (position_list != 0) {
                listView.setSelection(position_list);
                listView.smoothScrollToPosition(position_list);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void requestLocation(final View view) {
        final AlertDialog alertDialog;
        final int pos = listView.getPositionForView((View) view.getParent());
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        String name = arrayList.get(pos).getContactName();
        String nameFinal = name.substring(0, 1).toUpperCase() + name.substring(1);

        alertDialogBuilder.setTitle("Request location from " + "'" + nameFinal + "' ?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    callApiToRequestLocation(arrayList.get(pos).getContactNumber());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void sendYourLocation(View view) {
        int pos = listView.getPositionForView((View) view.getParent());

        position_list = pos;
        Intent intent = new Intent(getContext(), ShareLocation.class);
        intent.putExtra("reciever_mobile_no", arrayList.get(pos).getContactNumber());
        intent.putExtra("position", pos);
        startActivity(intent);

        //Log.e(TAG,e.toString());
    }

    void callApiToRequestLocation(String toPhoneNo) throws UnsupportedEncodingException {
        if (!Utils.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(getContext(), "");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String mobileNo = prefs.getString(getString(R.string.key_reg_no), "");

        String requestString = "mobileNo=" + URLEncoder.encode(mobileNo, "utf-8") + "&tomobileNo=" + URLEncoder.encode(String.valueOf(toPhoneNo), "utf-8");

        Log.i(TAG, "Sending location request callApiToRequestLocation " + requestString);
        NetworkEngine engine = new NetworkEngine();
        engine.requestForLocation(requestString, getContext());

    }



    public void locationRequestedResponse( com.arriwe.networkmanagers.NetworkDataModel  model) throws JSONException {
//       Utils.logLargeString("usersActivitiesResponse" + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(getContext(), model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (model.responseData == null) {
            Toast.makeText(getContext(), getString(R.string.str_no_activities), Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jObject = new JSONObject(model.responseData.toString());
        Log.d("jsonobject", jObject + "");
       /* if (jObject.getString("result").equals("Failure")) {
            Toast.makeText(getContext(), jObject.getString("msg"), Toast.LENGTH_SHORT).show();
            return;
        } else{*/
            Toast.makeText(getContext(), "Location Request Sent", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), Eight.class);
            intent.putExtra("setfragment", "three");
            startActivity(intent);
            getActivity().finish();
        //}

        //Toast.makeText(getContext(), jObject.getString("msg"), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        //  searchContactAdapter.notifyDataSetChanged();
    }
}
