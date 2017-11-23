package com.androidlearning.boris.familycentralcontroler.fragment02.base;

/**
 * Created by zhuyulin on 2017/11/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment02.Model.MediaSetBean;
import com.androidlearning.boris.familycentralcontroler.fragment02.contentResolver.FileItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by borispaul on 17-5-9.
 */

public class BottomDialogAdapter_app extends BaseQuickAdapter<MediaSetBean> {
    private Context context;

    public BottomDialogAdapter_app(Context context, List<MediaSetBean> data) {
        super(R.layout.tab02_listdialog_item, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, MediaSetBean fileBean) {
        ImageView thumbnialIV = baseViewHolder.getView(R.id.thumbnailIV);

        Glide.with(context).asBitmap().load(fileBean.thumbnailID).apply(new RequestOptions().dontAnimate().centerCrop()).into(thumbnialIV);
////        Glide.with(context).asBitmap()
////                .load(fileBean.thumbnailID)
////                .asBitmap()
////                .dontAnimate()
////                .centerCrop()
////                .into(thumbnialIV);
//
        baseViewHolder.setText(R.id.nameTV, fileBean.name)
                .setText(R.id.numTV, fileBean.numInfor);
    }
}
