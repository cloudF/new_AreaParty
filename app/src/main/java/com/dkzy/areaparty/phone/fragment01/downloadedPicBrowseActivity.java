package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.dkzy.areaparty.phone.R;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.io.File;

/**
 * Created by borispaul on 17-5-17.
 * 查看图片
 */

public class downloadedPicBrowseActivity extends Activity {

    private PhotoView downloadedPiBPV;
    private String imgPath = "";
    private TranslateAnimation mShowAction, mHiddenAction;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.imgPath = getIntent().getStringExtra("imgPath");

        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                                             Animation.RELATIVE_TO_SELF, 0.0f,
                                             Animation.RELATIVE_TO_SELF, -1.0f,
                                             Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(200);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                                               Animation.RELATIVE_TO_SELF, 0.0f,
                                               Animation.RELATIVE_TO_SELF, 0.0f,
                                               Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(200);
        initScreen();
        initView();
    }

    private void initScreen() {
        View root = LayoutInflater.from(this).inflate(R.layout.tab04_picbrowse_activity, null);
        setContentView(root);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Android 5.0 以上 全透明
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 状态栏（以上几行代码必须，参考setStatusBarColor|setNavigationBarColor方法源码）
            window.setStatusBarColor(Color.TRANSPARENT);
            // 虚拟导航键
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4 以上 半透明
            Window window = getWindow();
            // 状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟导航键
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initView() {
        downloadedPiBPV = (PhotoView) findViewById(R.id.downloadedPiBPV);
        downloadedPiBPV.enable();
        downloadedPiBPV.setClickable(true);
        downloadedPiBPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View top = findViewById(R.id.top);
                if(top.getVisibility() == View.VISIBLE) {
                    top.startAnimation(mHiddenAction);
                    top.setVisibility(View.GONE);
                } else {
                    top.startAnimation(mShowAction);
                    top.setVisibility(View.VISIBLE);
                }
            }
        });
        File file = new File(imgPath);
        Glide.with(this).load(file).into(downloadedPiBPV);
    }
}
