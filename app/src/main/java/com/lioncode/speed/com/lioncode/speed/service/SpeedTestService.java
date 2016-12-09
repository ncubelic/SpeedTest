package com.lioncode.speed.com.lioncode.speed.service;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.lioncode.speed.R;
import com.lioncode.speed.com.lioncode.speed.Constants;
import com.lioncode.speed.com.lioncode.speed.model.WifiValues;

import java.math.BigDecimal;
import java.math.RoundingMode;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.IRepeatListener;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;


public class SpeedTestService {

    private Activity activity;

    private TextView downloadSpeedLabel;
    private TextView uploadSpeedLabel;

    private TextView nameLabel;
    private TextView modeLabel;
    private TextView strengthLabel;
    private TextView channelLabel;
    private TextView encryptionLabel;
    private TextView frequencyLabel;
    private TextView ping;

    private Button downloadBtn;
    private Button uploadBtn;
    private Button uploadDownloadBtn;

    public SpeedTestService(Activity act){
        activity = act;

        downloadSpeedLabel = (TextView) activity.findViewById(R.id.download);
        uploadSpeedLabel = (TextView) activity.findViewById(R.id.upload);
        downloadBtn = (Button) activity.findViewById(R.id.button_dl);
        uploadBtn = (Button) activity.findViewById(R.id.button_ul);
        uploadDownloadBtn = (Button) activity.findViewById(R.id.button_ul_dl);

        nameLabel = (TextView) activity.findViewById(R.id.wifi_name);
        modeLabel = (TextView) activity.findViewById(R.id.wifi_mode);
        strengthLabel = (TextView) activity.findViewById(R.id.wifi_strength);
        channelLabel = (TextView) activity.findViewById(R.id.wifi_channel);
        encryptionLabel = (TextView) activity.findViewById(R.id.wifi_encryption);
        frequencyLabel = (TextView) activity.findViewById(R.id.wifi_freq);
        ping = (TextView) activity.findViewById(R.id.trip_time);
    }


    public void startDownload(){
        // instantiate speed examples
        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadSpeedLabel.setText("Calculating...");
            }
        });

        // add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override public void onDownloadFinished(final SpeedTestReport report) {}
            @Override public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) {
                Log.e("","ERROR kod skidanja.");}
            @Override public void onUploadFinished(final SpeedTestReport report) {}
            @Override public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) {}
            @Override public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) {}
            @Override public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) {}
            @Override public void onInterruption() {}
        });


        speedTestSocket.startDownloadRepeat(Constants.SPEED_TEST_SERVER_HOST, Constants.SPEED_TEST_SERVER_URI_DL,
                Constants.SPEED_TEST_DURATION, Constants.REPORT_INTERVAL, new
                        IRepeatListener() {
                            @Override
                            public void onFinish(final SpeedTestReport report) {
                                Log.d("","---DOWNLOAD COMPLETE---");
                                Log.d("","Total packet size: "+report.getTotalPacketSize());
                                Log.d("","Transfer rate bit: "+report.getTransferRateBit());
                                Log.d("","Transfer rate octet: "+report.getTransferRateOctet());

                                final BigDecimal result = report.getTransferRateBit().divide(new BigDecimal(1000000)).setScale(2, RoundingMode.CEILING);

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadSpeedLabel.setText(""+result+" Mbps");
                                        downloadBtn.setEnabled(true);
                                        uploadBtn.setEnabled(true);
                                        uploadDownloadBtn.setEnabled(true);
                                    }
                                });

                                WifiValues wifiValues = new WifiValues();
                                wifiValues.setSsid(String.valueOf(nameLabel.getText()));
                                wifiValues.setFrequency(String.valueOf(frequencyLabel.getText()));
                                wifiValues.setMode(String.valueOf(modeLabel.getText()));
                                wifiValues.setEncryption(String.valueOf(encryptionLabel.getText()));
                                wifiValues.setChannel(String.valueOf(channelLabel.getText()));
                                wifiValues.setStrength(String.valueOf(strengthLabel.getText()));
                                wifiValues.setDownloadSpeed(result.toString());
                                wifiValues.setTestType(2);
                                wifiValues.setPing(String.valueOf(ping.getText()));

                                new RestDownloadTask().execute(wifiValues);
                            }

                            @Override
                            public void onReport(final SpeedTestReport report) {
                                Log.d("",report.getProgressPercent()+"%");
                            }
                        });
    }

    public void startUpload(){
        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadSpeedLabel.setText("Calculating...");
            }
        });

        // add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override public void onDownloadFinished(final SpeedTestReport report) {}
            @Override public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) {
                Log.e("","ERROR kod skidanja.");}
            @Override public void onUploadFinished(final SpeedTestReport report) {}
            @Override public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) {}
            @Override public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) {}
            @Override public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) {}
            @Override public void onInterruption() {}
        });

        // start upload
        speedTestSocket.startUploadRepeat(Constants.SPEED_TEST_SERVER_HOST, Constants.SPEED_TEST_SERVER_URI_UL, Constants.SPEED_TEST_DURATION, Constants.REPORT_INTERVAL, Constants.FILE_SIZE,
                new IRepeatListener() {
                    @Override
                    public void onFinish(final SpeedTestReport report) {
                        Log.d("","---UPLOAD COMPLETE---");
                        Log.d("","Total packet size: "+report.getTotalPacketSize());
                        Log.d("","Transfer rate bit: "+report.getTransferRateBit());
                        Log.d("","Transfer rate octet: "+report.getTransferRateOctet());

                        final BigDecimal result = report.getTransferRateBit().divide(new BigDecimal(1000000)).setScale(2, RoundingMode.CEILING);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadSpeedLabel.setText(""+result+" Mbps");
                                downloadBtn.setEnabled(true);
                                uploadDownloadBtn.setEnabled(true);
                                uploadBtn.setEnabled(true);
                            }
                        });

                        WifiValues wifiValues = new WifiValues();
                        wifiValues.setSsid(String.valueOf(nameLabel.getText()));
                        wifiValues.setFrequency(String.valueOf(frequencyLabel.getText()));
                        wifiValues.setMode(String.valueOf(modeLabel.getText()));
                        wifiValues.setEncryption(String.valueOf(encryptionLabel.getText()));
                        wifiValues.setChannel(String.valueOf(channelLabel.getText()));
                        wifiValues.setStrength(String.valueOf(strengthLabel.getText()));
                        wifiValues.setUploadSpeed(result.toString());
                        wifiValues.setTestType(3);
                        wifiValues.setPing(String.valueOf(ping.getText()));

                        new RestUploadTask().execute(wifiValues);
                    }

                    @Override
                    public void onReport(final SpeedTestReport report) {
                        Log.d("",report.getProgressPercent()+"%");
                    }
                });
    }

    public void startUploadAndDownload(){
        // instantiate speed examples
        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadSpeedLabel.setText("Calculating...");
            }
        });

        // add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override public void onDownloadFinished(final SpeedTestReport report) { }
            @Override public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) { Log.e("","ERROR kod skidanja."); }
            @Override public void onUploadFinished(final SpeedTestReport report) { }
            @Override public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) { }
            @Override public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) { }
            @Override public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) { }
            @Override public void onInterruption() { }
        });

        speedTestSocket.startDownloadRepeat(Constants.SPEED_TEST_SERVER_HOST,
                Constants.SPEED_TEST_SERVER_URI_DL, Constants.SPEED_TEST_DURATION,
                Constants.REPORT_INTERVAL, new IRepeatListener() {
                    WifiValues wifiValues = new WifiValues();

                    @Override
                    public void onFinish(final SpeedTestReport report) {
                        Log.d("","---DOWNLOAD COMPLETE---");
                        Log.d("","Total packet size: "+report.getTotalPacketSize());
                        Log.d("","Transfer rate bit: "+report.getTransferRateBit());
                        Log.d("","Transfer rate octet: "+report.getTransferRateOctet());

                        final BigDecimal result = report.getTransferRateBit().divide(new BigDecimal(1000000)).setScale(2, RoundingMode.CEILING);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadSpeedLabel.setText(""+result+" Mbps");
                            }
                        });

                        // start upload
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadSpeedLabel.setText("Calculating...");
                            }
                        });

                        wifiValues.setSsid(String.valueOf(nameLabel.getText()));
                        wifiValues.setFrequency(String.valueOf(frequencyLabel.getText()));
                        wifiValues.setMode(String.valueOf(modeLabel.getText()));
                        wifiValues.setEncryption(String.valueOf(encryptionLabel.getText()));
                        wifiValues.setChannel(String.valueOf(channelLabel.getText()));
                        wifiValues.setStrength(String.valueOf(strengthLabel.getText()));
                        wifiValues.setDownloadSpeed(result.toString());
                        wifiValues.setPing(String.valueOf(ping.getText()));

                        speedTestSocket.startUploadRepeat(Constants.SPEED_TEST_SERVER_HOST,
                                Constants.SPEED_TEST_SERVER_URI_UL, Constants.SPEED_TEST_DURATION,
                                Constants.REPORT_INTERVAL, Constants.FILE_SIZE, new IRepeatListener() {
                                    @Override
                                    public void onFinish(final SpeedTestReport report) {
                                        Log.d("","---UPLOAD COMPLETE---");
                                        Log.d("","Total packet size: "+report.getTotalPacketSize());
                                        Log.d("","Transfer rate bit: "+report.getTransferRateBit());
                                        Log.d("","Transfer rate octet: "+report.getTransferRateOctet());

                                        final BigDecimal result = report.getTransferRateBit().divide(new BigDecimal(1000000)).setScale(2, RoundingMode.CEILING);

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                uploadSpeedLabel.setText(""+result+" Mbps");
                                                downloadBtn.setEnabled(true);
                                                uploadDownloadBtn.setEnabled(true);
                                                uploadBtn.setEnabled(true);
                                            }
                                        });

                                        wifiValues.setUploadSpeed(result.toString());
                                        wifiValues.setTestType(1);
                                        new RestFullTask().execute(wifiValues);
                                    }

                                    @Override
                                    public void onReport(final SpeedTestReport report) {
                                        Log.d("",report.getProgressPercent()+"%");
                                    }
                                });
                    }

                    @Override
                    public void onReport(final SpeedTestReport report) {
                        Log.d("",report.getProgressPercent()+"%");
                    }
                });
    }

}
