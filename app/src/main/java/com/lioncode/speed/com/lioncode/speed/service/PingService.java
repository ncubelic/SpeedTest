package com.lioncode.speed.com.lioncode.speed.service;

import com.lioncode.speed.com.lioncode.speed.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PingService {

    public String ping(){
        List<String> pingList = new ArrayList<>();

        String command[] = {"ping", "-c4", Constants.SPEED_TEST_SERVER_PING_HOST};
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;

        try {
            while((line = in.readLine()) != null){
                pingList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String finalLine = pingList.get(pingList.size()-1);
        String [] splitFinalLine = finalLine.split(" = ");
        String rtTime = splitFinalLine[1].substring(0,splitFinalLine[1].indexOf("/"));
        float rttFloat = Float.valueOf(rtTime);
        String rtt = String.format("%.0f",rttFloat)+" MS";
        return rtt;
    }

}
