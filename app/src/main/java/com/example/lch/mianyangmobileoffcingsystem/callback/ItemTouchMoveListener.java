package com.example.lch.mianyangmobileoffcingsystem.callback;

/**
 * Created by lch on 2017/3/18.
 */

public interface ItemTouchMoveListener {

    /**
     * 当Item上下拖动时调用
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * 当item左右滑动时调用
     */
    boolean onItemRemove(int position);
}

