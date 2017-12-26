package com.dkzy.areaparty.phone.fragment02.searchContent.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import es.dmoral.toasty.Toasty;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.IMultiItem;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;

/**
 * Created by zhuyulin on 2017/11/30.
 */

public class ImageItem implements IMultiItem {

    private Context context;

    private FileItem fileItem;
    private MediaItem mediaItem;

    public ImageItem(FileItem fileItem,Context context) {
        this.fileItem = fileItem;
        this.context = context;
        mediaItem = null;
    }

    public ImageItem(MediaItem mediaItem,Context context) {
        this.mediaItem = mediaItem;
        this.context = context;
        fileItem = null;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.tab02_imagelib_item;
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.findImage(R.id.addToSetIV).setVisibility(View.GONE);
        if (fileItem != null){
            holder.setText(R.id.nameTV, fileItem.getmFileName());
            ImageView imageView = holder.findImage(R.id.thumbnailIV);
            Glide.with(context).load(fileItem.getmFilePath()).apply(new RequestOptions().centerCrop().dontAnimate().placeholder( R.drawable.videotest)).into(imageView);
            holder.find(R.id.castIV).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.isSelectedTVOnline()){
                        dlnaCast(fileItem,"image");
                    } else  Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
            });
        }else if (mediaItem != null){
            holder.setText(R.id.nameTV, mediaItem.getName());
            ImageView imageView = holder.findImage(R.id.thumbnailIV);
            Glide.with(context).load(mediaItem.getUrl()).apply(new RequestOptions().centerCrop().dontAnimate().placeholder( R.drawable.videotest)).into(imageView);
            holder.find(R.id.castLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApplication.isSelectedPCOnline()) {
                        if(MyApplication.isSelectedTVOnline()) {
                            MediafileHelper.playMediaFile(mediaItem.getType(),
                                    mediaItem.getPathName(),
                                    mediaItem.getName(),
                                    MyApplication.getSelectedTVIP().name,
                                    myHandler);
                        } else  Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    } else  Toasty.warning(context, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }
            });

        }
    }

    @Override
    public int getSpanSize() {
        return 1;
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.playPCMedia_OK:
                    Toasty.success(context, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    MediafileHelper.addRecentVideos(mediaItem);
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(context, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };
}
