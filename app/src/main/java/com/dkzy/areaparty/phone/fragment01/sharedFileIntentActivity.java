package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.base.SharedFileContentAdapter;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.ui.SwipeListView;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import protocol.Msg.AddFileMsg;
import protocol.Msg.DeleteFileMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Project Name： android
 * Description:
 * Author: boris
 * Time: 2017/3/29 20:45
 */

public class sharedFileIntentActivity extends Activity implements View.OnClickListener {
    private ImageButton returnToDiskListIM;
    private LinearLayout sharedPicLL;
    private LinearLayout sharedMusicLL;
    private LinearLayout sharedMovieLL;
    private LinearLayout sharedDocumentLL;
    private LinearLayout sharedRarLL;
    private LinearLayout sharedOtherLL;
    private TextView sharedPicNumTV;
    private TextView sharedMusicNumTV;
    private TextView sharedMovieNumTV;
    private TextView sharedDocumentNumTV;
    private TextView sharedRarNumTV;
    private TextView sharedOtherNumTV;
    private ScrollView sharedFileSV;
    private SwipeListView sharedFileContentLV;
    private SwipeMenuCreator creator;

    private LinearLayout sharedFileRefreshLL;
    private LinearLayout sharedFileMoreLL;

    private View loadingView;
    private AlertDialog dialog;
    private SharedFileContentAdapter adapter;

    private List<SharedfileBean> pic = new ArrayList<>();
    private List<SharedfileBean> mus = new ArrayList<>();
    private List<SharedfileBean> mov = new ArrayList<>();
    private List<SharedfileBean> doc = new ArrayList<>();
    private List<SharedfileBean> rar = new ArrayList<>();
    private List<SharedfileBean> oth = new ArrayList<>();
    private int nowType;  // 标识当前类别（0, 1, 2, 3, 4, 5）,方便后续加删除操作使用

    public static Handler handler ;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab04_sharedfile_content);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initData();
        initView();
        initEvent();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        refreshData();
                        break;
                    case 2:
                        Toasty.error(sharedFileIntentActivity.this, "取消分享失败");
                    default:break;
                }
            }
        };

    }

    private void initData() {
        pic.clear();
        mus.clear();
        mov.clear();
        doc.clear();
        rar.clear();
        oth.clear();
        List<SharedfileBean> mySharedFiles = MyApplication.getMySharedFiles();
        for (SharedfileBean file : mySharedFiles) {
            int fileType = FileTypeConst.determineFileType(file.name);
            switch(fileType) {
                case FileTypeConst.pic:
                    pic.add(file);
                    break;
                case FileTypeConst.music:
                    mus.add(file);
                    break;
                case FileTypeConst.video:
                    mov.add(file);
                    break;
                case FileTypeConst.excel:
                case FileTypeConst.pdf:
                case FileTypeConst.ppt:
                case FileTypeConst.txt:
                case FileTypeConst.word:
                    doc.add(file);
                    break;
                case FileTypeConst.zip:
                    rar.add(file);
                    break;
                case FileTypeConst.none:
                    oth.add(file);
                    break;
                default:oth.add(file);
                    break;
            }
        }
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(sharedFileIntentActivity.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitle("取消分享");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);

                menu.addMenuItem(deleteItem);
            }
        };

    }
    public void refreshData(){
        initData();

        String numpic = "(" + pic.size() + ")";
        String nummus = "(" + mus.size() + ")";
        String numvid = "(" + mov.size() + ")";
        String numdoc = "(" + doc.size() + ")";
        String numrar = "(" + rar.size() + ")";
        String numoth = "(" + oth.size() + ")";
        sharedPicNumTV.setText(numpic);
        sharedMusicNumTV.setText(nummus);
        sharedMovieNumTV.setText(numvid);
        sharedDocumentNumTV.setText(numdoc);
        sharedRarNumTV.setText(numrar);
        sharedOtherNumTV.setText(numoth);
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    private void gettingFileInfor() {
        dialog.show();
    }

    private void initEvent() {
        returnToDiskListIM.setOnClickListener(this);
        sharedPicLL.setOnClickListener(this);
        sharedMusicLL.setOnClickListener(this);
        sharedMovieLL.setOnClickListener(this);
        sharedDocumentLL.setOnClickListener(this);
        sharedRarLL.setOnClickListener(this);
        sharedOtherLL.setOnClickListener(this);


        sharedFileRefreshLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();
            }
        });
        sharedFileContentLV.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });
        sharedFileContentLV.setOnTouchListener(new View.OnTouchListener() {
            private int localWidth = 0;
            private int localHeight = 0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        localWidth = (int)motionEvent.getX();
                        localHeight = (int)motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int sx = (int)motionEvent.getX();
                        int sy = (int)motionEvent.getY();
                        if (Math.abs(localWidth - sx) > 100) {  //重点就是来判断横向或者纵向滑动的距离来分配焦点
                            sharedFileSV.requestDisallowInterceptTouchEvent(true);  //拦截ScrollView的事件，让listView能滑动
                        }else {
                            sharedFileSV.requestDisallowInterceptTouchEvent(false);   //取消ScrollView的拦截事件，让listView不能滑动
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        localWidth = 0;
                        localHeight = 0;
                        break;                  }
                return false;
            }
        });
        sharedFileContentLV.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch(index) {
                    case 0:
                        // 取消分享
                        cancelShare(position);
                        final SharedfileBean file = getSharedfileBeanList(nowType).get(position);
                        Log.w("SharedFileIntent",file.toString());
                        new Thread(){
                            @Override
                            public void run() {

                                DeleteFileMsg.DeleteFileReq.Builder builder = DeleteFileMsg.DeleteFileReq.newBuilder();
                                builder.setFileId(file.id);
                                builder.setFileName(file.name);
                                builder.setUserId(Login.userId);
                                builder.setFileInfo(file.des);
                                builder.setFileSize(file.size);
                                //builder.setFileUrl(file.url);
                                //builder.setFilePwd(file.pwd);
                                try {
                                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.DELETE_FILE_REQ.getNumber(),
                                            builder.build().toByteArray());
                                    Login.base.writeToServer(Login.outputStream, byteArray);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                        break;
                }
                return false;
            }
        });
        // ...
    }

    private void cancelShare(int position) {
        List<SharedfileBean> list = new ArrayList<>();
        switch(nowType) {
            case 0:
                list = pic;
                break;
            case 1:
                list = mus;
                break;
            case 2:
                list = mov;
                break;
            case 3:
                list = doc;
                break;
            case 4:
                list = rar;
                break;
            case 5:
                list = oth;
                break;
        }
        // 后期添加取消分享操作.....................
        Log.e("sharedFile", "取消分享" + list.get(position).name + list.get(position).id);
    }

    private void initView() {
        returnToDiskListIM  = (ImageButton) findViewById(R.id.returnToDiskListIM);
        sharedPicLL         = (LinearLayout) findViewById(R.id.sharedPicLL);
        sharedMusicLL       = (LinearLayout) findViewById(R.id.sharedMusicLL);
        sharedMovieLL       = (LinearLayout) findViewById(R.id.sharedMovieLL);
        sharedDocumentLL    = (LinearLayout) findViewById(R.id.sharedDocumentLL);
        sharedRarLL         = (LinearLayout) findViewById(R.id.sharedRarLL);
        sharedOtherLL       = (LinearLayout) findViewById(R.id.sharedOtherLL);
        sharedPicNumTV      = (TextView) findViewById(R.id.sharedPicNumTV);
        sharedMusicNumTV    = (TextView) findViewById(R.id.sharedMusicNumTV);
        sharedMovieNumTV    = (TextView) findViewById(R.id.sharedMovieNumTV);
        sharedDocumentNumTV = (TextView) findViewById(R.id.sharedDocumentNumTV);
        sharedRarNumTV      = (TextView) findViewById(R.id.sharedRarNumTV);
        sharedOtherNumTV    = (TextView) findViewById(R.id.sharedOtherNumTV);
        sharedFileContentLV = (SwipeListView) findViewById(R.id.sharedFileContentLV);
        sharedFileRefreshLL = (LinearLayout) findViewById(R.id.sharedFileRefreshLL);
        sharedFileMoreLL    = (LinearLayout) findViewById(R.id.sharedFileMoreLL);
        sharedFileSV        = (ScrollView) findViewById(R.id.sharedFileSV);

        sharedFileContentLV.setMenuCreator(creator);


        loadingView = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView);
        builder.setCancelable(false);
        dialog = builder.create();

        String numpic = "(" + pic.size() + ")";
        String nummus = "(" + mus.size() + ")";
        String numvid = "(" + mov.size() + ")";
        String numdoc = "(" + doc.size() + ")";
        String numrar = "(" + rar.size() + ")";
        String numoth = "(" + oth.size() + ")";
        sharedPicNumTV.setText(numpic);
        sharedMusicNumTV.setText(nummus);
        sharedMovieNumTV.setText(numvid);
        sharedDocumentNumTV.setText(numdoc);
        sharedRarNumTV.setText(numrar);
        sharedOtherNumTV.setText(numoth);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.returnToDiskListIM:
                finish();
                break;
            case R.id.sharedPicLL:
                nowType = 0;
                //MyApplication.getSharedFileDBManager().addSharedFileSQL(
                //        new SharedfileBean("testname", "haha/ha", 100, "this is a test", System.currentTimeMillis()), DBConst.sharedPicTB);
                SharedFileContentAdapter picAdapter = new SharedFileContentAdapter(pic, this);
                adapter = picAdapter;
                sharedFileContentLV.setAdapter(adapter);

                break;
            case R.id.sharedMusicLL:
                //List<SharedfileBean> list = MyApplication.getSharedFileDBManager().selectMySharedFileSQL(DBConst.sharedPicTB);
                //Toasty.info(this, (list.size() > 0)? list.get(0).name : "未查询到数据", Toast.LENGTH_SHORT, true).show();
                nowType = 1;
                SharedFileContentAdapter musAdapter = new SharedFileContentAdapter(mus, this);
                adapter = musAdapter;
                sharedFileContentLV.setAdapter(adapter);

                break;
            case R.id.sharedMovieLL:
                nowType = 2;
                SharedFileContentAdapter movAdapter = new SharedFileContentAdapter(mov, this);
                adapter = movAdapter;
                sharedFileContentLV.setAdapter(adapter);

                //MyApplication.getSharedFileDBManager().deleteSharedFileSQL(1, DBConst.sharedPicTB);
                break;
            case R.id.sharedDocumentLL:
                //Toasty.info(this, MyApplication.getLaunchTimeId(), Toast.LENGTH_SHORT, true).show();
                nowType = 3;
                SharedFileContentAdapter docAdapter = new SharedFileContentAdapter(doc, this);
                adapter = docAdapter;
                sharedFileContentLV.setAdapter(adapter);

                break;
            case R.id.sharedRarLL:
                nowType = 4;
                SharedFileContentAdapter rarAdapter = new SharedFileContentAdapter(rar, this);
                adapter = rarAdapter;
                sharedFileContentLV.setAdapter(adapter);

                break;
            case R.id.sharedOtherLL:
                nowType = 5;
                SharedFileContentAdapter othAdapter = new SharedFileContentAdapter(oth, this);
                adapter = othAdapter;
                sharedFileContentLV.setAdapter(adapter);
                break;
        }
    }

    private List<SharedfileBean> getSharedfileBeanList(int type){
        switch (type){
            case 0: return pic;
            case 1: return mus;
            case 2: return mov;
            case 3: return doc;
            case 4: return rar;
            case 5: return oth;
            default: return new ArrayList<>();
        }
    }
}
