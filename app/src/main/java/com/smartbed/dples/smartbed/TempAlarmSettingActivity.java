package com.smartbed.dples.smartbed;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class TempAlarmSettingActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    private ListView alarmSettingList;
    private AlarmListViewAdapter alarmListViewAdapter;
    private Intent intent;

    String bodyTempData;
    String ringtoneName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_alarm);

        intent = new Intent();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container);

        // ActionBar 높이 구하기
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            Log.d("TAG", ""+actionBarHeight);
            linearLayout.setPadding(0, actionBarHeight + 15, 0, 0);
        }

        alarmListViewAdapter = new AlarmListViewAdapter();
        alarmSettingList = (ListView) findViewById(R.id.listAlarmSettings);
        alarmSettingList.setAdapter(alarmListViewAdapter);

        // 저장된 체온 가져오기
        SharedPreferences bodyTemp = getSharedPreferences("bodyTempData", MODE_PRIVATE);

        // 저장된 링톤 제목 가져오기
        SharedPreferences ringtoneData = getSharedPreferences("ringtoneData", MODE_PRIVATE);

        bodyTempData = bodyTemp.getString("body", "");
        ringtoneName = ringtoneData.getString("ringtoneTitle", "");

        alarmListViewAdapter.addItem("체온 설정", bodyTempData + "°C");
        alarmListViewAdapter.addItem("사운드", ringtoneName);

        alarmSettingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", ""+position);

                switch(position) {
                    // 체온 설정
                    case 0:
                        showTempDialog();
                        break;

                    // 알람음 설정
                    case 1:
                        getRingtoneList();
                        break;
                }
            }
        });

        Button btnSaveAlarm = (Button) findViewById(R.id.btnSave);

        btnSaveAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm();

                Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                startService(intent);
            }
        });
    }

    private void showTempDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("체온 설정");
        builder.setMessage("설정할 온도를 입력해주세요.");
        builder.setView(editText);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "설정 체온: " + editText.getText().toString());
                        bodyTempData = editText.getText().toString();

                        SharedPreferences bodyTempData = getSharedPreferences("bodyTempData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = bodyTempData.edit();
                        editor.putString("body", editText.getText().toString());
                        editor.apply();

                        alarmListViewAdapter.getItem(0).setNameText(editText.getText().toString() + "°C");
                        alarmListViewAdapter.notifyDataSetChanged();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public void getRingtoneList() {
        // 음악 리스트 받아오는 코드
//        musicInfoList = new ArrayList<>();
//
//        String[] projection = {
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.ARTIST
//        };
//
//        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                projection, null, null, null);
//
//        while(cursor.moveToNext()) {
//            MusicInfo musicInfo = new MusicInfo();
//
//            musicInfo.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
//            musicInfo.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
//            musicInfo.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//
//            musicInfoList.add(musicInfo);
//            Log.d("MEDIA", musicInfo.getTitle());
//        }
//        cursor.close();
//        intent.putExtra(""+musicInfoList.get(2).getTitle(), true);

        // 벨소리 리스트 받아오는 코드
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람음 선택");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);

        startActivityForResult(intent, 500);

        Log.d("TAG", "왓니?");
    }

    private void setAlarm(@NonNull Uri alarmUri, long second) {
        Log.d("TAG", "Set alarm");
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
        intent.putExtra("alarmUri", alarmUri);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + second, pendingIntent);
    }

    private void saveAlarm() {
        Log.d("TAG", "Saved Alarm");

        intent.putExtra("bodyTemp", bodyTempData);
        intent.putExtra("ringtoneTitle", ringtoneName);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if(resCode == RESULT_OK) {
            switch(reqCode) {
                // 알람음 설정 완
                case 500:
                    // Uri 가져오기
                    Uri alarmUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
                    ringtoneName = ringtone.getTitle(getApplicationContext());

                    Log.d("TAG", ringtoneName);

                    SharedPreferences ringtoneData = getSharedPreferences("ringtoneData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = ringtoneData.edit();
                    editor.putString("ringtoneTitle", ringtoneName);
                    editor.apply();

                    alarmListViewAdapter.getItem(1).setNameText(ringtoneName);
                    alarmListViewAdapter.notifyDataSetChanged();

                    break;
            }
        }
    }
}
