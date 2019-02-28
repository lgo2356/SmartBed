package com.smartbed.dples.smartbed;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothActivity extends AppCompatActivity implements ListViewAdapter.ListButtonClickListener {
    private static final int REQUEST_BT_ENABLE = 100;
    private static final int REQUEST_PAIRING_DELETE = 101;
    private static final int REQUEST_SETTING = 102;
    private static final int REQUEST_DISCONNECT = 102;
    private static final int RESULT_UNPAIR = 200;
    private static final int RESULT_DISCONNECT = 201;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bluetoothAdapter = null;
    BroadcastReceiver broadcastReceiver = null;
    BluetoothDevice mRemoteDevice = null;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    Set<BluetoothDevice> pairedDevices = null;
    ArrayList<BluetoothDevice> discoveredDevices = null;
    int pairedDeviceCount = 0;
    int selectedItem = -1;
    int selectedItemInfo = -1;

    Button btnDeviceScan = null;
    ListView pairedList = null;
    ListView discoveredList = null;
    ListViewAdapter pairedListAdapter = null;
    ListViewAdapter discoveredListAdapter = null;
    LinearLayout linearLayout = null;

    // Loading animation values
    ImageView loadingImage;
//    AnimationDrawable loadingAnimation;

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
        permissionCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH");

        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH}, REQUEST_BT_ENABLE);
        }

        // 리스트 어댑터 객체 생성
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            bluetoothAdapter.listenUsingRfcommWithServiceRecord("SerialPortProfile", uuid);
        } catch (IOException e) { }



        discoveredDevices = new ArrayList<>();
        pairedListAdapter = new ListViewAdapter(this);
        discoveredListAdapter = new ListViewAdapter(this);

        // View 객체 생성
        pairedList = (ListView) findViewById(R.id.list_paired);
        discoveredList = (ListView) findViewById(R.id.list_discovered);
        pairedList.setAdapter(pairedListAdapter);
        discoveredList.setAdapter(discoveredListAdapter);
        btnDeviceScan = (Button) findViewById(R.id.btnDeviceScan);

        // Broadcast 결과에 따른 처리
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch(action) {
                    // 블루투스 디바이스 검색이 시작됐을 때
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        discoveredDevices.clear();
                        break;

                    // 블루투스 디바이스 검색이 끝났을 때
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                        loadingAnimation.stop();
//                        loadingImage.setVisibility(View.GONE);
                        break;

                    // 블루투스 디바이스를 찾았을 때
                    case BluetoothDevice.ACTION_FOUND:
                        // 검색된 디바이스의 객체
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Log.d("TAG", "Name: " + device.getName());
                        Log.d("TAG", "Address: " + device.getAddress());

                        if(device.getName() != null) {
                            discoveredListAdapter.addItem(device.getName(), null, null);
                            discoveredListAdapter.notifyDataSetChanged();
                            discoveredDevices.add(device);
                        }
                        break;

                    // 블루투스 디바이스 본딩 상태 변화에 따른 처리
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                        switch(state) {
                            // 페어링 실패
                            case BluetoothDevice.BOND_NONE:
                                Log.d("Bond", "Bond none");
                                break;
                            // 페어링 중
                            case BluetoothDevice.BOND_BONDING:
                                Log.d("Bond", "Bond bonding");
                                break;
                            // 페어링 성공
                            case BluetoothDevice.BOND_BONDED:
                                Log.d("Bond", "Bond bonded");

                                pairedListAdapter.addItem(
                                        discoveredListAdapter.getItem(selectedItem).getDeviceName(),
                                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.bluetooth_disconnected_state),
                                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.settings)
                                );
                                pairedListAdapter.notifyDataSetChanged();
                                discoveredListAdapter.removeItem((int) discoveredListAdapter.getItemId(selectedItem));
                                discoveredListAdapter.notifyDataSetChanged();
                                selectedItem = -1;
                                break;
                        }
                        break;
                }
            }
        };

        // Scan Intent Filter 등록
        IntentFilter scanFilter = new IntentFilter();
        scanFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        scanFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        scanFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, scanFilter);

        // State Intent Filter 등록
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver, stateFilter);

        if(bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 디바이스 입니다.", Toast.LENGTH_SHORT).show();
        } else {
            if(!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_BT_ENABLE);
                finish();
            }
        }

        btnDeviceScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });

        pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDeviceCount = pairedDevices.size();

        if(pairedDeviceCount > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(BluetoothObject.getInstance().getConnectedDeviceName())) {
                    pairedListAdapter.addItem(device.getName(),
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.bluetooth_connected_state),
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.settings));
                } else {
                    pairedListAdapter.addItem(device.getName(),
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.bluetooth_disconnected_state),
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.settings));
                }
            }
        }

        pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "Paired list clicked!");
                progressON(BluetoothActivity.this);
                String selectedDeviceName = ((ListViewItem) parent.getItemAtPosition(position)).getDeviceName();

                connectToSelectedDevice(selectedDeviceName, position);
            }
        });

        discoveredList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                if(bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }

                if(!bluetoothAdapter.isDiscovering()) {
                    String selectedDeviceName = ((ListViewItem) adapterView.getItemAtPosition(position)).getDeviceName();
                    Log.d("TAG", selectedDeviceName);
                    selectedItem = position;

                    BluetoothDevice selectedDevice = discoveredDevices.get(position);
                    pairDevice(selectedDevice);
                }
            }
        });
    }

    @Override
    public void onListButtonClick(int position) {
        Log.d("TAG", position + "번 버튼 클릭 됨");
        selectedItemInfo = position;
        Intent intent = new Intent(getApplicationContext(), BluetoothInfoActivity.class);
        startActivityForResult(intent, REQUEST_SETTING);
    }

    private void connectToSelectedDevice(final String deviceName, final int position) {
        mRemoteDevice = SearchDeviceFromBondedList(deviceName);
        final Handler handler = new Handler();

        Thread connectingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();

                    mOutputStream = mSocket.getOutputStream();
                    mInputStream = mSocket.getInputStream();

                    mOutputStream.write("C".getBytes());

                    setBluetoothState(mSocket, mOutputStream, mInputStream, deviceName, true);

                    if(BluetoothObject.getInstance().isConnectionState()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                pairedListAdapter.setIcon(position, true);
                                pairedListAdapter.notifyDataSetChanged();
                                progressOFF();
                            }
                        });
                    }
                } catch(Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "블루투스 연결 실패했습니다.", Toast.LENGTH_SHORT).show();
                            progressOFF();
                        }
                    });
                }
            }
        });

        connectingThread.setDaemon(true);
        connectingThread.start();
    }

    // Bluetooth 상태 셋팅
    public void setBluetoothState(BluetoothSocket mSocket, OutputStream mOutput, InputStream mInput, String name, boolean state) {
        BluetoothObject bluetoothObject = BluetoothObject.getInstance();
        bluetoothObject.setSocket(mSocket);
        bluetoothObject.setOutputStream(mOutput);
        bluetoothObject.setInputStream(mInput);
        bluetoothObject.setConnectedDeviceName(name);
        bluetoothObject.setConnectionState(state);
    }

    private BluetoothDevice SearchDeviceFromBondedList(String name) {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDeviceCount = pairedDevices.size();

        for(BluetoothDevice device : pairedDevices) {
            if(name.equals(device.getName())) {
                return device;
            }
        }
        return null;
    }

    // 페어링 하기
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 페어링 해제하기
    private void unPairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 블루투스 디바이스 스캔하기
    private void doDiscovery() {
        discoveredListAdapter.clearItem();
        discoveredListAdapter.notifyDataSetChanged();

        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        } else {
//            loadingImage();
            bluetoothAdapter.startDiscovery();
        }
    }

    AppCompatDialog progressDialog;
    public void progressON(Activity activity) {
        if(activity == null || activity.isFinishing()) {
            return;
        }

        if(progressDialog != null && progressDialog.isShowing()) {
            Log.d("TAG", "NO!!!");
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.loading_dialog_layout);
            progressDialog.show();
        }
    }

    public void progressOFF() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

//    public void loadingImage() {
//        loadingImage = (ImageView) findViewById(R.id.imageLoading);
//        loadingAnimation = (AnimationDrawable) loadingImage.getBackground();
//        loadingImage.post(new Runnable() {
//            @Override
//            public void run() {
//                loadingImage.setVisibility(View.VISIBLE);
//                loadingAnimation.start();
//            }
//        });
//    }

    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        // 페어링 목록에 있는 디바이스 연결 설정하기
        if(reqCode == REQUEST_SETTING) {
            if(resCode == RESULT_UNPAIR) {
                BluetoothObject bluetoothObject = BluetoothObject.getInstance();

                if(bluetoothObject.isConnectionState()) {
                    try {
                        bluetoothObject.getOutputStream().close();
                        bluetoothObject.getInputStream().close();
                        bluetoothObject.getSocket().close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }

                setBluetoothState(null, null, null, null, false);

                String deviceName = pairedListAdapter.getItem(selectedItemInfo).getDeviceName();
                BluetoothDevice device = SearchDeviceFromBondedList(deviceName);
                unPairDevice(device);
                pairedListAdapter.removeItem(selectedItemInfo);
                pairedListAdapter.notifyDataSetChanged();
                selectedItemInfo = -1;
            }

            if(resCode == RESULT_DISCONNECT) {
                Log.d("TAG", "Disconnect OK");

                BluetoothObject bluetoothObject = BluetoothObject.getInstance();

                if(bluetoothObject.isConnectionState()) {
                    try {
                        bluetoothObject.getOutputStream().close();
                        bluetoothObject.getInputStream().close();
                        bluetoothObject.getSocket().close();

                        setBluetoothState(null, null, null, null, false);

                        pairedListAdapter.setIcon(selectedItemInfo, false);
                        pairedListAdapter.notifyDataSetChanged();
                        selectedItemInfo = -1;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(broadcastReceiver);
    }
}
