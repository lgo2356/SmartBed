package com.smartbed.dples.smartbed;

import android.util.Log;

public class ColorLayoutInfo {
    private int red = 255;
    private int green = 255;
    private int blue = 60;
    private int brightness = 100;
    private boolean ledState = false;

    private static class LazyHolder {
        private static final ColorLayoutInfo INSTANCE = new ColorLayoutInfo();

        private LazyHolder() {

        }
    }

    private ColorLayoutInfo() {

    }

    public static ColorLayoutInfo getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void setOnOff(boolean state) {
        ledState = state;
    }

    public void setRGB(int r, int g, int b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isLedState() {
        return ledState;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getBrightness() {
        return brightness;
    }
}
