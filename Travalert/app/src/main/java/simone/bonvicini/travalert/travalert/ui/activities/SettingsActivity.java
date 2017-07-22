package simone.bonvicini.travalert.travalert.ui.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import simone.bonvicini.travalert.travalert.R;

public class SettingsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_AUDIO = 101;

    private TextView mAlarmTone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initUi();
    }

    private void initUi(){


        mAlarmTone = (TextView)findViewById(R.id.alarm_tone);

        mAlarmTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioIntent();
            }
        });
    }

    private void openAudioIntent() {

        Intent audioIntent = new Intent();
        audioIntent.setType("audio/*");
        audioIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(audioIntent,REQUEST_CODE_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_AUDIO){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();

                mAlarmTone.setText(uri.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
