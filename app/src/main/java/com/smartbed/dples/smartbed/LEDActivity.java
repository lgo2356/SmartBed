package com.smartbed.dples.smartbed;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LEDActivity extends AppCompatActivity {
    private boolean onOffState = false;

    // 프로토콜 셋
    // byte data[] = {(byte)0xEA, (byte)0xAA, 0x15, (byte)0xFF, (byte)0xFF, 0x0F, 0x5A};
    private final static byte HEADER = (byte)0xEA;
    private final static byte IN = (byte)0xAA;
    private final static byte OUT = (byte) 0xAB;
//    private final static byte LED = 0x15;
    private final static byte WEIGHT = 0x16;
    private final static byte BODY_TEMP = 0x17;
    private final static byte RGB = 0x18;
    private final static byte LED_ON = 0x0F;
    private final static byte LED_OFF = 0x0D;
    private final static byte TAIL = 0x5A;
    private final static byte FF = (byte)0xFF;
    private final static byte REQ = (byte)0xEE;

    SeekBar brightSeekbar = null;
    Switch switchOnOff = null;

    private ColorPicker colorPicker;
    private ThreadStream threadStream;
    private ThreadGarbagePacket threadGarbagePacket;
    private boolean threadStartFlag = false;

    private ExecutorService mReadExecutor;
    private ExecutorService mWriteExecutor;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        mReadExecutor = Executors.newSingleThreadExecutor();
        mWriteExecutor = Executors.newSingleThreadExecutor();
        outputStream = BluetoothObject.getInstance().getOutputStream();

        LinearLayout colorLayout = (LinearLayout) findViewById(R.id.colorLayout);
        LinearLayout lightIcon01 = (LinearLayout) findViewById(R.id.lightIcon01);
        LinearLayout lightIcon02 = (LinearLayout) findViewById(R.id.lightIcon02);

        brightSeekbar = (SeekBar) findViewById(R.id.horizontalSeekbar);
        switchOnOff = (Switch) findViewById(R.id.switchOnOff);

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    LEDOn();
                } else {
                    LEDOff();
                }
            }
        });

        // 색상표 만들기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        new ColorLayout(getApplicationContext(), colorLayout, size);
//        colorPicker = new ColorPicker(getApplicationContext(), colorLayout, size);

        new LightIcon(getApplicationContext(), lightIcon01, false, size);
        new LightIcon(getApplicationContext(), lightIcon02, true, size);

        // Output, Input Stream thread
        threadGarbagePacket = new ThreadGarbagePacket();
//        threadGarbagePacket.start();

        // 밝기 조절 시크바 조작
        brightSeekbar.setThumb(null);
        brightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int curProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                curProgress = progress + 1;

                if(switchOnOff.isChecked()) {
                    brightnessControl(curProgress);
                    Log.d("LED", "Brightness: " + curProgress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reqLEDState();
        stayForResponse();
    }

    private void reqLEDState() {
        byte data[] = {HEADER, OUT, RGB, REQ, 0, 0, 0, 0, 0x0D, 0x0A, TAIL};
//        threadStream.write(data);
        write(data);
    }

    // 저장된 밝기 값을 전송하기
    private void LEDOn() {
        if(!threadStartFlag) {
            threadGarbagePacket.start();
            threadStartFlag = true;
        }

        final int colorRed = ColorLayoutInfo.getInstance().getRed();
        final int colorGreen = ColorLayoutInfo.getInstance().getGreen();
        final int colorBlue = ColorLayoutInfo.getInstance().getBlue();
        final int brightness = ColorLayoutInfo.getInstance().getBrightness();
        final byte[] dataPacket = {HEADER, IN, RGB, LED_ON, (byte) colorGreen, (byte) colorRed, (byte) colorBlue, (byte) brightness, 0x0A, TAIL};

        ColorLayoutInfo.getInstance().setOnOff(true);
        ColorLayoutInfo.getInstance().setRGB(colorRed, colorGreen, colorBlue);
        this.write(dataPacket);
    }

    // LED 끌 때 밝기 값 저장하기
    private void LEDOff() {
        if(!threadStartFlag) {
            threadGarbagePacket.start();
            threadStartFlag = true;
        }

        byte[] dataPacket = {HEADER, IN, RGB, LED_OFF, 0x0F, 0x0F, 0x0F, 0x0F, 0x0A, TAIL};

        ColorLayoutInfo.getInstance().setOnOff(false);
        this.write(dataPacket);
    }

    private void brightnessControl(final int brightness) {
        if(switchOnOff.isChecked()) {
            final int r = ColorLayoutInfo.getInstance().getRed();
            final int g = ColorLayoutInfo.getInstance().getGreen();
            final int b = ColorLayoutInfo.getInstance().getBlue();
            final byte[] brightnessPacket = {HEADER, IN, RGB, LED_ON, (byte) g, (byte) r, (byte) b, (byte) brightness, 0x0A, TAIL};

            ColorLayoutInfo.getInstance().setBrightness(brightness);
            this.write(brightnessPacket);

            Log.d("TAG", "R: " + r + " G: " + g + " B: " + b + "Brightness: " + brightness);
        }
    }

    private void stayForResponse() {
        byte[] packet = {HEADER, OUT, RGB, REQ, 0, 0, 0, 0, 0x0A, TAIL};

        threadStream = new ThreadStream();
        threadStream.setPacket(packet);
        threadStream.start();
    }

    private void write(final byte[] buffer) {
        mWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream.write(buffer);
                    outputStream.flush();
                } catch (IOException e) { }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        threadStream.interrupt();
        threadGarbagePacket.interrupt();
        threadStartFlag = false;
    }

    private class ThreadStream extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final DataInputStream dataInputStream;
        private final DataOutputStream dataOutputStream;
        private byte[] packet;
        private byte[] uiPacket;

        public ThreadStream() {
            OutputStream tmpOut = BluetoothObject.getInstance().getOutputStream();
            InputStream tmpIn = BluetoothObject.getInstance().getInputStream();

            outputStream = tmpOut;
            inputStream = tmpIn;
            dataOutputStream = new DataOutputStream(outputStream);
            dataInputStream = new DataInputStream(inputStream);
        }

        public void run() {
            final byte[] readBuffer = new byte[1024];
            final byte[] packetBytes = new byte[9];
            int readCount = 0;
            int packetCount = 0;
            byte b = 0;

            while(!Thread.currentThread().isInterrupted()) {
                try {
                    int byteAvailable = dataInputStream.available();
                    Log.d("LED", "Available: " + byteAvailable);

                    if(byteAvailable > 0) {
                        while(b != 10) {
                            b = dataInputStream.readByte();
                            readBuffer[readCount] = b;
                            Log.d("LED", "Stream byte: " + b);
                            readCount += 1;

                            if(dataInputStream.available() == 0) {
                                Log.d("LED", "Available:" + dataInputStream.available());

                                threadStream.write(packet);
                                readCount = 0;
                            }
                        }

                        while(packetCount <= 8) {
                            b = dataInputStream.readByte();
                            packetBytes[packetCount] = b;
                            packetCount += 1;
                        }

                        packetCount = 0;

                        if(packetBytes[0] == -22 && packetBytes[8] == 90) {
                            for(byte bytes : packetBytes) {
                                Log.d("LED", "Get UI byte: " + bytes);
                            }

                            setUI(packetBytes);

                            while(dataInputStream.available() > 0) {
                                byte garbageByte = dataInputStream.readByte();
                                Log.d("LED", "Stream thread garbage byte: " + garbageByte);
                            }

                            interrupt();
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] packet) {
            mWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(packet);
                    } catch (IOException e) { }
                }
            });
        }

        private void setUI(final byte[] packet) {
            for(byte bytes : packet) {
                Log.d("LED", "Setting UI packet byte: " + bytes);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(packet[3] == 15) {
                        switchOnOff.setChecked(true);
                    } else {
                        switchOnOff.setChecked(false);
                    }

                    brightSeekbar.setProgress(packet[7]);
                }
            });
        }

        public void setPacket(byte[] packet) {
            this.packet = packet;

            for(byte bytes : packet) {
                Log.d("SET SEND PACKET", "" + bytes);
            }
        }
    }

    private class ThreadGarbagePacket extends Thread {
        private final InputStream inputStream;
        private final DataInputStream dataInputStream;

        public ThreadGarbagePacket() {
            InputStream tmpIn = BluetoothObject.getInstance().getInputStream();

            inputStream = tmpIn;
            dataInputStream = new DataInputStream(inputStream);
        }

        public void run() {
            byte b;

            while(!Thread.currentThread().isInterrupted()) {
                try {
                    int byteAvailable = dataInputStream.available();

                    if(byteAvailable > 0) {
                        b = dataInputStream.readByte();
                        Log.d("LED", "Garbage thread garbage byte: " + b);
                    }
                } catch (IOException e) { }
            }
        }
    }
}
