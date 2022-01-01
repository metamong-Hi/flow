package com.example.myapplication.adapter;

import android.content.Context;
import com.example.myapplication.data.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class CallAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Call> callList;

    public CallAdapter(Context context, ArrayList<Call> data) {
        mContext = context;
        callList = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return callList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Call getItem(int position) {
        return callList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.list_item, null);

        ImageView callImg = (ImageView)view.findViewById(R.id.call_image);
        TextView callName = (TextView)view.findViewById(R.id.call_name);
        TextView callPhoneNum = (TextView)view.findViewById(R.id.call_phoneNum);

        callImg.setImageResource(callList.get(position).getUserImg());
        callName.setText(callList.get(position).getName());
        callPhoneNum.setText(callList.get(position).getPhoneNum());

        return view;
    }
}