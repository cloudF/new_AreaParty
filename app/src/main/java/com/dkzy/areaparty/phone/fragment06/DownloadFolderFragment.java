package com.dkzy.areaparty.phone.fragment06;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.dkzy.areaparty.phone.fragment01.diskContentActivity;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.model.fileBean;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_reName;
import com.dkzy.areaparty.phone.fragment01.ui.AddToMediaListDialog;
import com.dkzy.areaparty.phone.fragment01.ui.DeleteDialog;
import com.dkzy.areaparty.phone.fragment01.ui.SharedFileDialog;
import com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper;
import com.dkzy.areaparty.phone.fragment01.utils.TVFileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.dmoral.toasty.Toasty;
import protocol.Msg.DeleteFileMsg;
import protocol.ProtoHead;
import server.NetworkPacket;
import xyz.zpayh.adapter.IFullSpan;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.dkzy.areaparty.phone.utilseverywhere.utils.ContextUtils.getSystemService;

/**
 * Created by SnowMonkey on 2017/5/31.
 */

public class DownloadFolderFragment extends Fragment implements View.OnClickListener,NetBroadcastReceiver.netEventHandler{

    public static String rootPath;

    private final int SUCCESS = 0;
    private final int DOWNLOADING = 1;
    private final int PAUSE = 2;
    private final int DOWNLOADAGAIN = 3;

    private ListView downloadFolderFragmentList = null;
    private DownloadFolderFragmentFileAdapter downloadFolderFragmentFileAdapter = null;
    public static List<HashMap<String, Object>> downloadFileData = null;

    public static Handler mHandler;


    private final String TAG = getClass().getSimpleName();

    //private TextView page04DiskContentTitleTV;              // 盘符
    //private TextView page04DiskContentCurrentPathTV;        // 当前路径
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

    private View loadingView;
    private AlertDialog dialog;

    private String diskName;
    private int lastPoint = 0;
    private boolean isBack = false;
    private boolean isCheckBoxIn = false;
    private DiskContentAdapter adapter;
    private static com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper PCFileHelper;
    private final MyHandler myHanlder = new MyHandler(this);

    View rootView;

    @Override
    public void onClick(View view) {
        Log.e("diskContentActivity", "执行点击事件" + view.getId());
        switch (view.getId()) {
            case R.id.bar01AddFolderLL:
            case R.id.copyAddFolderLL:
            case R.id.cutAddFolderLL:
                /*if(page04DiskContentCurrentPathTV.getText().equals("")) {
                    Toasty.warning(getContext(), "当前路径为空!", Toast.LENGTH_SHORT, true).show();
                } else {
                    addFolderDialog();
                }*/
                break;
            case R.id.page04DiskContentErrorIV:
            case R.id.bar01RefreshLL:
                PCFileHelper.setNowFilePath(DownloadFolderFragment.rootPath);
                ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
                dialog.show();
                PCFileHelper.loadFiles();

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
                PCFileHelper.setIsInitial(false);
                PCFileHelper.setIsCut(false);
                PCFileHelper.setIsCopy(true);
                PCFileHelper.setSourcePath(PCFileHelper.getNowFilePath());
                page04DiskContentActionBar02LL.setVisibility(View.GONE);
                page04DiskContentCopyBarLL.setVisibility(View.VISIBLE);
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                selectedFolderList.clear();
                selectedFileList.clear();
                for(fileBean file : PCFileHelper.getDatas()) {
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
                PCFileHelper.setIsInitial(false);
                PCFileHelper.setIsCut(true);
                PCFileHelper.setIsCopy(false);
                PCFileHelper.setSourcePath(PCFileHelper.getNowFilePath());
                page04DiskContentActionBar02LL.setVisibility(View.GONE);
                page04DiskContentCutBarLL.setVisibility(View.VISIBLE);
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                selectedFolderList.clear();
                selectedFileList.clear();
                for(fileBean file : PCFileHelper.getDatas()) {
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
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                selectedFileList.clear();
                selectedFolderList.clear();
                for(fileBean file : PCFileHelper.getDatas()) {
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
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                selectedFileList.clear();
                selectedFolderList.clear();
                for(fileBean file : PCFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                if(selectedFileList.size() + selectedFolderList.size() == PCFileHelper.getDatas().size()) {
                    for(fileBean file : PCFileHelper.getDatas()) {
                        file.isShow = true;
                        file.isChecked = false;
                    }
                    selectedFileList.clear();
                    selectedFolderList.clear();
                } else {
                    for(fileBean file : PCFileHelper.getDatas()) {
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
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                selectedFileList.clear();
                selectedFolderList.clear();
                for(fileBean file : PCFileHelper.getDatas()) {
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
                PCFileHelper.setIsCopy(false);
                PCFileHelper.setIsCut(false);
                PCFileHelper.setIsInitial(true);
                PCFileHelper.getSelectedFolders().clear();
                PCFileHelper.getSelectedFiles().clear();
                page04DiskContentCopyBarLL.setVisibility(View.GONE);
                page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.copyPasteLL: {
                PCFileHelper.setIsCopy(false);
                PCFileHelper.setIsCut(false);
                PCFileHelper.setIsInitial(true);
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                for(fileBean file : PCFileHelper.getDatas()) {
                    if(file.isChecked) {
                        if(file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                dialog.show();
                PCFileHelper.copyFileAndFolder();
            }
            break;
            case R.id.cutCancelLL: {
                PCFileHelper.setIsCopy(false);
                PCFileHelper.setIsCut(false);
                PCFileHelper.setIsInitial(true);
                PCFileHelper.getSelectedFolders().clear();
                PCFileHelper.getSelectedFiles().clear();
                page04DiskContentCutBarLL.setVisibility(View.GONE);
                page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.cutPasteLL: {
                PCFileHelper.setIsCopy(false);
                PCFileHelper.setIsCut(false);
                PCFileHelper.setIsInitial(true);
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                for (fileBean file : PCFileHelper.getDatas()) {
                    if (file.isChecked) {
                        if (file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                        else selectedFileList.add(file);
                    }
                }
                dialog.show();
                PCFileHelper.cutFileAndFolder();
            }
            break;
            case R.id.page04DiskContentBar02MoreRootLL:
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                break;
            case R.id.bar02MoreRenameLL:{
                // ...
//                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
//                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
//                if (selectedFileList.size()>0){
//                    Log.w(TAG,selectedFileList.get(0).name);
//                }else if (selectedFolderList.size()>0){
//                    Log.w(TAG,selectedFolderList.get(0).name);
//                }
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
                List<fileBean> selectedFileList = PCFileHelper.getSelectedFiles();
                selectedFileList.clear();
                for (fileBean file : PCFileHelper.getDatas()) {
                    if (file.isChecked) {
                        if (!(file.type == FileTypeConst.folder))
                            selectedFileList.add(file);
                    }
                }
                dialog.show();
                PCFileHelper.downloadSelectedFiles();
            }
            break;
            case R.id.bar02MoreAddToVideoList:
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                List<fileBean> selectedFolderList = PCFileHelper.getSelectedFolders();
                selectedFolderList.clear();
                for (fileBean file : PCFileHelper.getDatas()) {
                    if (file.isChecked) {
                        if (file.type == FileTypeConst.folder)
                            selectedFolderList.add(file);
                    }
                }
                if (selectedFolderList.size()>0){
                    addToMediaList(selectedFolderList.get(0));
                }
                page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);


        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Fragment> mAcitivity;

        MyHandler(Fragment activity) {
            mAcitivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadFolderFragment activity =(DownloadFolderFragment) mAcitivity.get();
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

    public static com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper getPCFileHelper() {
        return PCFileHelper;
    }

    public void openFolderSuccess(Message msg) {
        if(isBack) {
            page04DiskContentLV.smoothScrollToPosition(lastPoint);
            lastPoint = 0;
            isBack = false;
        }
        if(page04DiskContentActionBar02LL.isShown()) {
            for(fileBean data : PCFileHelper.getDatas()) {
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

        /*if (PCFileHelper.getNowFilePath().equals(DownloadFolderFragment.rootPath)){
            initDownloadingFile();
        }*/
    }

    public void openFolderFail(Message msg) {
        adapter.notifyDataSetChanged();
        //....
        if(page04DiskContentActionBar02LL.isShown()) {
            for(fileBean data : PCFileHelper.getDatas()) {
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
        try {
            Toasty.error(getContext(), msg.getData().getString("error")+"", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public void actionSuccess(Message msg) {
        isCheckBoxIn = false;
        String actionType = msg.getData().getString("actionType");
        if (actionType == null) actionType = "";
        if(actionType.equals(OrderConst.fileOrFolderAction_copy_command)) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            PCFileHelper.loadFiles();
        } else if(actionType.equals(OrderConst.fileOrFolderAction_cut_command)) {
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            PCFileHelper.loadFiles();
        } else if(actionType.equals(OrderConst.addPathToHttp_command)) {
            adapter.notifyDataSetChanged();
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            dialog.hide();
            Toasty.success(getContext(), "所有选中添加到本地下载队列成功", Toast.LENGTH_SHORT, true).show();
        } else if (actionType.equals(OrderConst.fileOrFolderAction_renameInComputer_command)){
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            PCFileHelper.loadFiles();
        }else {
            PCFileHelper.loadFiles();
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
        //PCFileHelper.loadFiles();
        dialog.hide();
        Toasty.error(getContext(), "错误信息" + msg.getData().getString("error"), Toast.LENGTH_SHORT, true).show();
    }

    public void addSharedFilePathSuccess(Message msg) {
        isCheckBoxIn = false;
        dialog.hide();
        Toasty.success(getContext(), "分享文件成功", Toast.LENGTH_SHORT, true).show();
    }

    public void addSharedFilePathFail(Message msg) {
        isCheckBoxIn = false;
        dialog.hide();
        Toasty.error(getContext(), "分享文件失败", Toast.LENGTH_SHORT, true).show();
    }
    public void playMediaResult(Message msg) {
        if (msg.what == OrderConst.playPCMedia_OK){
            Toasty.success(getContext(), "即将在当前电视上打开媒体文件, 请观看电视", Toast.LENGTH_SHORT, true).show();
        }else {
            Toasty.success(getContext(), "打开媒体文件失败", Toast.LENGTH_SHORT, true).show();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(DownloadFolderFragment.rootPath) && MyApplication.isSelectedPCOnline()){
            PCFileHelper.setNowFilePath(DownloadFolderFragment.rootPath);
        }else {
            Toasty.error(getContext(),"当前电脑不在线").show();
        }




    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.tab06_download_manager_downfolderfragment, container, false);
        rootView = inflater.inflate(R.layout.tab06_download_manager_downfolderfragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initViews();
        initEvents();

        adapter = new DiskContentAdapter(getContext(), PCFileHelper.getDatas());
        page04DiskContentLV.setAdapter(adapter);
        PCFileHelper = new PCFileHelper(myHanlder);
        String initTitle = diskName + ":>";
        //page04DiskContentCurrentPathTV.setText(initTitle);
        Log.e("diskContentActivity", "onCreate结束, 当前文件个数： " + PCFileHelper.getDatas().size() + "当前路径" + PCFileHelper.getNowFilePath());

    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //...
        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(page04DiskContentBar02MoreRootLL.isShown()) {
                    page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
                } else if(page04DiskContentActionBar02LL.isShown()) {
                    PCFileHelper.getSelectedFolders().clear();
                    PCFileHelper.getSelectedFiles().clear();
                    List<fileBean> datas = PCFileHelper.getDatas();
                    for(fileBean data : datas) {
                        data.isChecked = false;
                        data.isShow = false;
                    }
                    isCheckBoxIn = false;
                    adapter.notifyDataSetChanged();
                    page04DiskContentActionBar02LL.setVisibility(View.GONE);
                    page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
                } else if(page04DiskContentBar01MoreRootLL.isShown()) {
                    page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
                } else {
                    isBack = true;
                    String nowPath = PCFileHelper.getNowFilePath();
                    if(TextUtils.isEmpty(nowPath) ||nowPath.equals(DownloadFolderFragment.rootPath)) {
                        getActivity().finish();
                    } else {
                        String tempPath = nowPath.substring(0, PCFileHelper.getNowFilePath().length() - 1);
                        tempPath = tempPath.substring(0, tempPath.lastIndexOf("\\") + 1);
                        PCFileHelper.setNowFilePath(tempPath);
                        PCFileHelper.loadFiles();
                        String[] nodeNames = tempPath.split("\\\\");
                        String pathShow = "";
                        for (String nowNode : nodeNames) {
                            pathShow += nowNode + " > ";
                        }
                        //page04DiskContentCurrentPathTV.setText(pathShow);
                    }

                }

        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(DownloadFolderFragment.rootPath) && MyApplication.isSelectedPCOnline()){
            dialog.show();
            PCFileHelper.loadFiles();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onNetChange() {
        /*if(page04DiskContentCurrentPathTV.getText().equals("")) {
            Toasty.warning(getContext(), "当前路径为空!", Toast.LENGTH_SHORT, true).show();
        } else {
            ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
            //dialog.show();
            PCFileHelper.loadFiles();
        }*/
    }

    private void initData(){
        downloadFileData = (downloadFileData==null)?new ArrayList<HashMap<String, Object>>():downloadFileData;
    }
    private void getData(){
        initData();
    }
    private void initViews() {
        downloadFolderFragmentList = (ListView) getActivity().findViewById(R.id.downloadFolderFragmentList);
        downloadFolderFragmentFileAdapter = new DownloadFolderFragmentFileAdapter(getActivity(),downloadFileData);
        downloadFolderFragmentList.setAdapter(downloadFolderFragmentFileAdapter);


        page04DiskContentBar01MoreRootLL = (LinearLayout) rootView.findViewById(R.id.page04DiskContentBar01MoreRootLL);
        page04DiskContentBar02MoreRootLL = (LinearLayout) rootView.findViewById(R.id.page04DiskContentBar02MoreRootLL);
       // page04DiskContentCurrentPathTV = (TextView) rootView.findViewById(R.id.page04DiskContentCurrentPathTV);
        page04DiskContentActionBar02LL = (LinearLayout) rootView.findViewById(R.id.page04DiskContentActionBar02LL);
        page04DiskContentActionBar01LL = (LinearLayout) rootView.findViewById(R.id.page04DiskContentActionBar01LL);
        page04DiskContentCopyBarLL = (LinearLayout) rootView.findViewById(R.id.page04DiskContentCopyBarLL);
        page04DiskContentErrorIV = (ImageView) rootView.findViewById(R.id.page04DiskContentErrorIV);
        page04DiskContentCutBarLL = (LinearLayout) rootView.findViewById(R.id.page04DiskContentCutBarLL);
        //page04DiskContentTitleTV = (TextView) rootView.findViewById(R.id.page04DiskContentTitleTV);
        page04DiskContentLV = (ListView) rootView.findViewById(R.id.page04DiskContentLV);
        bar02IconSelectAllIV = (ImageView) rootView.findViewById(R.id.bar02IconSelectAllIV);
        bar02TxSelectAllTV = (TextView) rootView.findViewById(R.id.bar02TxSelectAllTV);
        bar01AddFolderLL = (LinearLayout) rootView.findViewById(R.id.bar01AddFolderLL);
        bar01RefreshLL = (LinearLayout) rootView.findViewById(R.id.bar01RefreshLL);
        bar01SortLL = (LinearLayout) rootView.findViewById(R.id.bar01SortLL);
        bar01SearchLL = (LinearLayout) rootView.findViewById(R.id.bar01SearchLL);
        bar01MoreLL = (LinearLayout) rootView.findViewById(R.id.bar01MoreLL);
        bar01MoreAction1 = (TextView) rootView.findViewById(R.id.bar01MoreAction1);
        bar01MoreAction2 = (TextView) rootView.findViewById(R.id.bar01MoreAction2);
        bar02CopyLL = (LinearLayout) rootView.findViewById(R.id.bar02CopyLL);
        bar02IconCopyIV = (ImageView) rootView.findViewById(R.id.bar02IconCopyIV);
        bar02TxCopyTV = (TextView) rootView.findViewById(R.id.bar02TxCopyTV);
        bar02CutLL = (LinearLayout) rootView.findViewById(R.id.bar02CutLL);
        bar02IconCutIV = (ImageView) rootView.findViewById(R.id.bar02IconCutIV);
        bar02TxCutTV = (TextView) rootView.findViewById(R.id.bar02TxCutTV);
        bar02DeleteLL = (LinearLayout) rootView.findViewById(R.id.bar02DeleteLL);
        bar02IconDeleteIV = (ImageView) rootView.findViewById(R.id.bar02IconDeleteIV);
        bar02TxDeleteTV = (TextView) rootView.findViewById(R.id.bar02TxDeleteTV);
        bar02SelectAllLL = (LinearLayout) rootView.findViewById(R.id.bar02SelectAllLL);
        bar02MoreLL = (LinearLayout) rootView.findViewById(R.id.bar02MoreLL);
        copyAddFolderLL = (LinearLayout) rootView.findViewById(R.id.copyAddFolderLL);
        copyCancelLL = (LinearLayout) rootView.findViewById(R.id.copyCancelLL);
        copyPasteLL = (LinearLayout) rootView.findViewById(R.id.copyPasteLL);
        cutAddFolderLL = (LinearLayout) rootView.findViewById(R.id.cutAddFolderLL);
        cutCancelLL = (LinearLayout) rootView.findViewById(R.id.cutCancelLL);
        cutPasteLL = (LinearLayout) rootView.findViewById(R.id.cutPasteLL);
        bar02MoreRenameLL = (LinearLayout) rootView.findViewById(R.id.bar02MoreRenameLL);
        bar02MoreShareLL = (LinearLayout) rootView.findViewById(R.id.bar02MoreShareLL);
        bar02MoreDetailLL = (LinearLayout) rootView.findViewById(R.id.bar02MoreDetailLL);
        bar02MoreSaveLL = (LinearLayout) rootView.findViewById(R.id.bar02MoreSaveLL);
        bar02MoreAddToVideoList = (LinearLayout) rootView.findViewById(R.id.bar02MoreAddToVideoList);
        bar02MoreRenameTV = (TextView) rootView.findViewById(R.id.bar02MoreRenameTV);
        bar02MoreShareTV = (TextView) rootView.findViewById(R.id.bar02MoreShareTV);
        bar02MoreDetailTV = (TextView) rootView.findViewById(R.id.bar02MoreDetailTV);
        bar02MoreSaveTV = (TextView) rootView.findViewById(R.id.bar02MoreSaveTV);
        bar02MoreAddToVideoListTV = (TextView) rootView.findViewById(R.id.bar02MoreAddToVideoListTV);

        loadingView = LayoutInflater.from(getContext()).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(loadingView).setCancelable(true);
        dialog = builder.create();

        String title = diskName + "盘";
        //page04DiskContentTitleTV.setText(title);

        if(PCFileHelper.isCopy()) {
            page04DiskContentCopyBarLL.setVisibility(View.VISIBLE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        } else if(PCFileHelper.isCut()) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.VISIBLE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        } else if(PCFileHelper.isInitial()) {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.VISIBLE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.GONE);
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        } else {
            page04DiskContentCopyBarLL.setVisibility(View.GONE);
            page04DiskContentActionBar01LL.setVisibility(View.GONE);
            page04DiskContentBar01MoreRootLL.setVisibility(View.GONE);
            page04DiskContentActionBar02LL.setVisibility(View.VISIBLE);
            page04DiskContentCutBarLL.setVisibility(View.GONE);
            page04DiskContentBar02MoreRootLL.setVisibility(View.GONE);
        }
    }
    private void initEvents(){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    downloadFileData.clear();
//                    ReceivedFileManagerMessageFormat fileManagerMessage = (ReceivedFileManagerMessageFormat)
//                            prepareDataForFragment.getFileActionStateData(OrderConst.folderAction_name,
//                                    OrderConst.folderAction_openInComputer_command, "E:\\");
//                    if(fileManagerMessage.getStatus() == OrderConst.success) {
//                        NodeFormat nodeFormat = fileManagerMessage.getData();
//                        List<FileInforFormat> files = nodeFormat.getFiles();
//                        for(FileInforFormat file : files){
//                            HashMap<String, Object> hs = new HashMap<>();
//                            hs.put("folderFileName", file.getName());
//                            hs.put("folderFileInfo", file.getSize() + "KB" + "  " + file.getLastChangeTime());
//                            downloadFileData.add(hs);
//                        }
//                        if(downloadFolderFragmentList!=null){
//                            downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        downloadFileData.clear();
//                    }
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String fileName = (String)msg.obj;
                        Toast.makeText(getActivity(), fileName+"删除失败，请重试", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        fileName = (String)msg.obj;
                        Iterator<HashMap<String, Object>> it = downloadFileData.iterator();
                        while(it.hasNext()){
                            HashMap<String, Object> file = it.next();
                            if(file.get("folderFileName").equals(fileName)){
                                it.remove();
                                downloadFolderFragmentFileAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), fileName+"删除成功", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                        break;
                }
            }
        };


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

        page04DiskContentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("diskContentActivity", "Item点击事件");
                fileBean fileBeanClick = PCFileHelper.getDatas().get(i);
                if (fileBeanClick.type == FileTypeConst.folder) {
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("加载中...");
                    dialog.show();
                    lastPoint = i;
                    // 设置路径
                    PCFileHelper.setNowFilePath(PCFileHelper.getNowFilePath() + fileBeanClick.name + "\\");
                    PCFileHelper.loadFiles();
                    String[] nodeNames = PCFileHelper.getNowFilePath().split("\\\\");
                    String pathShow = "";
                    for (String nowNode : nodeNames) {
                        pathShow += nowNode + " > ";
                    }
                    //page04DiskContentCurrentPathTV.setText(pathShow);
                } else {
                    Toasty.info(getContext(), fileBeanClick.name, Toast.LENGTH_SHORT, true).show();
                    // 其他操作

                }
            }
        });
        page04DiskContentLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!isCheckBoxIn) {
                    if(PCFileHelper.isInitial()) {
                        page04DiskContentActionBar01LL.setVisibility(View.GONE);
                        page04DiskContentActionBar02LL.setVisibility(View.VISIBLE);
                        List<fileBean> datas = PCFileHelper.getDatas();
                        for(fileBean data : datas) {
                            data.isShow = true;
                            data.isChecked = false;
                        }
                        PCFileHelper.getDatas().get(i).isChecked = true;
                        isCheckBoxIn = true;
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });
    }
    public void addItem(String fileName, String fileInfo){
        HashMap<String, Object> fileItem = new HashMap<>();
        fileItem.put("folderFileName", fileName);
        fileItem.put("folderFileInfo", fileInfo);
        downloadFileData.add(fileItem);
        if(downloadFolderFragmentList!=null){
            downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        }
    }
    public void agreeDownload(Message msg){
    }
    public void downloadFilePauseReq(Message msg){
        int id = (int) msg.obj;
        downloadFileData.get(id).put("folderFileState",PAUSE);
        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "好友文件下载暂停", Toast.LENGTH_SHORT).show();
    }
    public void downloadFileStartReq(Message msg){
        int id = (int) msg.obj;
        downloadFileData.get(id).put("folderFileState",DOWNLOADING);
        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "好友文件下载开始", Toast.LENGTH_SHORT).show();
    }
    public void downloadFileCancelReq(Message msg){
        int id = (int) msg.obj;
        downloadFileData.get(id).put("folderFileState",DOWNLOADAGAIN);
        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "好友文件下载取消", Toast.LENGTH_SHORT).show();
    }

    private class DownloadFolderFragmentFileAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<HashMap<String, Object>> downloadFileData;
        public DownloadFolderFragmentFileAdapter(Context context, List<HashMap<String, Object>> downloadFileData) {
            mInflater = LayoutInflater.from(context);
            this.downloadFileData = downloadFileData;
        }
        @Override
        public int getCount() {
            return downloadFileData.size();
        }

        @Override
        public Object getItem(int position) {
            return downloadFileData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderFile holder = new ViewHolderFile();
            convertView = mInflater.inflate(R.layout.tab06_download_manager_downfolderitem, null);
            holder.folderFileImg = (ImageView) convertView.findViewById(R.id.folderFileImg);
            holder.folderFileName  = (TextView) convertView.findViewById(R.id.folderFileName);
            holder.folderFileInfo = (TextView) convertView.findViewById(R.id.folderFileInfo);
            holder.folderFileDownload = (TextView) convertView.findViewById(R.id.folderFileDownload);

            holder.folderFileImg.setImageResource(fileIndexToImgId.toImgId(FileTypeConst.determineFileType((String) downloadFileData.get(position).get("folderFileName"))));
            holder.folderFileInfo.setText((String) downloadFileData.get(position).get("folderFileInfo"));
            holder.folderFileName.setText((String) downloadFileData.get(position).get("folderFileName"));
//            if((int) downloadFileData.get(position).get("folderFileState") == SUCCESS){
//                holder.folderFileDownload.setText("已下载");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.disabledbuttonradius);
//                holder.folderFileDownload.setEnabled(false);
//            }else if((int) downloadFileData.get(position).get("folderFileState") == PAUSE){
//                holder.folderFileDownload.setText("继续");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
//                holder.folderFileDownload.setEnabled(true);
//            }else if((int) downloadFileData.get(position).get("folderFileState") == DOWNLOADING) {
//                holder.folderFileDownload.setText("暂停");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
//                holder.folderFileDownload.setEnabled(true);
//            }else if((int) downloadFileData.get(position).get("folderFileState") == DOWNLOADAGAIN) {
//                holder.folderFileDownload.setText("重新下载");
//                holder.folderFileDownload.setBackgroundResource(R.drawable.buttonradius);
//                holder.folderFileDownload.setEnabled(true);
//            }
//
            holder.folderFileDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String state = holder.folderFileDownload.getText().toString();
//                    if(state.equals("继续")){
//                        Message continueMsg = SubTitleUtil.stateHandler.obtainMessage();
//                        continueMsg.obj = position;
//                        continueMsg.what = OrderConst.downloadFileContinue;
//                        SubTitleUtil.stateHandler.sendMessage(continueMsg);
//                        downloadFileData.get(position).put("folderFileState",DOWNLOADING);
//                        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                    }else if(state.equals("暂停")){
//                        Message pauseMsg = SubTitleUtil.stateHandler.obtainMessage();
//                        pauseMsg.obj = position;
//                        pauseMsg.what = OrderConst.downloadFilePause;
//                        SubTitleUtil.stateHandler.sendMessage(pauseMsg);
//                        downloadFileData.get(position).put("folderFileState",PAUSE);
//                        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                    }else if (state.equals("重新下载")){
//                        HashMap<String, Object> fileItem = new HashMap<>();
//                        fileItem.put("downloadStateFileId", position);
//                        fileItem.put("downloadStateFileImg", fileIndexToImgId.toImgId(FileTypeConst.determineFileType((String) downloadFileData.get(position).get("folderFileName"))));
//                        fileItem.put("downloadStateFileState", DownloadStateFragment.DOWNLOADING);
//                        fileItem.put("downloadStateFileName", downloadFileData.get(position).get("folderFileName"));
//                        fileItem.put("downloadStateFileSize", downloadFileData.get(position).get("folderFileSize"));
//                        fileItem.put("downloadStateFileProgress", "0.1%");
//                        fileItem.put("downloadStateFileStyle", DownloadStateFragment.DOWNFILE);
//                        Message startMsg = SubTitleUtil.stateHandler.obtainMessage();
//                        startMsg.obj = fileItem;
//                        startMsg.what = OrderConst.downloadFileStart;
//                        SubTitleUtil.stateHandler.sendMessage(startMsg);
//                        downloadFileData.get(position).put("folderFileState",DOWNLOADING);
//                        downloadFolderFragmentFileAdapter.notifyDataSetChanged();
//                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DeleteFileMsg.DeleteFileReq.Builder builder = DeleteFileMsg.DeleteFileReq.newBuilder();
                            builder.setFileName((String) downloadFileData.get(position).get("folderFileName"));
                            try {
                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.DELETE_FILE_REQ_VALUE, builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream,byteArray);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });
            return convertView;
        }

        class ViewHolderFile {
            ImageView folderFileImg;
            TextView folderFileName;
            TextView folderFileInfo;
            TextView folderFileDownload;
        }
    }


    /**
     * <summary>
     *  显示新建文件夹对话框并执行相应监听操作
     * </summary>
     */
    private void addFolderDialog() {
        final ActionDialog_addFolder actionDialogAddFolder = new ActionDialog_addFolder(getContext());
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
                    Toasty.error(getContext(), "文件夹名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(actionDialogAddFolder.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                    dialog.show();
                    PCFileHelper.addFolder(tempFolderName);
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
        final DeleteDialog deleteDialog = new DeleteDialog(getContext());
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
                if((PCFileHelper.getSelectedFiles().size() + PCFileHelper.getSelectedFolders().size()) != 0) {
                    dialog.show();
                    PCFileHelper.deleteFileAndFolder();
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
        final SharedFileDialog sharedFileDialog = new SharedFileDialog(getContext());
        String name = PCFileHelper.getSelectedFiles().get(0).name;
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
                    Toasty.error(getContext(), "文件描述信息不能为空", Toast.LENGTH_SHORT).show();
                else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(sharedFileDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                    sharedFileDialog.dismiss();
                    dialog.show();
                    String name = PCFileHelper.getSelectedFiles().get(0).name;
                    int size = PCFileHelper.getSelectedFiles().get(0).size;
                    long time = System.currentTimeMillis();
                    PCFileHelper.setSelectedShareFile(new SharedfileBean(name, PCFileHelper.getNowFilePath() + name, size, des, time,
                            sharedFileDialog.getUrlEditText(), sharedFileDialog.getPwdEditText()));
                    Log.e("page04", "路径：" + PCFileHelper.getSelectedShareFile().path);
                    dialog.show();
                    PCFileHelper.shareFile(des, PCFileHelper.getSelectedShareFile());
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
        final ActionDialog_reName reNameDialog = new ActionDialog_reName(getContext());
        fileBean fileBean;
        if (PCFileHelper.getSelectedFiles().size()>0){
            fileBean = PCFileHelper.getSelectedFiles().get(0);
        }else if (PCFileHelper.getSelectedFolders().size()>0){
            fileBean = PCFileHelper.getSelectedFolders().get(0);
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
                    Toasty.error(getContext(), "文件夹名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(reNameDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (type == FileTypeConst.folder){
                        PCFileHelper.reNameFolder(name,des);
                    }else {
                        if (!des.contains(".")) des = des+name.substring(name.lastIndexOf("."));
                        PCFileHelper.reNameFile(name,des);
                    }
                    reNameDialog.dismiss();
//                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    manager.hideSoftInputFromWindow(sharedFileDialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
//                    sharedFileDialog.dismiss();
//                    dialog.show();
//                    String name = PCFileHelper.getSelectedFiles().get(0).name;
//                    int size = PCFileHelper.getSelectedFiles().get(0).size;
//                    long time = System.currentTimeMillis();
//                    PCFileHelper.setSelectedShareFile(new SharedfileBean(name, PCFileHelper.getNowFilePath() + name, size, des, time,
//                            sharedFileDialog.getUrlEditText(), sharedFileDialog.getPwdEditText()));
//                    Log.e("page04", "路径：" + PCFileHelper.getSelectedShareFile().path);
//                    dialog.show();
//                    PCFileHelper.shareFile(des, PCFileHelper.getSelectedShareFile());
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
        final AddToMediaListDialog addToMediaListDialog = new AddToMediaListDialog(getContext());
        addToMediaListDialog.setCanceledOnTouchOutside(false);
        addToMediaListDialog.show();
        addToMediaListDialog.setVideoButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PCFileHelper.addToVideoList(file,"VIDEO");
                addToMediaListDialog.dismiss();
                dialog.show();
            }
        });
        addToMediaListDialog.setAudioButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PCFileHelper.addToVideoList(file,"AUDIO");
                addToMediaListDialog.dismiss();
                dialog.show();
            }
        });
        addToMediaListDialog.setImageButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PCFileHelper.addToVideoList(file,"IMAGE");
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

    public void initDownloadingFile(){
        List<fileBean> downloadingList = new ArrayList<>();
        for (fileBean bean : PCFileHelper.getDatas()){
            if (bean.name.endsWith(".temp") && bean.type != FileTypeConst.folder){
                downloadingList.add(bean);
            }
        }
        Log.w("DownloadFolderFragment", downloadingList.size()+"");
    }
}
