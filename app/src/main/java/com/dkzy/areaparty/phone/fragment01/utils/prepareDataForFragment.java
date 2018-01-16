package com.dkzy.areaparty.phone.fragment01.utils;

import android.util.Log;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.MyConnector;
import com.dkzy.areaparty.phone.MyConnector_TV;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment01.model.downloadedFileBean;
import com.dkzy.areaparty.phone.fragment01.model.fileBean;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataControl;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileSystemType;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.ReceiveDownloadProcessFormat;
import com.dkzy.areaparty.phone.fragment06.zhuyulin.ReceivedDownloadListFormat;
import com.dkzy.areaparty.phone.model_comman.TVCommandItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.CommandUtil;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.AddPathToHttpParamBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.FileInforFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.FolderInforFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.NodeFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedAddPathToHttpMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedDiskListFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedFileManagerMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.RequestFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.RequestFormatAddPathToHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/2/16 21:41
 */

public class prepareDataForFragment {

    public static boolean getDlnaCastState(downloadedFileBean file) {
        boolean state = false;
        String ip = MyApplication.getIPStr();
        String TVIp = MyApplication.getSelectedTVIP().ip;
        if(!(ip.equals("")) && !(TVIp.equals(""))) {
            String fileType = "";
            if(file.getFileType() == FileTypeConst.video) {
                fileType = "video";
            } else if(file.getFileType() == FileTypeConst.music) {
                fileType = "audio";
            } else if(file.getFileType() == FileTypeConst.pic) {
                fileType = "image";
            }
            String secondcommand = "http://" + ip + ":" +
                    IPAddressConst.DLNAPHONEHTTPPORT_B + "/" + URLEncoder.encode(file.getPath());
            Log.e("test", secondcommand+"***"+file.getPath());
            String fourthcommand = file.getName();
            String fifthcommand  = fileType;
            TVCommandItem tvCommandItem = CommandUtil.createPlayUrlFileOnTVCommand(secondcommand, fourthcommand, fifthcommand);
            String requestStr = JsonUitl.objectToString(tvCommandItem);
            state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
        }

        return state;
    }
    public static boolean getDlnaCastState(FileItem file, String fileType) {
        boolean state = false;
        String ip = MyApplication.getIPStr();
        String TVIp = MyApplication.getSelectedTVIP().ip;
        if(!(ip.equals("")) && !(TVIp.equals(""))) {
            String secondcommand = "http://" + ip + ":" +
                    IPAddressConst.DLNAPHONEHTTPPORT_B + "/" + URLEncoder.encode(file.getmFilePath());
            Log.e("test", secondcommand+"***"+file.getmFilePath());
            String fourthcommand = file.getmFileName();
            String fifthcommand  = fileType;
            TVCommandItem tvCommandItem = CommandUtil.createPlayUrlFileOnTVCommand(secondcommand, fourthcommand, fifthcommand);
            String requestStr = JsonUitl.objectToString(tvCommandItem);
            state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
        }

        return state;
    }
    public static boolean getDlnaCastState(fileBean file, String fileType) {
        boolean state = false;
        String ip = MyApplication.getIPStr();
        String TVIp = MyApplication.getSelectedTVIP().ip;
        if(!(ip.equals("")) && !(TVIp.equals(""))) {
            String secondcommand;
            if (fileType.equals("video")){
                secondcommand = "file://"+TVFileHelper.getNowFilePath()+file.name;
            }else if (fileType.equals("image")){
                secondcommand = TVFileHelper.getNowFilePath()+file.name;
            }else secondcommand = "";
            String fourthcommand = file.name;
            String fifthcommand  = fileType;
            TVCommandItem tvCommandItem = CommandUtil.createPlayUrlFileOnTVCommand(secondcommand, fourthcommand, fifthcommand);
            String requestStr = JsonUitl.objectToString(tvCommandItem);
            state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
        }

        return state;
    }
    public static boolean getDlnaCastState(String folderName, String fileType) {
        boolean state = false;
        String ip = MyApplication.getIPStr();
        String TVIp = MyApplication.getSelectedTVIP().ip;
        List<String> urls = new ArrayList<>();
        if(!(ip.equals("")) && !(TVIp.equals(""))) {
            ArrayList<FileItem> fileList = new ArrayList<>();
            if (fileType.equals("image")){
                fileList.addAll(ContentDataControl.getFileItemListByFolder(FileSystemType.photo,folderName));
            }else if (fileType.equals("audio")){
                fileList.addAll(ContentDataControl.getFileItemListByFolder(FileSystemType.music,folderName));
            }
            for (FileItem file : fileList){
                String secondcommand = "http://" + ip + ":" +
                        IPAddressConst.DLNAPHONEHTTPPORT_B + "/" + URLEncoder.encode(file.getmFilePath());
                Log.e("test", secondcommand+"***"+file.getmFilePath());
                urls.add(secondcommand);
            }
            String fifthcommand  = fileType;
            TVCommandItem tvCommandItem = CommandUtil.createPlayUrlFileOnTVCommand(urls, "", fifthcommand);
            String requestStr = JsonUitl.objectToString(tvCommandItem);
            state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
        }
        return state;
    }
    public static boolean getDlnaCastState(String fileType) {
        boolean state = false;
        String ip = MyApplication.getIPStr();
        String TVIp = MyApplication.getSelectedTVIP().ip;
        List<String> urls = new ArrayList<>();
        if(!(ip.equals("")) && !(TVIp.equals(""))) {
            for (fileBean file : TVFileHelper.getDatas()){
                if (file.type == FileTypeConst.pic){
                    urls.add(TVFileHelper.getNowFilePath()+file.name);
                }
            }
            String fifthcommand  = fileType;
            TVCommandItem tvCommandItem = CommandUtil.createPlayUrlFileOnTVCommand(urls, "", fifthcommand);
            String requestStr = JsonUitl.objectToString(tvCommandItem);
            state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
        }

        return state;
    }
    public static boolean getDlnaCastState(List<FileItem> setList, String fileType) {
        boolean state = false;
        if (setList!=null&&setList.size()>0){
            String ip = MyApplication.getIPStr();
            String TVIp = MyApplication.getSelectedTVIP().ip;
            List<String> urls = new ArrayList<>();
            if(!(ip.equals("")) && !(TVIp.equals(""))) {
                for (FileItem file : setList){
                    String secondcommand = "http://" + ip + ":" +
                            IPAddressConst.DLNAPHONEHTTPPORT_B + "/" + URLEncoder.encode(file.getmFilePath());
                    Log.e("test", secondcommand+"***"+file.getmFilePath());
                    urls.add(secondcommand);
                }
                String fifthcommand  = fileType;
                TVCommandItem tvCommandItem = CommandUtil.createPlayUrlFileOnTVCommand(urls, "", fifthcommand);
                String requestStr = JsonUitl.objectToString(tvCommandItem);
                state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
            }
        }
        return state;
    }
    public static boolean getDlnaCastState_bgm(List<FileItem> setList, String fileType) {
        boolean state = false;
        if (setList!=null&&setList.size()>0){
            String ip = MyApplication.getIPStr();
            String TVIp = MyApplication.getSelectedTVIP().ip;
            List<String> urls = new ArrayList<>();
            if(!(ip.equals("")) && !(TVIp.equals(""))) {
                for (FileItem file : setList){
                    String secondcommand = "http://" + ip + ":" +
                            IPAddressConst.DLNAPHONEHTTPPORT_B + "/" + URLEncoder.encode(file.getmFilePath());
                    Log.e("test", secondcommand+"***"+file.getmFilePath());
                    urls.add(secondcommand);
                }
                String fifthcommand  = fileType;
                TVCommandItem tvCommandItem = CommandUtil.createPlayBGMOnTVCommand(urls, "", fifthcommand);
                String requestStr = JsonUitl.objectToString(tvCommandItem);
                state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
            }
        }
        return state;
    }

    public static boolean closeRDP() {
        boolean state = false;
        String TVIp = MyApplication.getSelectedTVIP().ip;
        if(!(TVIp.equals(""))) {
            TVCommandItem tvCommandItem = CommandUtil.closeRdp();
            String requestStr = JsonUitl.objectToString(tvCommandItem);
            state = MyConnector.getInstance().sendMsgToIP(TVIp, IPAddressConst.TVRECEIVEPORT_MM, requestStr);
        }

        return state;
    }


    /**
     * <summary>
     *  发送磁盘操作指令并获取执行状态
     * </summary>
     * <param name="name">操作名称</param>
     * <param name="command">操作</param>
     * <param name="param">参数</param>
     * <returns>执行结果</returns>
     */
    public static Object getDiskActionStateData(String name, String command, String param) {
        Object message = new Object();
        RequestFormat request = new RequestFormat();
        request.setName(name);
        request.setCommand(command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        Log.e("IPGET", "loadDisksPre");
        switch (command) {
            case OrderConst.diskAction_get_command:
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                Log.e("IPGET", msgReceived);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedDiskListFormat.class);
                }
                break;
        }
        Log.e("page04Fragment", "disk返回");
        return message;
    }
    public static Object getDiskActionStateData_tv(String name, String command, String param) {
        Object message = new Object();
        RequestFormat request = new RequestFormat();
        request.setName(name);
        request.setCommand(command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        Log.e("IPGET", "loadDisksPre");
        switch (command) {
            case OrderConst.diskAction_get_command:
                String msgReceived;
                msgReceived = MyConnector_TV.getInstance().getActionStateMsg(requestString);
                Log.e("IPGET", msgReceived);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedDiskListFormat.class);
                }else {
                    Log.e("IPGET", "msgReceived is null");
                }
                break;
        }
        Log.e("page04Fragment", "disk返回");
        return message;
    }


    /**
     * <summary>
     *  发送文件或文件夹操作指令并获取执行状态
     * </summary>
     * <param name="name">操作名称</param>
     * <param name="command">操作</param>
     * <param name="param">参数</param>
     * <returns>执行结果</returns>
     */
    public static Object getFileActionStateData(String name, String command, String param) {
        Object message = new Object();

        RequestFormat request = new RequestFormat();
        request.setName(name);
        request.setCommand(command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        switch (command) {
            case OrderConst.fileAction_share_command:
            case OrderConst.folderAction_addInComputer_command:
            case OrderConst.fileAction_openInComputer_command:
            case OrderConst.fileOrFolderAction_deleteInComputer_command:
            case OrderConst.fileOrFolderAction_renameInComputer_command:
            case OrderConst.fileOrFolderAction_copy_command:
            case OrderConst.FOLDER_NASDELETE_command:
            case OrderConst.fileOrFolderAction_cut_command: {
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                Log.e("PCAction", command + ": " + msgReceived);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedActionMessageFormat.class);
                }
            }
                break;
            case OrderConst.folderAction_openInComputer_command: {
                message = getFolerInforArray(request);
            }
                break;
            case OrderConst.diskAction_get_command:{
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedDiskListFormat.class);
                }
            }
                break;
            case OrderConst.GETDOWNLOADSTATE:{
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedDownloadListFormat.class);
                }
            }
                break;
            case OrderConst.GETDOWNLOADProcess:{
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceiveDownloadProcessFormat.class);
                }
            }
                break;
            case OrderConst.STOPDOWNLOAD:{
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    //message = JsonUitl.stringToBean(msgReceived, ReceiveDownloadProcessFormat.class);
                    message = msgReceived;
                }
            }
                break;
            case OrderConst.RECOVERDOWNLOAD:{
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    //message = JsonUitl.stringToBean(msgReceived, ReceiveDownloadProcessFormat.class);
                    message = msgReceived;
                }
            }
            break;
            case OrderConst.DELETEDOWNLOAD:{
                String msgReceived;
                msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    //message = JsonUitl.stringToBean(msgReceived, ReceiveDownloadProcessFormat.class);
                    message = msgReceived;
                }
            }
            break;
        }
        return message;
    }
    public static Object getFileActionStateData_tv(String name, String command, String param) {
        Object message = new Object();

        RequestFormat request = new RequestFormat();
        request.setName(name);
        request.setCommand(command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        switch (command) {
            case OrderConst.fileAction_share_command:
            case OrderConst.folderAction_addInComputer_command:
            case OrderConst.fileAction_openInComputer_command:
            case OrderConst.fileOrFolderAction_deleteInComputer_command:
            case OrderConst.fileOrFolderAction_renameInComputer_command:
            case OrderConst.fileOrFolderAction_copy_command:
            case OrderConst.FOLDER_NASDELETE_command:
            case OrderConst.fileOrFolderAction_cut_command: {
                String msgReceived;
                msgReceived = MyConnector_TV.getInstance().getActionStateMsg(requestString);
                Log.e("PCAction", command + ": " + msgReceived);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedActionMessageFormat.class);
                }
            }
            break;
            case OrderConst.folderAction_openInComputer_command: {
                message = getFolerInforArray_tv(request);
            }
            break;
            case OrderConst.diskAction_get_command:
                String msgReceived;
                msgReceived = MyConnector_TV.getInstance().getActionStateMsg(requestString);
                if(!msgReceived.equals("")) {
                    message = JsonUitl.stringToBean(msgReceived, ReceivedDiskListFormat.class);
                }
                break;
        }
        return message;
    }


    /**
     * <summary>
     *  发送添加指定路径到Http服务器指令并获取执行状态
     * </summary>
     * <param name="path">路径信息</param>
     * <returns>执行结果</returns>
     */
    public static Object getAddPathToHttpState(List<String> paths) {
        Object message = new Object();

        AddPathToHttpParamBean param = new AddPathToHttpParamBean();
        param.setPaths(paths);
        RequestFormatAddPathToHttp request = new RequestFormatAddPathToHttp();
        request.setName(OrderConst.addPathToHttp_Name);
        request.setCommand(OrderConst.addPathToHttp_command);
        request.setParam(param);
        String requestString = JsonUitl.objectToString(request);
        String msgReceived = MyConnector.getInstance().getAddPathToHttpStateMsg(requestString);
        Log.w("prepareDataForFragment",msgReceived);
        if(!msgReceived.equals("")) {
            message = JsonUitl.stringToBean(msgReceived, ReceivedAddPathToHttpMessageFormat.class);
        }
        return message;
    }

    public static Object addPathToList(String path) {
        RequestFormat request = new RequestFormat();
        request.setName(OrderConst.folderAction_name);
        request.setCommand(OrderConst.folderAction_addToList_command);
        request.setParam(path);
        String requestString = JsonUitl.objectToString(request);
        String msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
        Log.w("prepareDataForFragment",msgReceived);
        int status;
        try {
            JSONObject jsonObject = new JSONObject(msgReceived);
            status = jsonObject.getInt("status");
        }catch (JSONException e){
            status = 404;
        }

        return status;
    }

    /**
     * <summary>
     *  发送请求并获取指定文件夹下所有文件组、文件夹组，并组合起来，形成新的节点信息
     * </summary>
     * <param name="request">请求(类)</param>
     * <returns>重组并封装好的节点信息组</returns>
     */
    private static ReceivedFileManagerMessageFormat getFolerInforArray(RequestFormat request) {
        List<ReceivedFileManagerMessageFormat> receivedFileInforArray = new ArrayList<>();
        String requestMsg = JsonUitl.objectToString(request);

        ReceivedFileManagerMessageFormat messageTemp = new ReceivedFileManagerMessageFormat();
        String msgReceived = MyConnector.getInstance().getActionStateMsg(requestMsg);
        try {
            messageTemp = JsonUitl.stringToBean(msgReceived, ReceivedFileManagerMessageFormat.class);
        } catch (Exception e) {}
        receivedFileInforArray.add(messageTemp);

        boolean signal = (messageTemp.getStatus() == OrderConst.success
                && messageTemp.getMessage().equals(OrderConst.folderAction_openInComputer_more_message));
        while(signal) {
            request.setParam(OrderConst.folderAction_openInComputer_more_param);
            requestMsg = JsonUitl.objectToString(request);
            msgReceived = MyConnector.getInstance().getActionStateMsg(requestMsg);
            try {
                messageTemp = JsonUitl.stringToBean(msgReceived, ReceivedFileManagerMessageFormat.class);
            } catch (Exception e) {}
            receivedFileInforArray.add(messageTemp);
            signal = (messageTemp.getStatus() == OrderConst.success &&
                    messageTemp.getMessage().equals(OrderConst.folderAction_openInComputer_more_message));
        }

        ReceivedFileManagerMessageFormat allFileInfor = new ReceivedFileManagerMessageFormat();
        List<FileInforFormat> allFiles = new ArrayList<>();
        List<FolderInforFormat> allFolders = new ArrayList<>();
        for (ReceivedFileManagerMessageFormat temp : receivedFileInforArray) {
            allFolders.addAll(temp.getData().getFolders());
            allFiles.addAll(temp.getData().getFiles());
        }
        NodeFormat nodeTemp = new NodeFormat();
        nodeTemp.setPath(messageTemp.getData().getPath());
        nodeTemp.setFolders(allFolders);
        nodeTemp.setFiles(allFiles);
        int status = OrderConst.success;
        String message = null;
        if(allFiles.size() == 0 && allFolders.size() == 0) {
            status = messageTemp.getStatus();
            message = messageTemp.getMessage();
        }
        allFileInfor.setStatus(status);
        allFileInfor.setData(nodeTemp);
        allFileInfor.setMessage(message);

        return allFileInfor;
    }
    private static ReceivedFileManagerMessageFormat getFolerInforArray_tv(RequestFormat request) {
        List<ReceivedFileManagerMessageFormat> receivedFileInforArray = new ArrayList<>();
        String requestMsg = JsonUitl.objectToString(request);

        ReceivedFileManagerMessageFormat messageTemp = new ReceivedFileManagerMessageFormat();
        String msgReceived = MyConnector_TV.getInstance().getActionStateMsg(requestMsg);
        try {
            messageTemp = JsonUitl.stringToBean(msgReceived, ReceivedFileManagerMessageFormat.class);
        } catch (Exception e) {}
        receivedFileInforArray.add(messageTemp);

        boolean signal = (messageTemp.getStatus() == OrderConst.success
                && messageTemp.getMessage().equals(OrderConst.folderAction_openInComputer_more_message));
        while(signal) {
            request.setParam(OrderConst.folderAction_openInComputer_more_param);
            requestMsg = JsonUitl.objectToString(request);
            msgReceived = MyConnector_TV.getInstance().getActionStateMsg(requestMsg);
            try {
                messageTemp = JsonUitl.stringToBean(msgReceived, ReceivedFileManagerMessageFormat.class);
            } catch (Exception e) {}
            receivedFileInforArray.add(messageTemp);
            signal = (messageTemp.getStatus() == OrderConst.success &&
                    messageTemp.getMessage().equals(OrderConst.folderAction_openInComputer_more_message));
        }

        ReceivedFileManagerMessageFormat allFileInfor = new ReceivedFileManagerMessageFormat();
        List<FileInforFormat> allFiles = new ArrayList<>();
        List<FolderInforFormat> allFolders = new ArrayList<>();
        for (ReceivedFileManagerMessageFormat temp : receivedFileInforArray) {
            allFolders.addAll(temp.getData().getFolders());
            allFiles.addAll(temp.getData().getFiles());
        }
        NodeFormat nodeTemp = new NodeFormat();
        nodeTemp.setPath(messageTemp.getData().getPath());
        nodeTemp.setFolders(allFolders);
        nodeTemp.setFiles(allFiles);
        int status = OrderConst.success;
        String message = null;
        if(allFiles.size() == 0 && allFolders.size() == 0) {
            status = messageTemp.getStatus();
            message = messageTemp.getMessage();
        }
        allFileInfor.setStatus(status);
        allFileInfor.setData(nodeTemp);
        allFileInfor.setMessage(message);

        return allFileInfor;
    }
}
