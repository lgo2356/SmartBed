package com.smartbed.dples.smartbed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothIO extends Service {
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private DataInputStream dataInputStream;
    PacketReadThread packetReadThread;
    private IBinder mBinder = new LocalBinder();

    // 프로토콜 셋
    // byte data[] = {(byte)0xEA, (byte)0xAA, 0x15, (byte)0xFF, (byte)0xFF, 0x0F, 0x5A};
    private final static byte HEADER = (byte)0xEA;
    private final static byte IN = (byte)0xAA;
    private final static byte SENSOR = 0x16;
    private final static byte ON = 0x0F;
    private final static byte OFF = 0x00;
    private final static byte TAIL = 0x5A;
    private final static byte FF = (byte)0xFF;

    private int bodyTemperature;
    private int bodyWeight;

    private ExecutorService mWriteExecutor;

    public BluetoothIO() {
        packetReadThread = new PacketReadThread();
        mWriteExecutor = Executors.newSingleThreadExecutor();
    }

    public class LocalBinder extends Binder {
        public BluetoothIO getService() {
            return BluetoothIO.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        return START_NOT_STICKY;
    }

    public void startRequest() {
        Log.d("IO", "START REQ");
        byte[] packet = {HEADER, IN, SENSOR, FF, FF, FF, FF, 0x0D, ON, 0x0A, TAIL};
        packetReadThread.write(packet);
    }

    public void stopRequest() {
        Log.d("IO", "STOP REQ");
        byte[] packet = {HEADER, IN, SENSOR, FF, FF, FF, FF, 0x0D, OFF, 0x0A, TAIL};
        packetReadThread.write(packet);
    }

    public void dataReceiveStart() {
        startRequest();

        packetReadThread.start();
    }

    public void stopRead() {
        stopRequest();
        packetReadThread.interrupt();
    }

    public int[] getData() {
        int[] data = { this.bodyTemperature, this.bodyWeight };
        return data;
    }

    public class PacketReadThread extends Thread {
        byte[] readBuffer;
        byte[] packetBytes = new byte[9];

        public PacketReadThread() {
            outputStream = BluetoothObject.getInstance().getOutputStream();
            inputStream = BluetoothObject.getInstance().getInputStream();
            dataInputStream = new DataInputStream(inputStream);
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    int bytesAvailable = dataInputStream.available();
                    readBuffer = new byte[1024];
//                    Log.d("IO", "Available: " + bytesAvailable);

                    if(bytesAvailable > 0) {
                        int count = 0;

                        while(true) {
                            byte b = dataInputStream.readByte();
                            readBuffer[count] = b;
                            Log.d("IO", "BYTE: " + b);

                            if(b == 90) {
                                break;
                            }

                            if(count < 15) {
                                count += 1;
                            } else {
                                break;
                            }
                        }

                        for(int i=0; i<readBuffer.length; i++) {
                            if(readBuffer[i] == -22 && readBuffer[i+8] == 90) {
                                System.arraycopy(readBuffer, i, packetBytes, 0, packetBytes.length);
                            }
                        }

                        if(packetBytes[0] == -22 && packetBytes[packetBytes.length-1] == 90) {
                            int temp01 = packetBytes[3] << 8;
                            int temp02 = packetBytes[4];
                            int weight01 = packetBytes[5] << 8;
                            int weight02 = packetBytes[6];

                            if(temp02 < 0) {
                                temp02 += 256;
                            }

                            if(weight02 < 0) {
                                weight02 += 256;
                            }

                            bodyTemperature = temp01 + temp02;
                            bodyWeight = weight01 + weight02;
                            bodyWeight = (bodyWeight % 100)/10;

                            Log.d("IO", "정수: " + bodyTemperature + " 소수: " + bodyWeight);
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] packet) {
            try {
                outputStream.write(packet);
                outputStream.flush();
            } catch (IOException e) { }

//            mWriteExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        outputStream.write(packet);
//                        outputStream.flush();
//                    } catch (IOException e) { }
//                }
//            });
        }
    }
}
