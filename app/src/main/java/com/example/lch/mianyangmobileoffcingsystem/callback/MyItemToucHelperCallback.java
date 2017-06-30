package com.example.lch.mianyangmobileoffcingsystem.callback;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.lch.mianyangmobileoffcingsystem.R;

import java.util.Random;

/**
 * Created by lch on 2017/3/18.
 */

public class MyItemToucHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchMoveListener mItemTouchMoveListener;

    public MyItemToucHelperCallback(ItemTouchMoveListener listener) {
        this.mItemTouchMoveListener = listener;
    }

    /**
     * 设置RecyclerView支持拖动和滑动的方向
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        //支持上下拖动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        //支持左右滑动
        int swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        int flags = makeMovementFlags(dragFlags, swipeFlag);
        return flags;
    }

    /**
     * 当上下拖动的时候调用该方法
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            // 当item的类型不一样的时候不能交换
            return false;
        }

        boolean result = mItemTouchMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//        boolean result = mItemTouchMoveListener.onItemMove(viewHolder.getLayoutPosition(), target.getLayoutPosition());

        return result;
    }

    /**
     * 当左右滑动的时候调用该方法
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mItemTouchMoveListener.onItemRemove(viewHolder.getAdapterPosition());
    }

    /**
     * 选中状态改变时监听
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            int[] color = {R.color.red, R.color.orange, R.color.yellow, R.color.green,
                    R.color.blue, R.color.purple, R.color.deepbluesky};
            Random random = new Random();
            int seed = random.nextInt(6);
            //不是空闲状态
            //viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(color[seed]));
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 恢复item状态
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundResource(R.drawable.news_item_style);
        super.clearView(recyclerView, viewHolder);
    }

    /**
     * holde ItemView绘制，属性动画
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //在左右滑动时，让item的透明度随着移动而改变，并缩放
        float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setScaleX(alpha);
            viewHolder.itemView.setScaleY(alpha);

        }
        //防止item复用出现问题，如果大家不理解下面这段代码，可以自行注释效果，
        if (alpha <= 0) {
            viewHolder.itemView.setAlpha(1);
            viewHolder.itemView.setScaleX(1);
            viewHolder.itemView.setScaleY(1);

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}