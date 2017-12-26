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
import xyz.zpayh.adapter.IFullSpan;
import xyz.zpayh.adapter.IMultiItem;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;
import static com.dkzy.areaparty.phone.fragment02.utils.StringFormat.ToDBC;

/**
 * Created by zhuyulin on 2017/11/30.
 */

public class VideoItem implements IMultiItem,IFullSpan {

    private Context context;

    private FileItem fileItem;
    private MediaItem mediaItem;

    public VideoItem(FileItem fileItem,Context context) {
        this.fileItem = fileItem;
        this.context = context;
        mediaItem = null;
    }

    public VideoItem(MediaItem mediaItem,Context context) {
        this.mediaItem = mediaItem;
        this.context = context;
        fileItem = null;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.tab02_videolib_item;
    }

    @Override
    public void convert(BaseViewHolder holder) {
        if (fileItem != null){
            holder.setText(R.id.nameTV, fileItem.getmFileName());
            ImageView imageView = holder.findImage(R.id.thumbnailIV);
            Glide.with(context).load(fileItem.getmFilePath()).apply(new RequestOptions().centerCrop().dontAnimate().error( R.drawable.videotest)).into(imageView);
            holder.find(R.id.castLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.isSelectedTVOnline()){
                        dlnaCast(fileItem,"video");
                    } else  Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
            });
        }else if (mediaItem != null){
            holder.setText(R.id.nameTV, ToDBC(mediaItem.getName()));
            ImageView imageView = holder.findImage(R.id.thumbnailIV);
            Glide.with(context).load(mediaItem.getThumbnailurl()).apply(new RequestOptions().centerCrop().dontAnimate().error( R.drawable.videotest)).into(imageView);
            holder.find(R.id.castLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApplication.isSelectedPCOnline()) {
                        if(MyApplication.isSelectedTVOnline()) {
//                            Log.w("videoLibActivity1", MediafileHelper.getMediaType()+"*"+currentFile.getPathName()+"*"+currentFile.getName()+"*"+MyApplication.getSelectedTVIP().name);
                            MediafileHelper.playMediaFile(mediaItem.getType(),
                                    mediaItem.getPathName(),
                                    mediaItem.getName(),
                                    MyApplication.getSelectedTVIP().name,
                                    myHandler);
//                            startActivity(new Intent(getApplicationContext(), vedioPlayControl.class));
                        } else  Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    } else  Toasty.warning(context, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();

                }
            });
        }
    }

    @Override
    public int getSpanSize() {
        return 2;
    }

    @Override
    public boolean isFullSpan() {
        return true;
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
