package com.smartbed.dples.smartbed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MonitoringBackground {

    public MonitoringBackground(Context context, LinearLayout backgroundLayout, Point display) {
//        backgroundLayout.addView(new DrawView(context));
        backgroundLayout.setGravity(Gravity.CENTER);

    }

    private class DrawView extends View {
        Paint mPaint;

        public DrawView(Context context) {
            super(context);
            mPaint = new Paint();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(10.0F);
            mPaint.setColor(getResources().getColor(R.color.colorWhite));
            canvas.drawCircle(10, 10, 10, mPaint);
        }
    }
}
