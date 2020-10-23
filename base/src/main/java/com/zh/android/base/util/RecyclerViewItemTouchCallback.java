package com.zh.android.base.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author wally
 * @date 2020/10/23
 * RecyclerView拽托封装
 */
public class RecyclerViewItemTouchCallback extends ItemTouchHelper.Callback {
    private final OnItemMoveCallback mOnItemMoveCallback;

    public RecyclerViewItemTouchCallback(OnItemMoveCallback onItemMoveCallback) {
        this.mOnItemMoveCallback = onItemMoveCallback;
    }

    /**
     * 设置支持滑动的方向
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        int swipeFlags = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //网格允许上下左右
        if (layoutManager instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 是否长按后才进行操作拽托操作
     */
    @Override
    public boolean isLongPressDragEnabled() {
        super.isLongPressDragEnabled();
        return true;
    }

    /**
     * 是否支持条目滑动，我们只需要拽托，所以这个滑动我们不需要
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        super.isItemViewSwipeEnabled();
        //不支持Item滑动
        return false;
    }

    /**
     * 发生拽托时，回调该方法，返回true、false决定是否可以拽托，可以使用target参数判断条目的ViewType来决定
     */
    @Override
    public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
        if (mOnItemMoveCallback != null) {
            boolean isCanMove = mOnItemMoveCallback.isCanMove(current, target);
            //如果不能拖，直接拦截
            if (!isCanMove) {
                return false;
            }
        }
        return super.canDropOver(recyclerView, current, target);
    }

    /**
     * 条目发生交换时回调
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //Item交换回调，交换adapter的数据
        if (mOnItemMoveCallback != null) {
            mOnItemMoveCallback.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    /**
     * 当滑动时回调，我们只有拽托，所以不处理
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //不支持滑动，不做处理
    }

    /**
     * 提供给外部实现的接口
     */
    public interface OnItemMoveCallback {
        /**
         * 是否可以移动，
         *
         * @param current 当前拽托的条目
         * @param target  想交换的位置的条目
         */
        boolean isCanMove(RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target);

        /**
         * Item移动交换回调，这个回调要做Adapter数据交换
         *
         * @param fromPosition 从哪个位置
         * @param toPosition   到哪个位置
         */
        void onItemMove(int fromPosition, int toPosition);
    }
}