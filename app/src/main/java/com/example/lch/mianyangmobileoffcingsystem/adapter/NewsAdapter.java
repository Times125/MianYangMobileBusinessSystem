package com.example.lch.mianyangmobileoffcingsystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.callback.ItemTouchMoveListener;
import com.example.lch.mianyangmobileoffcingsystem.callback.StartDragListener;
import com.example.lch.mianyangmobileoffcingsystem.config.BaseApplication;
import com.example.lch.mianyangmobileoffcingsystem.main.activity.OpenNewsActivity;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.lch.mianyangmobileoffcingsystem.tools.Constants.colors;

/**
 * Created by lch on 2017/3/18.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements ItemTouchMoveListener {

    private Context context;
    private List<String> titlelist;
    private List<String> contentlist;
    private StartDragListener dragListener;

    private static final String TAG = "NewsAdapter";
    public NewsAdapter(Context context, List<String> titlelist, List<String> contentlist,StartDragListener dragListener) {
        this.context = context;
        this.titlelist = titlelist;//就是url链接
        this.contentlist = contentlist;
        this.dragListener = dragListener;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int seed = getRandom();
        GradientDrawable background = (GradientDrawable) holder.linearLayout.getBackground();
        background.setColor(BaseApplication.getContext().getResources().getColor(colors[seed]));
        holder.content.setText(contentlist.get(position));


        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dragListener.onStartDrag(holder);
                return false;
            }
        });

        /*holder.content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 执行开始拖动动画
                    dragListener.onStartDrag(holder);
                }
                return false;
            }
        });*/
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.recyclerview_item_click_anim);
                holder.linearLayout.startAnimation(animation);
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, OpenNewsActivity.class);
                intent.putExtra("url",titlelist.get(position));
                context.startActivity(intent);
            }
        });


    }
    /**
     * 当Item上下拖动会调用该方法
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(contentlist, fromPosition, toPosition);
        Collections.swap(titlelist, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 当Item左右滑动时调用该方法
     *
     * @param position
     * @return
     */
    @Override
    public boolean onItemRemove(int position) {
        contentlist.remove(position);
        titlelist.remove(position);
        notifyItemRemoved(position);

        return true;
    }
    @Override
    public int getItemCount() {
        return contentlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View myView;
        TextView content;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            linearLayout = (LinearLayout) itemView.findViewById(R.id.news_item);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }


    //根据随机数0-6换色
    private int getRandom() {
        Random random = new Random();
        int len = random.nextInt(6);
        return len;
    }


}
