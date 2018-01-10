package com.dkzy.areaparty.phone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dkzy.areaparty.phone.fragment01.page01Fragment;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_page;
import com.dkzy.areaparty.phone.fragment02.page02Fragment;
import com.dkzy.areaparty.phone.fragment03.page03Fragment;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.fragment05.page05Fragment;
import com.dkzy.areaparty.phone.fragment06.BtFolderFragment;
import com.dkzy.areaparty.phone.fragment06.ChatDBManager;
import com.dkzy.areaparty.phone.fragment06.ChatObj;
import com.dkzy.areaparty.phone.fragment06.DownloadFolderFragment;
import com.dkzy.areaparty.phone.fragment06.DownloadStateFragment;
import com.dkzy.areaparty.phone.fragment06.FileRequestDBManager;
import com.dkzy.areaparty.phone.fragment06.FriendRequestDBManager;
import com.dkzy.areaparty.phone.fragment06.fileObj;
import com.dkzy.areaparty.phone.fragment06.myChatList;
import com.dkzy.areaparty.phone.fragment06.page06Fragment;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;

import java.util.ArrayList;
import java.util.List;

import protocol.Data.ChatData;
import protocol.Data.UserData;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ViewPager id_ContentViewpager;
    private FragmentPagerAdapter adapter;
    private List<Fragment> fragments;

    private static final int page01 = 0;
    private static final int page02 = 1;
    private static final int page03 = 2;
    private static final int page04 = 3;
    private static final int page05 = 4;
    private static final int page06 = 5;

    private int currentPage;

    //private HorizontalScrollView id_tabBar;

    private LinearLayout bottom;
    private LinearLayout id_tab01;
    private LinearLayout id_tab02;
    private LinearLayout id_tab03;
    private LinearLayout id_tab04;
    private LinearLayout id_tab05;
    private LinearLayout id_tab06;

    private ImageButton id_tab01_image;
    private ImageButton id_tab02_image;
    private ImageButton id_tab03_image;
    private ImageButton id_tab04_image;
    private ImageButton id_tab05_image;
    private ImageButton id_tab06_image;

    private TextView id_tab01_text;
    private TextView id_tab02_text;
    private TextView id_tab03_text;
    private TextView id_tab04_text;
    private TextView id_tab05_text;
    private TextView id_tab06_text;

    private page01Fragment tabContent01;
    private page02Fragment tabContent02;
    private page03Fragment tabContent03;
    private page05Fragment tabContent05;
    private page06Fragment tabContent06;

    public static MyHandler handlerTab01;
    public static MyHandler handlerTab04;
    public static MyHandler handlerTab06 = null;

    private static ChatDBManager chatDBManager;
    private static FriendRequestDBManager friendRequestDBManager;
    private static FileRequestDBManager fileRequestDBManager;
    private static SharedPreferences sp;
    public static BtFolderFragment btFolderFragment = null;
    public static DownloadFolderFragment downloadFolderFragment = null;
    public static DownloadStateFragment downloadStateFragment = null;
    public static MyHandler stateHandler = null;
    public static MyHandler btHandler = null;
    public static MyHandler downloadHandler = null;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (currentPage == page05) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Message msg = new Message();
                    msg.what = OrderConst.returnToParentFolder;
                    handlerTab04.sendMessage(msg);
                    break;
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK){
            exitAppDialog();
            return true;
        }
        return false;
    }

    /**
     * <summary>
     * 显示完整退出应用对话框
     * </summary>
     */
    private void exitAppDialog() {
        final exitApplicationDialog exitDialog = new exitApplicationDialog(this);
        exitDialog.setCancelable(false);
        exitDialog.show();
        exitDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });
        exitDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
                if (MyApplication.isSelectedTVOnline() && exitDialog.isRadioButtonChecked()){
                    TVAppHelper.vedioPlayControlExit();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MyApplication.getInstance().exit();
                }else {
                    MyApplication.getInstance().exit();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("change", "Create");
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MyApplication.getInstance().addActivity(this);

        if(FillingIPInforList.getStatisticThread() != null && !FillingIPInforList.getStatisticThread().isAlive())
            FillingIPInforList.getStatisticThread().start();

        /**
         * 姜超
         * 获取聊天数据库，如果当前用户第一次登陆，新建表
         */
        if(Login.userId!=""){
            chatDBManager = new ChatDBManager(this);
            friendRequestDBManager = new FriendRequestDBManager(this);
            fileRequestDBManager = new FileRequestDBManager(this);
            boolean chatTbExist = chatDBManager.tabbleIsExist(Login.userId);
            boolean friendRequestTbExist = friendRequestDBManager.tabbleIsExist(Login.userId + "friend");
            boolean fileRequestTbExist = fileRequestDBManager.tabbleIsExist(Login.userId + "transform");
            if(!chatTbExist) {
                chatDBManager.createTable(Login.userId);
                chatTbExist = chatDBManager.tabbleIsExist(Login.userId);
                System.out.println(chatTbExist);
            }
            if(!friendRequestTbExist){
                friendRequestDBManager.createTable(Login.userId + "friend");
                friendRequestTbExist = friendRequestDBManager.tabbleIsExist(Login.userId + "friend");
                System.out.println(friendRequestTbExist);
            }
            if(!fileRequestTbExist){
                fileRequestDBManager.createTable(Login.userId + "transform");
                fileRequestTbExist = fileRequestDBManager.tabbleIsExist(Login.userId + "transform");
                System.out.println(fileRequestTbExist);
            }
        }


        initView();
        initEvent();
        setSelect(page01);
    }

    /**
     * 姜超
     * 获取聊天信息数据库
     */
    public static ChatDBManager getChatDBManager(){
        return chatDBManager;
    }
    public static FriendRequestDBManager getFriendRequestDBManager(){
        return friendRequestDBManager;
    }
    public static FileRequestDBManager getFileRequestDBManager(){
        return fileRequestDBManager;
    }
    public static SharedPreferences getSp(){
        return sp;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getPcAreaPartyPath();
        Log.e("change", "Resume");
        //startingTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("change", "Pause");
        //timer.cancel();
    }

    @Override
    protected void onDestroy() {
        Log.e("change", "Destroy");
        MyConnector.getInstance().closeLongConnect();
        super.onDestroy();
    }

    private void initEvent() {
        id_tab01.setOnClickListener(this);
        id_tab02.setOnClickListener(this);
        id_tab03.setOnClickListener(this);
        id_tab04.setOnClickListener(this);
//        id_tab05.setOnClickListener(this);
//        id_tab06.setOnClickListener(this);
        id_ContentViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /**
         * 姜超
         * 接收离线消息，保存进数据库
         */
        if(Login.userId!=""){
            try {
                sp = this.getSharedPreferences(Login.userId + "_chatNum", Context.MODE_PRIVATE);
                List<UserData.UserItem> friendList = Login.userFriend;
                for (UserData.UserItem friend : friendList) {
                    int chatNum = sp.getInt(friend.getUserId(), 0);
                    page06Fragment.friendChatNum.put(friend.getUserId(), chatNum);
                }
                SharedPreferences.Editor editor = sp.edit();
                Intent intent = getIntent();
                myChatList chats = (myChatList) intent.getExtras().get("chats");
                for (ChatData.ChatItem c : chats.getList()) {
                    if (c.getTargetType().equals(ChatData.ChatItem.TargetType.INDIVIDUAL)) {
                        if (page06Fragment.friendChatNum.containsKey(c.getSendUserId())) {
                            page06Fragment.friendChatNum.put(c.getSendUserId(), page06Fragment.friendChatNum.get(c.getSendUserId()) + 1);
                        } else {
                            page06Fragment.friendChatNum.put(c.getSendUserId(), 1);
                        }
                        ChatDBManager chatDB = getChatDBManager();
                        ChatObj chat = new ChatObj();
                        chat.date = c.getDate();
                        chat.msg = c.getChatBody();
                        chat.receiver_id = c.getReceiveUserId();
                        chat.sender_id = c.getSendUserId();
                        chatDB.addChatSQL(chat, Login.userId);
                        int chatNum = sp.getInt(c.getSendUserId(), 0);
                        editor.putInt(c.getSendUserId(), ++chatNum);
                        editor.commit();
                    } else if(c.getTargetType().equals(ChatData.ChatItem.TargetType.DOWNLOAD)){
                        fileObj fileRequest = new fileObj();
                        fileRequest.setFileDate(c.getFileDate());
                        fileRequest.setFileName(c.getFileName());
                        fileRequest.setSenderId(c.getSendUserId());
                        fileRequest.setFileSize(Integer.parseInt(c.getFileSize()));
                        FileRequestDBManager fileRequestDBManager = MainActivity.getFileRequestDBManager();
                        fileRequestDBManager.addFileRequestSQL(fileRequest, Login.userId + "transform");
                        Message fileRequestMsg = MainActivity.handlerTab06.obtainMessage();
                        fileRequestMsg.obj = fileRequest;
                        fileRequestMsg.what = OrderConst.addFileRequest;
                        MainActivity.handlerTab06.sendMessage(fileRequestMsg);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void initView() {
        bottom = (LinearLayout) findViewById(R.id.idbottom_linearLayout);
        //id_tabBar = (HorizontalScrollView) findViewById(R.id.id_tabBar);
        id_ContentViewpager = (ViewPager) findViewById(R.id.id_contentViewPager);
        id_tab01 = (LinearLayout) findViewById(R.id.id_tab01);
        id_tab02 = (LinearLayout) findViewById(R.id.id_tab02);
        id_tab03 = (LinearLayout) findViewById(R.id.id_tab03);
        id_tab04 = (LinearLayout) findViewById(R.id.id_tab04);
        //id_tab05 = (LinearLayout) findViewById(R.id.id_tab05);
        //id_tab06 = (LinearLayout) findViewById(R.id.id_tab06);
        id_tab01_image = (ImageButton) findViewById(R.id.id_tab01_image);
        id_tab02_image = (ImageButton) findViewById(R.id.id_tab02_image);
        id_tab03_image = (ImageButton) findViewById(R.id.id_tab03_image);
        id_tab04_image = (ImageButton) findViewById(R.id.id_tab04_image);
        //id_tab05_image = (ImageButton) findViewById(R.id.id_tab05_image);
        //id_tab06_image = (ImageButton) findViewById(R.id.id_tab06_image);
        id_tab01_text = (TextView) findViewById(R.id.id_tab01_text);
        id_tab02_text = (TextView) findViewById(R.id.id_tab02_text);
        id_tab03_text = (TextView) findViewById(R.id.id_tab03_text);
        id_tab04_text = (TextView) findViewById(R.id.id_tab04_text);
        //id_tab05_text = (TextView) findViewById(R.id.id_tab05_text);
        //id_tab06_text = (TextView) findViewById(R.id.id_tab06_text);

        fragments = new ArrayList<>();
        tabContent01 = new page01Fragment();
        tabContent02 = new page02Fragment();
        tabContent03 = new page03Fragment();
        //tabContent05 = new page05Fragment(); // VIP
        tabContent06 = new page06Fragment();

        //姜超
        btFolderFragment = new BtFolderFragment();
        downloadFolderFragment = new DownloadFolderFragment();
        downloadStateFragment = new DownloadStateFragment();

        fragments.add(tabContent01);
        fragments.add(tabContent02);
        fragments.add(tabContent03);
        fragments.add(tabContent06);
        //fragments.add(tabContent05);

        // 用于传递参数（更新首页中动态切换的图片）
        handlerTab01 = new MyHandler(tabContent01);
        handlerTab06 = new MyHandler(tabContent06);

        //姜超
        btHandler = new MyHandler(btFolderFragment);
        downloadHandler = new MyHandler(downloadFolderFragment);
        stateHandler = new MyHandler(downloadStateFragment);


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
        id_ContentViewpager.setAdapter(adapter);
        id_ContentViewpager.setOffscreenPageLimit(fragments.size());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_tab01:
                setSelect(page01);
                break;
            case R.id.id_tab02:
                setSelect(page02);
                break;
            case R.id.id_tab03:
                setSelect(page03);
                break;
            case R.id.id_tab04:
                setSelect(page04);
                break;
//            case R.id.id_tab05:
//                setSelect(page05);
//                break;
//            case R.id.id_tab06:
//                setSelect(page06);
//                break;
        }
    }

    /**
     * 1. 当用户点击按钮时切换内容区、切换按钮显示效果
     * 2. 当用户滑动内容区进行切换时切换按钮显示效果
     */
    private void setSelect(int pageNum) {
        currentPage = pageNum;
        resetImgText();
        if (id_ContentViewpager.getCurrentItem() != pageNum) {
            id_ContentViewpager.setCurrentItem(pageNum);
        }
        switch (pageNum) {
            case page01:
                //id_tabBar.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                id_tab01_image.setImageResource(R.drawable.tab01_pressed);
                id_tab01_text.setTextColor(Color.rgb(230, 87, 87));
                bottom.setVisibility(View.VISIBLE);
                break;
            case page02:
                //id_tabBar.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                id_tab02_image.setImageResource(R.drawable.tab02_pressed);
                id_tab02_text.setTextColor(Color.rgb(230, 87, 87));
                bottom.setVisibility(View.VISIBLE);
                break;
            case page03:
                id_tab03_image.setImageResource(R.drawable.tab03_pressed);
                id_tab03_text.setTextColor(Color.rgb(230, 87, 87));
                bottom.setVisibility(View.VISIBLE);
                break;
            case page04:
                id_tab04_image.setImageResource(R.drawable.tab06_pressed);
                id_tab04_text.setTextColor(Color.rgb(230, 87, 87));
                bottom.setVisibility(View.VISIBLE);
                break;
//            case page05:
//                //id_tabBar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//                id_tab05_image.setImageResource(R.drawable.tab04_pressed);
//                id_tab05_text.setTextColor(Color.rgb(230, 87, 87));
//                bottom.setVisibility(View.VISIBLE);
//                //tabContent04.loadDisks();
//                break;
//            case page06:
//                //id_tabBar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
//                id_tab06_image.setImageResource(R.drawable.tab05_pressed);
//                id_tab06_text.setTextColor(Color.rgb(230, 87, 87));
//                bottom.setVisibility(View.VISIBLE);
//                break;
        }
    }

    /**
     * 将底部栏目涉及的图片按文字置为暗色
     */
    private void resetImgText() {
        id_tab01_image.setImageResource(R.drawable.tab01_normal);
        id_tab02_image.setImageResource(R.drawable.tab02_normal);
        id_tab03_image.setImageResource(R.drawable.tab03_normal);
        id_tab04_image.setImageResource(R.drawable.tab06_normal);
//        id_tab05_image.setImageResource(R.drawable.tab04_normal);
//        id_tab06_image.setImageResource(R.drawable.tab05_normal);
        id_tab01_text.setTextColor(Color.rgb(153, 153, 153));
        id_tab02_text.setTextColor(Color.rgb(153, 153, 153));
        id_tab03_text.setTextColor(Color.rgb(153, 153, 153));
        id_tab04_text.setTextColor(Color.rgb(153, 153, 153));
//        id_tab05_text.setTextColor(Color.rgb(153, 153, 153));
//        id_tab06_text.setTextColor(Color.rgb(153, 153, 153));
    }

    public  void showHelpInfoDialog(int layout){
        final ActionDialog_page dialog = new ActionDialog_page(this,layout);
        dialog.setCancelable(true);
        dialog.show();
    }

}
