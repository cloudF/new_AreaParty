package com.dkzy.areaparty.phone.fragment02.searchContent.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.IFullSpan;
import xyz.zpayh.adapter.IMultiItem;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;

/**
 * Created by zhuyulin on 2017/11/30.
 */

public class AudioItem implements IMultiItem,IFullSpan {

    private Context context;

    private FileItem fileItem;
    private MediaItem mediaItem;

    public AudioItem(FileItem fileItem,Context context) {
        this.fileItem = fileItem;
        this.context = context;
        mediaItem = null;
    }

    public AudioItem(MediaItem mediaItem,Context context) {
        this.mediaItem = mediaItem;
        this.context = context;
        fileItem = null;
    }

    @Override
    public boolean isFullSpan() {
        return true;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.tab02_audio_item;
    }

    @Override
    public void convert(BaseViewHolder holder) {
        holder.findImage(R.id.addToSetIV).setVisibility(View.GONE);
        if (fileItem!=null){
            holder.setText(R.id.nameTV, fileItem.getmFileName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.isSelectedTVOnline()){
                        dlnaCast(fileItem,"audio");
                    } else  Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();

                }
            });
        }else if (mediaItem!=null){
            holder.setText(R.id.nameTV, mediaItem.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApplication.isSelectedPCOnline()) {
                        if(MyApplication.isSelectedTVOnline()) {
                            MediafileHelper.playMediaFile(mediaItem.getType(), mediaItem.getPathName(), mediaItem.getName(),
                                    MyApplication.getSelectedTVIP().name, myHandler);
                        } else Toasty.warning(context, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    } else Toasty.warning(context, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();

                }
            });
        }

    }

    @Override
    public int getSpanSize() {
        return 2;
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
