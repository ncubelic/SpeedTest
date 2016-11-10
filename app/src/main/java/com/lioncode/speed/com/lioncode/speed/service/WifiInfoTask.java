package com.lioncode.speed.com.lioncode.speed.service;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import com.lioncode.speed.MainActivity;
import com.lioncode.speed.R;
import com.lioncode.speed.com.lioncode.speed.model.WifiValues;

import java.util.List;

public class WifiInfoTask extends AsyncTask<Void, Void, WifiValues> {

    private Activity activity;
    private final Context _context;

    private TextView nameLabel;
    private TextView modeLabel;
    private TextView strengthLabel;
    private TextView channelLabel;
    private TextView encryptionLabel;
    private TextView frequencyLabel;

    public WifiInfoTask(Context context){
        _context = context;
        activity = (Activity) context;
    }

    @Override
    protected void onPreExecute() {
        nameLabel = (TextView) activity.findViewById(R.id.wifi_name);
        modeLabel = (TextView) activity.findViewById(R.id.wifi_mode);
        strengthLabel = (TextView) activity.findViewById(R.id.wifi_strength);
        channelLabel = (TextView) activity.findViewById(R.id.wifi_channel);
        encryptionLabel = (TextView) activity.findViewById(R.id.wifi_encryption);
        frequencyLabel = (TextView) activity.findViewById(R.id.wifi_freq);
    };


    @Override
    protected WifiValues doInBackground(Void... voids) {

        WifiManager mainWifiObj;
        mainWifiObj = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConf = new WifiConfiguration();

        if(mainWifiObj.isWifiEnabled()){
            WifiInfo wifiInfo = mainWifiObj.getConnectionInfo();

            // Connected to the wifi
            if(wifiInfo.getNetworkId() != -1){
                List<ScanResult> networkList = mainWifiObj.getScanResults();

                WifiValues wifiValues = new WifiValues();

                wifiValues.setSsid(wifiInfo.getSSID());
                wifiValues.setStrength(""+wifiInfo.getRssi());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    wifiValues.setFrequency(getFreqBandFromFreqUnit(wifiInfo.getFrequency()));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    wifiValues.setChannel(""+getChannelFromFreqUnit(wifiInfo.getFrequency()));
                }

                wifiValues.setMode(wifiInfo.getLinkSpeed()+" "+wifiInfo.LINK_SPEED_UNITS+" / "+getWifiStandard(wifiInfo));

                if (networkList != null) {
                    for (ScanResult network : networkList)
                    {
                        //check if current connected SSID
                        if (wifiInfo.getSSID().trim().equals("\""+network.SSID+"\"")){
                            String capabilities =  network.capabilities;
                            String enc = "";


                            if (capabilities.contains("WPA2-PSK")) {
                                enc = "WPA2-PSK";
                            }
                            else if (capabilities.contains("WPA-PSK")) {
                                enc = "WPA-PSK";
                            }
                            else if (capabilities.contains("WPA2")) {
                                enc = "WPA2";
                            }
                            else if (capabilities.contains("WPA")) {
                                enc = "WPA";
                            }
                            else if (capabilities.contains("WEP")) {
                                enc = "WEP";
                            }
                            wifiValues.setEncryption(enc);
                        }
                    }
                }
                return wifiValues;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(WifiValues wifiValues) {
        super.onPostExecute(wifiValues);

        MainActivity mainAct = (MainActivity) _context;

        if(wifiValues!=null){
            mainAct.setConnected(true);
            nameLabel.setText(wifiValues.getSsid());
            modeLabel.setText(wifiValues.getMode());
            strengthLabel.setText(wifiValues.getStrength());
            channelLabel.setText(wifiValues.getChannel());
            encryptionLabel.setText(wifiValues.getEncryption());
            frequencyLabel.setText(wifiValues.getFrequency());
        }
        else {
            mainAct.setConnected(false);
            nameLabel.setText("No connection");
        }

    }

    private String getWifiStandard(android.net.wifi.WifiInfo wifiInfo) {
        if(wifiInfo.getLinkSpeed()<=11)return "802.b";
        else if(wifiInfo.getLinkSpeed()<=54)return "802.g";
        else if(wifiInfo.getLinkSpeed()<=300)return "802.11n";
        else if(wifiInfo.getLinkSpeed()<=866.5)return "802.11ac";

        return "";
    }

    private int getChannelFromFreqUnit(int freq){
        switch(freq){
            case 2412:return 1;
            case 2417:return 2;
            case 2422:return 3;
            case 2427:return 4;
            case 2432:return 5;
            case 2437:return 6;
            case 2442:return 7;
            case 2447:return 8;
            case 2452:return 9;
            case 2457:return 10;
            case 2462:return 11;
            case 2467:return 12;
            case 2472:return 13;
            case 2484:return 14;
            default:return 0;
        }
    }

    private String getFreqBandFromFreqUnit(int freq){
        if(freq>2400 && freq<2500)return "2,4 Ghz";
        else if(freq>=4900 && freq<5000)return "4,9 Ghz";
        else return "unknown";
    }

}
