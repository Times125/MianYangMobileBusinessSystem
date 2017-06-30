package com.example.lch.mianyangmobileoffcingsystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;

import java.util.List;

/**
 * Created by lch on 2017/3/16.
 */

public class MyListViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> des;
    private List<Integer> res;
    private int resId;
    private static final String TAG = "MyListViewAdapter";
    public MyListViewAdapter(Context context, List<String> des, List<Integer> res, int resId) {
        this.context = context;
        this.des = des;
        this.res = res;
        this.resId = resId;
    }

    @Override
    public int getCount() {
        return des.size();
    }

    @Override
    public Object getItem(int position) {
        return des.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View contentView;
        ViewHolder holder;
        if (convertView == null) {
            contentView = LayoutInflater.from(context).inflate(resId, parent, false);
            holder = new ViewHolder();
            holder.left = (ImageView) contentView.findViewById(R.id.left);
            holder.destxt = (TextView) contentView.findViewById(R.id.describe);
            contentView.setTag(holder);
            Log.d(TAG, "getView: ================ null");
        }else {
            contentView = convertView;
            holder = (ViewHolder) contentView.getTag();
        }
        holder.left.setImageResource(res.get(position));
        holder.destxt.setText(des.get(position));
        return contentView;
    }
    class ViewHolder{
        ImageView left;
        TextView destxt;
    }
}
