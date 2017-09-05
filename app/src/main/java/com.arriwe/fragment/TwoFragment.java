package com.arriwe.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arriwe.Model.FavPlaceModel;
import com.arriwe.Model.FavPlaceModelTemp;
import com.arriwe.adapter.FavouriteAdapter;
import com.arriwe.adapter.FavouriteAdapterTemp;
import com.arriwe.adapter.PlaceArrayAdapter;
import com.arriwe.database.DBUtility;
import com.arriwe.database.FrequentlyVisitedLoc;
import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Nine;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sancsvision.arriwe.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {
	
	String TAG = "TwoFragment.java";
	Context context;
	ProgressDialog dialog = null;
	ListView favPlaceLV, listViewtemp;
	Toast toast;
	ListView mylistview;
	FavouriteAdapter myAdapter;
	Location lastKnownLocation;
	MyReceiver myReceiver = null;
	AutoCompleteTextView textView = null;
	PlaceArrayAdapter autoCompleteAdapter;
	MapFragment mapFragment;
	Place selectedPlace = null;
	TextView fav, fav_not_found;
	private Intent inten;
	private UpdateLocation updateLocation;
	private Boolean isscreenvisible;
	
	public TwoFragment() {
		// Required empty public constructor
		context = getActivity();
       /* if (context != null) {
            // Code goes here.
            myAdapter = new FavouriteAdapter(context,titles,details, R.layout.row);
            mylistview.setAdapter(myAdapter);
        }*/
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_two, container, false);
		context = getActivity();
		
		textView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
		favPlaceLV = (ListView) view.findViewById(R.id.listView2);
		listViewtemp = (ListView) view.findViewById(R.id.listViewtemp);
		fav = (TextView) view.findViewById(R.id.your_fav);
		fav_not_found = (TextView) view.findViewById(R.id.no_fav_found);
		textView.setThreshold(1);
		textView.setDropDownVerticalOffset(15);

        /*mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_layout_fragment);
        mapFragment.getView().setVisibility(View.INVISIBLE);*/
        /*mylistview = (ListView) view.findViewById(R.id.list);
        view.findViewById(R.id.txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  getActivity().startActivity(new Intent(getActivity(), Nine.class));
            }
        });
        myAdapter = new FavouriteAdapter(getActivity(),titles,details,R.layout.fav_rowview);
        mylistview.setAdapter(myAdapter);*/
		
		stopmyservice();
		return view;
	}
	
	
	void initGrid() {
		
		DBUtility utility = new DBUtility(context);
		ArrayList<FrequentlyVisitedLoc> places = utility.getFrequentlyVisitedPlaces();
		ArrayList<FavPlaceModel> model = new ArrayList<FavPlaceModel>();
		
		for (FrequentlyVisitedLoc obj : places) {
			Log.i(TAG, "Location saved as fav is " + obj.place_name);
			LatLng latLng = new LatLng(obj.latitude, obj.longitude);
			FavPlaceModel m = new FavPlaceModel(obj.place_name, latLng, obj.address, obj.user_saved_name);
			model.add(m);
		}
		
		for (int i = 0; i < model.size(); i++) {
			Log.d("fragment", model.get(i).userSavedName);
		}
		final FavouriteAdapter adapter = new FavouriteAdapter(context, R.layout.row, model);
		favPlaceLV.setAdapter(adapter);
		favPlaceLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				favLocationBtnClicked(position, adapter.getItem(position));
			}
		});
		
		favPlaceLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				
				final AlertDialog alertDialog;
				//  LayoutInflater inflater = WhereRUGoing.this.getLayoutInflater();
//                View layout=inflater.inflate(R.layout.alert_input,null);
				final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
				alertDialogBuilder.setTitle(getResources().getString(R.string.delete_fav_confirmation));
//                alertDialogBuilder.setView(layout);
				alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(WhereRUGoing.this, "You clicked yes button", Toast.LENGTH_LONG).show();
						deleteAFavLoc(adapter.getItem(position));
					}
				});
				
				alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						favPlaceLV.clearFocus();
						favPlaceLV.clearChoices();
						favPlaceLV.requestLayout();
					}
				});
				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return true;
			}
		});
		if (model.size() == 0) {
			//  Toast.makeText(context, "hhhd", Toast.LENGTH_SHORT).show();
			// noFavFrameLayout.setAlpha(1);
			fav.setVisibility(View.INVISIBLE);
			fav_not_found.setVisibility(View.VISIBLE);
			return;
		}
		//noFavFrameLayout.setAlpha(0);
		
	}
	
	void deleteAFavLoc(FavPlaceModel model) {
		DBUtility utility = new DBUtility(context);
		utility.deleteALoc(model);
		initGrid();
	}
	
	public void favLocationBtnClicked(int position, FavPlaceModel modelObj) {
		double cat_lat = 0.0;
		double cat_lng = 0.0;
		if (lastKnownLocation != null) {
			cat_lat = lastKnownLocation.getLatitude();
			cat_lng = lastKnownLocation.getLongitude();
		}
		
		hideKeyboard();
		Log.i(TAG, "Clicked position is " + modelObj.address);
		//case for adding new location
		Intent i = new Intent(getActivity(), Nine.class);
		i.putExtra((getResources().getString(R.string.key_freq_visit_place)), modelObj.placeName);
		i.putExtra((getResources().getString(R.string.key_freq_visit_lat)), modelObj.latLng.latitude);
		i.putExtra((getResources().getString(R.string.key_freq_visit_long)), modelObj.latLng.longitude);
		i.putExtra((getResources().getString(R.string.key_freq_visit_address)), modelObj.address);
		i.putExtra((getResources().getString(R.string.key_curr_lat)), cat_lat);
		i.putExtra((getResources().getString(R.string.key_curr_long)), cat_lng);
		i.putExtra((getResources().getString(R.string.key_tagged_title)), modelObj.placeName);
		i.putExtra("from_shared_loc", false);
		
		
		Bundle b = new Bundle();
		b.putDouble("lat", modelObj.latLng.latitude);//latitude
		b.putDouble("long", modelObj.latLng.longitude);//longitude
		b.putString("place", modelObj.placeName);
		b.putString("address", modelObj.address);
		b.putBoolean("fav_status", true);
		b.putString("saved_name", modelObj.userSavedName);
		i.putExtras(b);
		startActivity(i);
	}
	
	public void favLocationBtnClickedTemp(int position, FavPlaceModelTemp modelObj) {
		double cat_lat = 0.0;
		double cat_lng = 0.0;
		if (lastKnownLocation != null) {
			cat_lat = lastKnownLocation.getLatitude();
			cat_lng = lastKnownLocation.getLongitude();
		}
		
		hideKeyboard();
		Log.i(TAG, "Clicked position is " + modelObj.address);
		//case for adding new location
		Intent i = new Intent(getActivity(), Nine.class);
		i.putExtra((getResources().getString(R.string.key_freq_visit_place)), modelObj.placeName);
		i.putExtra((getResources().getString(R.string.key_freq_visit_lat)), modelObj.latLng.latitude);
		i.putExtra((getResources().getString(R.string.key_freq_visit_long)), modelObj.latLng.longitude);
		i.putExtra((getResources().getString(R.string.key_freq_visit_address)), modelObj.address);
		i.putExtra((getResources().getString(R.string.key_curr_lat)), cat_lat);
		i.putExtra((getResources().getString(R.string.key_curr_long)), cat_lng);
		i.putExtra((getResources().getString(R.string.key_tagged_title)), modelObj.placeName);
		i.putExtra("from_shared_loc", false);
		
		
		Bundle b = new Bundle();
		b.putDouble("lat", modelObj.latLng.latitude);//latitude
		b.putDouble("long", modelObj.latLng.longitude);//longitude
		b.putString("place", modelObj.placeName);
		b.putString("address", modelObj.address);
		b.putBoolean("fav_status", true);
		b.putString("saved_name", modelObj.userSavedName);
		i.putExtras(b);
		startActivity(i);
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isscreenvisible = isVisibleToUser;
		
	}
	
	
	private class MyReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context arg0, final Intent arg1) {
			// TODO Auto-generated method stub
			try {
				if (isscreenvisible) {
					inten = arg1;
					if (getActivity() != null) {
						if (Utils.isNetworkConnected(getActivity())) {
							updateLocation = new UpdateLocation();
							updateLocation.execute();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

            /*try {
                Thread t = new Thread(new Runnable() {
                    public void run() {

                        Location latestLoc = arg1.getParcelableExtra(getString(R.string.key_loc_changed));
                        lastKnownLocation = latestLoc;
                        if(lastKnownLocation!=null) {
                            LatLng l = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            LatLngBounds bounds = LatLngBounds.builder().include(l).build();
                            autoCompleteAdapter.setGoogleApiClient(LocationService.googleApiClient, bounds);
                            Log.i(TAG, "Google Places API connected. " + lastKnownLocation);
                            getActivity().stopService(new Intent(getContext(), LocationService.class));
                            getContext().unregisterReceiver(myReceiver);
                        }
                        else{
                            Toast.makeText(context,getString(R.string.str_unable_to_locate),Toast.LENGTH_LONG).show();
                        }
                    }
                });
                t.start();
            }
            catch (Exception e){
                Log.e(TAG,e.toString());
            }*/
			
			
		}
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getActivity().startService(new Intent(getContext(), LocationService.class));
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LocationService.LATEST_LOC);
		getContext().registerReceiver(myReceiver, intentFilter);
		
		initAdapter();
		initGrid();
		initGridTemp();
		
		
		// autoOnGPS();
	}
	
	private void initGridTemp() {
		DBUtility utility = new DBUtility(context);
		ArrayList<FrequentlyVisitedLoc> places = utility.getTempFrequentlyVisitedPlaces();
		ArrayList<FavPlaceModelTemp> model = new ArrayList<>();
		
		for (FrequentlyVisitedLoc obj : places) {
			Log.i(TAG, "Location saved as fav is " + obj.temp_place_name);
			LatLng latLng = new LatLng(obj.temp_latitude, obj.temp_longitude);
			FavPlaceModelTemp m = new FavPlaceModelTemp(obj.temp_place_name, latLng, obj.temp_address, obj.temp_user_saved_name);
			model.add(m);
		}
		
		for (int i = 0; i < model.size(); i++) {
			Log.d("fragment", model.get(i).userSavedName);
		}
		final FavouriteAdapterTemp adapter = new FavouriteAdapterTemp(context, R.layout.row, model);
		listViewtemp.setAdapter(adapter);
		listViewtemp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				favLocationBtnClickedTemp(position, adapter.getItem(position));
			}
		});
		
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().stopService(new Intent(getContext(), LocationService.class));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	void initAdapter() {
		autoCompleteAdapter = new PlaceArrayAdapter(getActivity(), R.layout.autocomplete_cell, null);
		textView.setAdapter(autoCompleteAdapter);
       /* LatLng l = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        LatLngBounds bounds = LatLngBounds.builder().include(l).build();
        autoCompleteAdapter.setGoogleApiClient(LocationService.googleApiClient, bounds);
       */ //textView.setDropDownBackgroundDrawable(getResources().getDrawable(R.color.gray));
		textView.setOnItemClickListener(mAutocompleteClickListener);
		textView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					selectedPlace = null;
					
					fav_not_found.setVisibility(View.VISIBLE);
					
					
					//  listViewtemp.setVisibility(View.VISIBLE);
				} else {
					fav_not_found.setVisibility(View.GONE);
	          /*  myReceiver = new MyReceiver();
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(LocationService.LATEST_LOC);
                    getContext().registerReceiver(myReceiver, intentFilter);
                    getActivity().startService(new Intent(getContext(), LocationService.class));*/
					
					// listViewtemp.setVisibility(View.GONE);
				}

                /*if(s.length() == 0) {
                    selectedPlace = null;
//                    reqLocationLayout.setVisibility(View.VISIBLE);
                }
                else {
                    favPlaceLV.setAlpha(0);
                    yourFavPlaceHeading.setAlpha(0);
                    noFavPlaceTV.setAlpha(0);
                    bottomLayout.setAlpha(0);
//                    reqLocationLayout.setVisibility(View.INVISIBLE);
                }*/
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	private void stopmyservice() {
		
		
	}
	
	private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			
			dialog = Utils.showProgressDialog(context, getString(R.string.str_fetching_loc_details));
			final PlaceArrayAdapter.PlaceAutocomplete item = autoCompleteAdapter.getItem(position);
			final String placeId = String.valueOf(item.placeId);
			String attributedAddress = "<h4>" + item.address + "</h4>";
//            textView.setText(item.name+"\n"+ Html.fromHtml(attributedAddress));
			textView.setText(Html.fromHtml("<medium><font color='#0070C0'>" + item.name + "</font></medium>"));//<br><small><font color='#595959'>" + item.address + "</small></font>"));
			Log.i(TAG, "Selected: " + item.name);
			PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
			    .getPlaceById(LocationService.googleApiClient, placeId);
			placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
			Log.i(TAG, "Fetching details for ID: " + item.placeId);
//            reqLocationLayout.setVisibility(View.INVISIBLE);
//            favPlaceLV.setAlpha(1);
//            yourFavPlaceHeading.setAlpha(1);
			//noFavPlaceTV.setAlpha(1);
			textView.setSelection(0);
			
			
			//addToFav.setVisibility(View.VISIBLE);
			//   nextLayout.setVisibility(View.VISIBLE);
			//reqALoc.setVisibility(View.INVISIBLE);
			hideKeyboard();
		}
	};
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
	}
	
	private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
		@Override
		public void onResult(PlaceBuffer places) {
			if (dialog != null) {
				dialog.dismiss();
			}
			if (!places.getStatus().isSuccess()) {
				Log.e(TAG, "Place query did not complete. Error: " +
				    places.getStatus().toString());
//                Toast.makeText(WhereRUGoing.this, Constants.ERROR_GETTING_PLACE,Toast.LENGTH_SHORT).show();
				showAToast(Constants.ERROR_GETTING_PLACE.toString());
				return;
			}
			// Selecting the first object buffer.
			selectedPlace = places.get(0);
			
			LatLng latLng = selectedPlace.getLatLng();
			
			//save temp database
			FrequentlyVisitedLoc loc = new FrequentlyVisitedLoc();
			loc.temp_user_saved_name = "";
			loc.temp_place_name = "";
			loc.temp_latitude = latLng.latitude;
			loc.temp_longitude = latLng.longitude;
			loc.temp_address = selectedPlace.getAddress().toString();
			DBUtility utility = new DBUtility(getActivity());
			Boolean res = utility.insertTempFavLocation(loc);
			
			//latLng.latitude;
			Intent intent = new Intent(getContext(), Nine.class);
			intent.putExtra((getResources().getString(R.string.key_freq_visit_place)), selectedPlace.getName().toString());
			intent.putExtra((getResources().getString(R.string.key_freq_visit_lat)), latLng.latitude);
			intent.putExtra((getResources().getString(R.string.key_freq_visit_long)), latLng.longitude);
			intent.putExtra((getResources().getString(R.string.key_freq_visit_address)), selectedPlace.getAddress().toString());
			intent.putExtra((getResources().getString(R.string.key_curr_lat)), lastKnownLocation.getLatitude());
			intent.putExtra((getResources().getString(R.string.key_curr_long)), lastKnownLocation.getLongitude());
			intent.putExtra((getResources().getString(R.string.key_tagged_title)), selectedPlace.getName().toString());
			intent.putExtra("from_shared_loc", false);
			Bundle b = new Bundle();
			b.putDouble("lat", latLng.latitude);//latitude
			b.putDouble("long", latLng.longitude);//longitude
			b.putString("place", selectedPlace.getName().toString());
			b.putString("address", selectedPlace.getAddress().toString());
			intent.putExtras(b);
			startActivity(intent);
			// getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
			//   initMap();
		}
	};
    /*void initMap() {
        mapFragment.getView().setVisibility(View.VISIBLE);
        mapFragment.getMapAsync(context);
    }*/
	//@Override
    /*public void onMapReady(GoogleMap googleMap) {
//        String placeName = getIntent().getStringExtra((getResources().getString(R.string.key_freq_visit_place)));
//        Double lat = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_lat)), 0.0);
//        Double longitude = getIntent().getDoubleExtra((getResources().getString(R.string.key_freq_visit_long)), 0.0);
//
//        final LatLng latLng = new LatLng(lat, longitude);
//        Log.i(TAG, "Lat long for annotatiing is " + lat + "," + longitude);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(selectedPlace.getLatLng()).title(selectedPlace.getName().toString()));
        final CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(selectedPlace.getLatLng(), 15);
        googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onCancel() {

            }
        });
    }*/
	
	public void showAToast(String st) { //"Toast toast" is declared in the class
		try {
			toast.getView().isShown();     // true if visible
			toast.setText(st);
		} catch (Exception e) {         // invisible if exception
			toast = Toast.makeText(context, st, Toast.LENGTH_LONG);
		}
		toast.show();  //finally display it
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//initGrid();
	}
	
	private class UpdateLocation extends AsyncTask<Void, Void, Void> {
		String result;
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// result = Utility.executeHttpGet(params[0]);
				Location latestLoc = inten.getParcelableExtra(getString(R.string.key_loc_changed));
				lastKnownLocation = latestLoc;
				if (lastKnownLocation != null) {
					LatLng l = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
					LatLngBounds bounds = LatLngBounds.builder().include(l).build();
					autoCompleteAdapter.setGoogleApiClient(LocationService.googleApiClient, bounds);
					Log.i(TAG, "Google Places API connected. " + lastKnownLocation);
					result = "connected";
				} else {
					result = getString(R.string.str_unable_to_locate);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			updateLocation.cancel(true);
		}
	}
}
