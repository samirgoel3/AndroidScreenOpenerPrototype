package com.samir.screenopener;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

public class MainOneSignalService extends NotificationExtenderService {

    private static final String TAG = "MainOneSignalService";

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        try{openDeliveryAllotmentScreen();}catch (Exception e){
            Log.e(TAG,""+e.getMessage());
        }
        return false;
    }


    @SuppressLint({ "InvalidWakeLockTag"})
    private void openDeliveryAllotmentScreen() throws  Exception{
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "" );
        wl.acquire(30 * 1000);
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(this.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
        lock.disableKeyguard();
        this.startActivity(new Intent(this, HelloActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
}
