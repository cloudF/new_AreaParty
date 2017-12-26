package com.dkzy.areaparty.phone.fragment02;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.ui.SwipeListView;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment02.utils.StringFormat.ToDBC;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class recentVideosActivity extends Activity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageButton returnLogoIB;
    private SwipeListView fileSGV;

    MyAdapter<MediaItem> fileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_vidoerecent_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        }
    }

    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
            MediafileHelper.loadRecentMediaFiles(myHandler);

        /*fileAdapter = new VideoAdapter(this, MediafileHelper.getRecentVideos());
        fileAdapter.isFirstOnly(false);
        fileAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                MediaItem file = MediafileHelper.getRecentVideos().get(i);
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        MediafileHelper.playMediaFile(file.getType(), file.getPathName(), file.getName(),
                                MyApplication.getSelectedTVIP().name, myHandler);
                        startActivity(new Intent(getApplicationContext(), vedioPlayControl.class));
                    } else Toasty.warning(recentVideosActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(recentVideosActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
            }
        });*/
        fileAdapter = new MyAdapter<MediaItem>(MediafileHelper.getRecentVideos(), R.layout.tab02_videolib_item) {
            @Override
            public void bindView(ViewHolder holder, final MediaItem obj) {
                holder.setText(R.id.nameTV, ToDBC(obj.getName()));
                holder.setImage(R.id.thumbnailIV, obj.getThumbnailurl(), R.drawable.videotest, recentVideosActivity.this);
                // 投屏事件
                holder.setOnClickListener(R.id.castLL, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(MyApplication.isSelectedPCOnline()) {
                            if(MyApplication.isSelectedTVOnline()) {
                                Log.w("videoLibActivity2",obj.getType()+"*"+obj.getPathName()+"*"+obj.getName()+"*"+MyApplication.getSelectedTVIP().name);
                                MediafileHelper.playMediaFile(obj.getType(),
                                        obj.getPathName(),
                                        obj.getName(),
                                        MyApplication.getSelectedTVIP().name,
                                        myHandler);
                                startActivity(new Intent(getApplicationContext(), vedioPlayControl.class));
                            } else  Toasty.warning(recentVideosActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                        } else  Toasty.warning(recentVideosActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                    }
                });
            }
        };
    }

    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        fileSGV = (SwipeListView) findViewById(R.id.fileSGV);
//        fileSGV.setItemAnimator(new DefaultItemAnimator());
//        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        fileSGV.setAdapter(fileAdapter);
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
    }



    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCMedia_OK:
                    fileAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getPCMedia_Fail:
                    break;
                case OrderConst.playPCMedia_OK:
                    Toasty.success(recentVideosActivity.this, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(recentVideosActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };


}
