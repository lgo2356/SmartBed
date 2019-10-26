package com.smartbed.dples.smartbed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime = 0;
    Button btnLED, btnMonitoring, btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Button view */
        btnLED = (Button) findViewById(R.id.btnLED);
        btnMonitoring = (Button) findViewById(R.id.btnMonitoring);
        btnSetting = (Button) findViewById(R.id.btnSetting);

        /* LED 버튼 리스너 */
        btnLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 블루투스가 연결되어 있는 상태일 때만 LED 화면으로 넘어간다 */
                if(BluetoothObject.getInstance().isConnectionState()) {
                    Intent intent = new Intent(MainActivity.this, LEDActivity.class);
                    startActivity(intent);
                }
                /* 블루투스가 연결되어 있지 않은 경우 */
                else {
                    /* AlertDialog를 통해 블루투스를 연결할 것인지 물어본다 */
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("블루투스를 연결");
                    builder.setMessage("블루투스가 연결되어 있지 않습니다. 연결하시겠습니까?");
                    /* Yes 선택시 블루투스 설정화면으로 넘어간다 */
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                                    startActivity(intent);
                                }
                            });
                    /* No 선택시 아무것도 안 하고 Dialog를 닫는다. */
                    builder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });

        /* 측정된 체중 확인 버튼 */
        btnMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 블루투스가 연결되어 있는 상태일 때만 체중 확인 화면으로 넘어간다 */
                if(BluetoothObject.getInstance().isConnectionState()) {
                    Intent intent = new Intent(MainActivity.this, MonitoringActivity.class);
                    startActivity(intent);
                }
                /* 블루투스가 연결되어 있지 않은 경우 */
                else {
                    /* AlertDialog를 통해 블루투스를 연결할 것인지 물어본다 */
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("블루투스를 연결");
                    builder.setMessage("블루투스가 연결되어 있지 않습니다. 연결하시겠습니까?");
                    /* Yes 선택시 블루투스 설정화면으로 넘어간다 */
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                                    startActivity(intent);
                                }
                            });
                    /* No 선택시 아무것도 안 하고 Dialog를 닫는다. */
                    builder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });

        /* 블루투스 설정 버튼 */
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }

    /* 스마트폰의 뒤로가기 버튼을 눌렀을 때 처리 */
    public void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();  // 시스템 현재 시간을 변수에 저장 (단위: ms)
        long j = currentTimeMillis - this.backPressedTime;  // 변수에 저장된 값(시간) - 0(초기값)

        /* 최초로 눌렀거나, 2초가 초과되어 다시 뒤로가기 버튼을 눌렀을 때 토스트 메세지와 함께 함수 종료 */
        if (j < 0 || j > 2000) {
            backPressedTime = currentTimeMillis;  // 버튼을 누른 시간 변수에 저장
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한 번 더 누르면 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        finish();  // 2초안에 두 번 연속 버튼을 누르면 프로그램을 종료한다
    }

    /* 앱 종료시 처리 */
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            /* Socket 초기화 */
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
