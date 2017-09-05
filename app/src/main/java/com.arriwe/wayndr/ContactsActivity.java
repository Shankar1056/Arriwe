package com.arriwe.wayndr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.sancsvision.arriwe.R;
import com.arriwe.adapter.ContactsListAdapter;
import com.arriwe.networkmanagers.NetworkDataModel;
import com.arriwe.networkmanagers.NetworkEngine;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.util.ShareLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

//import com.arriwe.networkmanagers.NetworkDataModel;

public class ContactsActivity extends Activity {

    ProgressDialog dialog = null ;
    private static String TAG = "ContactActivity.java";
    ImageButton backBtn = null;
    EditText searchTextField ;
    ArrayList completeData = null;
    ListView listView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        listView = (ListView) findViewById(R.id.listView3);

        backBtn = (ImageButton) findViewById(R.id.imageButton_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            initContacts();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        searchTextField = (EditText) findViewById(R.id.editText_search_bar);
        searchTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    try {
                        initializeListView(completeData,false,"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        initializeListView(completeData,true,s.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     *
     */

    void initContacts() throws JSONException, UnsupportedEncodingException {
        ArrayList<String> contactNos = Utils.getContactsFromPhone(this);
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

        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, getResources().getString(R.string.err_no_network), Toast.LENGTH_LONG).show();
            return;
        }
        dialog = Utils.showProgressDialog(this, "Loading your contacts");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(getResources().getString(R.string.key_phonenos), contactsJsonArray);
        String requestString = "phoneNos=" + URLEncoder.encode(String.valueOf(phoneNOs), "utf-8");
        Log.i(TAG, "Json obj to be processed is in registered contacts " + requestString);
        NetworkEngine engine = new NetworkEngine();
        //engine.getUsersUsingWayndr(requestString, this);
    }

    public void registeredConResponse(NetworkDataModel model) throws JSONException, UnsupportedEncodingException {
        Log.i(TAG, "registeredContactsResponse" + model.responseData.toString());
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (model.responseFailed) {
            Toast.makeText(this, model.error, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jObject = new JSONObject(model.responseData.toString());

        HashMap<String, Object> map = Utils.jsonToMap(model.responseData.toString());
        if (map.containsKey("result")) {
            if ((map.get("result").equals("Fail"))) {
//                noContactsTextView.setAlpha(1);
                return;
            }
        }
        JSONArray jsonArray = jObject.getJSONArray("data");

        ArrayList arrayList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getJSONObject(i));
            Log.i(TAG, "Matched contacts are " + jsonArray.get(i));
        }
        completeData = arrayList;
        initializeListView(arrayList,false,"");
    }

    void  initializeListView(final ArrayList list,Boolean applyFilter,String filterStr) throws JSONException {
        ArrayList finalList = list;

        if(applyFilter){
            ArrayList filteredList = new ArrayList();
            for(int i=0;i<list.size();i++){
                JSONObject jsonObject = (JSONObject) list.get(i);
                String name = jsonObject.getString("name");
                if(name.toLowerCase().contains(filterStr)){
                    filteredList.add(jsonObject);
                }
            }
            finalList = filteredList;
        }

        listView.setAdapter(new ContactsListAdapter(this,R.layout.list_item_contacts,finalList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showReqDialog(position);
            }
        });
    }

    public void showReqDialog(final int position){
        final AlertDialog alertDialog ;
        LayoutInflater inflater = ContactsActivity.this.getLayoutInflater();
//                View layout=inflater.inflate(R.layout.alert_input,null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactsActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setPositiveButton("Request Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(WhereRUGoing.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                Intent data = new Intent();
                String mobile = null;
                try {
                    JSONObject obj = (JSONObject) listView.getAdapter().getItem(position);
                    mobile = obj.getString("mobile");
                    data.putExtra("mobNO", mobile);
                    setResult(111, data);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Send your location", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(Utils.gpsStatus(ContactsActivity.this) == false){
                    Utils.checkGPSStatus(ContactsActivity.this);
                    return;
                }
//                String reqLocId = obj.getString("request_location_id");
                JSONObject obj = (JSONObject) listView.getAdapter().getItem(position);
                String mobile = null;
                try {
                    mobile = obj.getString("mobile");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(ContactsActivity.this, ShareLocation.class);
                intent.putExtra("receiver_mobile_no", mobile);
                startActivity(intent);

            }});
        alertDialog.show();
    }
}
