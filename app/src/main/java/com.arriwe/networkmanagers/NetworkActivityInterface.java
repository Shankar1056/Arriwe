package com.arriwe.networkmanagers;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by Abhi1 on 19/08/15.
 */

abstract class NetworkAbstract implements NetworkActivityInterface {
    @Override
    public void onSignUpResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onOTPVerificationResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onRegisteredContactResponse(NetworkDataModel response) throws JSONException, UnsupportedEncodingException {

    }

    @Override
    public void onRegisteredContactResponse1(NetworkDataModel response) throws JSONException, UnsupportedEncodingException {

    }

    @Override
    public void onTaggedTravellingWithResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onUserActivitesResponse(NetworkDataModel response) throws JSONException {

    }


    @Override
    public void onUpdateStatusResponse(NetworkDataModel response) throws JSONException, UnsupportedEncodingException {

    }

    @Override
    public void onUpdateProfileResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onCancelTripResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onTripArriwedResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onTripClearedResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onLocationRequestedResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onLocationRequestRejectedResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onLocationSharedResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onSharedLocDeletedResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onNearbyFriendsResponse(NetworkDataModel response) throws JSONException {

    }


    @Override
    public void onUsersJourneyPointsResponse(NetworkDataModel response) throws JSONException {

    }

    @Override
    public void onUserAccountDelete(NetworkDataModel response) throws JSONException{

    }

}
public interface  NetworkActivityInterface{

    void onSignUpResponse(NetworkDataModel response) throws JSONException;
    void onOTPVerificationResponse(NetworkDataModel response) throws JSONException;
    void onRegisteredContactResponse(NetworkDataModel response) throws JSONException, UnsupportedEncodingException;
    void onRegisteredContactResponse1(NetworkDataModel response) throws JSONException, UnsupportedEncodingException;
    void onTaggedTravellingWithResponse(NetworkDataModel response) throws JSONException;
    void onUserActivitesResponse(NetworkDataModel response) throws JSONException;
    void onUpdateStatusResponse(NetworkDataModel response) throws JSONException, UnsupportedEncodingException;
    void onUpdateProfileResponse(NetworkDataModel response) throws JSONException;
    void onCancelTripResponse(NetworkDataModel response) throws JSONException;
    void onTripArriwedResponse(NetworkDataModel response) throws JSONException;
    void onTripClearedResponse(NetworkDataModel response) throws JSONException;
    void onLocationRequestedResponse(NetworkDataModel response) throws JSONException;
    void onLocationRequestRejectedResponse(NetworkDataModel response) throws JSONException;
    void onLocationSharedResponse(NetworkDataModel response) throws JSONException;
    void onSharedLocDeletedResponse(NetworkDataModel response) throws JSONException;
    void onNearbyFriendsResponse(NetworkDataModel response) throws JSONException;
    void onUsersJourneyPointsResponse(NetworkDataModel response) throws JSONException;
    void onUserAccountDelete(NetworkDataModel response) throws JSONException;




}