package com.smartbed.dples.smartbed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LightIcon {
    private int CENTER_RADIUS;
    private int CENTER_X;
    private int CENTER_Y;
    private int EMPTY_AREA = 0;
    private boolean isDraw = false;

    public LightIcon(Context context, LinearLayout layout, boolean isDraw, Point size) {
        Log.d("TAG", ""+size.x);
        float scaleX = size.x / 1080;
        Log.d("TAG", ""+scaleX);
        CENTER_RADIUS = (int) (15.0f * scaleX);
        CENTER_X = CENTER_RADIUS * 2 + 10;
        CENTER_Y = CENTER_RADIUS * 2 + 10;

        layout.addView(new DrawView(context));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CENTER_X*2 + EMPTY_AREA, CENTER_Y*2 + EMPTY_AREA);
        layout.setLayoutParams(params);

        this.isDraw = isDraw;
    }

    private class DrawView extends View {
        Paint mPaint;
        Paint mPaint_stroke;
        Paint mPaint_sunLine;

        float rectangle_halfLength = (CENTER_X*2 + EMPTY_AREA)/2;
        float rectangle_Length = (CENTER_X*2 + EMPTY_AREA);
        float startX = (CENTER_RADIUS + CENTER_RADIUS * 0.22f) * toFloat(Math.cos(Math.PI/4));
        float startY = (CENTER_RADIUS + CENTER_RADIUS * 0.22f) * toFloat(Math.sin(Math.PI/4));
        float stopX = (CENTER_RADIUS + CENTER_RADIUS * 0.22f + CENTER_RADIUS*0.7f) * toFloat(Math.cos(Math.PI/4));
        float stopY = (CENTER_RADIUS + CENTER_RADIUS * 0.22f + CENTER_RADIUS*0.7f) * toFloat(Math.sin(Math.PI/4));

        public DrawView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint_stroke = new Paint();
            mPaint_sunLine = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(getResources().getColor(R.color.colorDarkGray));

            mPaint_stroke.setStyle(Paint.Style.STROKE);
            mPaint_stroke.setColor(getResources().getColor(R.color.colorDarkGray));
            mPaint_stroke.setStrokeWidth(6);

            mPaint_sunLine.setStyle(Paint.Style.FILL);
            mPaint_sunLine.setStrokeWidth(6);
            mPaint_sunLine.setColor(getResources().getColor(R.color.colorDarkGray));

            if(isDraw) {
                drawsunLine(canvas);
            } else {
//                canvas.drawCircle(rectangle_halfLength, rectangle_halfLength, CENTER_RADIUS, mPaint);
            }

            canvas.drawCircle(rectangle_halfLength, rectangle_halfLength, CENTER_RADIUS, mPaint_stroke);
        }

        private void drawsunLine(Canvas canvas) {
            // 직선
            canvas.drawLine(CENTER_X, CENTER_RADIUS/4, CENTER_X, CENTER_RADIUS, mPaint_sunLine);
//            canvas.drawLine(38.8908f, 38.8908f, 63.6396f, 63.6396f, mPaint_sunLine);

            // 대각선
            canvas.drawLine(startX, startY, stopX, stopY, mPaint_sunLine);

            // 직선
            canvas.drawLine(CENTER_RADIUS/4, CENTER_Y, CENTER_RADIUS, CENTER_Y, mPaint_sunLine);

            // 대각선
            canvas.drawLine(startX, rectangle_Length - startY, stopX, rectangle_Length - stopY, mPaint_sunLine);

            // 직선
            canvas.drawLine(CENTER_X, CENTER_Y*2 - CENTER_RADIUS/4, CENTER_X, CENTER_Y*2 - CENTER_RADIUS, mPaint_sunLine);

            // 대각선
            canvas.drawLine(rectangle_Length - startX, rectangle_Length - startY,
                    rectangle_Length - stopX, rectangle_Length - stopY, mPaint_sunLine);

            // 직선
            canvas.drawLine(CENTER_X*2 - CENTER_RADIUS/4, CENTER_Y, CENTER_X*2 - CENTER_RADIUS, CENTER_Y, mPaint_sunLine);

            // 대각선
            canvas.drawLine(rectangle_Length - startX, startY, rectangle_Length - stopX, stopY, mPaint_sunLine);
        }

        private float toFloat(double value) {
            return (float) value;
        }
    }
}
