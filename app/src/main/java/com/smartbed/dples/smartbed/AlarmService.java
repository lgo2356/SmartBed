package com.smartbed.dples.smartbed;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.InputStream;

public class AlarmService extends Service {
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
//        InputStream inputStream = BluetoothObject.getInstance().getInputStream();
//        final BluetoothIO io = new BluetoothIO(this);
//        io.BluetoothDataReceive();

        Thread alarmThread = new Thread(new Runnable() {
            @Override
            public void run() {
//                while(!Thread.currentThread().isInterrupted()) {
//                    String alarmData = io.getData();
//                    Log.d("TAG", "Alarm " + alarmData);

//                    if(alarmData.equals("7")) {
//                        Thread.currentThread().interrupt();
//                        startAlarm();
//                    }

//                    Intent intent = new Intent(this, AlarmActivity.class);
//                }
            }
        });

        alarmThread.start();

        return START_NOT_STICKY;
    }

    private void startAlarm() {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
