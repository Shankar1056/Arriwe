package com.arriwe.networkmanagers;

import android.content.Context;

/**
 * Created by Abhi1 on 12/08/15.
 */

public class NetworkDataModel{

    public String apiUrl;
    public String httpMethodType;
    public Object authParams;
    public Object responseData;
    public Object requestData;
    public Boolean responseFailed;
    public String error;
    public Context currentContext;


    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public Object getAuthParams() {
        return authParams;
    }

    public void setAuthParams(Object authParams) {
        this.authParams = authParams;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

    public Boolean getResponseFailed() {
        return responseFailed;
    }

    public void setResponseFailed(Boolean responseFailed) {
        this.responseFailed = responseFailed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHttpMethodType() {
        return httpMethodType;
    }

    public void setHttpMethodType(String httpMethodType) {
        this.httpMethodType = httpMethodType;
    }

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
