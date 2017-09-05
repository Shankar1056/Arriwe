package com.arriwe.networkmanagers;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import com.arriwe.utility.Constants;
import com.arriwe.utility.LocationService;
import com.arriwe.utility.Utils;
import com.arriwe.wayndr.Eight;
import com.arriwe.wayndr.IndividualTravelDetail;
import com.arriwe.wayndr.LoginActivity;
import com.arriwe.wayndr.MainActivity;
import com.arriwe.wayndr.NearByFriendsFragment;
import com.arriwe.wayndr.Nine;
import com.arriwe.wayndr.Settings;
import com.arriwe.wayndr.Seven;
import com.arriwe.wayndr.Six;
import com.arriwe.wayndr.util.ShareLocation;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by Abhi1 on 12/08/15.
 */


public class NetworkEngine {
    public static final String TAG = "NetworkEngine.java";

    public void signUp(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Register;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
//        manager.callbackObj = new NetworkActivityInterface() {
//            @Override
//            public void onSignUpResponse(NetworkDataModel response) throws JSONException {
//                Log.d(TAG,"onSignUpResponse called");
//                SetUpProfile obj= (SetUpProfile)response.currentContext;
//                obj.signUpResponse(response);
//            }
//        };
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onSignUpResponse(NetworkDataModel response) throws JSONException {
                super.onSignUpResponse(response);
                Log.d(TAG, "onSignUpResponse called");
                if (!Constants.islogin)
                {
                    Six obj = (Six) response.currentContext;
                    obj.signUpResponse(response);
                }
                else
                {
                    LoginActivity obj = (LoginActivity) response.currentContext;
                    obj.signUpResponse(response);
                }


            }
        };
    }


    public void verifyNo(String jsonString, Context context,String url) {
        NetworkDataModel model = new NetworkDataModel();


            model.apiUrl = url;


        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onOTPVerificationResponse(NetworkDataModel response) throws JSONException {
                super.onOTPVerificationResponse(response);
                Log.e(TAG, "onVerifyNoResponse called");
                Seven obj = (Seven) response.currentContext;
                obj.otpResponse(response);
            }
        };
    }

    public void getUsersUsingWayndr(String jsonString, final Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Wayndr_reg_users;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onRegisteredContactResponse(NetworkDataModel response) throws JSONException, UnsupportedEncodingException {
                super.onRegisteredContactResponse(response);
                Log.d(TAG, "onRegisteredContactResponse called");
                if ((context instanceof Nine)) {
                    if (Utils.isNetworkConnected(context)) {
                        Nine obj = (Nine) response.currentContext;
                        obj.registeredContactsResponse(response);
                    }
                }
//                }else if((context instanceof GroupTravelDetail)){
//                    GroupTravelDetail obj = (GroupTravelDetail) response.currentContext;
//                    obj.registeredContactsResponse(response);
//                }
                else {
                    if (Utils.isNetworkConnected(context)) {
                        Eight obj = (Eight) response.currentContext;
                        obj.registeredConResponse(response);
                    }
                }

            }
        };
    }

    public void getUsersUsingWayndr1(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Wayndr_reg_users;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onRegisteredContactResponse1(NetworkDataModel response) throws JSONException, UnsupportedEncodingException {
                super.onRegisteredContactResponse1(response);
                Log.d(TAG, "onRegisteredContactResponse called");
                MainActivity obj = (MainActivity) response.currentContext;
                obj.registeredContactsResponse(response);

            }
        };
    }

    public void sendTagAndTravelDetails(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Tagged_Travel_Users;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
//        String requestString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//                "<soap:Body>\n" +
//                "<saveTaggedLocation xmlns=\"http://tempuri.org/\">\n" +
//                "<Json>" + jsonString + "</Json>\n" +
//                "</saveTaggedLocation>\n" +
//                "</soap:Body>\n" +
//                "</soap:Envelope>\n";
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onTaggedTravellingWithResponse(NetworkDataModel response) throws JSONException {
                super.onTaggedTravellingWithResponse(response);
                Log.d(TAG, "onTaggedTravellingWithResponse called");
                Nine obj = (Nine) response.currentContext;
                obj.taggedTravellingResponse(response);

            }
        };
    }

    public void getUserActivities(String jsonString, final Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Get_User_Activities;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onUserActivitesResponse(NetworkDataModel response) throws JSONException {
                super.onUserActivitesResponse(response);
                if (Utils.isNetworkConnected(context)) {
                    Eight obj = (Eight) response.currentContext;
                    obj.usersActivitiesResponse(response);
                }

            }
        };
    }


    public void pushUsersLocationToServer(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Update_User_Loc;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
//        String requestString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//                "<soap:Body>\n" +
//                "<updateUserLocation xmlns=\"http://tempuri.org/\">\n" +
//                "<Json>" + jsonString + "</Json>\n" +
//                "</updateUserLocation>\n" +
//                "</soap:Body>\n" +
//                "</soap:Envelope>\n";
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
    }
    public void pushUsersLocationCityToServer(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Update_User_Loc;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
//        String requestString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//                "<soap:Body>\n" +
//                "<updateUserLocation xmlns=\"http://tempuri.org/\">\n" +
//                "<Json>" + jsonString + "</Json>\n" +
//                "</updateUserLocation>\n" +
//                "</soap:Body>\n" +
//                "</soap:Envelope>\n";
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
    }

    public void updateStatus(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Update_Status;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
//        String requestString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//                "<soap:Body>\n" +
//                "<updateStatus xmlns=\"http://tempuri.org/\">\n" +
//                "<Json>" + jsonString + "</Json>\n" +
//                "</updateStatus>\n" +
//                "</soap:Body>\n" +
//                "</soap:Envelope>\n";
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onUpdateStatusResponse(NetworkDataModel response) throws JSONException, UnsupportedEncodingException {
                super.onUpdateStatusResponse(response);
                Eight obj = (Eight) response.currentContext;
                obj.acceptDeclineResponse(response);
            }
        };
    }


    public void pushGCMTokenToServer(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Update_GCM_TOKEN;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
//        String requestString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//                "<soap:Body>\n" +
//                "<updateGoogleAppId xmlns=\"http://tempuri.org/\">\n" +
//                "<Json>" + jsonString + "</Json>\n" +
//                "</updateGoogleAppId\n>\n" +
//                "</soap:Body>\n" +
//                "</soap:Envelope>\n";
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
    }

    public void cancelATrip(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Cancel_Trip;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onCancelTripResponse(NetworkDataModel response) throws JSONException {
                super.onCancelTripResponse(response);
                Eight obj = (Eight) response.currentContext;
                try {
                    obj.cancelTripResponse(response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void markTripAsArriwed(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Arriwe_Trip;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onTripArriwedResponse(NetworkDataModel response) throws JSONException {
                super.onTripArriwedResponse(response);
                LocationService obj = (LocationService) response.currentContext;
                obj.onTripArriwed(response);
            }
        };
    }


    public void clearTrip(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Clear_Trip;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onTripClearedResponse(NetworkDataModel response) throws JSONException {
                super.onTripClearedResponse(response);
                Eight obj = (Eight) response.currentContext;
                try {
                    obj.clearTripResponse(response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void requestForLocation(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Req_Loc;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onLocationRequestedResponse(NetworkDataModel response) throws JSONException {
                super.onLocationRequestedResponse(response);
                Eight obj = (Eight) response.currentContext;
                obj.locationRequestedResponse(response);
            }
        };
    }

    public void shareYourLocation(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Share_Loc;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onLocationSharedResponse(NetworkDataModel response) throws JSONException {
                super.onLocationSharedResponse(response);
                ShareLocation obj = (ShareLocation) response.currentContext;
                try {
                    obj.locationSharedResponse(response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void deleteRecievedLoc(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Reject_Rcv_Loc;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onSharedLocDeletedResponse(NetworkDataModel response) throws JSONException {
                super.onSharedLocDeletedResponse(response);
                Eight obj = (Eight) response.currentContext;
                try {
                    obj.recievedLocRejectedResponse(response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void declineLocationReq(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Reject_Loc_Req;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onLocationRequestRejectedResponse(NetworkDataModel response) throws JSONException {
                super.onLocationRequestRejectedResponse(response);
                Eight obj = (Eight) response.currentContext;
                try {
                    obj.locationRequestRejectedResponse(response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    /*public void deleteAccount(String jsonString,Context context){
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Delete_Acccount;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onUserAccountDelete(NetworkDataModel response) throws JSONException {
                super.onUserAccountDelete(response);
                Settings obj = (Settings) response.currentContext;
                obj.deleteUserResponce(response);

            }
        };
    }*/

    public void updateProfile(String jsonString, Context context) {
        NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Register;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        String requestString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<soap:Body>\n" +
                "<register xmlns=\"http://tempuri.org/\">\n" +
                "<Json>" + jsonString + "</Json>\n" +
                "</register>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>\n";
        model.requestData = requestString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onUpdateProfileResponse(NetworkDataModel response) throws JSONException {
                super.onUpdateProfileResponse(response);

                Settings obj = (Settings) response.currentContext;
                obj.signUpResponse(response);
            }
        };
    }

    public void getnearbyFriends(String jsonString, final Fragment fragment) {
        final NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Get_Nearby_Contacts;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
//        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onNearbyFriendsResponse(NetworkDataModel response) throws JSONException {
                super.onNearbyFriendsResponse(response);
                Log.d(TAG, "onNearbyFriendsResponse called");
                NearByFriendsFragment obj = (NearByFriendsFragment) fragment;
                try {
                    obj.nearbyContactsResponse(model);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }



    public void getUsersLocationsPoint(String jsonString, Context context) {
        final NetworkDataModel model = new NetworkDataModel();
        model.apiUrl = Constants.Api_Get_Track;
        model.httpMethodType = Constants.HTTPMETHOD_POST;
        model.currentContext = context;
        model.requestData = jsonString;
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.execute(model, model);
        manager.callbackObj = new NetworkAbstract() {
            @Override
            public void onUsersJourneyPointsResponse(NetworkDataModel response) throws JSONException {
                super.onUsersJourneyPointsResponse(response);
                Log.d(TAG, "getUsersLocationsPoint called");
                IndividualTravelDetail obj = (IndividualTravelDetail) response.currentContext;
                try {
                    obj.usersLocationPointsResponse(response);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}