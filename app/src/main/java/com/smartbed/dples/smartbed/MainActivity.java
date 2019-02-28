package com.smartbed.dples.smartbed;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    Fragment cur_fragment = new Fragment();
    int MAX_PAGE = 2;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        BluetoothActivity bluetoothActivity = new BluetoothActivity();
//        ((BluetoothActivity) BluetoothActivity.mContext).scanPairedList();
//        boolean foundDevice = bluetoothActivity.scanPairedList();
//
//        if(foundDevice) {
//            boolean isSuccess = bluetoothActivity.connectBluetooth();
//
//            if(isSuccess) {
//                bluetoothActivity = null;
//                Toast.makeText(getApplicationContext(), "블루투스 자동연결 성공", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            bluetoothActivity = null;
//            Toast.makeText(getApplicationContext(), "블루투스 자동연결 실패", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, BluetoothActivity.class);
//            startActivity(intent);
//        }
//    }

    private class pagerAdapter extends FragmentPagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position < 0 || MAX_PAGE <= position) {
                return null;
            }

            switch(position) {
                case 0:
                    cur_fragment = new MainFragment();
                    break;
                case 1:
                    cur_fragment = new MainMonitoringFragment();
                    break;
            }

            return cur_fragment;
        }

        @Override
        public int getCount() {
            return MAX_PAGE;
        }
    }

    public void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.backPressedTime;
        if (j < 0 || j > 2000) {
            backPressedTime = currentTimeMillis;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한 번 더 누르면 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            BluetoothObject bluetoothObject = BluetoothObject.getInstance();
            bluetoothObject.getOutputStream().close();
            bluetoothObject.getInputStream().close();
            bluetoothObject.getSocket().close();

            bluetoothObject.setSocket(null);
            bluetoothObject.setOutputStream(null);
            bluetoothObject.setInputStream(null);
            bluetoothObject.setConnectionState(false);
            bluetoothObject.setConnectedDeviceName(null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
