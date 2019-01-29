package com.android.firebasetodo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

public class LoadMoreFooterVH extends RecyclerView.ViewHolder {
    private LinearLayout llLoadMore, llLoadMoreData;

    public LoadMoreFooterVH(View itemView) {
        super(itemView);

        llLoadMore = itemView.findViewById(R.id.llLoadMore);
        llLoadMoreData = itemView.findViewById(R.id.llLoadMoreData);
    }

    public LinearLayout getLoadMoreLayout() {
        return llLoadMore;
    }

    public void showLoadMoreProgress(boolean showLoadMoreProgress) {
        if (showLoadMoreProgress) {
            llLoadMore.setVisibility(View.GONE);
            llLoadMoreData.setVisibility(View.VISIBLE);
        } else {
            llLoadMore.setVisibility(View.VISIBLE);
            llLoadMoreData.setVisibility(View.GONE);
        }
    }
}
