package com.zh.android.base.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author wally
 * @date 2020/09/30
 * RecyclerView滚动帮助类
 */
public class RecyclerViewScrollHelper {
    //第一次进入界面时也会回调滚动，所以当手动滚动再监听
    private boolean isNotFirst = false;

    private RecyclerView mRecyclerView;
    private Callback mCallback;

    /**
     * 是否已经滚动到地址
     */
    private boolean isScrollToBottom;

    public RecyclerViewScrollHelper attachRecyclerView(RecyclerView recyclerView) {
        return attachRecyclerView(recyclerView, null);
    }

    public RecyclerViewScrollHelper attachRecyclerView(RecyclerView recyclerView, Callback callback) {
        this.mRecyclerView = recyclerView;
        this.mCallback = callback;
        setup();
        return this;
    }

    private void setup() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isNotFirst = true;
                if (mCallback != null) {
                    mCallback.onScrollStateChanged(recyclerView, newState);
                }
                //如果滚动到最后一行，RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        !recyclerView.canScrollVertically(1)) {
                    isScrollToBottom = true;
                    if (mCallback != null) {
                        mCallback.onScrolledToBottom();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isNotFirst) {
                    if (mCallback != null) {
                        mCallback.onScrolled(recyclerView, dx, dy);
                    }
                    //RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
                    if (!recyclerView.canScrollVertically(-1)) {
                        isScrollToBottom = false;
                        if (mCallback != null) {
                            mCallback.onScrolledToTop();
                        }
                    }
                    //下滑
                    if (dy < 0) {
                        isScrollToBottom = false;
                        if (mCallback != null) {
                            mCallback.onScrolledToDown();
                        }
                    }
                    //上滑
                    if (dy > 0) {
                        isScrollToBottom = false;
                        if (mCallback != null) {
                            mCallback.onScrolledToUp();
                        }
                    }
                }
            }
        });
    }

    public interface Callback {
        /**
         * 在滚动
         */
        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        /**
         * 滚动状态切换
         */
        void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState);

        /**
         * 当前正在向上滚动
         */
        void onScrolledToUp();

        /**
         * 当前正在向下滚动
         */
        void onScrolledToDown();

        /**
         * 滚动到顶部
         */
        void onScrolledToTop();

        /**
         * 滚动到底部
         */
        void onScrolledToBottom();
    }

    public static class SimpleCallback implements Callback {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        }

        @Override
        public void onScrolledToUp() {
        }

        @Override
        public void onScrolledToDown() {
        }

        @Override
        public void onScrolledToTop() {
        }

        @Override
        public void onScrolledToBottom() {
        }
    }

    /**
     * 是否滚动到了底部
     */
    public boolean isScrollToBottom() {
        return isScrollToBottom;
    }
}