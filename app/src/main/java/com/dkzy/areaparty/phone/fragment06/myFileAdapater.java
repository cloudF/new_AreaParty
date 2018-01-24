package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import protocol.Data.ChatData;
import protocol.Msg.SendChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

public class myFileAdapater extends BaseAdapter {
    LayoutInflater mInflater;
    List<HashMap<String, Object>> filedata;
    boolean isDownload;
    String user_id;
    public myFileAdapater(Context context, List<HashMap<String, Object>> filedata, boolean isDownload) {
        mInflater = LayoutInflater.from(context);
        this.filedata = filedata;
        this.isDownload = isDownload;
    }

    /*public myFileAdapater(Context context, List<HashMap<String, Object>> filedata, boolean isDownload, String user_id) {
        mInflater = LayoutInflater.from(context);
        this.filedata = filedata;
        this.isDownload = isDownload;
        this.user_id = user_id;
    }*/
    @Override
    public int getCount() {
        return filedata.size();
    }

    @Override
    public Object getItem(int i) {
        return filedata.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolderFile holder;
        if(view == null) {
            view = mInflater.inflate(R.layout.tab06_fileitem, null);
            holder = new ViewHolderFile();
            holder.fileImg  = (ImageView) view.findViewById(R.id.fileImg);
            holder.fileName  = (TextView) view.findViewById(R.id.fileName);
            holder.fileInfo = (TextView) view.findViewById(R.id.fileInfo);
            holder.file_download_btn = (Button) view.findViewById(R.id.file_download_btn);
            if(isDownload)
                holder.file_download_btn.setVisibility(View.VISIBLE);
            else
                holder.file_download_btn.setVisibility(View.GONE);
            view.setTag(holder);
        } else {
            holder = (ViewHolderFile) view.getTag();
        }

        HashMap<String, Object> file = filedata.get(i);
        int headIndex = (int) file.get("fileImg");
        final String fileName =  (String) file.get("fileName");
        final long fileDate = (long) file.get("fileDate");
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date dt = new Date(fileDate);
        String sDateTime = sdf.format(dt);
        final String fileSize = String.valueOf(file.get("fileSize"));
        String fileInfo = sDateTime + "  " + getSize(Integer.valueOf(fileSize));
        holder.fileImg.setImageResource(headIndex);
        holder.fileName.setText(fileName);
        if(fileInfo.equals(""))
            holder.fileInfo.setText("该用户什么都没写");
        else
            holder.fileInfo.setText(fileInfo);

        holder.file_download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread (new sendDownloadMsg(user_id, fileName, String.valueOf(fileDate), fileSize)).start();
                holder.file_download_btn.setText("已请求");
                holder.file_download_btn.setEnabled(false);
                holder.file_download_btn.setBackgroundResource(R.drawable.disabledbuttonradius);
            }
        });
        return view;
    }

    class ViewHolderFile {
        ImageView fileImg;
        TextView fileName;
        TextView fileInfo;
        Button file_download_btn;
    }

    class sendDownloadMsg implements Runnable{
        String user_id;
        String file_name;
        String file_date;
        String file_size;
        sendDownloadMsg(String user_id, String file_name, String file_date, String file_size){
            this.user_id = user_id;
            this.file_date = file_date;
            this.file_name = file_name;
            this.file_size = file_size;
        }
        @Override
        public void run() {
            try{
                SendChatMsg.SendChatReq.Builder builder = SendChatMsg.SendChatReq.newBuilder();
                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                chatItem.setTargetType(ChatData.ChatItem.TargetType.DOWNLOAD);
                chatItem.setSendUserId(Login.userId);
                chatItem.setReceiveUserId(user_id);
                chatItem.setFileName(file_name);
                chatItem.setFileDate(file_date);
                chatItem.setFileSize(file_size);
                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                chatItem.setChatBody("");
                builder.setChatData(chatItem);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
                sortFIleList.mHandler.sendEmptyMessage(0);
            }catch (IOException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String getSize(int size) {
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size*10 / 1024;
        }
        if (size < 10240) {
            //保留1位小数，
            return String.valueOf((size / 10)) + "."
                    + String.valueOf((size % 10)) + "MB";
        } else {
            //保留2位小数
            size = size * 10 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}

