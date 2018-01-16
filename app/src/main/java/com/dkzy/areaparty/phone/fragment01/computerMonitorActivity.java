package com.dkzy.areaparty.phone.fragment01;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.MyConnector;
import com.dkzy.areaparty.phone.MyHandler;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment03.pcInforActivity;
import com.dkzy.areaparty.phone.utils_comman.measureView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by boris on 2016/12/13.
 */

public class computerMonitorActivity extends FragmentActivity implements View.OnClickListener {

    private static final int page01 = 0;
    private static final int page02 = 1;

    private ImageView returnLogoIB;
    private TextView  computer01MemoryPercent_textView;
    private TextView  computer01Memory_textView;
    private TextView  computer01netUpLoad_textView;
    private TextView  computer01netDownLoad_textView;
    private ImageView computer01Logo_imageView;
    private ImageView computer01MonitorChart_imageView;
    private TextView  computer01MonitorChart_textView;
    private ImageView computer01MonitorProcess_imageView;
    private TextView  computer01MonitorProcess_textView;
    private ImageView computer01Indicator_imageView;
    private ImageButton computer01Setting_imageButton;
    private ViewPager computer01Content_viewPager;
    private FragmentPagerAdapter adapter;
    private List<Fragment> fragments;

    private MyHandler handler_computerFragment01;
    private MyHandler handler_computerFragment02;
    private MyHandler handler_activity;

    private MyConnector connector;
    private Timer timer;

    private int distance = 0;
    private int initX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("----------onCreate----------");
        setContentView(R.layout.tab01_computerinformation_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Thread() {
            @Override
            public void run() {
                boolean state = MyConnector.getInstance().connect();
                Log.e("MonitorActivity", state? "连接监控电脑成功" : "连接监控电脑失败");
            }
        }.start();

        initView();
        initEvent();
        startingTimer();
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("----------onStart----------");
    }

    private void startingTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean state = MyConnector.getInstance().connect();
                Log.e("MonitorActivity", state? "连接监控电脑成功": "连接监控电脑失败");

                prepareDataForFragment.getMonitoringData();

                Message msg1 = new Message();
                msg1.what = OrderConst.refreshTab01ComputerActivity_order;
                handler_activity.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.what = OrderConst.refreshTab01ComputerFragment01_order;
                handler_computerFragment01.sendMessage(msg2);

                Message msg3 = new Message();
                msg3.what = OrderConst.refreshTab01ComputerFragment02_order;
                handler_computerFragment02.sendMessage(msg3);
            }
        }, 5000, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("----------onResume----------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("----------onPause----------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("----------onStop----------");
    }

    @Override
    protected void onDestroy() {
        System.out.println("----------onDestroy----------");
        prepareDataForFragment.initDataGroups();
        timer.cancel();
        MyConnector.getInstance().closeLongConnect();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.returnLogoIB:
                finish();
                break;
            case R.id.computer01Setting_imgButton:
                Intent intent = new Intent(this, computerSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.computer01MonitorChart_imageView:
            case R.id.computer01MonitorChart_textView:
                setSelect(page01);
                break;
            case R.id.computer01MonitorProcess_imageView:
            case R.id.computer01MonitorProcess_textView:
                setSelect(page02);
                break;
        }
    }

    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        computer01Setting_imageButton.setOnClickListener(this);
        computer01MonitorChart_textView.setOnClickListener(this);
        computer01MonitorChart_imageView.setOnClickListener(this);
        computer01MonitorProcess_textView.setOnClickListener(this);
        computer01MonitorProcess_imageView.setOnClickListener(this);
        findViewById(R.id.PCInforLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(computerMonitorActivity.this, pcInforActivity.class));
            }
        });

        computer01Content_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(distance == 0) {
                    int[] location1 = new int[2];
                    int[] location2 = new int[2];
                    int[] location3 = new int[2];
                    computer01Indicator_imageView.getLocationOnScreen(location3);
                    computer01MonitorProcess_textView.getLocationOnScreen(location1);
                    computer01MonitorChart_textView.getLocationOnScreen(location2);
                    initX = location3[0];
                    distance = (location1[0] - location2[0]);
                }
                measureView.setLayoutX(computer01Indicator_imageView, (int)(initX + (positionOffset + position) * distance));

            }

            @Override
            public void onPageSelected(int position) {
                setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        returnLogoIB = (ImageView) findViewById(R.id.returnLogoIB);
        computer01MemoryPercent_textView   = (TextView) findViewById(R.id.computer01MemoryPercent_textView);
        computer01Memory_textView          = (TextView) findViewById(R.id.computer01Memory_textView);
        computer01netDownLoad_textView     = (TextView) findViewById(R.id.computer01netDownLoad_textView);
        computer01netUpLoad_textView       = (TextView) findViewById(R.id.computer01netUpLoad_textView);
        computer01Logo_imageView           = (ImageView) findViewById(R.id.computer01Logo_imageView);
        computer01MonitorChart_imageView   = (ImageView) findViewById(R.id.computer01MonitorChart_imageView);
        computer01MonitorChart_textView    = (TextView) findViewById(R.id.computer01MonitorChart_textView);
        computer01MonitorProcess_imageView = (ImageView) findViewById(R.id.computer01MonitorProcess_imageView);
        computer01MonitorProcess_textView  = (TextView) findViewById(R.id.computer01MonitorProcess_textView);
        computer01Indicator_imageView      = (ImageView) findViewById(R.id.computer01Indicator_ImageView);
        computer01Content_viewPager        = (ViewPager) findViewById(R.id.computer01Content_ViewPager);
        computer01Setting_imageButton      = (ImageButton) findViewById(R.id.computer01Setting_imgButton);

        fragments = new ArrayList<>();
        computerFragment01 monitorTab01 = new computerFragment01();
        computerFragment02 monitorTab02 = new computerFragment02();
        fragments.add(monitorTab01);
        fragments.add(monitorTab02);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
        };
        computer01Content_viewPager.setAdapter(adapter);

        handler_computerFragment01 = new MyHandler(monitorTab01);
        handler_computerFragment02 = new MyHandler(monitorTab02);
        handler_activity = new MyHandler(this);

    }

    private void setSelect(int pageNum) {
        resetImageTextColor();
        if(computer01Content_viewPager.getCurrentItem() != pageNum) {
            computer01Content_viewPager.setCurrentItem(pageNum);
        }
        switch (pageNum){
            case page01:
                computer01Logo_imageView.setImageResource(R.drawable.monitorchart);
                computer01Logo_imageView.setBackgroundResource(R.drawable.imageview_circleborder_whitesolid);
                computer01MonitorChart_imageView.setImageResource(R.drawable.monitor_selected);
                computer01MonitorChart_textView.setTextColor(Color.rgb(230, 87, 87));
                break;
            case page02:
                computer01Logo_imageView.setImageResource(R.drawable.processlogo);
                computer01Logo_imageView.setBackgroundResource(R.drawable.imageview_circleborder_redsolid);
                computer01MonitorProcess_imageView.setImageResource(R.drawable.process_selected);
                computer01MonitorProcess_textView.setTextColor(Color.rgb(230, 87, 87));
                break;
        }
    }

    private void resetImageTextColor() {
        computer01MonitorChart_imageView.setImageResource(R.drawable.monitor_normal);
        computer01MonitorProcess_imageView.setImageResource(R.drawable.process_normal);
        computer01MonitorChart_textView.setTextColor(Color.rgb(168, 168,168));
        computer01MonitorProcess_textView.setTextColor(Color.rgb(168, 168,168));
    }


    /**
     * <summary>
     *  更新Activity上半部分涉及到的内存、网络控件
     * </summary>
     * <param name="memoryPercent">已用内存百分比</param>
     * <param name="memory">已用内存(GB)</param>
     * <param name="netDl">下行网速(Kbps)</param>
     * <param name="netUl">上行网速(Kbps)</param>
     */
    public void refreshNetAndMemoryText(String memoryPercent, String memory, String netDl, String netUl) {
        computer01MemoryPercent_textView.setText(memoryPercent);
        computer01Memory_textView.setText(memory);
        computer01netDownLoad_textView.setText(netDl);
        computer01netUpLoad_textView.setText(netUl);
    }
}
