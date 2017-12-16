package com.jin.honeywell.transmitter.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jin.honeywell.transmitter.R;
import com.jin.honeywell.transmitter.util.ViewHolderUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jin on 14/08/2017.
 */

public class ScanningDeviceListAdapter extends BaseAdapter {

    private Context mContext = null;
    private List<String> mModelNameList;

    public ScanningDeviceListAdapter(Context ctx, List<String> modelNames) {
        mContext = ctx;
        mModelNameList = modelNames;
    }

    @Override
    public int getCount() {
        return mModelNameList == null ? 0 : mModelNameList.size();
    }

    @Override
    public String getItem(int position) {
        return mModelNameList == null ? null : mModelNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item_manual_select, null);
        }
        TextView mModelNameTextView = ViewHolderUtil.get(convertView, R.id.list_item_model_name_tv);
        mModelNameTextView.setText(mModelNameList.get(position));
        return convertView;
    }
}
