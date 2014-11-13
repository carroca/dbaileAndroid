package net.rbcode.dbaile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiverFavoritos extends BroadcastReceiver {
    AlarmReceiverFavoritos alarm = new AlarmReceiverFavoritos();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            alarm.setAlarm(context);
        }
    }
}