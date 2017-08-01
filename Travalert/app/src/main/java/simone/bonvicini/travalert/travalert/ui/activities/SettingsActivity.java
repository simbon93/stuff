package simone.bonvicini.travalert.travalert.ui.activities;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import simone.bonvicini.travalert.travalert.R;
import simone.bonvicini.travalert.travalert.helper.DialogHelper;
import simone.bonvicini.travalert.travalert.services.DataService;

public class SettingsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();

    public static final int REQUEST_CODE_AUDIO = 101;

    private static final String[] PROJECTION = {
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE
    };

    private TextView mAlarmTone;

    private TextView mHourFormat;

    private TextView mAlarmVolume;

    private AppCompatSeekBar mAlarmVolumeSeekbar;

    private Uri mUri;

    private MediaPlayer mMediaPlayer;

    private DataService mDataService;

    private int mSelectedInternalTone = -1;

    private int mCheckedItem = -1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDataService = DataService.get(this);
        initUi();
    }

    private void initUi() {

        mAlarmTone = (TextView) findViewById(R.id.alarm_tone);
        mHourFormat = (TextView) findViewById(R.id.hour_format);
        mAlarmVolume = (TextView) findViewById(R.id.alarm_volume);
        mAlarmVolumeSeekbar = (AppCompatSeekBar) findViewById(R.id.alarm_volume_seekbar);

        setUpAlarmSettings();
    }

    private void setUpAlarmSettings() {

        setUpAlarmVolume();
        setUpHourFormat();
        setUpAlarmTone();
    }

    private void setUpAlarmVolume() {

        final DataService service = DataService.get(SettingsActivity.this);
        mAlarmVolumeSeekbar.setProgress(service.getAlarmVolume() - 1);
        mAlarmVolume.setText(String.valueOf(service.getAlarmVolume()));
        mAlarmVolumeSeekbar.setMax(9);
        mAlarmVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {

                int volume = progress + 1;
                service.setAlarmVolume(volume);
                mAlarmVolume.setText(String.valueOf(volume));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setUpAlarmTone() {

        mAlarmTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] songs = getResources().getStringArray(R.array.song_names);
                final String[] keys = getResources().getStringArray(R.array.song_keys);

                DialogHelper.showSingleChoiceDialogWithButton(SettingsActivity.this, getString(R.string.alarm_tone), getString(R.string.ok)
                        , getString(R.string.load_from_phone), songs, mCheckedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == dialog.BUTTON_NEUTRAL) {
                                    open();
                                } else if (which == dialog.BUTTON_POSITIVE) {
                                    mDataService.setAlarmTone(keys[mSelectedInternalTone]);
                                    mAlarmTone.setText(songs[mSelectedInternalTone]);
                                }
                                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.stop();
                                }
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mSelectedInternalTone = which;
                                mCheckedItem = which;
                                playSound(keys[mSelectedInternalTone]);
                            }
                        });
            }
        });

        String alarmTone = mDataService.getAlarmTone();

        switch (alarmTone) {

            case "ringtone_1":
                updateToneData(0);
                break;
            case "ringtone_2":
                updateToneData(1);
                break;
            case "ringtone_3":
                updateToneData(2);
                break;
            case "ringtone_4":
                updateToneData(3);
                break;
            default:
                mUri = Uri.parse(alarmTone);
                mCheckedItem = -1;
                mSelectedInternalTone = -1;
                getLoaderManager().initLoader(0, null, this);
        }
    }

    private void updateToneData(int index) {

        String[] stringArray = getResources().getStringArray(R.array.song_names);

        mAlarmTone.setText(stringArray[index]);
        mCheckedItem = index;
        mSelectedInternalTone = index;
    }

    private void setUpHourFormat() {

        final DataService service = DataService.get(this);
        final String[] stringArray = getResources().getStringArray(R.array.time_format);
        mHourFormat.setText(stringArray[service.getAlarmFormat()]);
        mHourFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogHelper.showSingleChoiceDialog(SettingsActivity.this, getString(R.string.hour_format), stringArray, service.getAlarmFormat(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        service.setAlarmFormat(which);
                        mHourFormat.setText(stringArray[which]);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_AUDIO) {

            if (resultCode == RESULT_OK) {

                //the selected audio.
                mUri = data.getData();
                mCheckedItem = -1;
                mSelectedInternalTone = -1;
                mDataService.setAlarmTone(mUri.toString());
                getLoaderManager().restartLoader(0, null, this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void playSound(String filename) {

        int resourceId = this.getResources().getIdentifier(filename, "raw", this.getPackageName());

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        mMediaPlayer = MediaPlayer.create(this, resourceId);

        if (mMediaPlayer != null) {

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(1, 1);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.start();
        }
    }

    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        return (new CursorLoader(SettingsActivity.this, mUri, PROJECTION, null, null, null));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        Log.d(TAG, mUri.toString());

        if (c != null && c.moveToFirst()) {
            int displayNameColumn =
                    c.getColumnIndex(OpenableColumns.DISPLAY_NAME);

            if (displayNameColumn >= 0) {
                Log.d(TAG, "Display name: " + c.getString(displayNameColumn));
                mAlarmTone.setText(c.getString(displayNameColumn));
            }

            int sizeColumn = c.getColumnIndex(OpenableColumns.SIZE);

            if (sizeColumn < 0 || c.isNull(sizeColumn)) {
                Log.d(TAG, "Size not available");
            } else {
                Log.d(TAG, String.format("Size: %d",
                        c.getInt(sizeColumn)));
            }
        } else {
            Log.d(TAG, "...no metadata available?");
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // unused
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void open() {

        Intent i = new Intent().setType("audio/*");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            startActivityForResult(i.setAction(Intent.ACTION_OPEN_DOCUMENT)
                            .addCategory(Intent.CATEGORY_OPENABLE),
                    REQUEST_CODE_AUDIO);
        } else {
            startActivityForResult(i.setAction(Intent.ACTION_GET_CONTENT)
                            .addCategory(Intent.CATEGORY_OPENABLE),
                    REQUEST_CODE_AUDIO);
        }
    }
}


