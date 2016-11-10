package com.lioncode.speed.com.lioncode.speed.service;

import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;

import com.lioncode.speed.R;

import java.io.IOException;
import java.io.RandomAccessFile;


public class CpuUsageTask{

    private Activity activity;

    private TextView cpuUsageLabel;

    public CpuUsageTask(Activity act){
        activity = act;

        cpuUsageLabel = (TextView) activity.findViewById(R.id.cpu_usage);
    }

    public void start(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
                    String load = reader.readLine();

                    String[] toks = load.split(" +");  // Split on one or more spaces

                    long idle1 = Long.parseLong(toks[4]);
                    long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                    try {
                        Thread.sleep(360);
                    } catch (Exception e) {}

                    reader.seek(0);
                    load = reader.readLine();
                    reader.close();

                    toks = load.split(" +");

                    long idle2 = Long.parseLong(toks[4]);
                    long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

                    float result = (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

                    cpuUsageLabel.setText(String.format("%.2f",(result*100))+"%");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                handler.postDelayed(this, 2000);
            }
        });
    }

}
