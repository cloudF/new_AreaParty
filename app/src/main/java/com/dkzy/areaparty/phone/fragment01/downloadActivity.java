package com.dkzy.areaparty.phone.fragment01;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.base.BaseActivity;
import com.dkzy.areaparty.phone.fragment01.base.BaseFragment;
import com.dkzy.areaparty.phone.fragment01.model.DownloadFileModel;
import com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadTask;

import java.util.ArrayList;
import java.util.List;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.addLocalFiles;
import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.getDuration;
import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.setTab02Handle;

/**
 * Created by borispaul on 17-5-9.
 */

public class downloadActivity extends BaseActivity {
    private Toolbar page04DownloadTB;
    private ViewPager page04DownloadVP;
    private TabLayout page04DownloadTL;

    private downloadTab01Fragment tab01;
    private downloadTab02Fragment tab02;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownloadFileManagerHelper.REFRESHTAB02:
                    //...
                    tab02.dataChangeNotify();
                    break;
                case DownloadFileManagerHelper.DLNASUCCESSFUL:
                    tab02.dlnaNotify(true);
                    break;
                case DownloadFileManagerHelper.DLNAFAITH:
                    tab02.dlnaNotify(false);
                    break;
            }

        }
    };


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.downloadactionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (page04DownloadVP.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.toobar_action1).setVisible(true);
                menu.findItem(R.id.toobar_action2).setVisible(true);
                menu.findItem(R.id.toobar_action3).setVisible(true);
                menu.findItem(R.id.toobar_action4).setVisible(false);
                menu.findItem(R.id.toobar_action5).setVisible(false);
                menu.findItem(R.id.toobar_action6).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.toobar_action4).setVisible(true);
                menu.findItem(R.id.toobar_action5).setVisible(true);
                menu.findItem(R.id.toobar_action6).setVisible(true);
                menu.findItem(R.id.toobar_action1).setVisible(false);
                menu.findItem(R.id.toobar_action2).setVisible(false);
                menu.findItem(R.id.toobar_action3).setVisible(false);
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ...
        return super.onOptionsItemSelected(item);
    }*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab04_download_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        List<Progress> allTasks = DownloadManager.getInstance().getFinished();
        for(int i = 0; i < allTasks.size(); ++i) {
            DownloadTask task = OkDownload.getInstance().getTask(((DownloadFileModel)(allTasks.get(i).extra1)).getUrl());
            if(task != null)
                task.remove(false);
        }
        Log.e("circle1", "ResumeAc" + allTasks.size());


        setTab02Handle(myHandler);

        page04DownloadTB = (Toolbar) findViewById(R.id.page04DownloadTB);
        page04DownloadVP = (ViewPager) findViewById(R.id.page04DownloadVP);
        page04DownloadTL = (TabLayout) findViewById(R.id.page04DownloadTL);

        initToolBar(page04DownloadTB, true, "");

        ArrayList<BaseFragment> fragments = new ArrayList<>();
        tab01 = new downloadTab01Fragment();
        tab02 = new downloadTab02Fragment();
        tab01.setTitle("正在下载");
        tab02.setTitle("本地文件");
        fragments.add(tab01);
        fragments.add(tab02);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        page04DownloadVP.setAdapter(adapter);
        page04DownloadVP.setOffscreenPageLimit(fragments.size());
        page04DownloadVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        page04DownloadTL.setupWithViewPager(page04DownloadVP);
    }

    @Override
    protected void onResume() {
        super.onResume();

        addLocalFiles(DownloadManager.getInstance().getDownloading());
        getDuration();
        tab02.dataChangeNotify();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private List<BaseFragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
