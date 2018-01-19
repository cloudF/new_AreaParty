package com.dkzy.areaparty.phone.fragment01.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MyConnector;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment01.base.LogDownloadListener;
import com.dkzy.areaparty.phone.fragment01.model.DownloadFileModel;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.model.fileBean;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.FileInforFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.FolderInforFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.NodeFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedAddPathToHttpMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedFileManagerMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.SharedFilePathFormat;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import protocol.Msg.AddFileMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.IPAddressConst.LOCALHTTPPORT_Y;

/**
 * Created by borispaul on 17-5-6.
 */

public class PCFileHelper {

    private static final String HTTPINDEX = "";

    private static boolean isInitial = true;
    private static boolean isCopy = false;
    private static boolean isCut  = false;
    private static List<fileBean> datas = new ArrayList<>();
    private static String sourcePath = "";        // 复制、移动时的起始地址
    private static String nowFilePath = "";       // 当前路径
    private static List<fileBean> selectedFiles = new ArrayList<>();
    private static List<fileBean> selectedFolders = new ArrayList<>();
    private static SharedfileBean selectedShareFile;
    private static List<String> reCeivedActionErrorMessageList = new ArrayList<>();

    public static Handler myHandler;

    public PCFileHelper(Handler myHandler) {
        this.myHandler = myHandler;
    }


    /**
     * <summary>
     *  加载指定文件夹(路径)下的文件和文件夹，并发出消息
     * </summary>
     * <param name="path">路径(如："H:\\图片管理\\")</param>
     */
    public void loadFiles() {
        Log.e("PCFileHelper", "开始加载" + nowFilePath + "下的文件");
        new Thread(){
            @Override
            public void run() {
                datas.clear();
                try {
                    ReceivedFileManagerMessageFormat fileManagerMessage = (ReceivedFileManagerMessageFormat)
                            prepareDataForFragment.getFileActionStateData(OrderConst.folderAction_name,
                                    OrderConst.folderAction_openInComputer_command, nowFilePath);
                    if(fileManagerMessage.getStatus() == OrderConst.success) {
                        NodeFormat nodeFormat = fileManagerMessage.getData();
                        List<FolderInforFormat> folders = nodeFormat.getFolders();
                        List<FileInforFormat> files = nodeFormat.getFiles();
                        int i = 0;
                        int folderNum = folders.size();
                        int fileNum = files.size();
                        // 添加文件夹
                        for(; i < folderNum; ++i) {
                            fileBean file = new fileBean();
                            file.name = folders.get(i).getName();
                            file.subNum = folders.get(i).getSubNum();
                            file.type = FileTypeConst.folder;
                            datas.add(file);
                        }
                        // 添加文件
                        for(i = 0; i < fileNum; ++i) {
                            fileBean file = new fileBean();
                            file.name = files.get(i).getName();
                            file.size = files.get(i).getSize();
                            file.lastChangeTime = files.get(i).getLastChangeTime();
                            file.type = FileTypeConst.determineFileType(file.name);
                            datas.add(file);
                        }
                        Message message = new Message();
                        message.what = OrderConst.openFolder_order_successful;
                        myHandler.sendMessage(message);
                    } else {
                        datas.clear();
                        Message message = new Message();
                        message.what = OrderConst.openFolder_order_fail;
                        Bundle bundle = new Bundle();
                        bundle.putString("error", fileManagerMessage.getMessage());
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    }
                } catch(Exception e) {
                    datas.clear();
                    Message message = new Message();
                    message.what = OrderConst.openFolder_order_fail;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.getMessage());
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }

            }
        }.start();
    }

    public void downloadSelectedFiles() {
        Log.e("PCFileHelper", "添加选定的文件路径到HTTP服务器" + selectedFiles.size());
        reCeivedActionErrorMessageList.clear();
        final List<String> filePaths = new ArrayList<>();
        for(fileBean file : selectedFiles) {
            filePaths.add(nowFilePath + file.name);
            Log.e("PCFileHelper", "添加路径" + nowFilePath + file.name);
        }

        new Thread(){
            @Override
            public void run() {
                try {
                    ReceivedAddPathToHttpMessageFormat messageFormat = (ReceivedAddPathToHttpMessageFormat)
                            prepareDataForFragment.getAddPathToHttpState(filePaths);

                    //DownloadManager downloadManager = DownloadService.getDownloadManager();
                    if(messageFormat.getStatus() == OrderConst.failure) {
                        Log.e("PCFileHelper", "添加路径到PC服务器出错");
                        String errorFiles = "---";
                        reCeivedActionErrorMessageList = messageFormat.getData();
                        // 过滤错误的文件
                        for(String errorPath : reCeivedActionErrorMessageList) {
                            for(int i = 0; i < selectedFiles.size(); ++i) {
                                if((nowFilePath + selectedFiles.get(i).name).equals(errorPath)) {
                                    errorFiles += selectedFiles.get(i) + "---";
                                    selectedFiles.remove(i);
                                    break;
                                }
                            }
                        }
                        // 添加剩余文件到下载中
                        for(fileBean file : selectedFiles) {
                            DownloadFileModel downloadFile = new DownloadFileModel();
                            downloadFile.setCreateTime(System.currentTimeMillis());
                            downloadFile.setName(file.name);
                            downloadFile.setUrl("http://" + MyConnector.getInstance().getIP() + ":" +LOCALHTTPPORT_Y  + "/" + file.name);
                            GetRequest<File> request = OkGo.get(downloadFile.getUrl());
                            //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
                            OkDownload.request(downloadFile.getUrl(), request)
                                    .priority(1)
                                    .extra1(downloadFile)
                                    .register(new LogDownloadListener())
                                    .start();
                        }

                        if(selectedFiles.size() == 0)
                            errorFiles = "所有文件";
                        Message message = new Message();
                        message.what = OrderConst.actionFail_order;
                        Bundle bundle = new Bundle();
                        bundle.putString("actionType", OrderConst.addPathToHttp_command);
                        bundle.putString("error", errorFiles + "保存失败");
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    } else {
                        for(fileBean file : selectedFiles) {
                            String url = "http://" + MyConnector.getInstance().getIP() + ":" + LOCALHTTPPORT_Y + "/" + file.name;
                            DownloadFileModel downloadFile = new DownloadFileModel();
                            downloadFile.setCreateTime(System.currentTimeMillis());
                            downloadFile.setName(file.name);
                            downloadFile.setUrl(url);
                            Log.e("PCFileHelper", "添加路径正确" + url);
                            GetRequest<File> request = OkGo.get(url);
                            OkDownload.request(downloadFile.getUrl(), request)
                                    .priority(1)
                                    .extra1(downloadFile)
                                    .register(new LogDownloadListener())
                                    .start();
//                            if(downloadManager.getDownloadInfo(downloadFile.getUrl()) == null) {
//                                GetRequest request = OkGo.get(downloadFile.getUrl());
//                                downloadManager.addTask(downloadFile.getUrl(), downloadFile, request, null);
//                            }
                        }

                        Message message = new Message();
                        message.what = OrderConst.actionSuccess_order;
                        Bundle bundle = new Bundle();
                        bundle.putString("actionType", OrderConst.addPathToHttp_command);
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.getMessage());
                    bundle.putString("actionType", OrderConst.addPathToHttp_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }

                selectedFiles.clear();
                selectedFolders.clear();
                for(fileBean file : datas) {
                    file.isChecked = false;
                    file.isShow = false;
                }
            }
        }.start();
    }
    public static void playMedia(final fileBean file ) {
        Log.e("PCFileHelper", "添加选定的文件路径到HTTP服务器" + selectedFiles.size());
        final List<String> filePaths = new ArrayList<>();
        filePaths.add(nowFilePath + file.name);
        new Thread(){
            @Override
            public void run() {
                try {
                    ReceivedAddPathToHttpMessageFormat messageFormat = (ReceivedAddPathToHttpMessageFormat)
                            prepareDataForFragment.getAddPathToHttpState(filePaths);

                    //DownloadManager downloadManager = DownloadService.getDownloadManager();
                    if(messageFormat.getStatus() == OrderConst.failure) {
                        Log.e("PCFileHelper", "添加路径到PC服务器出错");
                    } else {
                        MediafileHelper.playMediaFile(OrderConst.videoAction_name,nowFilePath + file.name,file.name,MyApplication.getSelectedTVIP().name,myHandler);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * <summary>
     *  在当前路径下新建文件夹
     * </summary>
     * <param name="name">文件夹名称</param>
     */
    public void addFolder(final String name) {
        Log.e("PCFileHelper", "新建文件夹" + name);
        new Thread(){
            @Override
            public void run() {
                try {
                    ReceivedActionMessageFormat folderActionMessageReceived = (ReceivedActionMessageFormat)
                            prepareDataForFragment.getFileActionStateData(OrderConst.folderAction_name,
                                    OrderConst.folderAction_addInComputer_command, nowFilePath + name);
                    if(folderActionMessageReceived.getStatus() == OrderConst.success) {
                        Message message = new Message();
                        message.what = OrderConst.actionSuccess_order;
                        Bundle bundle = new Bundle();
                        bundle.putString("actionType", OrderConst.folderAction_addInComputer_command);
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = OrderConst.actionFail_order;
                        Bundle bundle = new Bundle();
                        bundle.putString("error", folderActionMessageReceived.getMessage());
                        bundle.putString("actionType", OrderConst.folderAction_addInComputer_command);
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.getMessage());
                    bundle.putString("actionType", OrderConst.folderAction_addInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }

            }
        }.start();
    }

    /**
     * <summary>
     *  删除选中文件和文件夹，并发出消息
     * </summary>
     */
    public void deleteFileAndFolder() {
        Log.e("PCFileHelper", "开始删除选中的文件(夹)" + (selectedFolders.size() + selectedFiles.size()));
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
                // 依次删除选中的文件
                for (fileBean file:selectedFiles) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.fileAction_name,
                                        OrderConst.fileOrFolderAction_deleteInComputer_command,
                                        nowFilePath + file.name);
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        }
                    } catch(Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                // 依次删除选中的文件夹
                for (fileBean folder:selectedFolders) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.folderAction_name,
                                        OrderConst.fileOrFolderAction_deleteInComputer_command,
                                        nowFilePath + folder.name);
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        }
                    } catch (Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_deleteInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件删除出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_deleteInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
                isCut = false;
                isCopy = false;
                isInitial = true;
                selectedFolders.clear();
                selectedFiles.clear();
            }

        }.start();
    }

    /**
     * <summary>
     *  删除选中文件和文件夹，并发出消息
     * </summary>
     */

    /**
     * <summary>
     *  删除选中文件和文件夹，并发出消息
     * </summary>
     */
    public void deleteFile(final List<String> paths) {
        Log.e("PCFileHelper", "开始删除选中的文件(夹)" + (paths.size()));
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
                // 依次删除选中的文件
                for (String path:paths) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.fileAction_name,
                                        OrderConst.fileOrFolderAction_deleteInComputer_command,
                                        path);
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        }
                    } catch(Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_deleteInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件删除出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_deleteInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }

        }.start();
    }


    public static void deleteNAS(final String path, final Handler handler) {
        new Thread(){
            @Override
            public void run() {
                // 依次删除选中的文件
                ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                        prepareDataForFragment.getFileActionStateData(
                                OrderConst.folderAction_name,
                                OrderConst.FOLDER_NASDELETE_command,
                                path);
                if(tmp.getStatus() == OrderConst.success) {
                    Message message = new Message();
                    message.what = OrderConst.success;
                    handler.sendMessage(message);
                }
//                if (tmp.getStatus() == O)
//                // 执行成功
//                if(reCeivedActionErrorMessageList.size() == 0) {
//                    Message message = new Message();
//                    message.what = OrderConst.actionSuccess_order;
//                    Bundle bundle = new Bundle();
//                    bundle.putString("actionType", OrderConst.fileOrFolderAction_deleteInComputer_command);
//                    message.setData(bundle);
//                    myHandler.sendMessage(message);
//                } else {
                    // 执行失败
//                    Message message = new Message();
//                    message.what = OrderConst.actionFail_order;
//                    Bundle bundle = new Bundle();
//                    bundle.putString("error", "部分文件删除出错，详情请查看错误日志。");
//                    bundle.putString("actionType", OrderConst.fileOrFolderAction_deleteInComputer_command);
//                    message.setData(bundle);
//                    myHandler.sendMessage(message);
//                }
            }

        }.start();
    }
    /**
     * <summary>
     *  移动选中文件和文件夹，并发出消息
     * </summary>
     */
    public void cutFileAndFolder() {
        Log.e("PCFileHelper", "开始移动选中的文件(夹)到" + nowFilePath);
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
                // 依次移动选中的文件到当前路径下
                for (fileBean file:selectedFiles) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.fileAction_name,
                                        OrderConst.fileOrFolderAction_cut_command,
                                        OrderConst.paramSourcePath + sourcePath + file.name + OrderConst.paramTargetPath +
                                                nowFilePath.substring(0, nowFilePath.length() - 1));
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        } else {
                            file.isShow = false;
                            file.isChecked = false;
                            datas.add(file);
                        }
                    } catch(Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                for (fileBean folder:selectedFolders) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.folderAction_name,
                                        OrderConst.fileOrFolderAction_cut_command,
                                        OrderConst.paramSourcePath + sourcePath + folder.name + OrderConst.paramTargetPath +
                                                nowFilePath.substring(0, nowFilePath.length() - 1));
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        } else {
                            folder.isChecked = false;
                            folder.isShow = false;
                            datas.add(0, folder);
                        }
                    } catch (Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_cut_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件移动出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_cut_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
                isCut = false;
                isCopy = false;
                isInitial = true;
                selectedFiles.clear();
                selectedFolders.clear();
            }
        }.start();
    }

    /**
     * <summary>
     *  复制选中文件和文件夹，并发出消息
     * </summary>
     */
    public void copyFileAndFolder() {
        Log.e("PCFileHelper", "开始复制选中的文件(夹)到" + nowFilePath);
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
                for (fileBean file:selectedFiles) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.fileAction_name,
                                        OrderConst.fileOrFolderAction_copy_command,
                                        OrderConst.paramSourcePath + sourcePath + file.name + OrderConst.paramTargetPath +
                                                nowFilePath.substring(0, nowFilePath.length() - 1));
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        } else {
                            file.isShow = false;
                            file.isChecked = false;
                            datas.add(file);
                        }
                    } catch(Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                for (fileBean folder:selectedFolders) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.folderAction_name,
                                        OrderConst.fileOrFolderAction_copy_command,
                                        OrderConst.paramSourcePath + sourcePath + folder.name + OrderConst.paramTargetPath +
                                                nowFilePath.substring(0, nowFilePath.length() - 1));
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        } else {
                            folder.isChecked = false;
                            folder.isShow = false;
                            datas.add(0, folder);
                        }
                    } catch (Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_copy_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件复制出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_copy_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
                isCut = false;
                isCopy = false;
                isInitial = true;
                selectedFolders.clear();
                selectedFiles.clear();
            }
        }.start();
    }
    /**
     * <summary>
     *  复制选中文件和文件夹，并发出消息
     * </summary>
     */
    public void copyFile(final List<String> paths, final String targetPath) {
        Log.e("PCFileHelper", "开始复制选中的文件(夹)到" + targetPath);
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
                for (String  path : paths) {
                    try {
                        ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(
                                        OrderConst.fileAction_name,
                                        OrderConst.fileOrFolderAction_copy_command,
                                        OrderConst.paramSourcePath + path + OrderConst.paramTargetPath +
                                                targetPath.substring(0, targetPath.length() - 1));
                        if(tmp.getStatus() == OrderConst.failure) {
                            reCeivedActionErrorMessageList.add(tmp.getMessage());
                        }
                    } catch(Exception e) {
                        reCeivedActionErrorMessageList.add(e.getMessage());
                    }
                }
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_copy_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件复制出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_copy_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }
        }.start();
    }

    /*重命名*/

    public void reNameFolder(final String name, final String targetPath) {
        //Log.e("PCFileHelper", "开始复制选中的文件(夹)到" + targetPath);
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
//
                try {
                    ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                            prepareDataForFragment.getFileActionStateData(
                                    OrderConst.folderAction_name,
                                    OrderConst.fileOrFolderAction_renameInComputer_command,
                                    OrderConst.paramSourcePath + nowFilePath + name + OrderConst.paramTargetPath  +
                                            targetPath);
                    if(tmp.getStatus() == OrderConst.failure) {
                        reCeivedActionErrorMessageList.add(tmp.getMessage());
                    }
                } catch(Exception e) {
                    reCeivedActionErrorMessageList.add(e.getMessage());
                }
//
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_renameInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件复制出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_renameInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }
        }.start();
    }
    public void reNameFile(final String name, final String targetPath) {
        //Log.e("PCFileHelper", "开始复制选中的文件(夹)到" + targetPath);
        reCeivedActionErrorMessageList.clear();
        new Thread(){
            @Override
            public void run() {
//
                try {
                    ReceivedActionMessageFormat tmp = (ReceivedActionMessageFormat)
                            prepareDataForFragment.getFileActionStateData(
                                    OrderConst.fileAction_name,
                                    OrderConst.fileOrFolderAction_renameInComputer_command,
                                    OrderConst.paramSourcePath + nowFilePath + name + OrderConst.paramTargetPath  +
                                            targetPath);
                    if(tmp.getStatus() == OrderConst.failure) {
                        reCeivedActionErrorMessageList.add(tmp.getMessage());
                    }
                } catch(Exception e) {
                    reCeivedActionErrorMessageList.add(e.getMessage());
                }
//
                // 执行成功
                if(reCeivedActionErrorMessageList.size() == 0) {
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_renameInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                } else {
                    // 执行失败
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "部分文件复制出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.fileOrFolderAction_renameInComputer_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * <summary>
     *  分享当前文件到服务器
     * </summary>
     * <param name="des">文件描述信息</param>
     * <param name="file">文件</param>
     */
    public void shareFile(final String des, final SharedfileBean file) {
        Log.e("PCFileHelper", "开始分享文件到服务器" + file.name);
        new Thread(){
            @Override
            public void run() {
                AddFileMsg.AddFileReq.Builder builder = AddFileMsg.AddFileReq.newBuilder();
                builder.setFileName(file.name);
                builder.setUserId(Login.userId);
                builder.setFileInfo(des);
                builder.setFileSize(String.valueOf(file.size));
                builder.setFileDate(String.valueOf(file.timeLong));
                builder.setFileUrl(file.url);
                builder.setFilePwd(file.pwd);

                try {
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ADD_FILE_REQ.getNumber(),
                            builder.build().toByteArray());
                    Login.base.writeToServer(Login.outputStream, byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * <summary>
     *  根据分享文件到服务器的状态决定是否向PC写入信息
     * </summary>
     * <param name="msg">状态对象</param>
     */
    public void shareFileState(Message msg){
        final boolean shareState = (boolean) msg.obj;
        Log.e("PCFileHelper", "开始向PC写入分享文件的信息,分享状态" + shareState);
        new Thread(){
            @Override
            public void run() {
                if(shareState) {
                    SharedFilePathFormat filePath = new SharedFilePathFormat(String.valueOf(selectedShareFile.timeLong), selectedShareFile.path, selectedShareFile.name, selectedShareFile.size);
                    try {
                        ReceivedActionMessageFormat fileActionMessageReceived = (ReceivedActionMessageFormat)
                                prepareDataForFragment.getFileActionStateData(OrderConst.fileAction_name,
                                        OrderConst.fileAction_share_command, JsonUitl.objectToString(filePath));
                        if(fileActionMessageReceived.getStatus() == OrderConst.success) {
                            Message message = new Message();
                            message.what = OrderConst.addSharedFilePath_successful;
                            myHandler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.what = OrderConst.addSharedFilePath_fail;
                            myHandler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        Message message = new Message();
                        message.what = OrderConst.addSharedFilePath_fail;
                        myHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = OrderConst.addSharedFilePath_fail;
                    myHandler.sendMessage(message);
                }
            }
        }.start();
    }

    public void addToVideoList(fileBean file,String type){
        final String path = type + nowFilePath + file.name;
        Log.w("PCFileHelper",path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = (int) prepareDataForFragment.addPathToList(path);
                if (status == OrderConst.success){
                    Message message = new Message();
                    message.what = OrderConst.actionSuccess_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("actionType", OrderConst.folderAction_addToList_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }else {
                    Message message = new Message();
                    message.what = OrderConst.actionFail_order;
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "添加到媒体库出错，详情请查看错误日志。");
                    bundle.putString("actionType", OrderConst.folderAction_addToList_command);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }
        }).start();

    }

    public static String getSource() {
        if(isCopy)
            return "copy";
        if(isCut)
            return "cut";
        if(isInitial)
            return "open";

        return "delete";
    }

    public static String getNowFilePath() {
        return nowFilePath;
    }

    public static void setNowFilePath(String nowFilePath) {
        PCFileHelper.nowFilePath = nowFilePath;
    }

    public static boolean isInitial() {
        return isInitial;
    }

    public static void setIsInitial(boolean isInitial) {
        PCFileHelper.isInitial = isInitial;
    }

    public static boolean isCopy() {
        return isCopy;
    }

    public static void setIsCopy(boolean isCopy) {
        PCFileHelper.isCopy = isCopy;
    }

    public static boolean isCut() {
        return isCut;
    }

    public static void setIsCut(boolean isCut) {
        PCFileHelper.isCut = isCut;
    }

    public static List<fileBean> getDatas() {
        return datas;
    }

    public static void clearDatas() {
        datas.clear();
    }

    public static void setDatas(List<fileBean> datas) {
        PCFileHelper.datas = datas;
    }

    public static String getSourcePath() {
        return sourcePath;
    }

    public static void setSourcePath(String sourcePath) {
        PCFileHelper.sourcePath = sourcePath;
    }

    public static List<String> getReCeivedActionErrorMessageList() {
        return reCeivedActionErrorMessageList;
    }

    public static void setReCeivedActionErrorMessageList(List<String> reCeivedActionErrorMessageList) {
        PCFileHelper.reCeivedActionErrorMessageList = reCeivedActionErrorMessageList;
    }

    public static List<fileBean> getSelectedFiles() {
        return selectedFiles;
    }

    public static void setSelectedFiles(List<fileBean> selectedFiles) {
        PCFileHelper.selectedFiles = selectedFiles;
    }

    public static List<fileBean> getSelectedFolders() {
        return selectedFolders;
    }

    public static void setSelectedFolders(List<fileBean> selectedFolders) {
        PCFileHelper.selectedFolders = selectedFolders;
    }

    public static SharedfileBean getSelectedShareFile() {
        return selectedShareFile;
    }

    public static void setSelectedShareFile(SharedfileBean selectedShareFile) {
        PCFileHelper.selectedShareFile = selectedShareFile;
    }
}
