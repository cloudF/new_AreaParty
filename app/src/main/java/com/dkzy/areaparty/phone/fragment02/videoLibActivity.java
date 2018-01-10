package com.dkzy.areaparty.phone.fragment02;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataControl;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataLoadTask;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileSystemType;
import com.dkzy.areaparty.phone.model_comman.MyAdapter;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.fragment01.ui.SwipeListView;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;
import static com.dkzy.areaparty.phone.fragment02.utils.StringFormat.ToDBC;

/**
 * Project Name： FamilyCentralControler
 * Description:   显示已扫描到的PC设备
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class videoLibActivity extends Activity implements View.OnClickListener,ContentDataLoadTask.OnContentDataLoadListener{

    private final String tag = this.getClass().getSimpleName();
    ContentDataLoadTask mContentDataLoadTask;
    ProgressDialog mProgressDialog;

    private ImageView returnIV;
    private ImageView pcStateIV, tvStateIV;
    private TextView pcStateNameTV, tvStateNameTV;
    private SwipeListView folderSLV, fileSLV;

    MyAdapter<MediaItem> folderAdapter;
    MyAdapter<MediaItem> fileAdapter;

    MyAdapter<String> folderAdapter_app;
    MyAdapter<FileItem> fileAdapter_app;

    private MediaItem currentFile;
    private LinearLayout shiftBar;
    private TextView app_file,pc_file;

    private boolean isAppContent = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (isAppContent){
                if (folderSLV.getVisibility() == View.VISIBLE){
                    this.finish();
                }else {
                    shiftBar.setVisibility(View.VISIBLE);
                    folderSLV.setVisibility(View.VISIBLE);
                    fileSLV.setVisibility(View.GONE);
                }

            } else if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                MediafileHelper.resetMediaInfors();
                this.finish();
            }
            else {
                String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                MediafileHelper.setCurrentPath("");
                MediafileHelper.loadMediaLibFiles(myHandler);
                shiftBar.setVisibility(View.VISIBLE);
                folderSLV.setVisibility(View.VISIBLE);
                fileSLV.setVisibility(View.GONE);
                folderAdapter.notifyDataSetChanged();
                fileAdapter.notifyDataSetChanged();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_videolib_activity);
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
            if (ContentDataControl.mVideoFolder!=null)
                ContentDataControl.mVideoFolder.clear();
            mContentDataLoadTask = new ContentDataLoadTask(this, FileSystemType.video);
            mContentDataLoadTask.setmOnContentDataLoadListener(this);
            mContentDataLoadTask.execute();
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
                if (isAppContent){
                    if (folderSLV.getVisibility() == View.VISIBLE){
                        this.finish();
                    }else {
                        shiftBar.setVisibility(View.VISIBLE);
                        folderSLV.setVisibility(View.VISIBLE);
                        fileSLV.setVisibility(View.GONE);
                    }

                } else if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                    MediafileHelper.resetMediaInfors();
                    this.finish();
                }
                else {
                    String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                    MediafileHelper.setCurrentPath(tempPath);
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    shiftBar.setVisibility(View.VISIBLE);
                    folderSLV.setVisibility(View.VISIBLE);
                    fileSLV.setVisibility(View.GONE);
                    folderAdapter.notifyDataSetChanged();
                    fileAdapter.notifyDataSetChanged();
                }
                break;
//            case R.id.app_video:
//                isAppContent = true;
//                app_video.setVisibility(View.GONE);
//                folderSLV.setAdapter(folderAdapter_app);
//                folderAdapter_app.notifyDataSetChanged();
//                Log.w("ContentDataControl", folderAdapter_app.getCount()+"");
//                break;
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
            public void bindView(final ViewHolder holder, final MediaItem obj) {
                if (RemoteDownloadActivity.rootPath != null && obj.getPathName().equals(RemoteDownloadActivity.rootPath)){
                    holder.setText(R.id.nameTV, "下载文件");
                }else {
                    holder.setText(R.id.nameTV, obj.getName());
                }

                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shiftBar.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.GONE);
                        fileSLV.setVisibility(View.VISIBLE);
                        String tempPath = obj.getPathName();
                        MediafileHelper.setCurrentPath(tempPath);
                        MediafileHelper.loadMediaLibFiles(myHandler);
                        folderAdapter.notifyDataSetChanged();
                        fileSLV.setAdapter(fileAdapter);
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
        fileAdapter = new MyAdapter<MediaItem>(MediafileHelper.mediaFiles, R.layout.tab02_videolib_item) {
            @Override
            public void bindView(ViewHolder holder, final MediaItem obj) {
                holder.setText(R.id.nameTV, ToDBC(obj.getName()));
                holder.setImage(R.id.thumbnailIV, obj.getThumbnailurl(), R.drawable.videotest, videoLibActivity.this);
                // 投屏事件
                holder.setOnClickListener(R.id.castLL, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(MyApplication.isSelectedPCOnline()) {
                            if(MyApplication.isSelectedTVOnline()) {
                                currentFile = obj;
                                Log.w("videoLibActivity1",MediafileHelper.getMediaType()+"*"+currentFile.getPathName()+"*"+currentFile.getName()+"*"+MyApplication.getSelectedTVIP().name);
                                MediafileHelper.playMediaFile(MediafileHelper.getMediaType(),
                                        currentFile.getPathName(),
                                        currentFile.getName(),
                                        MyApplication.getSelectedTVIP().name,
                                        myHandler);
                                startActivity(new Intent(getApplicationContext(), vedioPlayControl.class));
                            } else  Toasty.warning(videoLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                        } else  Toasty.warning(videoLibActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
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
        returnIV = (ImageView) findViewById(R.id.returnIV);
        pcStateIV = (ImageView) findViewById(R.id.pcStateIV);
        tvStateIV = (ImageView) findViewById(R.id.tvStateIV);
        pcStateNameTV = (TextView) findViewById(R.id.pcStateNameTV);
        tvStateNameTV = (TextView) findViewById(R.id.tvStateNameTV);
        folderSLV = (SwipeListView) findViewById(R.id.folderSLV);
        fileSLV = (SwipeListView) findViewById(R.id.fileSLV);

        shiftBar = (LinearLayout) findViewById(R.id.shiftBar);
        app_file = (TextView) findViewById(R.id.app_file);
        pc_file = (TextView) findViewById(R.id.pc_file);
        updateDeviceNetState(new TVPCNetStateChangeEvent(MyApplication.isSelectedTVOnline(),
                MyApplication.isSelectedPCOnline()));

        folderSLV.setAdapter(folderAdapter);
        fileSLV.setAdapter(fileAdapter);
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnIV.setOnClickListener(this);
        //app_video.setOnClickListener(this);
        app_file.setOnClickListener(this);
        pc_file.setOnClickListener(this);
        folderSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                shiftBar.setVisibility(View.GONE);
                folderSLV.setVisibility(View.GONE);
                fileSLV.setVisibility(View.VISIBLE);
                if (!isAppContent){
                    // ... 还要判断是否加 “\”
                    //.setVisibility(View.GONE);
                    String tempPath = MediafileHelper.mediaFolders.get(i).getPathName();
                    MediafileHelper.setCurrentPath(tempPath);
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    folderAdapter.notifyDataSetChanged();
                    fileSLV.setAdapter(fileAdapter);
                    fileAdapter.notifyDataSetChanged();
                }else {
                    Log.w("ContentDataControl",(String) adapterView.getAdapter().getItem(i));
                    fileAdapter_app = new MyAdapter<FileItem>(ContentDataControl.getFileItemListByFolder(FileSystemType.video,(String)adapterView.getAdapter().getItem(i)),R.layout.tab02_videolib_item) {
                        @Override
                        public void bindView(ViewHolder holder, final FileItem obj) {
                            holder.setText(R.id.nameTV, obj.getmFileName());
                            holder.setImage(R.id.thumbnailIV, obj.getmFilePath(), R.drawable.videotest, videoLibActivity.this);
                            holder.setOnClickListener(R.id.castLL, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (MyApplication.isSelectedTVOnline()){
                                        dlnaCast(obj,"video");
                                    } else  Toasty.warning(videoLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                                }
                            });
                        }
                    };
                    fileSLV.setAdapter(fileAdapter_app);

                }

            }
        });

        fileSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ....
            }
        });
    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCMedia_OK:
                    folderAdapter.notifyDataSetChanged();
                    fileAdapter.notifyDataSetChanged();
                    if (MediafileHelper.mediaFolders.size() == 0){
                       // app_video.setVisibility(View.GONE);
                    } //app_video.setVisibility(View.VISIBLE);
                    break;
                case OrderConst.getPCMedia_Fail:
                    break;
                case OrderConst.playPCMedia_OK:
                    Toasty.success(videoLibActivity.this, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    MediafileHelper.addRecentVideos(currentFile);
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(videoLibActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
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
        folderAdapter_app = new MyAdapter<String>(ContentDataControl.getFolder(FileSystemType.video), R.layout.tab02_folder_item) {
            @Override
            public void bindView(ViewHolder holder, final String obj) {
                holder.setText(R.id.nameTV, obj);
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shiftBar.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.GONE);
                        fileSLV.setVisibility(View.VISIBLE);
                        Log.w("ContentDataControl",obj);
                        fileAdapter_app = new MyAdapter<FileItem>(ContentDataControl.getFileItemListByFolder(FileSystemType.video,obj),R.layout.tab02_videolib_item) {
                            @Override
                            public void bindView(ViewHolder holder, final FileItem obj) {
                                holder.setText(R.id.nameTV, obj.getmFileName());
                                holder.setImage(R.id.thumbnailIV, obj.getmFilePath(), R.drawable.videotest, videoLibActivity.this);
                                holder.setOnClickListener(R.id.castLL, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (MyApplication.isSelectedTVOnline()){
                                            dlnaCast(obj,"video");
                                        } else  Toasty.warning(videoLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                            }
                        };
                        fileSLV.setAdapter(fileAdapter_app);
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
                new Send2PCThread(OrderConst.videoAction_name, OrderConst.mediaAction_DELETE_command, path, myHandler).start();
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
