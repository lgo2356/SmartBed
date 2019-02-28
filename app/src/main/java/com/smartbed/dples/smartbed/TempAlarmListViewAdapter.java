package com.smartbed.dples.smartbed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class TempAlarmListViewAdapter extends BaseAdapter implements View.OnClickListener {
    private ArrayList<TempAlarmListViewItem> listViewItemList = new ArrayList<>();
    private TempAlarmListViewItem tempAlarmListViewItem;

    public interface ListButtonClickListener {
        void onListButtonClick(int position);
    }

    private ListButtonClickListener listButtonClickListener;

    public TempAlarmListViewAdapter(ListButtonClickListener clickListener) {
        this.listButtonClickListener = clickListener;
    }

    public void onClick(View view) {
        if(this.listButtonClickListener != null) {
            this.listButtonClickListener.onListButtonClick((int)view.getTag());
        }
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        tempAlarmListViewItem = listViewItemList.get(position);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alarm_list_listview_layout, parent, false);
        }

        TextView tempInfoText = convertView.findViewById(R.id.textTemp);
        TextView musicInfoText = convertView.findViewById(R.id.textAlarmRing);
        Switch switchOnOff = convertView.findViewById(R.id.switchOnOff);

        switchOnOff.requestFocus();
        switchOnOff.setFocusable(false);
        switchOnOff.setTag(position);
        switchOnOff.setOnClickListener(this);

        tempInfoText.setText(tempAlarmListViewItem.getTempInfo());
        musicInfoText.setText(tempAlarmListViewItem.getMusicInfo());

        return convertView;
    }

    @Override
    public TempAlarmListViewItem getItem(int position) {
        if(listViewItemList.size() > 0) {
            return listViewItemList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    public void addItem(String tempInfo, String musicInfo) {
        TempAlarmListViewItem item = new TempAlarmListViewItem();
        item.setTempInfo(tempInfo);
        item.setMusicInfo(musicInfo);

        listViewItemList.add(item);
    }
}
