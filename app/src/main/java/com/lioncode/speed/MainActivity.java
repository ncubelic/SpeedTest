package com.lioncode.speed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lioncode.speed.com.lioncode.speed.Constants;
import com.lioncode.speed.com.lioncode.speed.service.CheckInternetConnectionTask;
import com.lioncode.speed.com.lioncode.speed.service.CpuUsageTask;
import com.lioncode.speed.com.lioncode.speed.service.PingService;
import com.lioncode.speed.com.lioncode.speed.service.SpeedTestService;
import com.lioncode.speed.com.lioncode.speed.service.WifiInfoTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private Button downloadBtn;
    private Button uploadBtn;
    private Button uploadDownloadBtn;

    private TextView pingLabel;
    private TextView wifiNameLabel;

    private boolean connected;

    private RelativeLayout transparentOverlay;

    //public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //MainActivity.mainActivity = this;

        registerReceiver(wifiReceiver, new IntentFilter("CONNECTION_LOST"));

        setContentView(R.layout.activity_speedtest);

        final Intent intent = new Intent(this, ServerActivity.class);

        transparentOverlay = (RelativeLayout) findViewById(R.id.transparentOverlay);
        transparentOverlay.setVisibility(View.INVISIBLE);

        downloadBtn = (Button) findViewById(R.id.button_dl);
        uploadBtn = (Button) findViewById(R.id.button_ul);
        uploadDownloadBtn = (Button) findViewById(R.id.button_ul_dl);

        pingLabel = (TextView) findViewById(R.id.trip_time);

        wifiNameLabel = (TextView) findViewById(R.id.wifi_name);


        // ACTIONS
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, 2);
            }
        });
        uploadDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, 1);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, 3);
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access to use this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
        else{
            // Find Wifi info
            WifiInfoTask wifiInfoTask = new WifiInfoTask(this);
            wifiInfoTask.execute();

            // Start cpu usage handler
            CpuUsageTask cpuUsageTask = new CpuUsageTask(this);
            cpuUsageTask.start();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            String serverHost = data.getStringExtra(ServerActivity.RESULT_SERVER);
            Constants.SPEED_TEST_SERVER_HOST = serverHost;

            String [] split = serverHost.split("/");
            if(split.length>0){
                Constants.SPEED_TEST_SERVER_PING_HOST = split[0];
                Constants.SPEED_TEST_SERVER_HOST = split[0];
            }
            if(split.length>1){
                Constants.SPEED_TEST_SERVER_URI_DL = "/"+split[1]+Constants.SPEED_TEST_SERVER_URI_DL;
                Constants.SPEED_TEST_SERVER_URI_UL = "/"+split[1]+Constants.SPEED_TEST_SERVER_URI_UL;

            }
            else{
                Constants.SPEED_TEST_SERVER_URI_DL = Constants.DL;
                Constants.SPEED_TEST_SERVER_URI_UL = Constants.UL;
            }

            if(requestCode == 1)runUploadDownloadTest(null);
            else if(requestCode == 2)runDownloadTest(null);
            else if(requestCode == 3)runUploadTest(null);

        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI){
                getWifiDetails();
                downloadBtn.setEnabled(true);
                uploadBtn.setEnabled(true);
                uploadDownloadBtn.setEnabled(true);
                new CheckInternetConnectionTask(context).execute();
            }
            else{
                wifiNameLabel.setText("No connection");
                downloadBtn.setEnabled(false);
                uploadBtn.setEnabled(false);
                uploadDownloadBtn.setEnabled(false);
                Log.d("No","No connection");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Find Wifi info
                    WifiInfoTask wifiInfoTask = new WifiInfoTask(this);
                    wifiInfoTask.execute();

                    // Start cpu usage handler
                    CpuUsageTask cpuUsageTask = new CpuUsageTask(this);
                    cpuUsageTask.start();

                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to use wifi scan in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    public void runDownloadTest(View view) {
        if (connected) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadBtn.setEnabled(false);
                    uploadBtn.setEnabled(false);
                    uploadDownloadBtn.setEnabled(false);
                }
            });

            PingService pingService = new PingService();
            final String rtt = pingService.ping();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pingLabel.setText(rtt);
                }
            });

            SpeedTestService speedTestService = new SpeedTestService(this);
            speedTestService.startDownload();
        }
    }

    public void runUploadTest(View view) {
        if (connected) {
            downloadBtn.setEnabled(false);
            uploadBtn.setEnabled(false);
            uploadDownloadBtn.setEnabled(false);

            PingService pingService = new PingService();
            final String rtt = pingService.ping();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pingLabel.setText(rtt);
                }
            });

            SpeedTestService speedTestService = new SpeedTestService(this);
            speedTestService.startUpload();
        }
    }

    public void runUploadDownloadTest(View view) {
        if (connected) {
            downloadBtn.setEnabled(false);
            uploadBtn.setEnabled(false);
            uploadDownloadBtn.setEnabled(false);

            PingService pingService = new PingService();
            final String rtt = pingService.ping();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pingLabel.setText(rtt);
                }
            });

            SpeedTestService speedTestService = new SpeedTestService(this);
            speedTestService.startUploadAndDownload();
        }
    }

    public void getWifiDetails(){
        // Find Wifi info
        WifiInfoTask wifiInfoTask = new WifiInfoTask(this);
        wifiInfoTask.execute();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
