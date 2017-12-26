package com.dkzy.areaparty.phone.fragment02;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.fragment01.ui.SwipeListView;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   音频库
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class recentAudiosActivity extends Activity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageButton returnLogoIB;
    private LinearLayout playAllLL, selectMoreLL;
    private TextView musicNumTV;
    private SwipeListView fileSLV;
    private TextView currentMusicNameTV;
    private ImageView playOrPauseIV;

    MyAdapter<MediaItem> fileAdapter;
    private boolean isPlaying = false;
    private MediaItem currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_audiorecent_activity);
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
            case R.id.playAllLL:
                // ...
                break;
            case R.id.selectMoreLL:
                // ... 进入新界面
                break;
            case R.id.playOrPauseIV:
                // ...
                if(currentFile != null) {
                    if(isPlaying) {
                        // 暂停
                        playOrPauseIV.setImageResource(R.drawable.music_pause);
                        MediafileHelper.vlcPause();
                        isPlaying = false;
                    } else {
                        playOrPauseIV.setImageResource(R.drawable.music_play);
                        MediafileHelper.vlcContinue();
                        isPlaying = true;
                    }
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
        if(MediafileHelper.getRecentAudios().size() <= 0)
            MediafileHelper.loadRecentMediaFiles(myHandler);

        fileAdapter = new MyAdapter<MediaItem>(MediafileHelper.recentAudios, R.layout.tab02_audio_item) {
            @Override
            public void bindView(ViewHolder holder, MediaItem obj) {
                holder.setText(R.id.nameTV, obj.getName());
                holder.setOnClickListener(R.id.addToSetIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ...
                        Toasty.warning(recentAudiosActivity.this, "该功能实现中", Toast.LENGTH_SHORT, true).show();
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
        playAllLL = (LinearLayout) findViewById(R.id.playAllLL);
        selectMoreLL = (LinearLayout) findViewById(R.id.selectMoreLL);
        fileSLV = (SwipeListView) findViewById(R.id.fileSLV);
        currentMusicNameTV = (TextView) findViewById(R.id.currentMusicNameTV);
        playOrPauseIV = (ImageView) findViewById(R.id.playOrPauseIV);
        musicNumTV = (TextView) findViewById(R.id.numTV);
        musicNumTV.setText("(共" + MediafileHelper.getRecentAudios().size() + "首)");

        fileSLV.setAdapter(fileAdapter);
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        playAllLL.setOnClickListener(this);
        selectMoreLL.setOnClickListener(this);
        playOrPauseIV.setOnClickListener(this);

        fileSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        currentFile = MediafileHelper.recentAudios.get(i);
                        MediafileHelper.playMediaFile(currentFile.getType(), currentFile.getPathName(), currentFile.getName(),
                                MyApplication.getSelectedTVIP().name, myHandler);
                    } else Toasty.warning(recentAudiosActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(recentAudiosActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
            }
        });
    }



    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCMedia_OK:
                    refreshView(true);
                    break;
                case OrderConst.getPCMedia_Fail:
                    refreshView(false);
                    break;
                case OrderConst.playPCMedia_OK:
                    Toasty.success(recentAudiosActivity.this, "即将在当前电视上打开音频文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    currentMusicNameTV.setText(currentFile.getName());
                    isPlaying = true;
                    playOrPauseIV.setClickable(true);
                    playOrPauseIV.setImageResource(R.drawable.music_play);
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(recentAudiosActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    isPlaying = false;
                    currentMusicNameTV.setText("无");
                    playOrPauseIV.setClickable(false);
                    playOrPauseIV.setImageResource(R.drawable.music_pause);
                    break;
            }
        }
    };

    private void refreshView(boolean state) {
        fileAdapter.notifyDataSetChanged();

        if(state) {
            if(MediafileHelper.mediaFiles.size() > 0) {
                musicNumTV.setText("(共" + MediafileHelper.mediaFiles.size() + "首)");
            } else {
                musicNumTV.setText("(共0首)");
            }
        } else {
            musicNumTV.setText("(共0首)");
        }
    }


}
