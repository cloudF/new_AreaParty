package com.dkzy.areaparty.phone.fragment02;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.LocalSetListContainer;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class imageSetContentActivity extends AppCompatActivity implements View.OnClickListener{

//    private final String tag = this.getClass().getSimpleName();

    private ImageButton returnLogoIB;
    private TextView setNameTV;
    private ImageView castSetIV;
    private RecyclerView fileSGV;

    Adapter fileAdapter;
    List<MediaItem> files;
    Adapter_app fileAdapter_app;
    List<FileItem> files_app;
    String setName = "";

    private boolean isAppContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_imageset_content_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent getIntent = getIntent();
        isAppContent = getIntent.getBooleanExtra("isAppContent",false);
        setName = getIntent.getStringExtra("setName");
        initData();
        initView();
        initEvent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
            case R.id.castSetIV:
                // 列表投屏
                if (!isAppContent){
                    if(MyApplication.isSelectedPCOnline()) {
                        if(MyApplication.isSelectedTVOnline()) {
                            if(files.size() > 0)
                                MediafileHelper.playMediaSet(OrderConst.imageAction_name,
                                        setName, MyApplication.getSelectedTVIP().name, myHandler);
                            else  Toasty.warning(imageSetContentActivity.this, "当前列表文件个数未0", Toast.LENGTH_SHORT, true).show();
                        } else Toasty.warning(imageSetContentActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    } else Toasty.warning(imageSetContentActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }else {
                    if (MyApplication.isSelectedTVOnline()){
                        if (files_app.size() > 0){
                            dlnaCast(files_app,"image");
                        }else Toasty.warning(imageSetContentActivity.this, "当前列表文件个数未0", Toast.LENGTH_SHORT, true).show();
                    }else Toasty.warning(imageSetContentActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();

                }
                break;
        }
    }

    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
        if (!isAppContent){
            if(MediafileHelper.imageSets.containsKey(setName)) {
                files = MediafileHelper.imageSets.get(setName);
            } else files = new ArrayList<>();

            fileAdapter = new Adapter(files, this);
            fileAdapter.isFirstOnly(false);
            fileAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    // ...item点击事件
                }
            });
        }else {
            files_app = LocalSetListContainer.localMapList_image.get(setName);
            fileAdapter_app = new Adapter_app(files_app, this);
            fileAdapter_app.isFirstOnly(false);
            fileAdapter_app.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    // ...item点击事件
                }
            });
        }

    }

    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        setNameTV = (TextView) findViewById(R.id.setNameTV);
        castSetIV = (ImageView) findViewById(R.id.castSetIV);
        fileSGV = (RecyclerView) findViewById(R.id.fileSGV);

        setNameTV.setText(setName);
        fileSGV.setItemAnimator(new DefaultItemAnimator());
        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        if(!isAppContent){
            fileSGV.setAdapter(fileAdapter);
        }else {
            fileSGV.setAdapter(fileAdapter_app);
        }

    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        castSetIV.setOnClickListener(this);

    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.playPCMediaSet_OK:
                    Toasty.success(imageSetContentActivity.this, "即将在当前电视上播放图片集, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.playPCMediaSet_Fail:
                    Toasty.info(imageSetContentActivity.this, "播放图片集失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };

    private class Adapter extends BaseQuickAdapter<MediaItem> {
        private Context context;

        public Adapter(List<MediaItem> data, Context context) {
            super(R.layout.tab02_imageset_content_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MediaItem mediaItem) {
            ImageView thumbnailIV = baseViewHolder.getView(R.id.thumbnailIV);
            Glide.with(context).asBitmap()
                    .load(mediaItem.getUrl()).apply(new RequestOptions().placeholder(R.drawable.default_pic_large).centerCrop())
//                    .asBitmap()
//                    .placeholder(R.drawable.default_pic_large)
//                    .centerCrop()
                    .into(thumbnailIV);
        }
    }
    private class Adapter_app extends BaseQuickAdapter<FileItem> {
        private Context context;

        public Adapter_app(List<FileItem> data, Context context) {
            super(R.layout.tab02_imageset_content_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, FileItem mediaItem) {
            ImageView thumbnailIV = baseViewHolder.getView(R.id.thumbnailIV);
            Glide.with(context).asBitmap()
                    .load(mediaItem.getmFilePath()).apply(new RequestOptions().placeholder(R.drawable.default_pic_large).centerCrop())
//                    .asBitmap()
//                    .placeholder(R.drawable.default_pic_large)
//                    .centerCrop()
                    .into(thumbnailIV);
        }
    }
}
