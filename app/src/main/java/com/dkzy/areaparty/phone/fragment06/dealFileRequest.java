package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import protocol.Data.ChatData;
import protocol.Msg.SendChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/3/10.
 */

public class dealFileRequest extends AppCompatActivity {

    private ListView downloadRequestList = null;
    private ImageButton downloadRequest_backBtn = null;
    private List<HashMap<String, Object>> requestFileData = null;
    private MyApater requestFileAdapter = null;
    private FileRequestDBManager fileRequestDB = MainActivity.getFileRequestDBManager();
    public static Handler mHandle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_downloadrequest);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getData();
        initView();
        initEvent();
    }
    private void initData(){
        requestFileData = (requestFileData==null)?new ArrayList<HashMap<String, Object>>():requestFileData;
    }
    private void getData(){
        initData();
        ArrayList<fileObj> requests = fileRequestDB.selectFileRequestSQL(Login.userId+"transform");
        System.out.println(requests.size());
        Iterator<fileObj> it = requests.iterator();
        while(it.hasNext()){
            fileObj request = it.next();
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("peerId", request.getSenderId());
            hm.put("fileName", request.getFileName());
            hm.put("fileDate", request.getFileDate());
            hm.put("fileSize", request.getFileSize());
            requestFileData.add(hm);
        }
    }
    private void initView(){
        downloadRequestList = (ListView) findViewById(R.id.downloadRequestList);
        downloadRequest_backBtn = (ImageButton) findViewById(R.id.downloadRequest_backBtn);
        requestFileAdapter = new MyApater(this);
        downloadRequestList.setAdapter(requestFileAdapter);
        downloadRequest_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealFileRequest.this.finish();
            }
        });
    }
    private void initEvent(){
        mHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        //服务器回复同意成功
                        Toast.makeText(dealFileRequest.this, "已同意对方下载",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //服务器回复拒绝成功
                        Toast.makeText(dealFileRequest.this, "已拒绝对方下载",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //双方电脑中有一台电脑不在线
                        Toast.makeText(dealFileRequest.this, "下载失败，请确保您和对方的电脑软件已连接远程服务器",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    class MyApater extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        MyApater(Context context){
            this.context = context;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return requestFileData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return requestFileData.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.tab06_downloadrequestitem, null);
            final ViewHolder holder = new ViewHolder();
            holder.requestFileImg = (ImageView) convertView.findViewById(R.id.requestFileImg);
            holder.requestUserId = (TextView) convertView.findViewById(R.id.requestUserId);
            holder.requestFileName = (TextView) convertView.findViewById(R.id.requestFileName);
            holder.agreeFileRequestBtn = (TextView) convertView.findViewById(R.id.agreeFileRequestBtn);
            holder.disagreeFileRequestBtn = (TextView) convertView.findViewById(R.id.disagreeFileRequestBtn);

            holder.requestFileImg.setImageResource(fileIndexToImgId.toImgId(FileTypeConst.determineFileType((String) requestFileData.get(position).get("fileName"))));
            holder.requestUserId.setText("用户：" +  requestFileData.get(position).get("peerId"));
            holder.requestFileName.setText("请求下载文件" +  requestFileData.get(position).get("fileName"));

            holder.agreeFileRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                SendChatMsg.SendChatReq.Builder responseBuilder = SendChatMsg.SendChatReq.newBuilder();
                                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                                chatItem.setTargetType(ChatData.ChatItem.TargetType.AGREEDOWNLOAD);
                                chatItem.setFileName((String)requestFileData.get(position).get("fileName"));
                                chatItem.setFileDate((String) requestFileData.get(position).get("fileDate"));
                                chatItem.setFileSize(String.valueOf(requestFileData.get(position).get("fileSize")));
                                chatItem.setSendUserId(Login.userId);
                                chatItem.setReceiveUserId((String) requestFileData.get(position).get("peerId"));
                                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                                chatItem.setChatBody((String)requestFileData.get(position).get("fileName"));
                                responseBuilder.setChatData(chatItem);
                                responseBuilder.setWhere("agreeDownload");
                                byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), responseBuilder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, reByteArray);
                            }catch (IOException e){
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    holder.disagreeFileRequestBtn.setVisibility(View.GONE);
                    holder.agreeFileRequestBtn.setText("已同意");
                    holder.agreeFileRequestBtn.setEnabled(false);
                    holder.agreeFileRequestBtn.setBackgroundResource(R.drawable.disabledbuttonradius);
                }
            });
            holder.disagreeFileRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SendChatMsg.SendChatReq.Builder responseBuilder = SendChatMsg.SendChatReq.newBuilder();
                                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                                chatItem.setTargetType(ChatData.ChatItem.TargetType.DISAGREEDOWNLOAD);
                                chatItem.setFileName((String) requestFileData.get(position).get("fileName"));
                                chatItem.setFileDate((String) requestFileData.get(position).get("fileDate"));
                                chatItem.setSendUserId(Login.userId);
                                chatItem.setReceiveUserId((String) requestFileData.get(position).get("peerId"));
                                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                                chatItem.setChatBody((String) requestFileData.get(position).get("fileName"));
                                responseBuilder.setChatData(chatItem);
                                responseBuilder.setWhere("disagreeDownload");
                                byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), responseBuilder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, reByteArray);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    holder.agreeFileRequestBtn.setVisibility(View.GONE);
                    holder.disagreeFileRequestBtn.setText("已拒绝");
                    holder.disagreeFileRequestBtn.setEnabled(false);
                    holder.disagreeFileRequestBtn.setBackgroundResource(R.drawable.disabledbuttonradius);
                }
            });
            return convertView;
        }
        class ViewHolder {
            ImageView requestFileImg;
            TextView requestUserId;
            TextView requestFileName;
            TextView agreeFileRequestBtn;
            TextView disagreeFileRequestBtn;
        }
    }
}
