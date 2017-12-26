package com.dkzy.areaparty.phone.fragment02.base;

import android.content.Context;
import android.widget.ImageView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaSetBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by borispaul on 17-5-9.
 */

public class BottomDialogAdapter extends BaseQuickAdapter<MediaSetBean> {
    private Context context;

    public BottomDialogAdapter(Context context, List<MediaSetBean> data) {
        super(R.layout.tab02_listdialog_item, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, MediaSetBean fileBean) {
        ImageView thumbnialIV = baseViewHolder.getView(R.id.thumbnailIV);

        Glide.with(context).asBitmap().load(fileBean.thumbnailID).apply(new RequestOptions().dontAnimate().centerCrop()).into(thumbnialIV);
//        Glide.with(context).asBitmap()
//                .load(fileBean.thumbnailID)
//                .asBitmap()
//                .dontAnimate()
//                .centerCrop()
//                .into(thumbnialIV);

        baseViewHolder.setText(R.id.nameTV, fileBean.name)
                .setText(R.id.numTV, fileBean.numInfor);
    }
}