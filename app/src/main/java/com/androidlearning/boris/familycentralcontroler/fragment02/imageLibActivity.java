package com.androidlearning.boris.familycentralcontroler.fragment02;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.model_comman.MyAdapter;
import com.androidlearning.boris.familycentralcontroler.OrderConst;
import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.androidlearning.boris.familycentralcontroler.fragment02.Model.MediaItem;
import com.androidlearning.boris.familycentralcontroler.fragment02.base.ImageAdapter;
import com.androidlearning.boris.familycentralcontroler.fragment02.ui.listBottomDialog;
import com.androidlearning.boris.familycentralcontroler.fragment02.utils.MediafileHelper;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class imageLibActivity extends AppCompatActivity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageView returnIV;
    private ImageView pcStateIV, tvStateIV;
    private TextView pcStateNameTV, tvStateNameTV, picsPlayListNumTV;
    private ListView folderSLV;
    private RecyclerView fileSGV;
    private LinearLayout picsPlayListLL;

    MyAdapter<MediaItem> folderAdapter;
    ImageAdapter fileAdapter;

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
        picsPlayListNumTV.setText("(" + MediafileHelper.getImageSets().size() + ")");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_imagelib_activity);
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
            case R.id.picsPlayListLL:
                if(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline()) {
                    startActivity(new Intent(getApplicationContext(), imageSetActivity.class));
                } else  Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            default:
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
        fileAdapter = new ImageAdapter(this, MediafileHelper.mediaFiles);
        fileAdapter.isFirstOnly(false);
        fileAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                MediaItem file = MediafileHelper.mediaFiles.get(i);
                switch (view.getId()) {
                    case R.id.castIV:
                        if(MyApplication.isSelectedPCOnline()) {
                            if(MyApplication.isSelectedTVOnline()) {
                                MediafileHelper.playMediaFile(file.getType(),
                                        file.getPathName(),
                                        file.getName(),
                                        MyApplication.getSelectedTVIP().name,
                                        myHandler);
                            } else  Toasty.warning(imageLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                        } else  Toasty.warning(imageLibActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                        break;
                    case R.id.addToSetIV:
                        listDialog(file);
                        break;
                }
            }
        });
    }

    private void listDialog(MediaItem file) {
        listBottomDialog dialog = new listBottomDialog();
        dialog.setFile(file);
        dialog.show(getSupportFragmentManager());
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
        folderSLV = (ListView) findViewById(R.id.folderSLV);
        fileSGV = (RecyclerView) findViewById(R.id.fileSGV);
        picsPlayListLL = (LinearLayout) findViewById(R.id.picsPlayListLL);
        picsPlayListNumTV = (TextView) findViewById(R.id.picsPlayListNumTV);

        updateDeviceNetState(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(),
                MyApplication.isSelectedPCOnline()));

        fileSGV.setItemAnimator(new DefaultItemAnimator());
        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        folderSLV.setAdapter(folderAdapter);
        fileSGV.setAdapter(fileAdapter);

    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnIV.setOnClickListener(this);
        picsPlayListLL.setOnClickListener(this);
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
                    Toasty.success(imageLibActivity.this, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(imageLibActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };


}
