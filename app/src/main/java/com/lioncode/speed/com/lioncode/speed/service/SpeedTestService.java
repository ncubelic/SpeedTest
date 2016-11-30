package com.lioncode.speed.com.lioncode.speed.service;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import com.lioncode.speed.MainActivity;
import com.lioncode.speed.R;
import com.lioncode.speed.com.lioncode.speed.Constants;

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
    }


    public void startDownload(){

        System.out.println("Test je pokrenut!");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadSpeedLabel.setText("Calculating...");
            }
        });

        // instantiate speed examples
        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        // add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onDownloadFinished(final SpeedTestReport report) {
            }

            @Override
            public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) {
                System.out.println("ERROR kod skidanja.");
            }

            @Override
            public void onUploadFinished(final SpeedTestReport report) {
            }

            @Override
            public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) {
            }

            @Override
            public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) {
            }

            @Override
            public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) {
            }

            @Override
            public void onInterruption() {
            }
        });


        speedTestSocket.startDownloadRepeat(Constants.SPEED_TEST_SERVER_HOST, Constants.SPEED_TEST_SERVER_URI_DL,
                Constants.SPEED_TEST_DURATION, Constants.REPORT_INTERVAL, new
                        IRepeatListener() {
                            @Override
                            public void onFinish(final SpeedTestReport report) {
                                System.out.println("---DOWNLOAD COMPLETE---");
                                System.out.println("Total packet size: "+report.getTotalPacketSize());
                                System.out.println("Transfer rate bit: "+report.getTransferRateBit());
                                System.out.println("Transfer rate octet: "+report.getTransferRateOctet());

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

                            }

                            @Override
                            public void onReport(final SpeedTestReport report) {
                                System.out.println(report.getProgressPercent()+"%");
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

            @Override
            public void onDownloadFinished(final SpeedTestReport report) {
            }

            @Override
            public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) {
                System.out.println("ERROR kod skidanja.");
            }

            @Override
            public void onUploadFinished(final SpeedTestReport report) {
            }

            @Override
            public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) {
            }

            @Override
            public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) {
//                System.out.print(downloadReport.getProgressPercent()+"% ,");
            }

            @Override
            public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) {
            }

            @Override
            public void onInterruption() {
            }
        });

        // start upload
        speedTestSocket.startUploadRepeat(Constants.SPEED_TEST_SERVER_HOST, Constants.SPEED_TEST_SERVER_URI_UL, Constants.SPEED_TEST_DURATION, Constants.REPORT_INTERVAL, Constants.FILE_SIZE,
                new IRepeatListener() {
                    @Override
                    public void onFinish(final SpeedTestReport report) {
                        System.out.println("---UPLOAD COMPLETE---");
                        System.out.println("Total packet size: "+report.getTotalPacketSize());
                        System.out.println("Transfer rate bit: "+report.getTransferRateBit());
                        System.out.println("Transfer rate octet: "+report.getTransferRateOctet());

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
                    }

                    @Override
                    public void onReport(final SpeedTestReport report) {
                        System.out.println(report.getProgressPercent()+"%");
                    }
                });
    }

    public void startUploadAndDownload(){
        System.out.println("Test je pokrenut!");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadSpeedLabel.setText("Calculating...");
            }
        });

        // instantiate speed examples
        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        // add a listener to wait for speed examples completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override public void onDownloadFinished(final SpeedTestReport report) { }
            @Override public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) { System.out.println("ERROR kod skidanja."); }
            @Override public void onUploadFinished(final SpeedTestReport report) { }
            @Override public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) { }
            @Override public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) { }
            @Override public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) { }
            @Override public void onInterruption() { }
        });


        speedTestSocket.startDownloadRepeat(Constants.SPEED_TEST_SERVER_HOST, Constants.SPEED_TEST_SERVER_URI_DL,
                Constants.SPEED_TEST_DURATION, Constants.REPORT_INTERVAL, new
                        IRepeatListener() {
                            @Override
                            public void onFinish(final SpeedTestReport report) {
                                System.out.println("---DOWNLOAD COMPLETE---");
                                System.out.println("Total packet size: "+report.getTotalPacketSize());
                                System.out.println("Transfer rate bit: "+report.getTransferRateBit());
                                System.out.println("Transfer rate octet: "+report.getTransferRateOctet());

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
                                speedTestSocket.startUploadRepeat(Constants.SPEED_TEST_SERVER_HOST, Constants.SPEED_TEST_SERVER_URI_UL, Constants.SPEED_TEST_DURATION, Constants.REPORT_INTERVAL, Constants.FILE_SIZE,
                                        new IRepeatListener() {
                                            @Override
                                            public void onFinish(final SpeedTestReport report) {
                                                System.out.println("---UPLOAD COMPLETE---");
                                                System.out.println("Total packet size: "+report.getTotalPacketSize());
                                                System.out.println("Transfer rate bit: "+report.getTransferRateBit());
                                                System.out.println("Transfer rate octet: "+report.getTransferRateOctet());

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
                                            }

                                            @Override
                                            public void onReport(final SpeedTestReport report) {
                                                System.out.println(report.getProgressPercent()+"%");
                                            }
                                        });

                            }

                            @Override
                            public void onReport(final SpeedTestReport report) {
                                System.out.println(report.getProgressPercent()+"%");
                            }
                        });
    }

}
