package com.smartbed.dples.smartbed;

import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TextView testView = findViewById(R.id.textView);
        Button btnStop = findViewById(R.id.btnStopAlarm);

        // 알람음 울리기
        Intent intent = getIntent();
        Uri alarmUri = intent.getParcelableExtra("alarmUri");
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        final Ringtone alarmRingtone = ringtoneManager.getRingtone(this, alarmUri);

        startAlarm(alarmRingtone, true);

        // 알람 종료
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "알람 종료");
                startAlarm(alarmRingtone, false);
                finish();
            }
        });
    }

    // 알람음 울리기 시작
    private void startAlarm(Ringtone alarmRingtone, boolean state) {
        if(state) {
            alarmRingtone.play();
        } else {
            alarmRingtone.stop();
        }
    }
}
