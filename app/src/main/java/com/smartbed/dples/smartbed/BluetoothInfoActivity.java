package com.smartbed.dples.smartbed;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BluetoothInfoActivity extends AppCompatActivity {
    private static final int RESULT_UNPAIR = 200;
    private static final int RESULT_DISCONNECT = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_info);

        Button btnDisconnect = findViewById(R.id.btnDisconnect);
        Button btnUnpairing = findViewById(R.id.btnDelete);

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_DISCONNECT, intent);
                finish();
            }
        });

        btnUnpairing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_UNPAIR, intent);
                finish();
            }
        });
    }
}
