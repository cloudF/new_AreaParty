package com.androidlearning.boris.familycentralcontroler.fragment02;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.model_comman.MyAdapter;
import com.androidlearning.boris.familycentralcontroler.OrderConst;
import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.androidlearning.boris.familycentralcontroler.fragment02.Model.MediaItem;
import com.androidlearning.boris.familycentralcontroler.fragment02.utils.MediafileHelper;
import com.androidlearning.boris.familycentralcontroler.fragment01.ui.SwipeListView;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import es.dmoral.toasty.Toasty;

import static com.androidlearning.boris.familycentralcontroler.fragment02.utils.StringFormat.ToDBC;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class videoLibActivity extends Activity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageView returnIV;
    private ImageView pcStateIV, tvStateIV;
    private TextView pcStateNameTV, tvStateNameTV;
    private SwipeListView folderSLV, fileSLV;

    MyAdapter<MediaItem> folderAdapter;
    MyAdapter<MediaItem> fileAdapter;
    private MediaItem currentFile;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                MediafileHelper.resetMediaInfors();
                this.finish();
            }
            else {
                String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                MediafileHelper.setCurrentPath(tempPath);
                MediafileHelper.loadMediaLibFiles(myHandler);
                folderAdapter.notifyDataSetChanged();
                fileAdapter.notifyDataSetChanged();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_videolib_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnIV:
                if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                    MediafileHelper.resetMediaInfors();
                    this.finish();
                } else {
                    String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                    MediafileHelper.setCurrentPath(tempPath);
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    folderAdapter.notifyDataSetChanged();
                    fileAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Subscriber(tag = "selectedDeviceStateChanged")
    private void updateDeviceNetState(TVPCNetStateChangeEvent event) {
        if(event.isPCOnline()) {
            // ... 判断是否已有数据
            pcStateIV.setImageResource(R.drawable.pcconnected);
            pcStateNameTV.setText(MyApplication.getSelectedPCIP().nickName);
            pcStateNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            pcStateIV.setImageResource(R.drawable.pcbroke);
            pcStateNameTV.setText("离线中");
            pcStateNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
        if(event.isTVOnline()) {
            tvStateIV.setImageResource(R.drawable.tvconnected);
            tvStateNameTV.setText(MyApplication.getSelectedTVIP().nickName);
            tvStateNameTV.setTextColor(Color.parseColor("#ffffff"));
        } else {
            tvStateIV.setImageResource(R.drawable.tvbroke);
            tvStateNameTV.setText("离线中");
            tvStateNameTV.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
        MediafileHelper.loadMediaLibFiles(myHandler);
        folderAdapter = new MyAdapter<MediaItem>(MediafileHelper.mediaFolders, R.layout.tab02_folder_item) {
            @Override
            public void bindView(ViewHolder holder, MediaItem obj) {
                holder.setText(R.id.nameTV, obj.getName());
            }
        };
        fileAdapter = new MyAdapter<MediaItem>(MediafileHelper.mediaFiles, R.layout.tab02_videolib_item) {
            @Override
            public void bindView(ViewHolder holder, final MediaItem obj) {
                holder.setText(R.id.nameTV, ToDBC(obj.getName()));
                holder.setImage(R.id.thumbnailIV, obj.getThumbnailurl(), R.drawable.videotest, videoLibActivity.this);
                // 投屏事件
                holder.setOnClickListener(R.id.castLL, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(MyApplication.isSelectedPCOnline()) {
                            if(MyApplication.isSelectedTVOnline()) {
                                currentFile = obj;
                                Log.w("videoLibActivity1",MediafileHelper.getMediaType()+"*"+currentFile.getPathName()+"*"+currentFile.getName()+"*"+MyApplication.getSelectedTVIP().name);
                                MediafileHelper.playMediaFile(MediafileHelper.getMediaType(),
                                        currentFile.getPathName(),
                                        currentFile.getName(),
                                        MyApplication.getSelectedTVIP().name,
                                        myHandler);
                                startActivity(new Intent(getApplicationContext(), vedioPlayControl.class));
                            } else  Toasty.warning(videoLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                        } else  Toasty.warning(videoLibActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
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
        returnIV = (ImageView) findViewById(R.id.returnIV);
        pcStateIV = (ImageView) findViewById(R.id.pcStateIV);
        tvStateIV = (ImageView) findViewById(R.id.tvStateIV);
        pcStateNameTV = (TextView) findViewById(R.id.pcStateNameTV);
        tvStateNameTV = (TextView) findViewById(R.id.tvStateNameTV);
        folderSLV = (SwipeListView) findViewById(R.id.folderSLV);
        fileSLV = (SwipeListView) findViewById(R.id.fileSLV);

        updateDeviceNetState(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(),
                MyApplication.isSelectedPCOnline()));

        folderSLV.setAdapter(folderAdapter);
        fileSLV.setAdapter(fileAdapter);
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnIV.setOnClickListener(this);
        folderSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ... 还要判断是否加 “\”
                String tempPath = MediafileHelper.mediaFolders.get(i).getPathName();
                MediafileHelper.setCurrentPath(tempPath);
                MediafileHelper.loadMediaLibFiles(myHandler);
                folderAdapter.notifyDataSetChanged();
                fileAdapter.notifyDataSetChanged();
            }
        });

        fileSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ....
            }
        });
    }



    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCMedia_OK:
                    folderAdapter.notifyDataSetChanged();
                    fileAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getPCMedia_Fail:
                    break;
                case OrderConst.playPCMedia_OK:
                    Toasty.success(videoLibActivity.this, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    MediafileHelper.addRecentVideos(currentFile);
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(videoLibActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };


}
