package com.lioncode.speed;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lioncode.speed.com.lioncode.speed.service.CpuUsageTask;
import com.lioncode.speed.com.lioncode.speed.service.PingService;
import com.lioncode.speed.com.lioncode.speed.service.SpeedTestService;
import com.lioncode.speed.com.lioncode.speed.service.WifiInfoTask;

public class MainActivity extends AppCompatActivity {

    private Button downloadBtn;
    private Button uploadBtn;
    private Button uploadDownloadBtn;

    private TextView pingLabel;

    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_speedtest);

        downloadBtn = (Button) findViewById(R.id.button_dl);
        uploadBtn = (Button) findViewById(R.id.button_ul);
        uploadDownloadBtn = (Button) findViewById(R.id.button_ul_dl);

        pingLabel = (TextView) findViewById(R.id.trip_time);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Find Wifi info
        WifiInfoTask wifiInfoTask = new WifiInfoTask(this);
        wifiInfoTask.execute();

        // Start cpu usage handler
        CpuUsageTask cpuUsageTask = new CpuUsageTask(this);
        cpuUsageTask.start();
    }

    public void runDownloadTest(View view){
        if(connected){
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

    public void runUploadTest(View view){
        if(connected){
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

    public void runUploadDownloadTest(View view){
        if(connected){
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
