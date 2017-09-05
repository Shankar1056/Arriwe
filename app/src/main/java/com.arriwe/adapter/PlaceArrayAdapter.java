package com.arriwe.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sancsvision.arriwe.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Abhi1 on 16/08/15.
 */
public class PlaceArrayAdapter
        extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> implements Filterable {
    private static final String TAG = "PlaceArrayAdapter.java";
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;
    private  int layoutResourceId;
    private Context context;

    /**
     * Constructor
     *
     * @param context  Context
     * @param resource Layout resource
     * @param filter   Used to specify place types
     */
    public PlaceArrayAdapter(Context context, int resource,
                             AutocompleteFilter filter) {
        super(context, resource);
        this.context = context;
        mPlaceFilter = filter;
        this.layoutResourceId = resource;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient,LatLngBounds bounds) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
        mBounds = bounds;
    }

    @Override
    public int getCount() {
        int size =0;
        try {
            size = mResultList.size();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public  PlaceAutocomplete getItem(int position){
        return  mResultList.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.layoutResourceId, parent, false);
        }

        PlaceAutocomplete autocompleteObj = mResultList.get(position);
        TextView textViewItem = (TextView) convertView.findViewById(R.id.textview_autocomplete);
        textViewItem.setText(autocompleteObj.name);

        TextView textViewAddress = (TextView) convertView.findViewById(R.id.text_view_address);
        textViewAddress.setText(autocompleteObj.address);
        View view = convertView.findViewById(R.id.divider);
        RelativeLayout ll_name = (RelativeLayout) convertView.findViewById(R.id.ll_name);
        if(position!=0){
            view.setVisibility(View.GONE);
        }


        return  convertView;
    }

//    @Override
//    public PlaceAutocomplete getItem(int position) {
//        return mResultList.get(position);
//    }

    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint);
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);
            // Wait for predictions, set the timeout.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(10, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
//                Toast.makeText(getContext(), "Timeout error",
//                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting place predictions: " + status
                        .toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getPrimaryText(null),prediction.getFullText(null)));
            }
            // Buffer release
            autocompletePredictions.release();
            return resultList;
        }
        Log.e(TAG, "Google API client is not connected.");
        return null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    mResultList = getPredictions(constraint);
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence name;
        public CharSequence address;

        PlaceAutocomplete(CharSequence placeId, CharSequence name,CharSequence address) {
            this.placeId = placeId;
            this.name = name;
            this.address = address;
        }

        @Override
        public String toString() {
            return name.toString();
        }
    }
}