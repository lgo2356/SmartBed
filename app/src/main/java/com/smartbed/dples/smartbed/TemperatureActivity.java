package com.smartbed.dples.smartbed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TemperatureActivity extends AppCompatActivity implements TempAlarmListViewAdapter.ListButtonClickListener {
    TempAlarmListViewAdapter listViewAdapter;
    private static final int ALARM_INFO = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        // ActionBar 높이 구하기
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            Log.d("TAG", ""+actionBarHeight);
            linearLayout.setPadding(0, actionBarHeight + 22, 0, 0);
        }

        TextView textTemp = (TextView) findViewById(R.id.textTemp);
        ListView alarmList = (ListView) findViewById(R.id.tempAlarmList);
        Button btnAlarm = (Button) findViewById(R.id.btnAlarm);

        listViewAdapter = new TempAlarmListViewAdapter(this);
        alarmList.setAdapter(listViewAdapter);

//        listViewAdapter.addItem("Temp", "Music");

        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", position + "번 아이템 클릭됨");
            }
        });

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TemperatureActivity.this, TempAlarmSettingActivity.class);
                startActivityForResult(intent, ALARM_INFO);
            }
        });


    }

    @Override
    public void onListButtonClick(int position) {
        Log.d("TAG", "Touch 됨");
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if(resCode == RESULT_OK) {
            switch(reqCode) {
                // 알람음 설정 완
                case ALARM_INFO:
                    String bodyTemp = data.getStringExtra("bodyTemp");
                    String ringtoneTitle = data.getStringExtra("ringtoneTitle");

                    Log.d("TAG", "넘겨받은 데이터: " + bodyTemp + " " + ringtoneTitle);
                    break;
            }
        }
    }
}
