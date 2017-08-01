package simone.bonvicini.travalert.travalert.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.ui.activities.AlarmActivity;
import simone.bonvicini.travalert.travalert.ui.activities.MainActivity;

public class NotificationHelper {

    public static final String MESSAGE_KEY = "MESSAGE_KEY";
    public static final String EVENT_KEY = "EVENT_KEY";

    public static void showNotification(final Context context, final String message) {

        try {
            final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            final Intent notificationIntent = new Intent(context, AlarmActivity.class);
            notificationIntent.putExtra(MESSAGE_KEY, message);

            // importante il flag FLAG_UPDATE_CURRENT altrimenti l'extras
            // ricevuto è sempre null
            final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_delete)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setTicker(context.getString(R.string.app_name))
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(message)
                    .build();

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            nm.notify(0, notification);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}