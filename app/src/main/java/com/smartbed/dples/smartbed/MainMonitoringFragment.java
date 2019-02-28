package com.smartbed.dples.smartbed;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMonitoringFragment extends Fragment {
    BluetoothIO mService;
    boolean mBind;
    Thread thread;

    public MainMonitoringFragment() {

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothIO.LocalBinder binder = (BluetoothIO.LocalBinder) service;
            mService = binder.getService();
            mBind = true;

            // 데이터 읽기 시작
            mService.dataReceiveStart();
            thread.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBind = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        final Context context = getContext();

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.main_monitoring_layout, container, false);
        final TextView textLED = layout.findViewById(R.id.textLEDState);
        final TextView textWeight = layout.findViewById(R.id.textWeight);
        final TextView textTemp = layout.findViewById(R.id.textTemp);
        final Handler handler = new Handler();

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("TAG", "Fragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
