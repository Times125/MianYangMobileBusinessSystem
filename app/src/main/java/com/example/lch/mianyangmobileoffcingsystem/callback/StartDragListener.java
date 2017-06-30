package com.example.lch.mianyangmobileoffcingsystem.callback;

/**
 * Created by lch on 2017/3/18.
 */

import android.support.v7.widget.RecyclerView.ViewHolder;

public interface StartDragListener {
    /**
     * 开始拖动
     * @param viewHolder
     */
    public void onStartDrag(ViewHolder viewHolder);

}