package com.dkzy.areaparty.phone.fragment02.searchContent.adapter;

import android.support.annotation.LayoutRes;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.searchContent.SearchMediaActivity;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.ExpandableAdapter;

/**
 * Created by zhuyulin on 2017/11/30.
 */

public class MyExpandableAdapter extends ExpandableAdapter {
    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {

    }

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head, SearchMediaActivity.headText);
    }

}
