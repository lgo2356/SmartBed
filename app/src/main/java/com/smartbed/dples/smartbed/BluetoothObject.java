package com.smartbed.dples.smartbed;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothObject {
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private byte[] data = null;
    private String mConnectedDeviceName = null;
    private boolean connectionState = false;

    // Single pattern
    private static class LazyHolder {
        private static final BluetoothObject INSTANCE = new BluetoothObject();

        private LazyHolder() {

        }
    }

    private BluetoothObject() {
        this.mSocket = null;
        this.mOutputStream = null;
        this.mInputStream = null;
        Log.d("SYSTEM", "Bluetooth object 생성");
    }

    public static BluetoothObject getInstance() {
        return LazyHolder.INSTANCE;
    }

    // Object set
    public void setSocket(BluetoothSocket socket) {
        this.mSocket = socket;
    }

    public void setOutputStream(OutputStream output) {
        this.mOutputStream = output;
    }

    public void setInputStream(InputStream input) {
        this.mInputStream = input;
    }

    public void setReceivedData(byte[] data) {
        this.data = data;
    }

    public void setConnectedDeviceName(String name) {
        this.mConnectedDeviceName = name;
    }

    public void setConnectionState(boolean state) {
        this.connectionState = state;
    }

    // Object get
    public BluetoothSocket getSocket() {
        return this.mSocket;
    }

    public OutputStream getOutputStream() {
        return this.mOutputStream;
    }

    public InputStream getInputStream() {
        return this.mInputStream;
    }

    public byte[] getReceivedData() {
        return this.data;
    }

    public String getConnectedDeviceName() {
        return this.mConnectedDeviceName;
    }

    public boolean isConnectionState() {
        return connectionState;
    }
}
