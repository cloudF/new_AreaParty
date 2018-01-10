package com.dkzy.areaparty.phone.fragment02;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_playPicList;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment02.base.ImageAdapter_APP;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataControl;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataLoadTask;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileSystemType;
import com.dkzy.areaparty.phone.fragment02.ui.listBottomDialog_app;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.base.ImageAdapter;
import com.dkzy.areaparty.phone.fragment02.ui.listBottomDialog;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class imageLibActivity extends AppCompatActivity implements View.OnClickListener,ContentDataLoadTask.OnContentDataLoadListener{

    private final String tag = this.getClass().getSimpleName();

    ContentDataLoadTask mContentDataLoadTask;
    ProgressDialog mProgressDialog;

    private ImageView returnIV;
    private ImageView pcStateIV, tvStateIV;
    private TextView pcStateNameTV, tvStateNameTV, picsPlayListNumTV;
    private ListView folderSLV;
    private RecyclerView fileSGV;
    private LinearLayout picsPlayListLL, playList , menuList, playFolderList, toSelectBGM;

    MyAdapter<MediaItem> folderAdapter;
    ImageAdapter fileAdapter;

    MyAdapter<String> folderAdapter_app;
    ImageAdapter_APP fileAdapter_app;

    private LinearLayout shiftBar;
    private TextView app_file,pc_file;

    private boolean isAppContent = false;
    private String stringFolder = "";
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            shiftBar.setVisibility(View.VISIBLE);
            playList.setVisibility(View.VISIBLE);
            if (!isAppContent){
                if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                    MediafileHelper.resetMediaInfors();
                    this.finish();
                }
                else {
                    fileSGV.setVisibility(View.GONE);
                    menuList.setVisibility(View.GONE);
                    folderSLV.setVisibility(View.VISIBLE);
                    String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                    MediafileHelper.setCurrentPath(tempPath);
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    folderAdapter.notifyDataSetChanged();
                    fileAdapter.notifyDataSetChanged();
                }
            }else {
                if (folderSLV.getVisibility() == View.GONE){
                    fileSGV.setVisibility(View.GONE);
                    menuList.setVisibility(View.GONE);
                    folderSLV.setVisibility(View.VISIBLE);
                }else {
                    this.finish();
                }
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

        if (!(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())){
            isAppContent = true;
            app_file.setTextColor(Color.parseColor("#FF5050"));
            app_file.setBackgroundResource(R.drawable.barback03_right_pressed);
            pc_file.setTextColor(Color.parseColor("#707070"));
            pc_file.setBackgroundResource(R.drawable.barback03_left_normal);
        }

        if (mContentDataLoadTask == null){
            mContentDataLoadTask = new ContentDataLoadTask(this, FileSystemType.photo);
            mContentDataLoadTask.setmOnContentDataLoadListener(this);
            mContentDataLoadTask.execute();
            if (ContentDataControl.mPhotoFolder!=null)
                ContentDataControl.mPhotoFolder.clear();
        }
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
                shiftBar.setVisibility(View.VISIBLE);
                playList.setVisibility(View.VISIBLE);
                if (!isAppContent){
                    if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                        MediafileHelper.resetMediaInfors();
                        this.finish();
                    }
                    else {
                        fileSGV.setVisibility(View.GONE);
                        menuList.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.VISIBLE);
                        String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                        MediafileHelper.setCurrentPath("");
                        MediafileHelper.loadMediaLibFiles(myHandler);
                        folderAdapter.notifyDataSetChanged();
                        fileAdapter.notifyDataSetChanged();
                    }
                }else {
                    if (folderSLV.getVisibility() == View.GONE){
                        fileSGV.setVisibility(View.GONE);
                        menuList.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.VISIBLE);
                    }else {
                        this.finish();
                    }
                }
                break;
            case R.id.picsPlayListLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    Intent intent = new Intent(imageLibActivity.this, imageSetActivity.class);
                    intent.putExtra("isAppContent",isAppContent);
                    startActivity(intent);
                } else  Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                break;

            case R.id.app_file:
                isAppContent = true;
                app_file.setTextColor(Color.parseColor("#FF5050"));
                app_file.setBackgroundResource(R.drawable.barback03_right_pressed);
                pc_file.setTextColor(Color.parseColor("#707070"));
                pc_file.setBackgroundResource(R.drawable.barback03_left_normal);
                folderSLV.setAdapter(folderAdapter_app);
                break;
            case R.id.pc_file:
                if (!(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())){
                    Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }else {
                    isAppContent = false;
                    pc_file.setTextColor(Color.parseColor("#FF5050"));
                    pc_file.setBackgroundResource(R.drawable.barback03_left_pressed);
                    app_file.setTextColor(Color.parseColor("#707070"));
                    app_file.setBackgroundResource(R.drawable.barback03_right_normal);
                    folderSLV.setAdapter(folderAdapter);
                    if (MediafileHelper.mediaFolders.size() == 0) MediafileHelper.loadMediaLibFiles(myHandler);
                }
                break;
            case R.id.play_folder_list:
                showDialog();

                break;
            case R.id.to_select_bgm:
                startActivity(new Intent(imageLibActivity.this, audioSetActivity.class).putExtra("asBGM",true));
                break;
        }
    }

    public void showDialog(){
        final ActionDialog_playPicList dialog = new ActionDialog_playPicList(this);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAppContent){
                    if (MyApplication.isSelectedTVOnline()){
                        dlnaCast(stringFolder,"image");
                    } else  Toasty.warning(imageLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }
                else{
                    if(MyApplication.isSelectedPCOnline()) {
                        if(MyApplication.isSelectedTVOnline()) {
                            MediafileHelper.playAllMediaFile(OrderConst.imageAction_name,
                                    MediafileHelper.getCurrentPath(),
                                    MyApplication.getSelectedTVIP().name,
                                    myHandler);
                        } else  Toasty.warning(imageLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                    } else  Toasty.warning(imageLibActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();

                }
                dialog.dismiss();
            }
        });
    };
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
            public void bindView(ViewHolder holder, final MediaItem obj) {
                holder.setText(R.id.nameTV, obj.getName());
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shiftBar.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.GONE);
                        playList.setVisibility(View.GONE);
                        fileSGV.setVisibility(View.VISIBLE);
                        menuList.setVisibility(View.VISIBLE);

                        String tempPath = obj.getPathName();
                        MediafileHelper.setCurrentPath(tempPath);
                        MediafileHelper.loadMediaLibFiles(myHandler);
                        folderAdapter.notifyDataSetChanged();
                        fileSGV.swapAdapter(fileAdapter,false);
                        fileAdapter.notifyDataSetChanged();
                    }
                });

                holder.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        deleteDialog(obj.getPathName());
                        return true;
                    }
                });
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
    private void listDialog_app(FileItem file) {
        listBottomDialog_app dialog = new listBottomDialog_app();
        dialog.setFile(file,"image");
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
        playList = (LinearLayout) findViewById(R.id.play_list);
        picsPlayListNumTV = (TextView) findViewById(R.id.picsPlayListNumTV);
        menuList = (LinearLayout) findViewById(R.id.menu_list);
        playFolderList = (LinearLayout) findViewById(R.id.play_folder_list);
        toSelectBGM = (LinearLayout) findViewById(R.id.to_select_bgm);

        updateDeviceNetState(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(),
                MyApplication.isSelectedPCOnline()));

        fileSGV.setItemAnimator(new DefaultItemAnimator());
        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        folderSLV.setAdapter(folderAdapter);
        fileSGV.setAdapter(fileAdapter);

        shiftBar = (LinearLayout) findViewById(R.id.shiftBar);
        app_file = (TextView) findViewById(R.id.app_file);
        pc_file = (TextView) findViewById(R.id.pc_file);

    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnIV.setOnClickListener(this);
        picsPlayListLL.setOnClickListener(this);

        app_file.setOnClickListener(this);
        pc_file.setOnClickListener(this);
        playFolderList.setOnClickListener(this);
        toSelectBGM.setOnClickListener(this);

        folderSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                shiftBar.setVisibility(View.GONE);
                folderSLV.setVisibility(View.GONE);
                playList.setVisibility(View.GONE);
                fileSGV.setVisibility(View.VISIBLE);
                menuList.setVisibility(View.VISIBLE);

                if (!isAppContent){
                    // ... 还要判断是否加 “\”
                    String tempPath = MediafileHelper.mediaFolders.get(i).getPathName();
                    MediafileHelper.setCurrentPath(tempPath);
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    folderAdapter.notifyDataSetChanged();
                    fileSGV.swapAdapter(fileAdapter,false);
                    fileAdapter.notifyDataSetChanged();
                }else {

                    fileAdapter_app = new ImageAdapter_APP(imageLibActivity.this,ContentDataControl.getFileItemListByFolder(FileSystemType.photo,(String)adapterView.getAdapter().getItem(i)));
                    fileAdapter_app.isFirstOnly(false);
                    fileSGV.swapAdapter(fileAdapter_app,false);

                    fileAdapter_app.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
                        @Override
                        public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                            FileItem fileItem = (FileItem) baseQuickAdapter.getItem(i);
                            switch (view.getId()){
                                case R.id.castIV:
                                    if (MyApplication.isSelectedTVOnline()){
                                        dlnaCast(fileItem,"image");
                                    } else  Toasty.warning(imageLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                                    break;
                                case R.id.addToSetIV:
                                    listDialog_app(fileItem);
                                    break;
                            }
                        }
                    });
                }

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
                case OrderConst.mediaAction_DELETE_OK:
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    break;
            }
        }
    };


    @Override
    public void onStartLoad() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.show();
    }

    @Override
    public void onFinishLoad() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        folderAdapter_app = new MyAdapter<String>(ContentDataControl.getFolder(FileSystemType.photo), R.layout.tab02_folder_item) {
            @Override
            public void bindView(ViewHolder holder, final String obj) {
                holder.setText(R.id.nameTV, obj);
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shiftBar.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.GONE);
                        playList.setVisibility(View.GONE);
                        fileSGV.setVisibility(View.VISIBLE);
                        menuList.setVisibility(View.VISIBLE);

                        stringFolder = obj;
                        fileAdapter_app = new ImageAdapter_APP(imageLibActivity.this,ContentDataControl.getFileItemListByFolder(FileSystemType.photo,obj));
                        fileAdapter_app.isFirstOnly(false);
                        fileSGV.swapAdapter(fileAdapter_app,false);

                        fileAdapter_app.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
                            @Override
                            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                                FileItem fileItem = (FileItem) baseQuickAdapter.getItem(i);
                                switch (view.getId()){
                                    case R.id.castIV:
                                        if (MyApplication.isSelectedTVOnline()){
                                            dlnaCast(fileItem,"image");
                                        } else  Toasty.warning(imageLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();

                                        break;
                                    case R.id.addToSetIV:
                                        listDialog_app(fileItem);
                                        break;
                                }
                            }
                        });
                    }
                });
            }
        };
        if (!(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())){
            folderSLV.setAdapter(folderAdapter_app);
        }
    }

    private void deleteDialog(final String path) {
        final DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();
        deleteDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                Log.w("videoLibActivity","DELETE");
                new Send2PCThread(OrderConst.imageAction_name, OrderConst.mediaAction_DELETE_command, path, myHandler).start();
            }
        });
        deleteDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
    }
}
