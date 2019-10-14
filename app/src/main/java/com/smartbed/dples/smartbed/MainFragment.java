package com.smartbed.dples.smartbed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainFragment extends Fragment {
    Button btnLED;
    Button btnMonitoring;
    Button btnSetting;

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = getContext();

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.main_layout, container, false);
        btnLED = layout.findViewById(R.id.btnLED);
        btnMonitoring = layout.findViewById(R.id.btnMonitoring);
        btnSetting = layout.findViewById(R.id.btnSetting);

        btnLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, LEDActivity.class);
//                startActivity(intent);
                if(BluetoothObject.getInstance().isConnectionState()) {
                    Intent intent = new Intent(context, LEDActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("블루투스를 연결");
                    builder.setMessage("블루투스가 연결되어 있지 않습니다. 연결하시겠습니까?");
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, BluetoothActivity.class);
                                    startActivity(intent);
                                }
                            });
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

        btnMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, MonitoringActivity.class);
//                startActivity(intent);
                if(BluetoothObject.getInstance().isConnectionState()) {
                    Intent intent = new Intent(context, MonitoringActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("블루투스를 연결");
                    builder.setMessage("블루투스가 연결되어 있지 않습니다. 연결하시겠습니까?");
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, BluetoothActivity.class);
                                    startActivity(intent);
                                }
                            });
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

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BluetoothActivity.class);
                startActivity(intent);
            }
        });
        return layout;
    }
}
