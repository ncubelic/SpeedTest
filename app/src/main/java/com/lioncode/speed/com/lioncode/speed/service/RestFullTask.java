package com.lioncode.speed.com.lioncode.speed.service;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.lioncode.speed.com.lioncode.speed.AndroidNameUtility;
import com.lioncode.speed.com.lioncode.speed.Constants;
import com.lioncode.speed.com.lioncode.speed.model.WifiValues;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class RestFullTask extends AsyncTask<WifiValues, Void, Void> {

    @Override
    protected void onPreExecute() {
    };


    @Override
    protected Void doInBackground(WifiValues... wifiValues) {

        WifiValues values = wifiValues[0];

        String ip = getIpAddress();

        String URL ="/service/full";

        String androidOS = Build.VERSION.RELEASE;
        int androidAPI = Build.VERSION.SDK_INT;

        RequestParams params = new RequestParams();
        params.put("android", AndroidNameUtility.getDeviceName());
        params.put("version", androidOS);
        params.put("apiLevel", androidAPI);
        params.put("ssid", String.valueOf(values.getSsid()));
        params.put("testType", String.valueOf(values.getTestType()));
        params.put("uploadSpeed", String.valueOf(values.getUploadSpeed()));
        params.put("downloadSpeed", String.valueOf(values.getDownloadSpeed()));
        params.put("ip", ip);
        params.put("ping",values.getPing());
        params.put("server", Constants.SPEED_TEST_SERVER_HOST);

        RestClient.client.setBasicAuth("dora","w8^x#n6ae#MmPXjz38b8!9Y4k*Nq#_");
        RestClient.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Success: ", String.valueOf(statusCode));
                Log.d("responseBody: ",new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Failure: ", String.valueOf(statusCode));
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
    }


    public String getIpAddress() {
        String ip = null;

        URL whatismyip = null;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ip = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("IP: ",ip);
        return ip;
    }
}
