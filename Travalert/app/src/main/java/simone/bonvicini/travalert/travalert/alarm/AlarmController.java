package simone.bonvicini.travalert.travalert.alarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import simone.bonvicini.travalert.travalert.services.DataService;

/**
 * Created by simone on 16/05/17.
 */

public class AlarmController {

    public enum AlarmType {
        LOCATION, TIME
    }

    private static AlarmController sOurInstance = new AlarmController();

    private static Context sContext;

    private boolean mGpsDisabled;

    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private AlarmController.OnAlarmListener mOnAlarmListener;

    public static AlarmController init(Context context) {

        sContext = context;
        return sOurInstance;
    }

    public static AlarmController get() {

        return sOurInstance;
    }

    public interface OnAlarmListener {

        void onAlarm(boolean ringing);
    }

    public void setOnAlarmListener(AlarmController.OnAlarmListener onAlarmListener) {

        mOnAlarmListener = onAlarmListener;
    }

    public void cancel() {

        stopSound();
        Log.d("ALARM_MEDIA", "Alarm cancelled, mediaplayer stopped");

        if (mOnAlarmListener != null) {
            mOnAlarmListener.onAlarm(false);
        }
    }

    private void playSound() {

        AudioManager audioManager = (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {

            mMediaPlayer.reset();

            DataService dataService = DataService.get(sContext);

            String alarmTone = dataService.getAlarmTone();

            Uri uri;

            if (alarmTone.startsWith("ringtone_")) {

                uri = Uri.parse("android.resource://" + sContext.getPackageName() + "/raw/" + alarmTone);

            } else {

                uri = Uri.parse(alarmTone);
            }

            try {
                mMediaPlayer.setDataSource(sContext, uri);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                float selectedVolume = ((float) dataService.getAlarmVolume() / 10);
                Log.d("AlarmStatus", "Volume level: " + selectedVolume);
                mMediaPlayer.setVolume(selectedVolume, selectedVolume);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setLooping(true);
            } catch (IOException e) {
                Log.d("AlarmStatus", "Error");
                e.printStackTrace();
            }
        }
    }

    public boolean isGpsDisabled() {

        return mGpsDisabled;
    }

    public void setGpsDisabled(boolean gpsDisabled) {

        mGpsDisabled = gpsDisabled;
    }

    private void stopSound() {

        mMediaPlayer.stop();
    }

    public void makeAlarmRing(AlarmType alarmType) {

        if ((alarmType == AlarmType.TIME && mGpsDisabled) || alarmType == AlarmType.LOCATION) {

            playSound();
            Log.d("ALARM_MEDIA", "Alarm started, mediaplayer started");

            if (mOnAlarmListener != null) {
                mOnAlarmListener.onAlarm(true);
            }
        }
    }

    /*public boolean isRinging() {

        Alarm alarm = getCurrentAlarm();

        if (alarm != null) {
            return alarm.isRinging();
        }

        Log.d("ALARM_FAULT", "Can't get currentAlarm");
        return false;

    }*/

}
