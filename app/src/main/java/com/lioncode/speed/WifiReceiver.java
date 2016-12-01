package com.lioncode.speed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("CONNECTION_LOST"));
    }
}
