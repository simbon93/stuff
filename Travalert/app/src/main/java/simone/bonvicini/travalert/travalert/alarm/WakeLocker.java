package simone.bonvicini.travalert.travalert.alarm;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import simone.bonvicini.travalert.travalert.ui.activities.AlarmActivity;

public abstract class WakeLocker {

    public static final String TAG = "WakeLocker";

    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {

        if (wakeLock != null) wakeLock.release();

        Log.d(TAG,"Acquire");

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, AlarmActivity.class.getSimpleName());
        wakeLock.acquire();
    }

    public static void release() {
        Log.d(TAG,"Release");

        if (wakeLock != null) wakeLock.release();
        wakeLock = null;
    }
}