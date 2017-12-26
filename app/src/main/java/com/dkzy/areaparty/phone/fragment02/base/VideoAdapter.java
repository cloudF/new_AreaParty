package com.dkzy.areaparty.phone.fragment02.base;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import static com.dkzy.areaparty.phone.fragment02.utils.StringFormat.ToDBC;

/**
 * Created by borispaul on 17-5-9.
 */

public class VideoAdapter extends BaseQuickAdapter<MediaItem> {
    private Context context;

    public VideoAdapter(Context context, List<MediaItem> data) {
        super(R.layout.tab02_videolib_item, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, MediaItem fileBean) {
        ImageView thumbnialIV = baseViewHolder.getView(R.id.thumbnailIV);

        Glide.with(context).asBitmap()
                .load(fileBean.getThumbnailurl())
                .apply(new RequestOptions().placeholder(R.drawable.default_pic).dontAnimate().centerCrop())
//                .placeholder(R.drawable.default_pic)
//                .dontAnimate()
//                .centerCrop()
                .into(thumbnialIV);

        baseViewHolder.setText(R.id.nameTV, ToDBC(fileBean.getName()));
        Log.w("textview",fileBean.getName());
    }


}