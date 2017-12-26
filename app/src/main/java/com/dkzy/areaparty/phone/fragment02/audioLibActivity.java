package com.dkzy.areaparty.phone.fragment02;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
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
import com.dkzy.areaparty.phone.fragment02.ui.listBottomDialog;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.fragment01.ui.SwipeListView;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;

/**
 * Project Name： FamilyCentralControler
 * Description:   音频库
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class audioLibActivity extends AppCompatActivity implements View.OnClickListener,ContentDataLoadTask.OnContentDataLoadListener{

    private final String tag = this.getClass().getSimpleName();

    ContentDataLoadTask mContentDataLoadTask;
    ProgressDialog mProgressDialog;

    private ImageView returnIV;
    private ImageView pcStateIV, tvStateIV;
    private TextView pcStateNameTV, tvStateNameTV;
    private TextView musicNumTV;
    private SwipeListView folderSLV, fileSLV;
    private TextView currentMusicNameTV, audiosPlayListNumTV;
    private ImageView playOrPauseIV;
    private LinearLayout audiosPlayListLL, playList, musicCount, audioPlayControl, menuList, addToList;

    MyAdapter<MediaItem> folderAdapter;
    MyAdapter<MediaItem> fileAdapter;

    MyAdapter<String> folderAdapter_app;
    MyAdapter<FileItem> fileAdapter_app;

    private boolean isPlaying = false;
    private MediaItem currentFile;

    private LinearLayout shiftBar;
    private TextView app_file,pc_file;
    private boolean isAppContent = false;

    private boolean isSelectModel = false;
    private List<Integer> selectedList = new ArrayList<>();

    private String stringFolder = "";

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (isSelectModel){
                cancelSelectModel();
            }else{
                musicCount.setVisibility(View.GONE);
                shiftBar.setVisibility(View.VISIBLE);
                playList.setVisibility(View.VISIBLE);
                if (!isAppContent){
                    if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                        MediafileHelper.resetMediaInfors();
                        this.finish();
                    }
                    else {
                        if (MediafileHelper.getCurrentPath().lastIndexOf("\\")!=-1){
                            String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                            MediafileHelper.setCurrentPath("");
                            MediafileHelper.loadMediaLibFiles(myHandler);
                            fileSLV.setVisibility(View.GONE);
                            folderSLV.setVisibility(View.VISIBLE);
                            folderAdapter.notifyDataSetChanged();
                            fileAdapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    if (folderSLV.getVisibility() == View.GONE){
                        fileSLV.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.VISIBLE);
                    }else {
                        this.finish();
                    }

                }
            }


        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        audiosPlayListNumTV.setText("(" + MediafileHelper.getAudioSets().size() + ")");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_audiolib_activity);
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
            if (ContentDataControl.mMusicFolder!=null)
                ContentDataControl.mMusicFolder.clear();
            mContentDataLoadTask = new ContentDataLoadTask(this, FileSystemType.music);
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
                if (isSelectModel){
                    cancelSelectModel();
                }else{
                    musicCount.setVisibility(View.GONE);
                    shiftBar.setVisibility(View.VISIBLE);
                    playList.setVisibility(View.VISIBLE);
                    if (!isAppContent){
                        if(MediafileHelper.isPathContained(MediafileHelper.getCurrentPath(), MediafileHelper.getStartPathList())) {
                            MediafileHelper.resetMediaInfors();
                            this.finish();
                        }
                        else {
                            String tempPath = MediafileHelper.getCurrentPath().substring(0, MediafileHelper.getCurrentPath().lastIndexOf("\\"));
                            MediafileHelper.setCurrentPath(tempPath);
                            MediafileHelper.loadMediaLibFiles(myHandler);
                            fileSLV.setVisibility(View.GONE);
                            folderSLV.setVisibility(View.VISIBLE);
                            folderAdapter.notifyDataSetChanged();
                            fileAdapter.notifyDataSetChanged();
                        }
                    }else{
                        if (folderSLV.getVisibility() == View.GONE){
                            fileSLV.setVisibility(View.GONE);
                            folderSLV.setVisibility(View.VISIBLE);
                        }else {
                            this.finish();
                        }

                    }
                }
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
            case R.id.audiosPlayListLL:
                if((MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())||(MyApplication.selectedTVVerified && MyApplication.isSelectedTVOnline())) {
                    Intent intent = new Intent(audioLibActivity.this, audioSetActivity.class);
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
            case R.id.addToList:
                if (selectedList.size()>0){
                    for (Integer integer : selectedList){
                        Log.w("audioLibActivity",""+integer);
                    }
                }
                if (!isAppContent){
                    List<MediaItem> fileList = new ArrayList<>();
                    for (Integer i : selectedList){
                        MediaItem item = fileAdapter.getItem(i);
                        fileList.add(item);
                    }
                    listDialog(fileList);
                }

                break;
            case R.id.music_count:
                if (isAppContent){
                    if (MyApplication.isSelectedTVOnline()){
                        dlnaCast(stringFolder,"audio");
                    } else  Toasty.warning(audioLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                }else {

                }
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
            public void bindView(ViewHolder holder, final MediaItem obj) {
                holder.setText(R.id.nameTV, obj.getName());
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shiftBar.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.GONE);
                        playList.setVisibility(View.GONE);
                        musicCount.setVisibility(View.VISIBLE);
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
        fileAdapter = new MyAdapter<MediaItem>(MediafileHelper.mediaFiles, R.layout.tab02_audio_item) {
            @Override
            public void bindView(final ViewHolder holder, MediaItem obj) {
                final MediaItem file = MediafileHelper.mediaFiles.get(holder.getItemPosition());
                holder.setText(R.id.nameTV, obj.getName());
                holder.setOnClickListener(R.id.addToSetIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isSelectModel){
                            listDialog(file);
                        }
                    }
                });
                holder.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!isSelectModel){
                            Log.w("audioLibActivity","setOnLongClickListener");
                            isSelectModel = true;
                            selectedList.add(holder.getItemPosition());
                            holder.setBackgroundSelect();
                            audioPlayControl.setVisibility(View.GONE);
                            menuList.setVisibility(View.VISIBLE);
                            return true;
                        }
                        return false;
                    }
                });
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isSelectModel){
                            Integer i = holder.getItemPosition();
                            if (!selectedList.contains(i)){
                                selectedList.add(i);
                                holder.setBackgroundSelect();
                            }else {
                                selectedList.remove(i);
                                holder.clearBackgroundSelect();
                                if (selectedList.size() == 0){
                                    menuList.setVisibility(View.GONE);
                                    audioPlayControl.setVisibility(View.VISIBLE);
                                    isSelectModel = false;
                                }
                            }

                        }else {
                            if(MyApplication.isSelectedPCOnline()) {
                                if(MyApplication.isSelectedTVOnline()) {
                                    currentFile = MediafileHelper.mediaFiles.get(holder.getItemPosition());
                                    MediafileHelper.playMediaFile(currentFile.getType(), currentFile.getPathName(), currentFile.getName(),
                                            MyApplication.getSelectedTVIP().name, myHandler);
                                } else Toasty.warning(audioLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                            } else Toasty.warning(audioLibActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();

                        }
                    }
                });
            }
        };
    }

    private void listDialog(MediaItem file) {
        listBottomDialog dialog = new listBottomDialog();
        dialog.setFile(file);
        dialog.show(getSupportFragmentManager());
    }
    private void listDialog(List<MediaItem> fileList) {
        listBottomDialog dialog = new listBottomDialog();
        dialog.setFileList(fileList);
        dialog.show(getSupportFragmentManager());
        cancelSelectModel();
    }
    private void listDialog_app(FileItem file) {
        listBottomDialog_app dialog = new listBottomDialog_app();
        dialog.setFile(file,"audio");
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
        folderSLV = (SwipeListView) findViewById(R.id.folderSLV);
        fileSLV = (SwipeListView) findViewById(R.id.fileSLV);
        currentMusicNameTV = (TextView) findViewById(R.id.currentMusicNameTV);
        playOrPauseIV = (ImageView) findViewById(R.id.playOrPauseIV);
        musicNumTV = (TextView) findViewById(R.id.numTV);
        audiosPlayListLL = (LinearLayout) findViewById(R.id.audiosPlayListLL);
        playList = (LinearLayout) findViewById(R.id.play_list);
        musicCount = (LinearLayout) findViewById(R.id.music_count);
        audiosPlayListNumTV = (TextView) findViewById(R.id.audiosPlayListNumTV);
        audioPlayControl = (LinearLayout) findViewById(R.id.audio_play_control);
        menuList = (LinearLayout) findViewById(R.id.menu_list);
        addToList = (LinearLayout) findViewById(R.id.addToList);

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
        playOrPauseIV.setOnClickListener(this);
        audiosPlayListLL.setOnClickListener(this);

        app_file.setOnClickListener(this);
        pc_file.setOnClickListener(this);
        addToList.setOnClickListener(this);
        musicCount.setOnClickListener(this);

        folderSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                shiftBar.setVisibility(View.GONE);
                folderSLV.setVisibility(View.GONE);
                playList.setVisibility(View.GONE);
                musicCount.setVisibility(View.VISIBLE);
                fileSLV.setVisibility(View.VISIBLE);
                if (!isAppContent){
                    String tempPath = MediafileHelper.mediaFolders.get(i).getPathName();
                    MediafileHelper.setCurrentPath(tempPath);
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    folderAdapter.notifyDataSetChanged();
                    fileSLV.setAdapter(fileAdapter);
                    fileAdapter.notifyDataSetChanged();
                }else {
                    fileAdapter_app = new MyAdapter<FileItem>(ContentDataControl.getFileItemListByFolder(FileSystemType.music,(String)adapterView.getAdapter().getItem(i)),R.layout.tab02_audio_item) {
                        @Override
                        public void bindView(ViewHolder holder, final FileItem obj) {
                            holder.setText(R.id.nameTV, obj.getmFileName());
                            holder.setOnClickListener(R.id.addToSetIV, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //dlnaCast(obj,"video");
                                }
                            });
                        }
                    };
                    fileSLV.setAdapter(fileAdapter_app);
                    musicNumTV.setText("(共" + fileAdapter_app.getCount() + "首)");
                }

            }
        });

        fileSLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isSelectModel){
                    if (!isAppContent){
                        if(MyApplication.isSelectedPCOnline()) {
                            if(MyApplication.isSelectedTVOnline()) {
                                currentFile = MediafileHelper.mediaFiles.get(i);
                                MediafileHelper.playMediaFile(currentFile.getType(), currentFile.getPathName(), currentFile.getName(),
                                        MyApplication.getSelectedTVIP().name, myHandler);
                            } else Toasty.warning(audioLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();
                        } else Toasty.warning(audioLibActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                    }else {
                        if (MyApplication.isSelectedTVOnline()){
                            dlnaCast((FileItem) adapterView.getAdapter().getItem(i),"audio");
                        } else  Toasty.warning(audioLibActivity.this, "当前电视不在线", Toast.LENGTH_SHORT, true).show();

                    }
                }else {

                }

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
                    Toasty.success(audioLibActivity.this, "即将在当前电视上打开音频文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
                    currentMusicNameTV.setText(currentFile.getName());
                    isPlaying = true;
                    playOrPauseIV.setClickable(true);
                    playOrPauseIV.setImageResource(R.drawable.music_play);
                    MediafileHelper.addRecentAudios(currentFile);
                    break;
                case OrderConst.playPCMedia_Fail:
                    Toasty.info(audioLibActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
                    isPlaying = false;
                    currentMusicNameTV.setText("无");
                    playOrPauseIV.setClickable(false);
                    playOrPauseIV.setImageResource(R.drawable.music_pause);
                    break;
                case OrderConst.mediaAction_DELETE_OK:
                    MediafileHelper.loadMediaLibFiles(myHandler);
                    break;
            }
        }
    };

    private void refreshView(boolean state) {
        folderAdapter.notifyDataSetChanged();
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
        folderAdapter_app = new MyAdapter<String>(ContentDataControl.getFolder(FileSystemType.music), R.layout.tab02_folder_item) {
            @Override
            public void bindView(ViewHolder holder, final String obj) {
                holder.setText(R.id.nameTV, obj);

                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shiftBar.setVisibility(View.GONE);
                        folderSLV.setVisibility(View.GONE);
                        playList.setVisibility(View.GONE);
                        musicCount.setVisibility(View.VISIBLE);
                        fileSLV.setVisibility(View.VISIBLE);

                        stringFolder = obj;
                        fileAdapter_app = new MyAdapter<FileItem>(ContentDataControl.getFileItemListByFolder(FileSystemType.music, obj),R.layout.tab02_audio_item) {
                            @Override
                            public void bindView(ViewHolder holder, final FileItem obj) {
                                holder.setText(R.id.nameTV, obj.getmFileName());
                                holder.setOnClickListener(R.id.addToSetIV, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        listDialog_app(obj);
                                    }
                                });
                            }
                        };
                        fileSLV.setAdapter(fileAdapter_app);
                        musicNumTV.setText("(共" + fileAdapter_app.getCount() + "首)");
                    }
                });
            }
        };
        if (!(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())){
            folderSLV.setAdapter(folderAdapter_app);
        }
    }

    private void cancelSelectModel(){
        menuList.setVisibility(View.GONE);
        audioPlayControl.setVisibility(View.VISIBLE);
        isSelectModel = false;
        selectedList.clear();
        if (!isAppContent){
            fileSLV.setAdapter(fileAdapter);

        }else {
            fileSLV.setAdapter(fileAdapter_app);
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
                new Send2PCThread(OrderConst.audioAction_name, OrderConst.mediaAction_DELETE_command, path, myHandler).start();
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
