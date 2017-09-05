package com.arriwe;

/**
 * Created by Saurabhk on 20/04/2017.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by WebcodeplusD on 9/7/2016.
 */
public class ServiceHandler {

    public ServiceHandler(){}

    public final static int GET = 1;
    public final static int POST = 2;

    String result, line;
    InputStream inputStream;

    int timeoutConnection = 20000;
    int timeoutSocket = 21000;

    @SuppressWarnings("deprecation")
    public String ServiceHandler(String url, int method, String jsonString){
        try {
            HttpClient httpClient;
            HttpGet httpGet;
            HttpPost httpPost;
            HttpResponse httpResponse;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters,timeoutSocket);

            if (method == GET){
                httpGet = new HttpGet(url);
                httpClient = new DefaultHttpClient(httpParameters);
                httpResponse = httpClient.execute(httpGet);
                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null){
                    result = ConverStreamToString(inputStream);
                }else {
                    result = "Something went wrong...";
                }
            }else if (method == POST){
                httpPost = new HttpPost(url);
                httpClient = new DefaultHttpClient(httpParameters);
                StringEntity se = new StringEntity(jsonString);
                httpPost.setEntity(se);
                httpPost.setHeader(HTTP.CONTENT_TYPE,"charset=utf-8");
                httpResponse = httpClient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null){
                    result = ConverStreamToString(inputStream);
                }else{
                    result = "Something went wrong...";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String ConverStreamToString(InputStream inputStream) throws IOException {
        String result = "", lint = "";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null){
            result += line;
        }
        inputStream.close();
        return result;
    }
}
