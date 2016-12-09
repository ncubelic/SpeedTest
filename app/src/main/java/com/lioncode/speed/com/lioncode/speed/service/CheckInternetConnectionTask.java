package com.lioncode.speed.com.lioncode.speed.service;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lioncode.speed.MainActivity;
import com.lioncode.speed.R;
import com.lioncode.speed.com.lioncode.speed.model.WifiValues;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CheckInternetConnectionTask extends AsyncTask<Void, Void, Boolean> {

    private Activity activity;

    private RelativeLayout transparentOverlay;

    public CheckInternetConnectionTask(Context context){
        activity = (Activity) context;
    }

    @Override
    protected void onPreExecute() {
        transparentOverlay = (RelativeLayout) activity.findViewById(R.id.transparentOverlay);
    };


    @Override
    protected Boolean doInBackground(Void... voids) {
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        super.onPostExecute(null);
        Log.d("isConnected: ", String.valueOf(isConnected));

        if(isConnected)transparentOverlay.setVisibility(View.INVISIBLE);
        else transparentOverlay.setVisibility(View.VISIBLE);
    }

}
