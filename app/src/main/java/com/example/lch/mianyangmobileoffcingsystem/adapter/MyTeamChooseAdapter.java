package com.example.lch.mianyangmobileoffcingsystem.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.tools.AsyncImageLoader;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;

import java.util.List;

/**
 * Created by lch on 2017/3/18.
 */

public class MyTeamChooseAdapter extends BaseAdapter {
    private AsyncImageLoader asyncImageLoader;
    private ListView listView;
    private List<String> teamlist;
    private List<String> idlist;
    private List<String> headList;
    private int resId;
    private Context context;
    private int selectedItem = -1;
    private static final String TAG = "MyTeamChooseAdapter";
    public MyTeamChooseAdapter(Context context, List<String> teamlist, List<String> idList, int resId, List<String> headList, ListView listView) {
        this.context = context;
        this.teamlist = teamlist;
        this.idlist = idList;
        this.headList = headList;
        this.resId = resId;
        this.listView = listView;
        asyncImageLoader = new AsyncImageLoader();
    }

    @Override
    public int getCount() {
        return teamlist.size();
    }

    @Override
    public Object getItem(int position) {
        return teamlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contentView;
        ViewHolder holder;
        if (convertView == null) {
            contentView = LayoutInflater.from(context).inflate(resId,parent,false);
            holder = new ViewHolder();
            holder.circleImageView = (CircleImageView) contentView.findViewById(R.id.team_head_view);
            holder.textView = (TextView) contentView.findViewById(R.id.choose_team_tv);
            holder.textView2 = (TextView) contentView.findViewById(R.id.choose_team_tv2);
            holder.imageView = (ImageView) contentView.findViewById(R.id.choose_team_iv);
            contentView.setTag(holder);
        } else {
            contentView = convertView;
            holder = (ViewHolder) contentView.getTag();
        }
        String url = headList.get(position);
        holder.circleImageView.setTag(url);
        Drawable drawable = asyncImageLoader.loadDrawable(url, new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                    imageViewByTag.setImageDrawable(imageDrawable);
                }
            }
        });
        if (drawable == null) {
            holder.circleImageView.setImageResource(R.drawable.nim_avatar_default);
        }else {
            holder.circleImageView.setImageDrawable(drawable);
        }
        //holder.circleImageView.setImageResource(R.mipmap.ic_advanced_team);


        holder.textView.setText(teamlist.get(position));
        holder.textView2.setText(idlist.get(position));
        //holder.circleImageView.setImageResource(headList.get(position));
        if (selectedItem == position) {
            Log.e(TAG, "getView: " +(position == selectedItem));
            holder.imageView.setImageResource(R.drawable.nim_contact_checkbox_checked_green);
        }else {
            holder.imageView.setImageResource(R.drawable.nim_contact_checkbox_unchecked);
        }
        return contentView;
    }
    class ViewHolder{
        CircleImageView circleImageView;
        TextView textView;
        TextView textView2;
        ImageView imageView;
    }
}
