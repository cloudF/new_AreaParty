package com.dkzy.areaparty.phone.fragment06;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.dkzy.areaparty.phone.fragment06.DownloadStateFragment.DOWNFILE;
import static com.dkzy.areaparty.phone.fragment06.DownloadStateFragment.DOWNLOADING;

/**
 * Created by SnowMonkey on 2017/5/31.
 */

public class downloadManager extends FragmentActivity {
    private TabLayout tlTitle;
    private ViewPager vp_FindFragment_pager;
    private FragmentPagerAdapter fAdapter;
    private List<Fragment> list_fragment;
    private ImageButton tab06_download_managerBackBtn;

    private DownloadFolderFragment downloadFolderFragment = null;
    private DownloadStateFragment downloadStateFragment = null;
    private String[] mTitles = {"好友下载","下载状态"};
    public static Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_download_manager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews();
        initEvents();
    }

    private void initViews(){
        try {
            tab06_download_managerBackBtn = (ImageButton) this.findViewById(R.id.tab06_download_managerBackBtn);
            tlTitle = (TabLayout) this.findViewById(R.id.tlTitle);
            vp_FindFragment_pager = (ViewPager) this.findViewById(R.id.vp_FindFragment_pager);
            downloadFolderFragment = MainActivity.downloadFolderFragment;
            downloadStateFragment = MainActivity.downloadStateFragment;
            list_fragment = new ArrayList<>();
            list_fragment.add(downloadFolderFragment);
            list_fragment.add(downloadStateFragment);
            tlTitle.setTabMode(TabLayout.MODE_FIXED);

            tlTitle.addTab(tlTitle.newTab().setText(mTitles[0]));
            tlTitle.addTab(tlTitle.newTab().setText(mTitles[1]));

            fAdapter = new Find_tab_Adapter(this.getSupportFragmentManager(), list_fragment, mTitles);
            vp_FindFragment_pager.setAdapter(fAdapter);
            tlTitle.setupWithViewPager(vp_FindFragment_pager);


        }catch (Exception e){
            e.printStackTrace();
        }
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        try {
                            downloadStateFragment.downloadFileStateData.clear();
                            downloadFolderFragment.downloadFileData.clear();
                            String fileListStr = (String) msg.obj;
                            System.out.println(fileListStr);
                            Gson gson = new Gson();
                            List<ProgressObj> fileList = gson.fromJson(fileListStr, new TypeToken<List<ProgressObj>>(){}.getType());
                            for(ProgressObj progress : fileList){
                                String fileName = progress.getFileName();
                                String fileSize = progress.getFileSize();
                                String fileTotalSize = progress.getFileTotalSize();
                                String fileProgress = progress.getProgress();
                                int fileState = progress.getState();
                                if(fileState == 0)
                                    downloadStateFragment.addToList(fileSize, fileTotalSize, fileIndexToImgId.toImgId(FileTypeConst.determineFileType(fileName)), fileProgress, DOWNLOADING, DOWNFILE, fileName);
                                else if(fileState == 1)
                                    downloadFolderFragment.addItem(fileName, Long.valueOf(fileSize)/1024+"MB");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }
    private void initEvents(){
        tab06_download_managerBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.this.finish();
            }
        });
    }
    public class Find_tab_Adapter extends FragmentPagerAdapter {
        private List<Fragment> list_fragment; //fragment列表
        private String[] list_Title; //tab名的列表
        public Find_tab_Adapter(FragmentManager fm, List<Fragment> list_fragment, String[] list_Title) {
            super(fm);
            this.list_fragment = list_fragment;
            this.list_Title = list_Title;
        }
        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }
        @Override
        public int getCount() {
            return list_Title.length;
        }
        //此方法用来显示tab上的名字
        @Override
        public CharSequence getPageTitle(int position) {
            return list_Title[position % list_Title.length];
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (vp_FindFragment_pager.getCurrentItem() == 0){
            ((DownloadFolderFragment)fAdapter.getItem(0)).onKeyUp(keyCode,event);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
