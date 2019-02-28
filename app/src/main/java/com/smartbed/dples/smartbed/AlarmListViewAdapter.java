package com.smartbed.dples.smartbed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AlarmListViewAdapter extends BaseAdapter {
    private ArrayList<AlarmListViewItem> listViewItemList = new ArrayList<AlarmListViewItem>();
    private AlarmListViewItem alarmListViewItem;

    public AlarmListViewAdapter() {

    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alarm_listview_layout, parent, false);
        }

        alarmListViewItem = listViewItemList.get(position);

        TextView labelText = (TextView) convertView.findViewById(R.id.label);
        TextView nameText = (TextView) convertView.findViewById(R.id.name);

        labelText.setText(alarmListViewItem.getLabelText());
        nameText.setText(alarmListViewItem.getNameText());

        return convertView;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public AlarmListViewItem getItem(int position) {
        if(listViewItemList.size() > 0) {
            return listViewItemList.get(position);
        }
        return null;
    }

    public void addItem(String label, String name) {
        AlarmListViewItem item = new AlarmListViewItem();

        item.setLabelText(label);
        item.setNameText(name);
        listViewItemList.add(item);
    }
}
