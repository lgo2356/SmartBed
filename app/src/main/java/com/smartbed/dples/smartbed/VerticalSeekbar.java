package com.smartbed.dples.smartbed;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import java.util.jar.Attributes;

public class VerticalSeekbar extends AppCompatSeekBar {
    public VerticalSeekbar(Context context) {
        super(context);
    }

    public VerticalSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekbar(Context context, AttributeSet attr) {
        super(context, attr);
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(height, width, oldHeight, oldWidth);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);

        super.onDraw(canvas);
    }

    private OnSeekBarChangeListener onSeekBarChangeListener;

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    private int lastProgress = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()) {
            return false;
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onSeekBarChangeListener.onStartTrackingTouch(this);
                setPressed(true);
                setSelected(true);
                break;
            case MotionEvent.ACTION_MOVE:
                super.onTouchEvent(event);

                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());

                if(progress < 0) {
                    progress = 0;
                }

                if(progress > getMax()) {
                    progress = getMax();
                }

                setProgress(progress);

                if(progress != lastProgress) {
                    lastProgress = progress;
                    onSeekBarChangeListener.onProgressChanged(this, progress, true);
                }

                onSizeChanged(getWidth(), getHeight(), 0, 0);
                setPressed(true);
                setSelected(true);
                break;
            case MotionEvent.ACTION_UP:
                onSeekBarChangeListener.onStopTrackingTouch(this);
                setPressed(false);
                setSelected(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(event);
                setPressed(false);
                setSelected(false);
                break;
        }
        return true;
    }

    public synchronized void setMaximum(int maximum) {
        setMax(maximum);
    }

    public synchronized int getMaximum() {
        return getMax();
    }
}
