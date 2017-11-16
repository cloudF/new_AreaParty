package com.androidlearning.boris.familycentralcontroler.fragment02;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlearning.boris.familycentralcontroler.OrderConst;
import com.androidlearning.boris.familycentralcontroler.R;
import com.androidlearning.boris.familycentralcontroler.fragment02.Model.MediaItem;
import com.androidlearning.boris.familycentralcontroler.fragment02.utils.MediafileHelper;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class audioSetContentActivity extends AppCompatActivity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageButton returnLogoIB;
    private TextView setNameTV, numTV, playAsBGM;
    private LinearLayout playAllLL;
    private RecyclerView fileSGV;

    Adapter fileAdapter;
    List<MediaItem> files;
    String setName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_audioset_content_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent getIntent = getIntent();
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
            case R.id.playAllLL:
                // 列表投屏
                Log.w("audioSetContentActivity","playAllLL");
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        if(files.size() > 0)
                            MediafileHelper.playMediaSet(OrderConst.audioAction_name,
                                    setName, MyApplication.getSelectedTVIP().name, myHandler);
                        else  Toasty.warning(audioSetContentActivity.this, "当前列表文件个数未0", Toast.LENGTH_SHORT, true).show();
                    } else Toasty.warning(audioSetContentActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(audioSetContentActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.play_as_bgm:
                Log.w("audioSetContentActivity","play_as_bgm");
                if(MyApplication.isSelectedPCOnline()) {
                    if(MyApplication.isSelectedTVOnline()) {
                        if(files.size() > 0)
                            MediafileHelper.playMediaSetAsBGM(OrderConst.audioAction_name,
                                    setName, MyApplication.getSelectedTVIP().name, myHandler);
                        else  Toasty.warning(audioSetContentActivity.this, "当前列表文件个数未0", Toast.LENGTH_SHORT, true).show();
                    } else Toasty.warning(audioSetContentActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                } else Toasty.warning(audioSetContentActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;
        }
    }

    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
        if(MediafileHelper.audioSets.containsKey(setName)) {
            files = MediafileHelper.audioSets.get(setName);
        } else files = new ArrayList<>();

        fileAdapter = new Adapter(files, this);
        fileAdapter.isFirstOnly(false);
        fileAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                // ...item点击事件
            }
        });
    }

    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {
        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        setNameTV = (TextView) findViewById(R.id.setNameTV);
        numTV = (TextView) findViewById(R.id.numTV);
        playAllLL = (LinearLayout) findViewById(R.id.playAllLL);
        fileSGV = (RecyclerView) findViewById(R.id.fileSGV);
        playAsBGM = (TextView) findViewById(R.id.play_as_bgm);

        setNameTV.setText(setName);
        numTV.setText("(共" + files.size() + "首)");
        fileSGV.setItemAnimator(new DefaultItemAnimator());
        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        fileSGV.setAdapter(fileAdapter);
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        playAllLL.setOnClickListener(this);
        playAsBGM.setOnClickListener(this);

    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.playPCMediaSet_OK:
                    Toasty.success(audioSetContentActivity.this, "即将在当前电视上播放当前音频集合, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.playPCMediaSet_Fail:
                    Toasty.info(audioSetContentActivity.this, "播放音频集失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };

    private class Adapter extends BaseQuickAdapter<MediaItem> {
        private Context context;

        public Adapter(List<MediaItem> data, Context context) {
            super(R.layout.tab02_audioset_content_item, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MediaItem mediaItem) {
            baseViewHolder.setText(R.id.nameTV, mediaItem.getName());
        }
    }
}
