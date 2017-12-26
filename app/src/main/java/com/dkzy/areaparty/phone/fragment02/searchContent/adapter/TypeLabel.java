package com.dkzy.areaparty.phone.fragment02.searchContent.adapter;

import com.dkzy.areaparty.phone.R;

import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.DefaultExpandable;
import xyz.zpayh.adapter.IFullSpan;

/**
 * 文 件 名: TypeLabel
 * 创 建 人: 陈志鹏
 * 创建日期: 2017/4/12 00:11
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */

public class TypeLabel extends DefaultExpandable<String> implements IFullSpan {

    public TypeLabel(String label) {
        super(R.layout.item_image_label,label, Integer.MAX_VALUE);
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.setText(R.id.tv_image_label,mData);
        if (this.isExpandable()){
            holder.findImage(R.id.iv_expand).setImageResource(R.drawable.ic_collapse);
        }else holder.findImage(R.id.iv_expand).setImageResource(R.drawable.ic_expand);

    }

    @Override
    public boolean isFullSpan() {
        return true;
    }
}
