package com.dkzy.areaparty.phone.fragment01.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.downloadTab02Fragment;
import com.dkzy.areaparty.phone.fragment01.model.downloadedFileBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by borispaul on 17-5-9.
 */

public class DownloadedAdapter extends BaseQuickAdapter<downloadedFileBean> {
    private Context context;
    private downloadTab02Fragment tab;

    public DownloadedAdapter(downloadTab02Fragment tab, Context context, List<downloadedFileBean> data) {
        super(R.layout.tab04_downloaded_item, data);
        this.context = context;
        this.tab = tab;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, downloadedFileBean downloadedFileBean) {
        tab.changeBgView();

        ImageView thumbnialIV = baseViewHolder.getView(R.id.downloadedFileImageIV);

        switch (downloadedFileBean.getFileType()) {

            case FileTypeConst.apk:
                RequestBuilder<Bitmap> requestBuilder = Glide.with(context).asBitmap();
                requestBuilder.load(R.drawable.apktest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.apktest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false);
                break;
            case FileTypeConst.video:
                Glide.with(context).load(downloadedFileBean.getPath()).apply(new RequestOptions().placeholder(R.drawable.videotest).dontAnimate().centerCrop()).into(thumbnialIV);
                //Glide.with(context).load(downloadedFileBean.getPath()).placeholder(R.drawable.videotest).dontAnimate().centerCrop().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, true)
                        .setVisible(R.id.downloadedMediaFileLengthTV, true)
                        .setVisible(R.id.downloadMediaFileCastLL, true)
                        .setText(R.id.downloadedMediaFileLengthTV, downloadedFileBean.getTimeLenStr());
                break;
            case FileTypeConst.music:
                RequestBuilder<Bitmap> requestBuilder1 = Glide.with(context).asBitmap();
                requestBuilder1.load(R.drawable.musictest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.musictest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, true)
                        .setVisible(R.id.downloadMediaFileCastLL, true)
                        .setText(R.id.downloadedMediaFileLengthTV, downloadedFileBean.getTimeLenStr());
                break;
            case FileTypeConst.pic:

                Glide.with(context).load(downloadedFileBean.getPath()).apply(new RequestOptions().placeholder(R.drawable.picturetest).dontAnimate().centerCrop()).into(thumbnialIV);
                //Glide.with(context).load(downloadedFileBean.getPath()).placeholder(R.drawable.picturetest).dontAnimate().centerCrop().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, true)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.excel:
                RequestBuilder<Bitmap> requestBuilder2 = Glide.with(context).asBitmap();
                requestBuilder2.load(R.drawable.exceltest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.exceltest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.pdf:
                RequestBuilder<Bitmap> requestBuilder3 = Glide.with(context).asBitmap();
                requestBuilder3.load(R.drawable.pdftest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.pdftest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.ppt:
                RequestBuilder<Bitmap> requestBuilder4 = Glide.with(context).asBitmap();
                requestBuilder4.load(R.drawable.ppttest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.ppttest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.word:
                RequestBuilder<Bitmap> requestBuilder5 = Glide.with(context).asBitmap();
                requestBuilder5.load(R.drawable.wordtest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.wordtest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.zip:
                RequestBuilder<Bitmap> requestBuilder6 = Glide.with(context).asBitmap();
                requestBuilder6.load(R.drawable.rartest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.rartest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.txt:
                RequestBuilder<Bitmap> requestBuilder7 = Glide.with(context).asBitmap();
                requestBuilder7.load(R.drawable.txttest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.txttest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
            case FileTypeConst.none:
                RequestBuilder<Bitmap> requestBuilder8 = Glide.with(context).asBitmap();
                requestBuilder8.load(R.drawable.nonetest).apply(new RequestOptions().dontAnimate().fitCenter()).into(thumbnialIV);
                //Glide.with(context).load(R.drawable.nonetest).asBitmap().dontAnimate().fitCenter().into(thumbnialIV);
                baseViewHolder.setVisible(R.id.downloadedVideoPlayingIconIV, false)
                        .setVisible(R.id.downloadMediaFileCastLL, false)
                        .setVisible(R.id.downloadedMediaFileLengthTV, false);
                break;
        }

        baseViewHolder.setText(R.id.downloadedFileNameTV, downloadedFileBean.getName())
                .setText(R.id.downloadedFileSizeTV, downloadedFileBean.getSizeInfor())
                .setText(R.id.downloadedFileTimeTV, downloadedFileBean.getCreateTimeStr())
                .setOnClickListener(R.id.downloadMediaFileCastLL, new OnItemChildClickListener())
                .setOnClickListener(R.id.downloadFileDeleteLL, new OnItemChildClickListener())
                .setOnClickListener(R.id.downloadedFileImageIV, new OnItemChildClickListener())
                .setOnClickListener(R.id.downloadedVideoPlayingIconIV, new OnItemChildClickListener());
    }
}