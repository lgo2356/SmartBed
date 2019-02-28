package com.smartbed.dples.smartbed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.OutputStream;

public class ColorLayout {
    Context context;
    private int CENTER_RADIUS = 35;
    private int CENTER_X;
    private int CENTER_Y;
    private int INTERVAL = 25;

    private final static byte HEADER = (byte)0xEA;
    private final static byte IN = (byte)0xAA;
    private final static byte OUT = (byte) 0xAB;
    //    private final static byte LED = 0x15;
    private final static byte WEIGHT = 0x16;
    private final static byte BODY_TEMP = 0x17;
    private final static byte RGB = 0x18;
    private final static byte LED_ON = 0x0F;
    private final static byte LED_OFF = 0x00;
    private final static byte TAIL = 0x5A;
    private final static byte FF = (byte)0xFF;
    private final static byte REQ = (byte)0xEE;

    public ColorLayout(Context context, LinearLayout colorLayout, Point display) {
        this.context = context;
        colorLayout.addView(new DrawView(context));
        colorLayout.setGravity(Gravity.CENTER);

        int scaleX = display.x / 480;
        int scaleY = display.y / 800;

        CENTER_RADIUS *= scaleX;
        CENTER_X = CENTER_RADIUS;
        CENTER_Y = CENTER_RADIUS;
        INTERVAL *= scaleX;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((CENTER_RADIUS*2+INTERVAL)*3-INTERVAL, (CENTER_RADIUS*2+INTERVAL)*3-INTERVAL);
        colorLayout.setLayoutParams(params);
    }

    private class DrawView extends View {
        private final int colorRed = getResources().getColor(R.color.colorRed);
        private final int colorGreen = getResources().getColor(R.color.colorGreen);
        private final int colorBlue = getResources().getColor(R.color.colorBlue);

        private final int colorJadeGreen = getResources().getColor(R.color.colorJungleGreen);
        private final int colorMintGreen = getResources().getColor(R.color.colorMint);
        private final int colorCornFlower = getResources().getColor(R.color.colorCornFlower);

        private final int colorBabyBlue = getResources().getColor(R.color.colorBabyBlue);
        private final int colorLightCoral = getResources().getColor(R.color.colorSolidPink);
        private final int colorWarmWhite = getResources().getColor(R.color.colorWarmWhite);

        private final int[] PAINT_COLOR = {colorRed, colorGreen, colorBlue, colorJadeGreen,
                colorMintGreen, colorCornFlower, colorBabyBlue, colorLightCoral, colorWarmWhite};
//        private final int[] PAINT_COLOR = {colorRed, colorGreen, colorBlue};

        Paint mPaint;
        Paint mPaintBorder;

        public DrawView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaintBorder = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float scaleX = getWidth() / 120f;
            float scaleY = getHeight() / 120f;

            // Black
            int i = 0;
            mPaint.setStyle(Paint.Style.FILL);
            mPaintBorder.setStyle(Paint.Style.STROKE);

            for(int col=0; col<3; col++) {
                for(int row=0; row<3; row++) {
                    mPaint.setColor(PAINT_COLOR[i]);
                    mPaintBorder.setColor(getResources().getColor(R.color.colorDarkGray));
                    mPaintBorder.setStrokeWidth(1);
                    canvas.drawCircle(CENTER_X + (CENTER_RADIUS*2+INTERVAL) * row, CENTER_Y + (CENTER_RADIUS*2+INTERVAL) * col, CENTER_RADIUS, mPaint);
                    canvas.drawCircle(CENTER_X + (CENTER_RADIUS*2+INTERVAL) * row, CENTER_Y + (CENTER_RADIUS*2+INTERVAL) * col, CENTER_RADIUS, mPaintBorder);
                    i += 1;
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            // (x - a)^2 + (y - b)^2 = r^2
            // (원의 중심, 원의 중심) (a, b)
            boolean inRed = Math.pow((event.getX() - CENTER_X), 2) + Math.pow((event.getY() - CENTER_Y), 2) <= Math.pow(CENTER_RADIUS, 2);
            boolean inGreen = Math.pow((event.getX() - (CENTER_X + (CENTER_RADIUS*2+INTERVAL))), 2) + Math.pow((event.getY() - CENTER_Y), 2) <= Math.pow(CENTER_RADIUS, 2);
            boolean inBlue = Math.pow((event.getX() - (CENTER_X + (CENTER_RADIUS*2+INTERVAL)*2)), 2) + Math.pow((event.getY() - CENTER_Y), 2) <= Math.pow(CENTER_RADIUS, 2);

            boolean inJadeGreen = Math.pow((event.getX() - CENTER_X), 2) + Math.pow((event.getY() - (CENTER_Y + (CENTER_RADIUS*2+INTERVAL))), 2) <= Math.pow(CENTER_RADIUS, 2);
            boolean inMint = Math.pow((event.getX() - (CENTER_X + (CENTER_RADIUS*2+INTERVAL))), 2) + Math.pow((event.getY() - (CENTER_Y + (CENTER_RADIUS*2+INTERVAL))), 2) <= Math.pow(CENTER_RADIUS, 2);
            boolean inCornFlower = Math.pow((event.getX() - (CENTER_X + (CENTER_RADIUS*2+INTERVAL)*2)), 2) + Math.pow((event.getY() - (CENTER_Y + (CENTER_RADIUS*2+INTERVAL))), 2) <= Math.pow(CENTER_RADIUS, 2);

            boolean inBabyBlue = Math.pow((event.getX() - CENTER_X), 2) + Math.pow((event.getY() - (CENTER_Y + (CENTER_RADIUS*2+INTERVAL)*2)), 2) <= Math.pow(CENTER_RADIUS, 2);
            boolean inLightCoral = Math.pow((event.getX() - (CENTER_X + (CENTER_RADIUS*2+INTERVAL))), 2) + Math.pow((event.getY() - (CENTER_Y + (CENTER_RADIUS*2+INTERVAL)*2)), 2) <= Math.pow(CENTER_RADIUS, 2);
            boolean inWarmWhite = Math.pow((event.getX() - (CENTER_X + (CENTER_RADIUS*2+INTERVAL)*2)), 2) + Math.pow((event.getY() - (CENTER_Y + (CENTER_RADIUS*2+INTERVAL)*2)), 2) <= Math.pow(CENTER_RADIUS, 2);

            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if(inRed) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(255, 0, 0);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inGreen) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(0, 255, 0);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inBlue) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(0, 0, 255);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inJadeGreen) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(11, 171, 35);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inMint) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(82, 231, 35);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inCornFlower) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(70, 130, 180);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inBabyBlue) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(137, 207, 240);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inLightCoral) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(255, 99, 71);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(inWarmWhite) {
                        if(ColorLayoutInfo.getInstance().isLedState()) {
                            setColor(255, 255, 60);
                        } else {
                            Toast.makeText(context, "조명을 켜주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
            return true;
        }

        private void setColor(int r, int g, int b) {
            ColorLayoutInfo.getInstance().setRGB(r, g, b);

            try {
                if(BluetoothObject.getInstance().getSocket().isConnected()) {
                    OutputStream outputStream = BluetoothObject.getInstance().getOutputStream();
                    int brightness = ColorLayoutInfo.getInstance().getBrightness();

                    Log.d("TAG", "R: " + r + " G: " + g + " B: " + b);

                    byte data[] = {HEADER, IN, RGB, LED_ON, (byte)g, (byte)r, (byte)b, 0x0D, (byte)brightness, 0x0A, TAIL};
                    outputStream.write(data);
                    outputStream.flush();
                }
            } catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "블루투스 연결해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
