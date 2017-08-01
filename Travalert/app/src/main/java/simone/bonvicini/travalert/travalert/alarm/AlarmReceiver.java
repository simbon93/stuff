package simone.bonvicini.travalert.travalert.alarm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import simone.bonvicini.travalert.travalert.ui.activities.AlarmActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("ALARM_FIRE", "ALARM!!!");

        if (isScreenLocked(context)) {
            WakeLocker.acquire(context);
        }
        AlarmController.get().makeAlarmRing(AlarmController.AlarmType.TIME);
        startAlarmActivity(context);
        if (isScreenLocked(context)) {
            WakeLocker.release();
        }
        sendNotification(context, intent);
    }

    private void sendNotification(Context context, Intent intent) {

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(), AlarmActivity.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    private boolean isScreenLocked(Context context) {

        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    private void startAlarmActivity(Context context) {

        Intent foregroundIntent = new Intent(context, AlarmActivity.class);
        foregroundIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(foregroundIntent);
    }
}