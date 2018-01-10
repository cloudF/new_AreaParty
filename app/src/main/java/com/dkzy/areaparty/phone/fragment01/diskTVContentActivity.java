package com.dkzy.areaparty.phone.fragment01;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.base.DiskContentAdapter;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.model.fileBean;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_reName;
import com.dkzy.areaparty.phone.fragment01.ui.AddToMediaListDialog;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.ui.SharedFileDialog;
import com.dkzy.areaparty.phone.fragment01.utils.*;
import com.dkzy.areaparty.phone.fragment02.audioSetActivity;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;

import java.lang.ref.WeakReference;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.dkzy.areaparty.phone.fragment01.utils.DownloadFileManagerHelper.dlnaCast;

/**
 * Created by borispaul on 17-5-6.
 *
 */

public class diskTVContentActivity extends Activity implements View.OnClickListener, NetBroadcastReceiver.netEventHandler {
    private final String TAG = getClass().getSimpleName();

    private TextView page04DiskContentTitleTV;              // 盘符
    private TextView page04DiskContentCurrentPathTV;        // 当前路径
    private ListView page04DiskContentLV;                   // 当前路径下文件信息列表
    private ImageView page04DiskContentErrorIV;             // 加载失败显示的图片
    // 初始菜单栏(一级菜单栏相关控件)
    private LinearLayout page04DiskContentActionBar01LL;    // 初始显示的操作栏
    private LinearLayout bar01AddFolderLL;                  // bar01上显示的新建文件夹
    private LinearLayout bar01RefreshLL;                    // bar01上显示的刷新
    private LinearLayout bar01SortLL;                       // bar01上显示的排序
    private LinearLayout bar01SearchLL;                     // bar01上显示的搜索
    private LinearLayout bar01MoreLL;                       // bar01上显示的更多
    // 初始菜单栏点击更多弹出的操作栏相关控件
    private LinearLayout page04DiskContentBar01MoreRootLL;  // 更多操作栏父布局
    private TextView     bar01MoreAction1;                  // 当前为传输管理(后期更改)
    private TextView     bar01MoreAction2;                  // 当前为设置(后期更改)
    // 二级菜单栏相关控件
    private LinearLayout page04DiskContentActionBar02LL;    // 二级操作栏
    private LinearLayout bar02CopyLL;                       // bar022上显示的复制
    private ImageView    bar02IconCopyIV;                   // 复制图标
    private TextView     bar02TxCopyTV;                     // 复制文字
    private LinearLayout bar02CutLL;                        // bar02上显示的移动
    private ImageView    bar02IconCutIV;                    // 移动图标
    private TextView     bar02TxCutTV;                      // 移动文字
    private LinearLayout bar02DeleteLL;                     // bar02上显示的删除
    private ImageView    bar02IconDeleteIV;                 // 删除图标
    private TextView     bar02TxDeleteTV;                   // 删除文字
    private LinearLayout bar02SelectAllLL;                  // bar02上显示的全选
    private ImageView    bar02IconSelectAllIV;              // 全选图标
    private TextView     bar02TxSelectAllTV;                // 全选文字
    private LinearLayout bar02MoreLL;                       // bar02上显示的更多
    // 点击复制弹出的菜单栏
    private LinearLayout page04DiskContentCopyBarLL;        // 复制操作栏
    private LinearLayout copyAddFolderLL;                   // 复制操作栏上显示的新建文件夹
    private LinearLayout copyCancelLL;                      // 复制操作栏上显示的取消
    private LinearLayout copyPasteLL;                       // 复制操作栏上显示的粘贴
    // 点击移动弹出的菜单栏
    private LinearLayout page04DiskContentCutBarLL;         // 移动操作栏
    private LinearLayout cutAddFolderLL;                    // 移动操作栏上显示的新建文件夹
    private LinearLayout cutCancelLL;                       // 移动操作栏上显示的取消
    private LinearLayout cutPasteLL;                        // 移动操作栏上显示的粘贴
    // 二级菜单栏点击更多弹出的操作栏相关控件
    private LinearLayout page04DiskContentBar02MoreRootLL;  // 更多操作栏父布局
    private LinearLayout bar02MoreRenameLL;                 // 重命名
    private LinearLayout bar02MoreShareLL;                  // 分享
    private LinearLayout bar02MoreDetailLL;                 // 详情
    private LinearLayout bar02MoreSaveLL;                   // 保存到本地
    private LinearLayout bar02MoreAddToVideoList;           //添加到视频库
    private TextView     bar02MoreRenameTV;
    private TextView     bar02MoreShareTV;
    private TextView     bar02MoreDetailTV;
    private TextView     bar02MoreSaveTV;
    private TextView     bar02MoreAddToVideoListTV;

    private LinearLayout playFolderList, toSelectBGM;

    private View loadingView;
    private AlertDialog dialog;

    private String diskName;
    private int lastPoint = 0;
    private boolean isBack = false;
    private boolean isCheckBoxIn = false;
    private DiskContentAdapter adapter;
    private static com.dkzy.areaparty.phone.fragment01.utils.TVFileHelper TVFileHelper;
    private final MyHandler myHanlder = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<diskTVContentActivity> mAcitivity;

        MyHandler(diskTVContentActivity activity) {
            mAcitivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            diskTVContentActivity activity = mAcitivity.get();
            if(activity != null) {
                switch (msg.what) {
                    case OrderConst.openFolder_order_successful:
                        activity.openFolderSuccess(msg);
                        break;
                    case OrderConst.openFolder_order_fail:
                        activity.openFolderFail(msg);
                        break;
                    case OrderConst.actionSuccess_order:
                        activity.actionSuccess(msg);
                        break;
                    case OrderConst.actionFail_order:
                        activity.actionFail(msg);
                        break;
                    case OrderConst.addSharedFilePath_successful:
                        activity.addSharedFilePathSuccess(msg);
                        break;
                    case OrderConst.addSharedFilePath_fail:
                        activity.addSharedFilePathFail(msg);
                        break;
                    case OrderConst.playPCMedia_OK:
                    case OrderConst.playPCMedia_Fail:
                        activity.playMediaResult(msg);
                        break;
                }
            }

        }
    }

    public static com.dkzy.areaparty.phone.fragment01.utils.TVFileHelper getTVFileHelper() {
        return TVFileHelper;
    }

    public void openFolderSuccess(Message msg) {
        if(isBack) {
            page04DiskContentLV.smoothScrollToPosition(lastPoint);
            lastPoint = 0;
            isBack = false;
        }
        page04DiskContentActionBar01LL.setVisibility(View.GONE);
        for(fileBean data : TVFileHelper.getDatas()) {
            if (data.type == FileTypeConst.pic){
                page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
                break;
            }
        }
        if(page04DiskContentActionBar02LL.isShown()) {
            for(fileBean data : TVFileHelper.getDatas()) {
                data.isShow = true;
                data.isChecked = false;
            }
            isCheckBoxIn = true;
            bar02CopyLL.setClickable(false);
            bar02CutLL.setClickable(false);
            bar02DeleteLL.setClickable(false);
            bar02SelectAllLL.setClickable(true);
            bar02IconCopyIV.setImageResource(R.drawable.copy_pressed);
            bar02IconCutIV.setImageResource(R.drawable.cut_pressed);
            bar02IconDeleteIV.setImageResource(R.drawable.delete_pressed);
            bar02IconSelectAllIV.setImageResource(R.drawable.selectedall_normal);
            bar02TxSelectAllTV.setText("全选");
            bar02TxCopyTV.setTextColor(Color.rgb(211, 211, 211));
            bar02TxCutTV.setTextColor(Color.rgb(211, 211, 211));
            bar02TxDeleteTV.setTextColor(Color.rgb(211, 211, 211));
        } else
            isCheckBoxIn = false;
        page04DiskContentErrorIV.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    public void openFolderFail(Message msg) {
        adapter.notifyDataSetChanged();
        //....
        if(page04DiskContentActionBar02LL.isShown()) {
            for(fileBean data : TVFileHelper.getDatas()) {
                data.isShow = true;
                data.isChecked = false;
            }
            isCheckBoxIn = true;
            bar02CopyLL.setClickable(false);
            bar02CutLL.setClickable(false);
            bar02DeleteLL.setClickable(false);
            bar02SelectAllLL.setClickable(false);
            bar02IconCopyIV.setImageResource(R.drawable.copy_pressed);
            bar02IconCutIV.setImageResource(R.drawable.cut_pressed);
            bar02IconDeleteIV.setImageResource(R.drawable.delete_pressed);
            bar02IconSelectAllIV.setImageResource(R.drawable.selectedall_normal);
            bar02TxSelectAllTV.setText("全选");
            bar02TxCopyTV.setTextColor(Color.rgb(211, 211, 211));
            bar02TxCutTV.setTextColor(Color.rgb(211, 211, 211));
            bar02TxDeleteTV.setTextColor(Color.rgb(211, 211, 211));
        } else
            isCheckBoxIn = false;
        page04DiskContentErrorIV.setVisibility(View.VISIBLE);
        dialog.dismiss();
        Toasty.error(this, msg.getData().getString("error")+"", Toast.LENGTH_SHORT, true).show();
    }

    public void actionSuccess(Message msg) {
        isCheckBoxIn = false;
        String actionType = msg.getData().getString("actionType");
        if (actionType == null) actionType = "";
        if(actionType.equals(OrderConst.fileOrFolderAction_copy_command)) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            TVFileHelper.loadFiles();
        } else if(actionType.equals(OrderConst.fileOrFolderAction_cut_command)) {
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            TVFileHelper.loadFiles();
        } else if(actionType.equals(OrderConst.addPathToHttp_command)) {
            adapter.notifyDataSetChanged();
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            dialog.hide();
            Toasty.success(this, "所有选中添加到本地下载队列成功", Toast.LENGTH_SHORT, true).show();
        } else if (actionType.equals(OrderConst.fileOrFolderAction_renameInComputer_command)){
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            TVFileHelper.loadFiles();
        }else {
            TVFileHelper.loadFiles();
        }

    }

    public void actionFail(Message msg) {
        isCheckBoxIn = false;
        adapter.notifyDataSetChanged();
        String actionType = msg.getData().getString("actionType");
        if(actionType.equals(OrderConst.fileOrFolderAction_copy_command)) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
        } else if(actionType.equals(OrderConst.fileOrFolderAction_cut_command)) {
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
        } else if(actionType.equals(OrderConst.fileOrFolderAction_deleteInComputer_command)) {
            isCheckBoxIn = true;
        } else if(actionType.equals(OrderConst.addPathToHttp_command)) {
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
        }
        //TVFileHelper.loadFiles();
        dialog.hide();
        Toasty.error(this, "错误信息" + msg.getData().getString("error"), Toast.LENGTH_SHORT, true).show();
    }

    public void addSharedFilePathSuccess(Message msg) {
        isCheckBoxIn = false;
        dialog.hide();
        Toasty.success(this, "分享文件成功", Toast.LENGTH_SHORT, true).show();
    }

    public void addSharedFilePathFail(Message msg) {
        isCheckBoxIn = false;
        dialog.hide();
        Toasty.error(this, "分享文件失败", Toast.LENGTH_SHORT, true).show();
    }
    public void playMediaResult(Message msg) {
        if (msg.what == OrderConst.playPCMedia_OK){
            Toasty.success(diskTVContentActivity.this, "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
        }else {
            Toasty.success(diskTVContentActivity.this, "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
        }
    }


    // 返回按钮监听操作
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //...
        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(page04DiskContentBar02MoreRootLL.isShown()) {
                    page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                } else if(page04DiskContentActionBar02LL.isShown()) {
                    TVFileHelper.getSelectedFolders().clear();
                    TVFileHelper.getSelectedFiles().clear();
                    List<fileBean> datas = TVFileHelper.getDatas();
                    for(fileBean data : datas) {
                        data.isChecked = false;
                        data.isShow = false;
                    }
                    isCheckBoxIn = false;
                    adapter.notifyDataSetChanged();
                    page04DiskContentActionBar02LL.setVisibility(View.GONE);
                    page04DiskContentActionBar01LL.setVisibility(View.GONE);
                } else if(page04DiskContentBar01MoreRootLL.isShown()) {
                    page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
                } else {
                    isBack = true;
                    String nowPath = TVFileHelper.getNowFilePath();
                    if(countOfChar(nowPath,'/')<=4) {
                        this.finish();
                    } else {
                        String tempPath = nowPath.substring(0, TVFileHelper.getNowFilePath().length() - 1);
                        tempPath = tempPath.substring(0, tempPath.lastIndexOf("/") + 1);
                        TVFileHelper.setNowFilePath(tempPath);
                        TVFileHelper.loadFiles();
                        String[] nodeNames = tempPath.split("\\\\");
                        String pathShow = "";
                        for (String nowNode : nodeNames) {
                            pathShow += nowNode + " > ";
                        }
                        page04DiskContentCurrentPathTV.setText(pathShow);
                    }

                }

        }
        return true;
    }

    @Override
    public void onNetChange() {
        if(page04DiskContentCurrentPathTV.getText().equals("")) {
            Toasty.warning(this, "当前路径为空!", Toast.LENGTH_SHORT, true).show();
        } else {
            ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
            //dialog.show();
            TVFileHelper.loadFiles();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab04_disktvcontent_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.diskName = getIntent().getStringExtra("diskName");
        TVFileHelper.setNowFilePath(diskName + "/");

        initView();
        initEvent();
        adapter = new DiskContentAdapter(this, TVFileHelper.getDatas());
        page04DiskContentLV.setAdapter(adapter);
        TVFileHelper = new TVFileHelper(myHanlder);
        String initTitle = diskName + ":>";
        page04DiskContentCurrentPathTV.setText(initTitle);
        Log.e("diskContentActivity", "onCreate结束, 当前文件个数： " + TVFileHelper.getDatas().size() + "当前路径" + TVFileHelper.getNowFilePath());
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("diskContentActivity", "onStart结束" + TVFileHelper.getNowFilePath());
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.show();
        TVFileHelper.loadFiles();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog != null) {
            dialog.dismiss();
        }
        Log.e("diskContentActivity", "onPause结束");
    }

    @Override
    protected void onStop() {
        super.onStop();
        TVFileHelper.clearDatas();
        Log.e("diskContentActivity", "onStop结束, 文件清空" + TVFileHelper.getDatas().size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        Log.e("diskContentActivity", "执行点击事件" + view.getId());
        switch (view.getId()) {
            case R.id.bar01AddFolderLL:
            case R.id.copyAddFolderLL:
            case R.id.cutAddFolderLL:
                if(page04DiskContentCurrentPathTV.getText().equals("")) {
                    Toasty.warning(this, "当前路径为空!", Toast.LENGTH_SHORT, true).show();
                } else {
                    addFolderDialog();
                }
                break;
            case R.id.page04DiskContentErrorIV:
            case R.id.bar01RefreshLL:
                if(page04DiskContentCurrentPathTV.getText().equals("")) {
                    Toasty.warning(this, "当前路径为空!", Toast.LENGTH_SHORT, true).show();
                } else {
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
                    dialog.show();
                    TVFileHelper.loadFiles();
                }
                break;
            case R.id.bar01SortLL:
                // ....
                break;
            case R.id.bar01SearchLL:
                // ...
                break;
            case R.id.bar01MoreLL:
                page04DiskContentBar01MoreRootLL.setVisibility(View.VISIBLE);
                break;
            case R.id.page04DiskContentBar01MoreRootLL:
                page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
                break;
            case R.id.bar01MoreAction1:
                // ...
                break;
            case R.id.bar01MoreAction2:
                // ...
                break;
            case R.id.bar02CopyLL: {
                isCheckBoxIn = false;
                TVFileHelper.setIsInitial(false);
                TVFileHelper.setIsCut(false);
                TVFileHelper.setIsCopy(true);
                TVFileHelper.setSourcePath(TVFileHelper.getNowFilePath());
                page04DiskContentActionBar02LL.setVisibility(View.GONE);
                page04DiskContentCopyBarLL.setVisibility(View.VISIBLE);
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                selectedFolderList.clear();
                selectedFileList.clear();
                for(fileBean file : TVFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                    file.isShow = false;
                }
                adapter.notifyDataSetChanged();
            }
                break;
            case R.id.bar02CutLL: {
                isCheckBoxIn = false;
                TVFileHelper.setIsInitial(false);
                TVFileHelper.setIsCut(true);
                TVFileHelper.setIsCopy(false);
                TVFileHelper.setSourcePath(TVFileHelper.getNowFilePath());
                page04DiskContentActionBar02LL.setVisibility(View.GONE);
                page04DiskContentCutBarLL.setVisibility(View.VISIBLE);
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                selectedFolderList.clear();
                selectedFileList.clear();
                for(fileBean file : TVFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                    file.isShow = false;
                }
                adapter.notifyDataSetChanged();
            }
                break;
            case R.id.bar02DeleteLL: {
                isCheckBoxIn = true;
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                selectedFileList.clear();
                selectedFolderList.clear();
                for(fileBean file : TVFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                deleteFileAndFolderDialog();
            }
                break;
            case R.id.bar02SelectAllLL: {
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                selectedFileList.clear();
                selectedFolderList.clear();
                for(fileBean file : TVFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                if(selectedFileList.size() + selectedFolderList.size() == TVFileHelper.getDatas().size()) {
                    for(fileBean file : TVFileHelper.getDatas()) {
                        file.isShow = true;
                        file.isChecked = false;
                    }
                    selectedFileList.clear();
                    selectedFolderList.clear();
                } else {
                    for(fileBean file : TVFileHelper.getDatas()) {
                        file.isShow = true;
                        file.isChecked = true;
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                adapter.notifyDataSetChanged();
            }
                break;
            case R.id.bar02MoreLL: {
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                selectedFileList.clear();
                selectedFolderList.clear();
                for(fileBean file : TVFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                if(selectedFileList.size() + selectedFolderList.size() == 1) {
                    bar02MoreRenameLL.setClickable(true);
                    bar02MoreRenameTV.setTextColor(Color.rgb(0, 0, 0));
                    bar02MoreDetailLL.setClickable(true);
                    bar02MoreDetailTV.setTextColor(Color.rgb(0, 0, 0));
                    if(selectedFileList.size() == 1) {
                        bar02MoreSaveLL.setClickable(true);
                        bar02MoreSaveTV.setTextColor(Color.rgb(0, 0, 0));
                        if(!(Login.userId.equals(""))) {
                            bar02MoreShareLL.setClickable(true);
                            bar02MoreShareTV.setTextColor(Color.rgb(0, 0, 0));
                        } else {
                            bar02MoreShareLL.setVisibility(View.GONE);
                        }
                        bar02MoreAddToVideoList.setClickable(false);
                        bar02MoreAddToVideoListTV.setTextColor(Color.rgb(211, 211, 211));
                    } else {
                        bar02MoreSaveLL.setClickable(false);
                        bar02MoreSaveTV.setTextColor(Color.rgb(211, 211, 211));
                        if(!(Login.userId.equals(""))) {
                            bar02MoreShareLL.setClickable(false);
                            bar02MoreShareTV.setTextColor(Color.rgb(211, 211, 211));
                        } else {
                            bar02MoreShareLL.setVisibility(View.GONE);
                        }
                        bar02MoreAddToVideoList.setClickable(true);
                        bar02MoreAddToVideoListTV.setTextColor(Color.rgb(0, 0, 0));
                    }
                } else {
                    bar02MoreRenameLL.setClickable(false);
                    bar02MoreRenameTV.setTextColor(Color.rgb(211, 211, 211));
                    bar02MoreDetailLL.setClickable(false);
                    bar02MoreDetailTV.setTextColor(Color.rgb(211, 211, 211));
                    bar02MoreSaveLL.setClickable(false);
                    bar02MoreSaveTV.setTextColor(Color.rgb(211, 211, 211));
                    if(selectedFolderList.size() != 0) {
                        bar02MoreSaveLL.setClickable(false);
                        bar02MoreSaveTV.setTextColor(Color.rgb(211, 211, 211));
                        if(!(Login.userId.equals(""))) {
                            bar02MoreShareLL.setClickable(false);
                            bar02MoreShareTV.setTextColor(Color.rgb(211, 211, 211));
                        } else {
                            bar02MoreShareLL.setVisibility(View.GONE);
                        }
                    } else {
                        bar02MoreSaveLL.setClickable(true);
                        bar02MoreSaveTV.setTextColor(Color.rgb(0, 0, 0));
                        if(!(Login.userId.equals(""))) {
                            bar02MoreShareLL.setClickable(false);
                            bar02MoreShareTV.setTextColor(Color.rgb(211, 211, 211));
                        } else {
                            bar02MoreShareLL.setVisibility(View.GONE);
                        }
                    }
                }
                page04DiskContentBar02MoreRootLL.setVisibility(View.VISIBLE);
            }
                break;
            case R.id.copyCancelLL: {
                TVFileHelper.setIsCopy(false);
                TVFileHelper.setIsCut(false);
                TVFileHelper.setIsInitial(true);
                TVFileHelper.getSelectedFolders().clear();
                TVFileHelper.getSelectedFiles().clear();
                page04DiskContentCopyBarLL.setVisibility(View.GONE);
                page04DiskContentActionBar01LL.setVisibility(View.GONE);
            }
                break;
            case R.id.copyPasteLL: {
                TVFileHelper.setIsCopy(false);
                TVFileHelper.setIsCut(false);
                TVFileHelper.setIsInitial(true);
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                for(fileBean file : TVFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                dialog.show();
                TVFileHelper.copyFileAndFolder();
            }
                break;
            case R.id.cutCancelLL: {
                TVFileHelper.setIsCopy(false);
                TVFileHelper.setIsCut(false);
                TVFileHelper.setIsInitial(true);
                TVFileHelper.getSelectedFolders().clear();
                TVFileHelper.getSelectedFiles().clear();
                page04DiskContentCutBarLL.setVisibility(View.GONE);
                page04DiskContentActionBar01LL.setVisibility(View.GONE);
            }
                break;
            case R.id.cutPasteLL: {
                TVFileHelper.setIsCopy(false);
                TVFileHelper.setIsCut(false);
                TVFileHelper.setIsInitial(true);
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                for (fileBean file : TVFileHelper.getDatas()) {
                    if (file.isChecked) {
                        if (file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                dialog.show();
                TVFileHelper.cutFileAndFolder();
            }
                break;
            case R.id.page04DiskContentBar02MoreRootLL:
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                break;
            case R.id.bar02MoreRenameLL:{
                reNameDialog();
            }

                break;
            case R.id.bar02MoreShareLL: {
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                shareFileDialog();
            }
                break;
            case R.id.bar02MoreDetailLL: {
                //...
            }
                break;
            case R.id.bar02MoreSaveLL: {
                //...
                List<fileBean> selectedFileList = TVFileHelper.getSelectedFiles();
                selectedFileList.clear();
                for (fileBean file : TVFileHelper.getDatas()) {
                    if (file.isChecked) {
                        if (!(file.type == FileTypeConst.folder))
                            selectedFileList.add(file);
                    }
                }
                dialog.show();
                TVFileHelper.downloadSelectedFiles();
            }
                break;
            case R.id.bar02MoreAddToVideoList:
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                List<fileBean> selectedFolderList = TVFileHelper.getSelectedFolders();
                selectedFolderList.clear();
                for (fileBean file : TVFileHelper.getDatas()) {
                    if (file.isChecked) {
                        if (file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                    }
                }
                if (selectedFolderList.size()>0){
                    addToMediaList(selectedFolderList.get(0));
                }
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                break;
            case R.id.play_folder_list:
                dlnaCast("image");
                break;
            case R.id.to_select_bgm:
                startActivity(new Intent(diskTVContentActivity.this, audioSetActivity.class).putExtra("asBGM",true));
                break;

        }
    }



    private void initEvent() {
        page04DiskContentErrorIV.setOnClickListener(this);
        bar01AddFolderLL.setOnClickListener(this);
        copyAddFolderLL.setOnClickListener(this);
        cutAddFolderLL.setOnClickListener(this);
        bar01RefreshLL.setOnClickListener(this);
        bar01SortLL.setOnClickListener(this);
        bar01SearchLL.setOnClickListener(this);
        bar01MoreLL.setOnClickListener(this);
        page04DiskContentBar01MoreRootLL.setOnClickListener(this);
        bar01MoreAction1.setOnClickListener(this);
        bar01MoreAction2.setOnClickListener(this);
        bar02CopyLL.setOnClickListener(this);
        bar02CutLL.setOnClickListener(this);
        bar02DeleteLL.setOnClickListener(this);
        bar02SelectAllLL.setOnClickListener(this);
        bar02MoreLL.setOnClickListener(this);
        copyCancelLL.setOnClickListener(this);
        copyPasteLL.setOnClickListener(this);
        cutCancelLL.setOnClickListener(this);
        cutPasteLL.setOnClickListener(this);
        page04DiskContentBar02MoreRootLL.setOnClickListener(this);
        bar02MoreRenameLL.setOnClickListener(this);
        bar02MoreShareLL.setOnClickListener(this);
        bar02MoreDetailLL.setOnClickListener(this);
        bar02MoreSaveLL.setOnClickListener(this);
        bar02MoreAddToVideoList.setOnClickListener(this);
        NetBroadcastReceiver.mListeners.add(this);
        playFolderList.setOnClickListener(this);
        toSelectBGM.setOnClickListener(this);

        page04DiskContentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("diskContentActivity", "Item点击事件");
                final fileBean fileBeanClick = TVFileHelper.getDatas().get(i);
                if (fileBeanClick.type == FileTypeConst.folder) {
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
                    dialog.show();
                    lastPoint = i;
                    // 设置路径
                    TVFileHelper.setNowFilePath(TVFileHelper.getNowFilePath() + fileBeanClick.name + "/");
                    TVFileHelper.loadFiles();
                    String[] nodeNames = TVFileHelper.getNowFilePath().split("\\\\");
                    String pathShow = "";
                    for (String nowNode : nodeNames) {
                        pathShow += nowNode + " > ";
                    }
                    page04DiskContentCurrentPathTV.setText(pathShow);
                } else {
                    if (fileBeanClick.type == FileTypeConst.pic && MyApplication.isSelectedTVOnline()){//是视频则可以播放
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                com.dkzy.areaparty.phone.fragment01.utils.prepareDataForFragment.getDlnaCastState(fileBeanClick,"image");
                            }
                        }).start();
                    }else {
                        Toasty.info(diskTVContentActivity.this, fileBeanClick.name, Toast.LENGTH_SHORT, true).show();
                        // 其他操作
                    }
                }
            }
        });
        /*page04DiskContentLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){
                if(!isCheckBoxIn) {
                    if(TVFileHelper.isInitial()) {
                        page04DiskContentActionBar01LL.setVisibility(View.GONE);
                        page04DiskContentActionBar02LL.setVisibility(View.GONE);
                        List<fileBean> datas = TVFileHelper.getDatas();
                        for(fileBean data : datas) {
                            data.isShow = true;
                            data.isChecked = false;
                        }
                        TVFileHelper.getDatas().get(i).isChecked = true;
                        isCheckBoxIn = true;
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });*/
    }

    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {
        page04DiskContentBar01MoreRootLL = (LinearLayout) findViewById(R.id.page04DiskContentBar01MoreRootLL);
        page04DiskContentBar02MoreRootLL = (LinearLayout) findViewById(R.id.page04DiskContentBar02MoreRootLL);
        page04DiskContentCurrentPathTV = (TextView) findViewById(R.id.page04DiskContentCurrentPathTV);
        page04DiskContentActionBar02LL = (LinearLayout) findViewById(R.id.page04DiskContentActionBar02LL);
        page04DiskContentActionBar01LL = (LinearLayout) findViewById(R.id.page04DiskContentActionBar01LL);
        page04DiskContentCopyBarLL = (LinearLayout) findViewById(R.id.page04DiskContentCopyBarLL);
        page04DiskContentErrorIV = (ImageView) findViewById(R.id.page04DiskContentErrorIV);
        page04DiskContentCutBarLL = (LinearLayout) findViewById(R.id.page04DiskContentCutBarLL);
        page04DiskContentTitleTV = (TextView) findViewById(R.id.page04DiskContentTitleTV);
        page04DiskContentLV = (ListView) findViewById(R.id.page04DiskContentLV);
        bar02IconSelectAllIV = (ImageView) findViewById(R.id.bar02IconSelectAllIV);
        bar02TxSelectAllTV = (TextView) findViewById(R.id.bar02TxSelectAllTV);
        bar01AddFolderLL = (LinearLayout) findViewById(R.id.bar01AddFolderLL);
        bar01RefreshLL = (LinearLayout) findViewById(R.id.bar01RefreshLL);
        bar01SortLL = (LinearLayout) findViewById(R.id.bar01SortLL);
        bar01SearchLL = (LinearLayout) findViewById(R.id.bar01SearchLL);
        bar01MoreLL = (LinearLayout) findViewById(R.id.bar01MoreLL);
        bar01MoreAction1 = (TextView) findViewById(R.id.bar01MoreAction1);
        bar01MoreAction2 = (TextView) findViewById(R.id.bar01MoreAction2);
        bar02CopyLL = (LinearLayout) findViewById(R.id.bar02CopyLL);
        bar02IconCopyIV = (ImageView) findViewById(R.id.bar02IconCopyIV);
        bar02TxCopyTV = (TextView) findViewById(R.id.bar02TxCopyTV);
        bar02CutLL = (LinearLayout) findViewById(R.id.bar02CutLL);
        bar02IconCutIV = (ImageView) findViewById(R.id.bar02IconCutIV);
        bar02TxCutTV = (TextView) findViewById(R.id.bar02TxCutTV);
        bar02DeleteLL = (LinearLayout) findViewById(R.id.bar02DeleteLL);
        bar02IconDeleteIV = (ImageView) findViewById(R.id.bar02IconDeleteIV);
        bar02TxDeleteTV = (TextView) findViewById(R.id.bar02TxDeleteTV);
        bar02SelectAllLL = (LinearLayout) findViewById(R.id.bar02SelectAllLL);
        bar02MoreLL = (LinearLayout) findViewById(R.id.bar02MoreLL);
        copyAddFolderLL = (LinearLayout) findViewById(R.id.copyAddFolderLL);
        copyCancelLL = (LinearLayout) findViewById(R.id.copyCancelLL);
        copyPasteLL = (LinearLayout) findViewById(R.id.copyPasteLL);
        cutAddFolderLL = (LinearLayout) findViewById(R.id.cutAddFolderLL);
        cutCancelLL = (LinearLayout) findViewById(R.id.cutCancelLL);
        cutPasteLL = (LinearLayout) findViewById(R.id.cutPasteLL);
        bar02MoreRenameLL = (LinearLayout) findViewById(R.id.bar02MoreRenameLL);
        bar02MoreShareLL = (LinearLayout) findViewById(R.id.bar02MoreShareLL);
        bar02MoreDetailLL = (LinearLayout) findViewById(R.id.bar02MoreDetailLL);
        bar02MoreSaveLL = (LinearLayout) findViewById(R.id.bar02MoreSaveLL);
        bar02MoreAddToVideoList = (LinearLayout) findViewById(R.id.bar02MoreAddToVideoList);
        bar02MoreRenameTV = (TextView) findViewById(R.id.bar02MoreRenameTV);
        bar02MoreShareTV = (TextView) findViewById(R.id.bar02MoreShareTV);
        bar02MoreDetailTV = (TextView) findViewById(R.id.bar02MoreDetailTV);
        bar02MoreSaveTV = (TextView) findViewById(R.id.bar02MoreSaveTV);
        bar02MoreAddToVideoListTV = (TextView) findViewById(R.id.bar02MoreAddToVideoListTV);
        playFolderList = (LinearLayout) findViewById(R.id.play_folder_list);
        toSelectBGM = (LinearLayout) findViewById(R.id.to_select_bgm);

        loadingView = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView).setCancelable(true);
        dialog = builder.create();

        String title = diskName + "盘";
        page04DiskContentTitleTV.setText(title);

        if(TVFileHelper.isCopy()) {
            page04DiskContentCopyBarLL.setVisibility(View.VISIBLE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        } else if(TVFileHelper.isCut()) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.VISIBLE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        } else if(TVFileHelper.isInitial()) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        } else {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        }
    }

    /**
     * <summary>
     *  显示新建文件夹对话框并执行相应监听操作
     * </summary>
     */
    private void addFolderDialog() {
        final ActionDialog_addFolder actionDialogAddFolder = new ActionDialog_addFolder(this);
        actionDialogAddFolder.setCanceledOnTouchOutside(false);
        actionDialogAddFolder.show();
        final EditText editText = actionDialogAddFolder.getEditTextView();
        actionDialogAddFolder.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempFolderName = editText.getText().toString();
                if(tempFolderName.equals("") || tempFolderName.endsWith(".") ||
                        tempFolderName.contains("\\") || tempFolderName.contains("/") ||
                        tempFolderName.contains(":")  || tempFolderName.contains("*") ||
                        tempFolderName.contains("?")  || tempFolderName.contains("\"") ||
                        tempFolderName.contains("<")  || tempFolderName.contains(">") ||
                        tempFolderName.contains("|")){
                    Toasty.error(diskTVContentActivity.this, "文件夹名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialogAddFolder.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                    dialog.show();
                    TVFileHelper.addFolder(tempFolderName);
                    actionDialogAddFolder.dismiss();
                }
            }
        });
        actionDialogAddFolder.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDialogAddFolder.dismiss();
            }
        });
    }
    /**
     * <summary>
     *  显示删除文件(夹)对话框并执行相应监听操作
     * </summary>
     */
    private void deleteFileAndFolderDialog() {
        final DeleteDialog deleteDialog = new DeleteDialog(this);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.show();
        deleteDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((TVFileHelper.getSelectedFiles().size() + TVFileHelper.getSelectedFolders().size()) != 0) {
                    dialog.show();
                    TVFileHelper.deleteFileAndFolder();
                    deleteDialog.dismiss();
                }
            }
        });
    }
    /**
     * <summary>
     *  显示分享文件对话框并执行相应监听操作
     * </summary>
     */
    private void shareFileDialog() {
        final SharedFileDialog sharedFileDialog = new SharedFileDialog(this);
        String name = TVFileHelper.getSelectedFiles().get(0).name;
        sharedFileDialog.setCanceledOnTouchOutside(false);
        sharedFileDialog.show();
        sharedFileDialog.setFileName(name);

        final EditText editText = sharedFileDialog.getEditTextView();
        sharedFileDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                String des = editText.getText().toString();
                if(des.equals(""))
                    Toasty.error(diskTVContentActivity.this, "文件描述信息不能为空", Toast.LENGTH_SHORT).show();
                else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(sharedFileDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                    sharedFileDialog.dismiss();
                    dialog.show();
                    String name = TVFileHelper.getSelectedFiles().get(0).name;
                    int size = TVFileHelper.getSelectedFiles().get(0).size;
                    long time = System.currentTimeMillis();
                    TVFileHelper.setSelectedShareFile(new SharedfileBean(name, TVFileHelper.getNowFilePath() + name, size, des, time,
                            sharedFileDialog.getUrlEditText(), sharedFileDialog.getPwdEditText()));
                    Log.e("page04", "路径：" + TVFileHelper.getSelectedShareFile().path);
                    dialog.show();
                    TVFileHelper.shareFile(des, TVFileHelper.getSelectedShareFile());
                }

            }
        });
        sharedFileDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedFileDialog.dismiss();
            }
        });
    }
    /**
     * <summary>
     *  显示重命名对话框
     * </summary>
     */
    private void reNameDialog() {
        final ActionDialog_reName reNameDialog = new ActionDialog_reName(this);
        fileBean fileBean;
        if (TVFileHelper.getSelectedFiles().size()>0){
            fileBean = TVFileHelper.getSelectedFiles().get(0);
        }else if (TVFileHelper.getSelectedFolders().size()>0){
            fileBean = TVFileHelper.getSelectedFolders().get(0);
        }else {
            return;
        }
        final int type = fileBean.type;
        final String name = fileBean.name;

        reNameDialog.setCanceledOnTouchOutside(false);
        reNameDialog.show();

        reNameDialog.setOldName(name);

        final EditText editText = reNameDialog.getValueEditTextView();
        reNameDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                String des = editText.getText().toString();
                if(des.equals("") || des.endsWith(".") ||
                        des.contains("\\") || des.contains("/") ||
                        des.contains(":")  || des.contains("*") ||
                        des.contains("?")  || des.contains("\"") ||
                        des.contains("<")  || des.contains(">") ||
                        des.contains("|")){
                    Toasty.error(diskTVContentActivity.this, "文件夹名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(reNameDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (type == FileTypeConst.folder){
                        TVFileHelper.reNameFolder(name,des);
                    }else {
                        if (!des.contains(".")) des = des+name.substring(name.lastIndexOf("."));
                        TVFileHelper.reNameFile(name,des);
                    }
                    reNameDialog.dismiss();
//                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    manager.hideSoftInputFromWindow(sharedFileDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
//                    sharedFileDialog.dismiss();
//                    dialog.show();
//                    String name = TVFileHelper.getSelectedFiles().get(0).name;
//                    int size = TVFileHelper.getSelectedFiles().get(0).size;
//                    long time = System.currentTimeMillis();
//                    TVFileHelper.setSelectedShareFile(new SharedfileBean(name, TVFileHelper.getNowFilePath() + name, size, des, time,
//                            sharedFileDialog.getUrlEditText(), sharedFileDialog.getPwdEditText()));
//                    Log.e("page04", "路径：" + TVFileHelper.getSelectedShareFile().path);
//                    dialog.show();
//                    TVFileHelper.shareFile(des, TVFileHelper.getSelectedShareFile());
                }

            }
        });
        reNameDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reNameDialog.dismiss();
            }
        });
    }
    /**
     * <summary>
     *  显示添加到视频库对话框并执行相应监听操作
     * </summary>
     */
    private void addToMediaList(final fileBean file){
        final AddToMediaListDialog addToMediaListDialog = new AddToMediaListDialog(this);
        addToMediaListDialog.setCanceledOnTouchOutside(false);
        addToMediaListDialog.show();
        addToMediaListDialog.setVideoButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVFileHelper.addToVideoList(file,"VIDEO");
                addToMediaListDialog.dismiss();
                dialog.show();
            }
        });
        addToMediaListDialog.setAudioButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVFileHelper.addToVideoList(file,"AUDIO");
                addToMediaListDialog.dismiss();
                dialog.show();
            }
        });
        addToMediaListDialog.setImageButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVFileHelper.addToVideoList(file,"IMAGE");
                addToMediaListDialog.dismiss();
                dialog.show();
            }
        });
    }
    public static boolean isValidFileName(String fileName)
    {
        if (fileName == null || fileName.length() > 255)
            return false;
        else
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"); }

    public static int countOfChar(String s, char a){
        int count = 0;
        for (int i = 0; i < s.length(); i++){
            if (a == s.charAt(i)){
                count++;
            }
        }
        return count;
    }
}
