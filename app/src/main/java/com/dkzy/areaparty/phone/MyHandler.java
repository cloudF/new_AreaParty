package com.dkzy.areaparty.phone;


import android.os.Handler;
import android.os.Message;

import com.dkzy.areaparty.phone.fragment01.computerMonitorActivity;
import com.dkzy.areaparty.phone.fragment01.page01Fragment;
import com.dkzy.areaparty.phone.fragment01.prepareDataForFragment;
import com.dkzy.areaparty.phone.fragment02.page02Fragment;
import com.dkzy.areaparty.phone.fragment03.page03Fragment;
import com.dkzy.areaparty.phone.fragment01.diskContentActivity;
import com.dkzy.areaparty.phone.fragment06.BtFolderFragment;
import com.dkzy.areaparty.phone.fragment06.DownloadFolderFragment;
import com.dkzy.areaparty.phone.fragment06.DownloadStateFragment;
import com.dkzy.areaparty.phone.fragment06.page06Fragment;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ProcessFormat;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by boris on 2016/12/12.
 */

public class MyHandler extends Handler {
    page01Fragment fragment01;
    page02Fragment fragment02;
    page03Fragment fragment03;
    page06Fragment fragment06;
    com.dkzy.areaparty.phone.fragment01.computerFragment01 computerFragment01;
    com.dkzy.areaparty.phone.fragment01.computerFragment02 computerFragment02;
    computerMonitorActivity activity;
    DownloadStateFragment downloadStateFragment;
    DownloadFolderFragment downloadFolderFragment;
    BtFolderFragment btFolderFragment;

    public MyHandler() {}

    MyHandler(page01Fragment fragment) {
        this.fragment01 = new WeakReference<>(fragment).get();
    }

    MyHandler(page02Fragment fragment) {
        this.fragment02 = new WeakReference<>(fragment).get();
    }

    MyHandler(page03Fragment fragment) {
        this.fragment03 = new WeakReference<>(fragment).get();
    }

    MyHandler(page06Fragment fragment) {
        this.fragment06 = new WeakReference<>(fragment).get();
    }

    public MyHandler(com.dkzy.areaparty.phone.fragment01.computerFragment01 monitorTab01) {
        this.computerFragment01 = monitorTab01;
    }

    public MyHandler(com.dkzy.areaparty.phone.fragment01.computerFragment02 monitorTab02) {
        this.computerFragment02 = monitorTab02;
    }

    public MyHandler(computerMonitorActivity computerMonitorActivity) {
        this.activity = computerMonitorActivity;
    }

    public MyHandler(DownloadStateFragment fragment) {
        this.downloadStateFragment = new WeakReference<>(fragment).get();
    }

    public MyHandler(DownloadFolderFragment fragment) {
        this.downloadFolderFragment = new WeakReference<>(fragment).get();
    }

    public MyHandler(BtFolderFragment fragment) {
        this.btFolderFragment = new WeakReference<>(fragment).get();
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case OrderConst.setUserName:
                fragment01.setUserName(msg);
                break;
            case OrderConst.refreshTab01ComputerFragment01_order: {
                String memoryPercent      = prepareDataForFragment.getMemoryPercentData() + "";
                String memoryUsed         = prepareDataForFragment.getMemory_used() + "";
                String memoryTotal        = prepareDataForFragment.getMemory_total() + "";
                String cpuPercent         = prepareDataForFragment.getCpuPercentData() + "";
                int    memoryPercentValue = prepareDataForFragment.getMemoryPercentData();
                int[]  memoryPercentArray = prepareDataForFragment.getMemoryPercentArray();
                int[]  cpuPercentArray    = prepareDataForFragment.getCpuPercentArray();
                computerFragment01.refreshViews(memoryPercent, memoryUsed, memoryTotal, cpuPercent,
                        memoryPercentValue, memoryPercentArray, cpuPercentArray);
            }
                break;
            case OrderConst.refreshTab01ComputerActivity_order: {
                String memoryPercent = prepareDataForFragment.getMemoryPercentData() + "";
                String memory = prepareDataForFragment.getMemory_used() + "";
                String netDl = prepareDataForFragment.getNet_down() + "";
                String netUl = prepareDataForFragment.getNet_up() + "";
                activity.refreshNetAndMemoryText(memoryPercent, memory, netDl, netUl);
            }
                break;
            case OrderConst.refreshTab01ComputerFragment02_order: {
                List<ProcessFormat> increasedProcesses = prepareDataForFragment.getIncreasedProcesses();
                List<Integer> decreasedProcessesID = prepareDataForFragment.getDecreasedProcesses();
                computerFragment02.refreshItems(increasedProcesses, decreasedProcessesID);
            }
                break;
            case OrderConst.shareFileState: {
                diskContentActivity.getPCFileHelper().shareFileState(msg);
                //fragment04.shareFileState(msg);
            }
            break;
            case OrderConst.addFriend_order:
                fragment06.addFriend(msg);
                break;
            case OrderConst.shareUserLogIn_order:
                fragment06.shareUserLogIn(msg);
                break;
            case OrderConst.friendUserLogIn_order:
                fragment06.friendUserLogIn(msg);
                break;
            case OrderConst.netUserLogIn_order:
                fragment06.netUserLogIn(msg);
                break;
            case OrderConst.getUserMsgFail_order:
                fragment06.getUserMsgFail();
                break;
            case OrderConst.delFriend_order:
                fragment06.delFriend(msg);
                break;
            case OrderConst.userFriendAdd_order:
                fragment06.friendUserAdd(msg);
                break;
			case OrderConst.userLogOut:
                fragment06.userLogOut(msg);
                break;
            case OrderConst.showUnfriendFiles:
                fragment06.showFileList(msg);
                break;
            case OrderConst.showFriendFiles:
                fragment06.showFriendFiles(msg);
                break;
            case OrderConst.shareFileSuccess:
                fragment06.shareFileSuccess(msg);
                break;
            case OrderConst.deleteShareFileSuccess:
                fragment06.deleteFileSuccess(msg);
                break;
            case OrderConst.shareFileFail:
                fragment06.shareFileFail();
                break;
            case OrderConst.addChatNum:
                fragment06.addChatNum(msg);
                break;
            case OrderConst.addFileRequest:
                fragment06.addFileRequest(msg);
                break;

            //downloadFragment
            case OrderConst.torrentFileStartReq:
                btFolderFragment.torrentFileStartReq(msg);
                break;
            case OrderConst.torrentFilePauseReq:
                btFolderFragment.torrentFilePauseReq(msg);
                break;
            case OrderConst.torrentFileStart:
                downloadStateFragment.torrentFileStart(msg);
                break;
            case OrderConst.torrentFileContinue:
                downloadStateFragment.torrentFileContinue(msg);
                break;
            case OrderConst.torrentFileCancelReq:
                btFolderFragment.torrentFileCancelReq(msg);
                break;
            case OrderConst.downloadFileContinue:
                downloadStateFragment.downloadFileContinue(msg);
                break;
            case OrderConst.agreeDownload:
                downloadFolderFragment.agreeDownload(msg);
                break;
            case OrderConst.agreeDownloadState:
                downloadStateFragment.agreeDownload(msg);
                break;
            case OrderConst.downloadFilePauseReq:
                downloadFolderFragment.downloadFilePauseReq(msg);
                break;
            case OrderConst.downloadFileStartReq:
                downloadFolderFragment.downloadFileStartReq(msg);
                break;
            case OrderConst.downloadFileCancelReq:
                downloadFolderFragment.downloadFileCancelReq(msg);
                break;
            case OrderConst.downloadFilePause:
                downloadStateFragment.downloadFilePause(msg);
                break;
            case OrderConst.torrentFilePause:
                downloadStateFragment.torrentFilePause(msg);
                break;
            case OrderConst.downloadFileStart:
                downloadStateFragment.downloadFileStart(msg);
                break;
        }
    }

}
