package com.smartbed.dples.smartbed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter implements View.OnClickListener{
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();
    private ImageView iconImage = null;
    private ImageButton btnInfo = null;
    private ListViewItem listViewItem = null;
    private Context context = null;

    public interface ListButtonClickListener {
        void onListButtonClick(int position);
    }

    private ListButtonClickListener listButtonClickListener;
    private int resourceId = 0;

//    public ListViewAdapter(Context context, int resource, ArrayList<ListViewItem> list, ListButtonClickListener clickListener) {
//        super(context, resource, list);
//        this.resourceId = resource;
//        this.listButtonClickListener = clickListener;
//    }
    public ListViewAdapter(ListButtonClickListener clickListener) {
        this.listButtonClickListener = clickListener;
    }


    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        context = parent.getContext();
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_layout, parent, false);
        }

        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        btnInfo = (ImageButton) convertView.findViewById(R.id.btnInfo);
        iconImage = (ImageView) convertView.findViewById(R.id.listIcon);

        listViewItem = listViewItemList.get(position);

        btnInfo.requestFocus();
        btnInfo.setFocusable(false);
        btnInfo.setTag(position);
        btnInfo.setOnClickListener(this);

        iconImage.setImageDrawable(listViewItem.getIconImage());
        deviceName.setText(listViewItem.getDeviceName());
        btnInfo.setImageDrawable(listViewItem.getInfoImage());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListViewItem getItem(int position) {
        if(listViewItemList.size() > 0) {
            return listViewItemList.get(position);
        }
        return null;
    }

    public void addItem(String deviceName, Drawable icon, Drawable infoImg) {
        ListViewItem item = new ListViewItem();

        item.setDeviceName(deviceName);
        item.setIconImage(icon);
        item.setInfoImage(infoImg);

        listViewItemList.add(item);
    }

    public void removeItem(int index) {
        if(listViewItemList.contains(getItem(index))) {
            Log.d("TAG", "Remove the follow item: " + getItem(index).getDeviceName());
            listViewItemList.remove(index);
        }
    }

    public void clearItem() {
        for(int i=0; i<listViewItemList.size(); i++) {
            listViewItemList.remove(i);
        }
    }

    public void setIcon(int position, boolean state) {
        if(state) {
            getItem(position).setIconImage(ContextCompat.getDrawable(context, R.drawable.bluetooth_connected_state));
        } else {
            getItem(position).setIconImage(ContextCompat.getDrawable(context, R.drawable.bluetooth_disconnected_state));
        }

    }

    public void setEnable(boolean state) {
        if(state) {
            listViewItem.setButtonState(state);
        } else {
            listViewItem.setButtonState(state);
        }
    }

    public void onClick(View view) {
        if(this.listButtonClickListener != null) {
            this.listButtonClickListener.onListButtonClick((int)view.getTag());
        }
    }

    // 대화상자 정의
//    public void dialogShow() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.bluetooth_info_dialog_layout, null);
//    }
}
