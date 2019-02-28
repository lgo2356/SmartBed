package com.smartbed.dples.smartbed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonitoringActivity extends AppCompatActivity {
    BluetoothIO mService;
    boolean mBind;
    ThreadUI threadUI;

    TextView textWeight;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothIO.LocalBinder binder = (BluetoothIO.LocalBinder) service;
            mService = binder.getService();
            mBind = true;

            // 데이터 읽기 시작
            mService.dataReceiveStart();

            threadUI.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBind = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, BluetoothIO.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        threadUI = new ThreadUI();
        threadUI.setDaemon(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        LinearLayout backgroundWeight = findViewById(R.id.containerWeight);

        textWeight = findViewById(R.id.textWeight);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MONITOR", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("MONITORING", "onStop");

        if(mBind) {
            threadUI.interrupt();
            threadUI = null;
            mService.stopRead();
            unbindService(mConnection);
            mBind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MONITOR", "onDestroy");
    }

    private class ThreadUI extends Thread {

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                final int[] data = mService.getData();

                if(data != null) {
                    final String weight_int = String.valueOf(data[0]);
                    final String weight_under = String.valueOf(data[1]);
                    final String weight = weight_int + "." + weight_under;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textWeight.setText(weight);
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }
}
