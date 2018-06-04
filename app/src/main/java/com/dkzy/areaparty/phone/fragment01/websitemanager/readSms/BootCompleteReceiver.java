package com.dkzy.areaparty.phone.fragment01.websitemanager.readSms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.w("BootCompleteReceiver","监听到开机启动");
        Toast.makeText(context, "监听到开机启动", Toast.LENGTH_SHORT).show();
        Intent service = new Intent(context,ReadSmsService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.startService(service);
    }
}
