package com.dkzy.areaparty.phone;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.setting.SettingAddressActivity;
import com.dkzy.areaparty.phone.fragment01.setting.SettingNameActivity;
import com.dkzy.areaparty.phone.fragment01.setting.SettingPwdActivity;
import com.dkzy.areaparty.phone.fragment01.sharedFileIntentActivity;
import com.dkzy.areaparty.phone.fragment01.utils.PCFileHelper;
import com.dkzy.areaparty.phone.fragment06.ChatDBManager;
import com.dkzy.areaparty.phone.fragment06.ChatObj;
import com.dkzy.areaparty.phone.fragment06.DownloadFolderFragment;
import com.dkzy.areaparty.phone.fragment06.DownloadStateFragment;
import com.dkzy.areaparty.phone.fragment06.FileRequestDBManager;
import com.dkzy.areaparty.phone.fragment06.FriendRequestDBManager;
import com.dkzy.areaparty.phone.fragment06.RequestFriendObj;
import com.dkzy.areaparty.phone.fragment06.chat;
import com.dkzy.areaparty.phone.fragment06.dealFileRequest;
import com.dkzy.areaparty.phone.fragment06.downloadManager;
import com.dkzy.areaparty.phone.fragment06.fileList;
import com.dkzy.areaparty.phone.fragment06.fileObj;
import com.dkzy.areaparty.phone.fragment06.page06Fragment;
import com.dkzy.areaparty.phone.fragment06.searchFriend;
import com.dkzy.areaparty.phone.fragment06.userObj;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import protocol.Data.ChatData;
import protocol.Data.FileData;
import protocol.Msg.AccreditMsg;
import protocol.Msg.AddFileMsg;
import protocol.Msg.AddFriendMsg;
import protocol.Msg.ChangeFriendMsg;
import protocol.Msg.DeleteFileMsg;
import protocol.Msg.GetDownloadFileInfo;
import protocol.Msg.GetPersonalInfoMsg;
import protocol.Msg.GetUserInfoMsg;
import protocol.Msg.KeepAliveMsg;
import protocol.Msg.LoginMsg;
import protocol.Msg.PersonalSettingsMsg;
import protocol.Msg.ReceiveChatMsg;
import protocol.Msg.SendChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;
import tools.DataTypeTranslater;

/**
 * Created by SnowMonkey on 2016/12/29.
 */

public class Base {
    public static final int HEAD_INT_SIZE = 4;
    public static final int FILENUM = 3;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    public List<String> onlineUserId = Collections.synchronizedList(new ArrayList<String>());

    public Base(Socket socket, InputStream inputStream, OutputStream outputStream){
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

//    public void link(Socket socket, InputStream inputStream, OutputStream outputStream) throws UnknownHostException, IOException {
//        socket = new Socket(host, port);
//        inputStream = socket.getInputStream();
//        outputStream = socket.getOutputStream();
//    }

    // 处理服务器回复问题
//    public byte[] readFromServer() throws IOException {
//        byte[] byteArray = new byte[200];
//        inputStream.read(byteArray);
//        byteArray = cutResult(byteArray);
//        return byteArray;
//    }

    public byte[] readFromServer(InputStream inputStream) throws IOException {
//        byte[] byteArray = new byte[20000];
//        inputStream.read(byteArray);
//        byteArray = cutResult(byteArray);
//        return byteArray;
        byte[] sizeByte = new byte[12];
        inputStream.read(sizeByte);
        int size = DataTypeTranslater.bytesToInt(sizeByte, 0);
        int count = size - 12;
        byte[] b = new byte[count];
        int readCount = 0;
        while(readCount < count){
            readCount += inputStream.read(b, readCount, count - readCount);
        }
        byte[] all = new byte[size];
        System.arraycopy(sizeByte, 0, all, 0, 12);
        System.arraycopy(b, 0, all, 12, count);
        return all;
    }

//    public void writeToServer(byte[] arrayBytes) throws IOException {
//        outputStream.write(arrayBytes);
//    }

    public void writeToServer(OutputStream outputStream, byte[] arrayBytes) throws IOException {
        outputStream.write(arrayBytes);
    }

    /**
     * 用于剪切从服务器发过来的byte[]
     *
     * @param byteArray
     * @return
     */
    public byte[] cutResult(byte[] byteArray) {
        int size = DataTypeTranslater.bytesToInt(byteArray, 0);
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++)
            result[i] = byteArray[i];
        return result;
    }

    Runnable listen = new Runnable(){
        @Override
        public void run() {
            try{
                while (socket.isConnected()) {
                    byte[] byteArray = readFromServer(inputStream);
                    int size = DataTypeTranslater.bytesToInt(byteArray, 0);
                    System.out.println("BaseServer size: " + size);
                    ProtoHead.ENetworkMessage type = ProtoHead.ENetworkMessage.valueOf(DataTypeTranslater.bytesToInt(byteArray,HEAD_INT_SIZE));
                    System.out.println("BaseServer Type : " + type.toString());
                    switch (type){
                        case KEEP_ALIVE_SYNC:
                            keepAlive(byteArray, size);
                            break;
                        case LOGIN_RSP:
                            logInMsg(byteArray, size);
                            break;
                        case GET_USERINFO_RSP :
                            getUserInfo(byteArray, size);
                            break;
                        case SEND_CHAT_RSP:
                            sendChat(byteArray, size);
                            break;
                        case RECEIVE_CHAT_SYNC:
                            receiveChat(byteArray, size);
                            break;
                        /*case GET_FILE_INFO_RSP:
                            getFileInfo(byteArray, size);
                            break;*/
                        case GET_PERSONALINFO_RSP:
                            getPersonalInfo(byteArray,size);
//                            new Thread(new getPersonalInfo(byteArray, size));
                            break;
                        case ADD_FRIEND_RSP:
                            addFriend(byteArray, size);
                            break;
                        case CHANGE_FRIEND_SYNC:
                            changeFriend(byteArray, size);
                            break;
                        case ADD_FILE_RSP:
                            addFile(byteArray, size);
                            break;
                        case ACCREDIT_RSP:
                            accredit(byteArray, size);
                            break;
                        case GET_DOWNLOAD_FILE_INFO_RSP:
                            getProgress(byteArray, size);
                            break;
                        case PERSONALSETTINGS_RSP:
                            personalSetting(byteArray, size);
                            break;
                        case OFFLINE_SYNC:
                            offlineSync(byteArray, size);
                            break;
                        case DELETE_FILE_RSP:
                            deleteFile(byteArray, size);
                            break;
                        default:
                            break;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public void keepAlive(byte[] byteArray, int size){
        try {
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            KeepAliveMsg.KeepAliveSyncPacket response = KeepAliveMsg.KeepAliveSyncPacket.parseFrom(objBytes);
            if(response.getClientsId().equals("")) return;
            String ids[] = response.getClientsId().split(",");
            System.out.println(response.getClientsId());
            System.out.println(onlineUserId);
            Iterator<String> it = onlineUserId.iterator();
            HashMap<String, Integer> hm = new HashMap<>();
            while(it.hasNext()){
                String id = it.next();
                hm.put(id, 5);
            }
            for(int i = 0; i < ids.length; i++){
                if(hm.containsKey(ids[i])){
                    hm.put(ids[i], 7);
                }else{
                    hm.put(ids[i], 2);
                }
            }
            Set<String> s = hm.keySet();
            Iterator<String> i = s.iterator();
            while(i.hasNext()){
                final String id = i.next();
                if(hm.get(id) == 2){
                    //新登录用户
                    try {
                        if(!id.equals(Login.userId)){
                            GetPersonalInfoMsg.GetPersonalInfoReq.Builder builder = GetPersonalInfoMsg.GetPersonalInfoReq.newBuilder();
                            builder.setWhere("baseLogin");
                            builder.setUserId(id);
                            builder.setUserInfo(true);
                            byte[] getUserInfo = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_PERSONALINFO_REQ.getNumber(), builder.build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, getUserInfo);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if(hm.get(id) == 5){
                    //有用户登出
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(id+"logout");
                            if (MainActivity.handlerTab06 != null){
                                Message userMsg = MainActivity.handlerTab06.obtainMessage();
                                userObj user = new userObj();
                                user.setUserId(id);
                                userMsg.what = OrderConst.userLogOut;
                                userMsg.obj = user;
                                MainActivity.handlerTab06.sendMessage(userMsg);
                                onlineUserId.remove(id);
                            }
                        }
                    }).start();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void logInMsg(final byte[] byteArray, final int size){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                    for (int i = 0; i < objBytes.length; i++)
                        objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                    LoginMsg.LoginRsp response = LoginMsg.LoginRsp.parseFrom(objBytes);
                    String id = response.getUserItem(0).getUserId();
                    //另一个用户登录时如果MainActivity.handlerTab06还没有初始化，造成闪退
                    if(!response.getUserItem(0).getUserId().equals(Login.userId)){
                        if(MainActivity.handlerTab06 == null){
                            Date d1 = new Date();
                            if(new Date().getTime() - d1.getTime() > 1000){
                                Message userMsg = MainActivity.handlerTab06.obtainMessage();
                                userObj user = new userObj();
                                user.setUserId(id);
                                user.setUserName(response.getUserItem(0).getUserName());
                                user.setFileNum(response.getUserItem(0).getFileNum());
                                user.setHeadIndex(response.getUserItem(0).getHeadIndex());
                                System.out.println(response.getUserItem(0).getIsFriend());
                                if(response.getUserItem(0).getIsFriend())
                                    userMsg.what = OrderConst.friendUserLogIn_order;//好友用户登录
                                else if(response.getUserItem(0).getFileNum() >= FILENUM)
                                    userMsg.what = OrderConst.shareUserLogIn_order;//多文件用户登录
                                else
                                    userMsg.what = OrderConst.netUserLogIn_order;//网络状况好的用户登录
                                userMsg.obj = user;
                                MainActivity.handlerTab06.sendMessage(userMsg);
                            }
                        }else{
                            Message userMsg = MainActivity.handlerTab06.obtainMessage();
                            userObj user = new userObj();
                            user.setUserId(id);
                            user.setUserName(response.getUserItem(0).getUserName());
                            user.setFileNum(response.getUserItem(0).getFileNum());
                            user.setHeadIndex(response.getUserItem(0).getHeadIndex());
                            System.out.println(response.getUserItem(0).getIsFriend());
                            if(response.getUserItem(0).getIsFriend())
                                userMsg.what = OrderConst.friendUserLogIn_order;//好友用户登录
                            else if(response.getUserItem(0).getFileNum() >= FILENUM)
                                userMsg.what = OrderConst.shareUserLogIn_order;//多文件用户登录
                            else
                                userMsg.what = OrderConst.netUserLogIn_order;//网络状况好的用户登录
                            userMsg.obj = user;
                            MainActivity.handlerTab06.sendMessage(userMsg);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void getUserInfo(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];

            GetUserInfoMsg.GetUserInfoRsp response = GetUserInfoMsg.GetUserInfoRsp.parseFrom(objBytes);

            System.out.println("Response : " + GetUserInfoMsg.GetUserInfoRsp.ResultCode.valueOf(response.getResultCode().getNumber()));
            if (response.getResultCode().equals(GetUserInfoMsg.GetUserInfoRsp.ResultCode.SUCCESS)) {
                if(!response.getUserItem(0).getUserId().equals(Login.userId)){
                    chat.lf = response.getFilesList();
                    chat.mHandler.sendEmptyMessage(0);
                }
            }else{
                MainActivity.handlerTab06.sendEmptyMessage(OrderConst.getUserMsgFail_order);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void sendChat(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            SendChatMsg.SendChatRsp response = SendChatMsg.SendChatRsp.parseFrom(objBytes);
            System.out.println("Response : " + SendChatMsg.SendChatRsp.ResultCode.valueOf(response.getResultCode().getNumber()));
            if(response.getResultCode().equals(SendChatMsg.SendChatRsp.ResultCode.SUCCESS)){
                if(response.getWhere().equals("chat")) {
                    long chatId = response.getChatId();
                    long date = response.getDate();
                    ArrayList<Long> msgObj = new ArrayList<>();
                    msgObj.add(chatId);
                    msgObj.add(date);
                    Message msg = fileList.mHandler.obtainMessage();
                    msg.obj = msgObj;
                    msg.what = 0;
                    fileList.mHandler.sendMessage(msg);
                }else if(response.getWhere().equals("download")){

                }else if(response.getWhere().equals("agreeDownload")){
                    FileRequestDBManager fileRequestDBManager = MainActivity.getFileRequestDBManager();
                    fileRequestDBManager.deleteFileRequestSQL(response.getPeerId(), response.getFileDate(), Login.userId + "transform");
                    dealFileRequest.mHandle.sendEmptyMessage(1);
                }else if(response.getWhere().equals("disagreeDownload")){
                    FileRequestDBManager fileRequestDBManager = MainActivity.getFileRequestDBManager();
                    fileRequestDBManager.deleteFileRequestSQL(response.getPeerId(), response.getFileDate(), Login.userId + "transform");
                    dealFileRequest.mHandle.sendEmptyMessage(2);
                }
            }else{
                if(response.getWhere().equals("agreeDownload")){
                    dealFileRequest.mHandle.sendEmptyMessage(3);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receiveChat(final byte[] byteArray, final int size){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                    for (int i = 0; i < objBytes.length; i++)
                        objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                    ReceiveChatMsg.ReceiveChatSync response = ReceiveChatMsg.ReceiveChatSync.parseFrom(objBytes);
                    System.out.println(response);
                    if (response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.SYSTEM) {
                        String chatData = response.getChatData(0).getChatBody();
                        if(chatData.contains("logOut")){
                            String logOutUserId = chatData.substring(0,chatData.length()-6);
                            Message userMsg = MainActivity.handlerTab06.obtainMessage();
                            userObj user = new userObj();
                            user.setUserId(logOutUserId);
                            userMsg.what = OrderConst.userLogOut;
                            userMsg.obj = user;
                            MainActivity.handlerTab06.sendMessage(userMsg);
                        }else if(chatData.split(",")[0].equals("mobile accredit request")){
                            Intent intent = new Intent(MyApplication.getContext(),AlertAccreditActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("style","accreditRequest");
                            intent.putExtra("mobileInfo",chatData.split(",")[2]);
                            intent.putExtra("mobileMac",chatData.split(",")[1]);
                            intent.putExtra("deviceType","mobile");
                            MyApplication.getContext().startActivity(intent);
                            //MyApplication.showAccreditDialog(chatData.split(",")[2]);
//                            AccreditMsg.AccreditReq.Builder accreditBuilder = AccreditMsg.AccreditReq.newBuilder();
//                            accreditBuilder.setAccreditCode("11");
//                            accreditBuilder.setAccreditMac(chatData.split(",")[1]);
//                            accreditBuilder.setUserId("petter");
//                            accreditBuilder.setType(AccreditMsg.AccreditReq.Type.AGREE);
//                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ACCREDIT_REQ.getNumber(), accreditBuilder
//                                    .build().toByteArray());
//                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }else if(chatData.split(",")[0].equals("pc accredit request")){
                            System.out.println("授权消息");
                            Intent intent = new Intent(MyApplication.getContext(), AlertAccreditActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("style", "accreditRequest");
                            intent.putExtra("mobileInfo", chatData.split(",")[2]);
                            intent.putExtra("mobileMac", chatData.split(",")[1]);
                            intent.putExtra("deviceType", "pc");
                            MyApplication.getContext().startActivity(intent);
                        }else if(chatData.contains("FILE_EXIST_RECEIVER")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo","已存在相同文件"+chatData.split(",")[1]);
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("FILE_EXIST_SENDER")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo","对方已存在相同文件"+chatData.split(",")[1]);
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("NO_SUCH_FILE_RECEIVER")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo",chatData.split(",")[1]+"已被对方删除或更换了路径");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("NO_SUCH_FILE_SENDER")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo",chatData.split(",")[1]+"已删除或更换了路径");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("HOLE_SUCCESS")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo",chatData.split(",")[1]+"打洞成功,开始直接传输");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("HOLE_FAIL")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo",chatData.split(",")[1]+"由于运营商问题直接传输失败，开始中继传输");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("FILE_OVER")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo",chatData.split(",")[1]+"文件接收完成");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("RELAY_SUCCESS")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo","中继传输成功");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("RELAY_FAIL")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo","中继传输失败，请重试");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("PAIR_CONNECTION_FULL")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo",chatData.split(",")[1]+"正在下载，请等待传输完成后再重试");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("UDP_CONNECTION_FULL")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo","下载通路已满，请等待正在下载的文件下载完成后重试");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("RELAY_CONNECTION_FULL")){
                            Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                            intent.putExtra("style","holeMsg");
                            intent.putExtra("msgInfo","下载通路已满，请等待正在下载的文件下载完成后重试");
                            MyApplication.getContext().startService(intent);
                        }
                        else if(chatData.contains("fileProcess")){
                            Message friendDownloadStateMsg = DownloadStateFragment.mHandler.obtainMessage();
                            String fileList = chatData.substring(chatData.indexOf(",")+1);
                            friendDownloadStateMsg.obj = fileList;
                            friendDownloadStateMsg.what = 0;
                            downloadManager.mHandler.sendMessage(friendDownloadStateMsg);
                        }
                    }
                    if (response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.DOWNLOAD) {
                        Message fileRequestMsg = MainActivity.handlerTab06.obtainMessage();
                        fileObj fileRequest = new fileObj();
                        fileRequest.setFileDate(response.getChatData(0).getFileDate());
                        fileRequest.setFileName(response.getChatData(0).getFileName());
                        fileRequest.setSenderId(response.getChatData(0).getSendUserId());
                        fileRequest.setFileSize(Integer.parseInt(response.getChatData(0).getFileSize()));
                        FileRequestDBManager fileRequestDBManager = MainActivity.getFileRequestDBManager();
                        fileRequestDBManager.addFileRequestSQL(fileRequest, Login.userId + "transform");
                        fileRequestMsg.obj = fileRequest;
                        fileRequestMsg.what = OrderConst.addFileRequest;
                        MainActivity.handlerTab06.sendMessage(fileRequestMsg);

                        Intent intent = new Intent(MyApplication.getContext(),AlertRequestActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("fileName",response.getChatData(0).getFileName());
                        intent.putExtra("userId",response.getChatData(0).getSendUserId());
                        MyApplication.getContext().startActivity(intent);
//                            SendChatMsg.SendChatReq.Builder responseBuilder = SendChatMsg.SendChatReq.newBuilder();
//                            ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
//                            chatItem.setTargetType(ChatData.ChatItem.TargetType.AGREEDOWNLOAD);
//                            chatItem.setFileName(response.getChatData(0).getFileName());
//                            chatItem.setFileDate(response.getChatData(0).getFileDate());
//                            chatItem.setSendUserId(response.getChatData(0).getReceiveUserId());
//                            chatItem.setReceiveUserId(response.getChatData(0).getSendUserId());
//                            chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
//                            chatItem.setChatBody(response.getChatData(0).getFileName());
//                            responseBuilder.setChatData(chatItem);
//                            byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), responseBuilder.build().toByteArray());
//                            Login.base.writeToServer(Login.outputStream, reByteArray);

                    }
                    if(response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.AGREEDOWNLOAD){
                        try {
                            Message friendDownloadMsg = MainActivity.downloadHandler.obtainMessage();
                            Message friendDownloadStateMsg = MainActivity.stateHandler.obtainMessage();
                            fileObj fileInfo = new fileObj();
                            fileInfo.setFileName(response.getChatData(0).getFileName());
                            fileInfo.setFileSize(Integer.parseInt(response.getChatData(0).getFileSize()));
                            fileInfo.setSenderId(response.getChatData(0).getSendUserId());
                            friendDownloadMsg.obj = fileInfo;
                            friendDownloadMsg.what = OrderConst.agreeDownload;
                            friendDownloadStateMsg.obj = fileInfo;
                            friendDownloadStateMsg.what = OrderConst.agreeDownloadState;
                            MainActivity.downloadHandler.sendMessage(friendDownloadMsg);
                            MainActivity.stateHandler.sendMessage(friendDownloadStateMsg);
                            MainActivity.handlerTab06.sendEmptyMessage(OrderConst.shareFileFail);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.DISAGREEDOWNLOAD){
                        //对方拒绝传输文件
                    }
                    //成为发送端，发送到pc
                    if(response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.SEND){
                        String receiverId = response.getChatData(0).getSendUserId();
                        String senderId = response.getChatData(0).getReceiveUserId();
                        String fileDate = response.getChatData(0).getFileDate();
                        String fileName = response.getChatData(0).getFileName();

                        String PcIp = "";
                        if(MyApplication.getInstance().getPC_YInforList().size() == 0){
                            return;
                        }else{
                            // PcIp = "192.168.1.132";
                            PcIp = MyApplication.getInstance().getPC_YInforList().get(0).ip;
                        }
                        int PcPort = 4003;
                        Socket socket = new Socket(PcIp, PcPort);
                        PrintStream pw = new PrintStream(socket.getOutputStream());
                        String message = "send:"+fileDate+","+senderId+","+receiverId;
                        System.out.println(message);
                        pw.println(message);
                        InputStream is = socket.getInputStream();
                        BufferedReader br =new BufferedReader(new InputStreamReader(is));
                        String s = br.readLine();
                        if(s.equals("server is busy")){
                            System.out.println("服务器忙，请稍后在试");
                        }
                        if(s.equals("server get")){
                            System.out.println("发送成功");
                        }
                        socket.close();
                        socket.shutdownOutput();
                        pw.flush();
                        pw.close();
                        socket.close();
                    }
                    //成为接收端，发送到PC
                    if(response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.RECEIVE){
                        String fileName = response.getChatData(0).getFileName();
                        String senderId = response.getChatData(0).getSendUserId();
                        String receiverId = response.getChatData(0).getReceiveUserId();

                        String PcIp = "";
                        if(MyApplication.getInstance().getPC_YInforList().size() == 0){
                            return;
                        }else{
                           // PcIp = "192.168.1.132";
                            PcIp = MyApplication.getInstance().getPC_YInforList().get(0).ip;
                        }
                        int PcPort = 4003;
                        Socket socket = new Socket(PcIp, PcPort);
                        PrintStream pw = new PrintStream(socket.getOutputStream());
                        String message = "receive:"+fileName+","+senderId+","+receiverId;
                        System.out.println(message);
                        pw.println(message);
                        InputStream is = socket.getInputStream();
                        BufferedReader br =new BufferedReader(new InputStreamReader(is));
                        String s = br.readLine();
                        if(s.equals("server is busy")){
                            System.out.println("服务器忙，请稍后在试");
                        }
                        if(s.equals("server get")){
                            System.out.println("发送成功");
                        }
                        socket.close();
                        socket.shutdownOutput();
                        pw.flush();
                        pw.close();
                        socket.close();
                    }
                    if(response.getChatData(0).getTargetType() == ChatData.ChatItem.TargetType.INDIVIDUAL){
                        String chatContent = response.getChatData(0).getChatBody();
                        String senderId = response.getChatData(0).getSendUserId();
                        ChatObj chat = new ChatObj();
                        chat.date = response.getChatData(0).getDate();
                        chat.msg = chatContent;
                        chat.receiver_id = response.getChatData(0).getReceiveUserId();
                        chat.sender_id = senderId;
                        ChatDBManager chatDB = MainActivity.getChatDBManager();
                        chatDB.addChatSQL(chat, Login.userId);

                        ActivityManager manager = (ActivityManager)  MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                        String topActivityName = manager.getRunningTasks(1).get(0).topActivity.getClassName();
                        String className = topActivityName.substring(topActivityName.lastIndexOf(".") + 1, topActivityName.length());
                        Log.e("base", "current activity is " + className);
                        if(className.equals("fileList") && senderId.equals(fileList.user_id)){
                                Message chatMsg = fileList.mHandler.obtainMessage();
                                chatMsg.obj = chat;
                                chatMsg.what = 2;
                                fileList.mHandler.sendMessage(chatMsg);
                        }else{
                            //Message chatMsg = SubTitleUtil.handlerTab06.obtainMessage();
                            if(page06Fragment.friendChatNum.containsKey(senderId)){
                                page06Fragment.friendChatNum.put(senderId, page06Fragment.friendChatNum.get(senderId)+1);
                            }else{
                                page06Fragment.friendChatNum.put(senderId,1);
                                System.out.println(page06Fragment.friendChatNum.get(senderId));
                            }
                            Message chatMsg = MainActivity.handlerTab06.obtainMessage();
                            userObj user = new userObj();
                            user.setUserId(senderId);
                            chatMsg.obj = user;
                            chatMsg.what = OrderConst.addChatNum;
                            MainActivity.handlerTab06.sendMessage(chatMsg);
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //    class getPersonalInfo implements Runnable{
//        byte[] byteArray;
//        int size;
//        getPersonalInfo(byte[] byteArray, int size){
//            this.byteArray = byteArray;
//            this.size = size;
//        }
//        @Override
//        public void run() {
    public void getPersonalInfo(byte[] byteArray, int size){
            try{
                byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
                for (int i = 0; i < objBytes.length; i++)
                    objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
                GetPersonalInfoMsg.GetPersonalInfoRsp response = GetPersonalInfoMsg.GetPersonalInfoRsp.parseFrom(objBytes);
                if (response.getResultCode().equals(GetPersonalInfoMsg.GetPersonalInfoRsp.ResultCode.FAIL) && response.getWhere().equals("")){
                    if (!(searchFriend.mHandler == null)){
                        Message userMsg = searchFriend.mHandler.obtainMessage();
                        userMsg.what = 1;
                        searchFriend.mHandler.sendMessage(userMsg);
                    }
                }
                else if(response.getWhere().equals("searchFriend")){
                    Message userMsg = searchFriend.mHandler.obtainMessage();
                    userObj user = new userObj();
                    user.setUserId(response.getUserInfo().getUserId());
                    user.setUserName(response.getUserInfo().getUserName());
                    user.setIsFriend(response.getUserInfo().getIsFriend());
                    user.setFileNum(response.getUserInfo().getFileNum());
                    user.setIsOnline(response.getUserInfo().getIsOnline());
                    user.setHeadIndex(response.getUserInfo().getHeadIndex());
                    user.setShareFiles(response.getFilesList());
                    userMsg.what = 0;
                    userMsg.obj = user;
                    searchFriend.mHandler.sendMessage(userMsg);
                }
                else if(response.getWhere().equals("page06FragmentUnfriend")){
                    Message filesMsg = MainActivity.handlerTab06.obtainMessage();
                    List<FileData.FileItem> showFilesList = response.getFilesList();
                    filesMsg.what = OrderConst.showUnfriendFiles;
                    filesMsg.obj = showFilesList;
                    MainActivity.handlerTab06.sendMessage(filesMsg);
                }
                else if(response.getWhere().equals("page06FragmentFriend")){
                    Message filesMsg = MainActivity.handlerTab06.obtainMessage();
                    List<FileData.FileItem> showFilesList = response.getFilesList();
                    filesMsg.what = OrderConst.showFriendFiles;
                    filesMsg.obj = showFilesList;
                    MainActivity.handlerTab06.sendMessage(filesMsg);
                }
                else if(response.getWhere().equals("baseLogin")){
                    String id = response.getUserInfo().getUserId();
                    if(!id.equals(Login.userId)){
                        Message userMsg = MainActivity.handlerTab06.obtainMessage();
                        userObj user = new userObj();
                        user.setUserId(id);
                        user.setUserName(response.getUserInfo().getUserName());
                        user.setFileNum(response.getUserInfo().getFileNum());
                        user.setHeadIndex(response.getUserInfo().getHeadIndex());
                        if(response.getUserInfo().getIsFriend())
                            userMsg.what = OrderConst.friendUserLogIn_order;//好友用户登录
                        else if(response.getUserInfo().getFileNum() >= FILENUM)
                            userMsg.what = OrderConst.shareUserLogIn_order;//多文件用户登录
                        else
                            userMsg.what = OrderConst.netUserLogIn_order;//网络状况好的用户登录
                        userMsg.obj = user;
                        MainActivity.handlerTab06.sendMessage(userMsg);
                        onlineUserId.add(id);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
//    }
    public void addFriend(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            AddFriendMsg.AddFriendRsp response = AddFriendMsg.AddFriendRsp.parseFrom(objBytes);
            if(response.getRequestType().equals(AddFriendMsg.AddFriendRsp.RequestType.REQUEST)){
                Message friendRequestMsg = MainActivity.handlerTab06.obtainMessage();
                userObj user = new userObj();
                user.setUserId(response.getUser().getUserId());
                user.setFileNum(response.getUser().getFileNum());
                user.setUserName(response.getUser().getUserName());
                user.setHeadIndex(response.getUser().getHeadIndex());
                friendRequestMsg.what = OrderConst.addFriend_order;
                friendRequestMsg.obj = user;
                RequestFriendObj request = new RequestFriendObj();
                request.friend_id = response.getUser().getUserId();
                request.friend_filenum = response.getUser().getFileNum();
                request.friend_headindex = response.getUser().getHeadIndex();
                request.friend_name = response.getUser().getUserName();
                request.isagree = 0;
                MainActivity.handlerTab06.sendMessage(friendRequestMsg);
                FriendRequestDBManager friendRequestDB = MainActivity.getFriendRequestDBManager();
                friendRequestDB.addRequestFriendSQL(request, Login.userId + "friend");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void changeFriend(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            ChangeFriendMsg.ChangeFriendSync response = ChangeFriendMsg.ChangeFriendSync.parseFrom(objBytes);
            if(response.getChangeType().equals(ChangeFriendMsg.ChangeFriendSync.ChangeType.ADD)){
                Message addFriendMsg = MainActivity.handlerTab06.obtainMessage();
                userObj user = new userObj();
                user.setUserId(response.getUserItem().getUserId());
                user.setFileNum(response.getUserItem().getFileNum());
                user.setUserName(response.getUserItem().getUserName());
                user.setIsFriend(response.getUserItem().getIsFriend());
                user.setHeadIndex(response.getUserItem().getHeadIndex());
                addFriendMsg.what = OrderConst.userFriendAdd_order;
                addFriendMsg.obj = user;
                RequestFriendObj request = new RequestFriendObj();
                request.friend_id = response.getUserItem().getUserId();
                request.friend_filenum = response.getUserItem().getFileNum();
                request.friend_headindex = response.getUserItem().getHeadIndex();
                request.friend_name = response.getUserItem().getUserName();
                request.isagree = 1;
                MainActivity.handlerTab06.sendMessage(addFriendMsg);
                FriendRequestDBManager friendRequestDB = MainActivity.getFriendRequestDBManager();
                friendRequestDB.changeRequestStateSQL(request, Login.userId + "friend");
                Log.i("addfriend","add finish");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addFile(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            AddFileMsg.AddFileRsp response = AddFileMsg.AddFileRsp.parseFrom(objBytes);
            if(response.getResultCode().equals(AddFileMsg.AddFileRsp.ResultCode.SUCCESS)){

                MyApplication.addMySharedFiles(PCFileHelper.getSelectedShareFile());

                Message shareFile04 = MainActivity.handlerTab01.obtainMessage();
                Message shareFile06 = MainActivity.handlerTab06.obtainMessage();
                boolean shareState = true;
                shareFile04.what = OrderConst.shareFileState;
                shareFile04.obj = shareState;
                MainActivity.handlerTab01.sendMessage(shareFile04);

                fileObj file = new fileObj();
                file.setFileName(response.getFileName());
                file.setFileInfo(response.getFileInfo());
                file.setFileSize(Integer.parseInt(response.getFileSize()));
                file.setFileDate(response.getFileDate());
                shareFile06.what = OrderConst.shareFileSuccess;
                shareFile06.obj = file;
                MainActivity.handlerTab06.sendMessage(shareFile06);
            }else {
                Message shareFile04 = MainActivity.handlerTab01.obtainMessage();
                Message shareFile06 = MainActivity.handlerTab06.obtainMessage();
                boolean shareState = false;
                shareFile04.what = OrderConst.shareFileState;
                shareFile04.obj = shareState;
                MainActivity.handlerTab01.sendMessage(shareFile04);

                shareFile06.what = OrderConst.shareFileFail;
                shareFile06.obj = shareState;
                MainActivity.handlerTab06.sendMessage(shareFile06);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accredit(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            AccreditMsg.AccreditRsp response = AccreditMsg.AccreditRsp.parseFrom(objBytes);
            if(response.getResultCode().equals(AccreditMsg.AccreditRsp.ResultCode.FAIL)){
                Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                intent.putExtra("style","accreditFail");
                MyApplication.getContext().startService(intent);
            }else if(response.getResultCode().equals(AccreditMsg.AccreditRsp.ResultCode.SUCCESS)){
                Intent intent = new Intent(MyApplication.getContext(),MyService.class);
                intent.putExtra("style","accreditSuccess");
                MyApplication.getContext().startService(intent);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProgress(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            GetDownloadFileInfo.GetDownloadFileInfoRsp response = GetDownloadFileInfo.GetDownloadFileInfoRsp.parseFrom(objBytes);
            if(response.getResultCode().equals(GetDownloadFileInfo.GetDownloadFileInfoRsp.ResultCode.FAIL)){
                DownloadStateFragment.mHandler.sendEmptyMessage(0);
            }else if(response.getResultCode().equals(GetDownloadFileInfo.GetDownloadFileInfoRsp.ResultCode.SUCCESS)){
                DownloadStateFragment.mHandler.sendEmptyMessage(1);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void personalSetting(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            PersonalSettingsMsg.PersonalSettingsRsp response = PersonalSettingsMsg.PersonalSettingsRsp.parseFrom(objBytes);
            if(response.getChangeType().equals(PersonalSettingsMsg.PersonalSettingsRsp.ChangeType.PASSWORD)) {
                if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.FAIL)) {
                    SettingPwdActivity.mHandler.sendEmptyMessage(0);
                } else if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.SUCCESS)) {
                    SettingPwdActivity.mHandler.sendEmptyMessage(1);
                } else if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.OLDPASSWORDWRONG)) {
                    SettingPwdActivity.mHandler.sendEmptyMessage(2);
                }
            }else if(response.getChangeType().equals(PersonalSettingsMsg.PersonalSettingsRsp.ChangeType.NAME)) {
                if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.FAIL)) {
                    SettingNameActivity.mHandler.sendEmptyMessage(0);
                } else if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.SUCCESS)) {
                    SettingNameActivity.mHandler.sendEmptyMessage(1);
                }
            }else if(response.getChangeType().equals(PersonalSettingsMsg.PersonalSettingsRsp.ChangeType.ADDRESS)) {
                if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.FAIL)) {
                    SettingAddressActivity.mHandler.sendEmptyMessage(0);
                } else if (response.getResultCode().equals(PersonalSettingsMsg.PersonalSettingsRsp.ResultCode.SUCCESS)) {
                    SettingAddressActivity.mHandler.sendEmptyMessage(1);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void offlineSync(byte[] byteArray, int size){
        SettingPwdActivity.mHandler.sendEmptyMessage(3);
    }

    public void deleteFile(byte[] byteArray, int size){
        try{
            byte[] objBytes = new byte[size - NetworkPacket.getMessageObjectStartIndex()];
            for (int i = 0; i < objBytes.length; i++)
                objBytes[i] = byteArray[NetworkPacket.getMessageObjectStartIndex() + i];
            DeleteFileMsg.DeleteFileRsp response = DeleteFileMsg.DeleteFileRsp.parseFrom(objBytes);
            if(response.getResultCode().equals(DeleteFileMsg.DeleteFileRsp.ResultCode.FAIL)){

            if (sharedFileIntentActivity.handler != null){
                sharedFileIntentActivity.handler.sendEmptyMessage(2);
            }
            }else if(response.getResultCode().equals(DeleteFileMsg.DeleteFileRsp.ResultCode.SUCCESS)){
                for (int i = 0; i < MyApplication.getMySharedFiles().size(); i++){
                    SharedfileBean file = MyApplication.getMySharedFiles().get(i);
                    if (file.name.equals(response.getFileName()) && file.des.equals(response.getFileInfo())){
                        MyApplication.getMySharedFiles().remove(i);
                        i--;
                    }
                }
                /*if (Login.files != null && Login.files.size() > 0){
                    for (int i = 0; i < Login.files.size() ;i ++ ){
                        FileData.FileItem file = Login.files.get(i);
                        if (file.getFileName().equals(response.getFileName()) && file.getFileInfo().equals(response.getFileInfo())){
                            Login.files.remove(i);
                            i-- ;
                        }
                    }
                }*/
                if (sharedFileIntentActivity.handler != null){
                    sharedFileIntentActivity.handler.sendEmptyMessage(1);
                }

                Message shareFile06 = MainActivity.handlerTab06.obtainMessage();
                shareFile06.what = OrderConst.deleteShareFileSuccess;
                MainActivity.handlerTab06.sendMessage(shareFile06);

                /*Message msg = DownloadFolderFragment.mHandler.obtainMessage();
                msg.obj = response.getFileName();
                msg.what = 1;
                DownloadFolderFragment.mHandler.sendMessage(msg);*/
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
