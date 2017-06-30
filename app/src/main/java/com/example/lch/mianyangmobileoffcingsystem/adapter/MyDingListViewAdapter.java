package com.example.lch.mianyangmobileoffcingsystem.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.config.BaseApplication;
import com.example.lch.mianyangmobileoffcingsystem.tools.AsyncImageLoader;
import com.example.lch.mianyangmobileoffcingsystem.tools.Constants;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by lch on 2017/3/17.
 */

public class MyDingListViewAdapter extends BaseAdapter {

    private AsyncImageLoader asyncImageLoader;
    private Context context;
    private int resId;
    private ListView listView;
    private List<String> res;//头像资源文件
    private List<String> namelist;//公告发布者姓名
    private List<Long> timelist;//公告发布的时间
    private List<String> contentlist;//公告内容
    private List<String> titlelist;//公告里面的url
    SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String time ;

    public MyDingListViewAdapter(Context context, int resId, List<String> res,
                                 List<String> namelist, List<Long> timelist,
                                 List<String> contentlist, List<String> titlelist,ListView listView) {
        this.context = context;
        this.resId = resId;
        this.res = res;
        this.namelist = namelist;
        this.timelist = timelist;
        this.contentlist = contentlist;
        this.titlelist = titlelist;
        this.listView = listView;
        asyncImageLoader = new AsyncImageLoader();
    }

    @Override
    public int getCount() {
        return namelist.size();
    }

    @Override
    public Object getItem(int position) {
        return namelist.get(position);
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
            holder.linearLayout = (LinearLayout) contentView.findViewById(R.id.ding_ding_layout);
            holder.headView = (ImageView) contentView.findViewById(R.id.ding_headimage);
            holder.dingName = (TextView) contentView.findViewById(R.id.ding_name1);
            holder.dingTime = (TextView) contentView.findViewById(R.id.ding_time1);
            holder.title = (TextView) contentView.findViewById(R.id.ding_title_text);
            holder.content = (TextView) contentView.findViewById(R.id.ding_content_text);
            contentView.setTag(holder);
        } else {
            contentView = convertView;
            holder = (ViewHolder) contentView.getTag();
        }
        int seed = getRandom();
        GradientDrawable background = (GradientDrawable) holder.linearLayout.getBackground();
        background.setColor(BaseApplication.getContext().getResources().getColor(Constants.colors[seed]));
        String url = res.get(position);
        holder.headView.setTag(url);
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
            holder.headView.setImageResource(R.drawable.nim_avatar_default);
        }else {
            holder.headView.setImageDrawable(drawable);
        }
        time = spf.format(timelist.get(position) * 1000);
        holder.headView.setImageResource(R.drawable.nim_avatar_default);
        holder.dingName.setText(namelist.get(position));
        holder.dingTime.setText(time);
        holder.title.setText(titlelist.get(position));
        holder.content.setText(contentlist.get(position));
        return contentView;
    }

    class ViewHolder {
        LinearLayout linearLayout;
        ImageView headView;
        TextView dingName;
        TextView dingTime;
        TextView title;
        TextView content;
    }
    //根据随机数0-6换色
    private int getRandom() {
        Random random = new Random();
        int len = random.nextInt(6);
        return len;
    }
}
