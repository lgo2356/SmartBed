package com.smartbed.dples.smartbed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.OutputStream;

public class ColorPicker {
    private int CENTER_RADIUS = 330;
    private int CENTER_X = 400;
    private int CENTER_Y = 400;

    private final static byte HEADER = (byte)0xEA;
    private final static byte IN = (byte)0xAA;
    private final static byte OUT = (byte) 0xAB;
    private final static byte WEIGHT = 0x16;
    private final static byte BODY_TEMP = 0x17;
    private final static byte RGB = 0x18;
    private final static byte LED_ON = 0x0F;
    private final static byte LED_OFF = 0x00;
    private final static byte TAIL = 0x5A;
    private final static byte FF = (byte)0xFF;
    private final static byte REQ = (byte)0xEE;

    Context context;
//    private int INTERVAL = 25;

    public ColorPicker(Context context, LinearLayout pickerLayout, Point display) {
        this.context = context;
        pickerLayout.addView(new DrawView(this.context));

        int displayX = display.x;
        int displayY = display.y;

        float scaleX = displayX / 1440.0f;
        float scaleY = displayY / 800.0f;
//

        CENTER_RADIUS *= scaleX;
        CENTER_X *= scaleX;
        CENTER_Y *= scaleX;
//        CENTER_X = CENTER_RADIUS;
//        CENTER_Y = CENTER_RADIUS;
//        INTERVAL *= scaleX;
        float laoutWidth = (CENTER_X*2) * 1.3f;
        float laoutHeight = (CENTER_Y*2) * 1.3f;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CENTER_X * 2, CENTER_Y * 2);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)laoutWidth, (int)laoutHeight);
        pickerLayout.setLayoutParams(params);
    }

    private class DrawView extends View {
        private final int[] mColors;
        Paint mPaint;
        Paint mPaint_color;

        public DrawView(Context context) {
            super(context);
            mColors = new int[] {0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint_color = new Paint(Paint.ANTI_ALIAS_FLAG);

            Shader shader = new SweepGradient(0, 0, mColors, null);
            mPaint.setShader(shader);

            mPaint_color.setStyle(Paint.Style.FILL);
            mPaint_color.setColor(getResources().getColor(R.color.colorAquaBlue));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dpTopx(30));
            mPaint.setColor(getResources().getColor(R.color.colorAquaBlue));

            canvas.translate(CENTER_X, CENTER_Y);
            canvas.drawCircle(0, 0, CENTER_RADIUS, mPaint);
            canvas.drawCircle(0, 0, CENTER_RADIUS/2, mPaint_color);
//            canvas.drawOval(new RectF(100, 100, 100, 100), mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inPicker = Math.hypot(x, y) <= CENTER_RADIUS;
            Log.d("TOUCH", "Is it in the color picker? " + inPicker);

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                case MotionEvent.ACTION_MOVE:
                    if(inPicker) {
                        float angle = (float)java.lang.Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle/(2*3.1415926f);
                        if (unit < 0) {
                            unit += 1;
                        }
                        mPaint_color.setColor(interruptColor(mColors, unit));
                        invalidate();
                        break;
                    }
            }

            return true;
        }

        public int dpTopx(int dp) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }

        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interruptColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int)p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];

            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            setColor(r, g, b);

            return Color.argb(a, r, g, b);
        }

        private void setColor(int r, int g, int b) {
            try {
                if(BluetoothObject.getInstance().getSocket().isConnected()) {
                    OutputStream outputStream = BluetoothObject.getInstance().getOutputStream();

                    byte data[] = {HEADER, IN, RGB, (byte)g, (byte)r, (byte)b, TAIL};
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
