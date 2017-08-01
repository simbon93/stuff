package simone.bonvicini.travalert.travalert.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import simone.bonvicini.travalert.travalert.helper.DateTimeHelper;
import simone.bonvicini.travalert.travalert.model.LocationAlarm;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by simone on 15/05/17.
 */

public class AlarmScheduler {

    private static Context mContext;

    public static final int ALARM_ID = 69;

    public static AlarmScheduler mInstance = new AlarmScheduler();

    public static void init(Context context) {

        mContext = context;
    }

    public static AlarmScheduler get() {

        return mInstance;
    }

    public void setAlarm(LocationAlarm alarm) {

        if (alarm == null) {
            return;
        }

        cancelAlarm();
        Calendar now = Calendar.getInstance();
        Log.d("ALARM_NOW", "Setting alarms at: " + DateTimeHelper.formatCompleteDate(now.getTime()));

        if (now.after(alarm.getEmergencyAlarm())) {
            alarm.getEmergencyAlarm().add(Calendar.DATE, 7);
        }

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(mContext.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarm.getEmergencyAlarm().getTimeInMillis(), null), pendingIntent);
            Log.d("ALARM_SETEXACT", "Alarm was set exact on " + DateTimeHelper.formatCompleteDate(alarm.getEmergencyAlarm().getTime()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getEmergencyAlarm().getTimeInMillis(), pendingIntent);
            Log.d("ALARM_SETEXACT", "Alarm was set exact on " + DateTimeHelper.formatCompleteDate(alarm.getEmergencyAlarm().getTime()));
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getEmergencyAlarm().getTimeInMillis(), pendingIntent);
            Log.d("ALARM_SETEXACT", "Alarm was set on " + DateTimeHelper.formatCompleteDate(alarm.getEmergencyAlarm().getTime()));
        }
    }

    public void cancelAlarm() {

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(mContext.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("ALARM_CANCEL", "Alarm with id: " + ALARM_ID + " was cancelled");
        } else {
            Log.d("ALARM_CANCEL", "Alarm with id: " + ALARM_ID + " was NOT cancelled");
        }
    }

}
