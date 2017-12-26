package com.dkzy.areaparty.phone.fragment02.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaSetBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by borispaul on 17-5-9.
 */

public class SetAdapter extends BaseQuickAdapter<MediaSetBean> {
    private Context context;
    private String type;  // AUDIO、IMAGE

    public SetAdapter(Context context, List<MediaSetBean> data, int layoutId, String type) {
        super(layoutId, data);
        this.context = context;
        this.type = type;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, MediaSetBean fileBean) {
        ImageView thumbnialIV = baseViewHolder.getView(R.id.thumbnailIV);

        if(type.equals(OrderConst.audioAction_name)) {
            RequestBuilder<Bitmap> requestBuilder = Glide.with(context).asBitmap();
            requestBuilder.load(fileBean.thumbnailID).apply(new RequestOptions().placeholder(R.drawable.logo_empty)
                    .dontAnimate()
                    .centerCrop()).into(thumbnialIV);
//            Glide.with(context)
//                    .load(fileBean.thumbnailID)
//                    .asBitmap()
//                    .placeholder(R.drawable.logo_empty)
//                    .dontAnimate()
//                    .centerCrop()
//                    .into(thumbnialIV);
        } else if(type.equals(OrderConst.imageAction_name)) {
            RequestBuilder<Bitmap> requestBuilder = Glide.with(context).asBitmap();
            requestBuilder.load(fileBean.thumbnailURL).apply(new RequestOptions().placeholder(R.drawable.logo_empty)
                    .dontAnimate()
                    .centerCrop()).into(thumbnialIV);
//            Glide.with(context)
//                    .load(fileBean.thumbnailURL)
//                    .asBitmap()
//                    .placeholder(R.drawable.logo_empty)
//                    .dontAnimate()
//                    .centerCrop()
//                    .into(thumbnialIV);
        }


        baseViewHolder.setText(R.id.nameTV, fileBean.name)
                .setText(R.id.numTV, fileBean.numInfor)
                .setOnClickListener(R.id.deleteIV, new OnItemChildClickListener());
    }
}