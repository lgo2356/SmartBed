package com.smartbed.dples.smartbed;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable IconImage;
    private String deviceName;
    private Drawable infoImage;
    private boolean btnState;

    private static class LazyHolder {
        private static final ListViewItem Instance = new ListViewItem();

        private LazyHolder() {

        }
    }

    public ListViewItem() {
        this.IconImage = null;
        this.deviceName = null;
    }

    public static ListViewItem getInstance() {
        return LazyHolder.Instance;
    }

    // SET
    public void setIconImage(Drawable iconImage) {
        this.IconImage = iconImage;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setInfoImage(Drawable infoImage) {
        this.infoImage = infoImage;
    }

    public void setButtonState(boolean state) {
        this.btnState = state;
    }

    // GET
    public Drawable getIconImage() {
        return this.IconImage;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public Drawable getInfoImage() {
        return this.infoImage;
    }

    public boolean isBtnState() {
        return this.btnState;
    }
}
