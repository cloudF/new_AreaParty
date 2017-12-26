package com.dkzy.areaparty.phone.fragment06;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import protocol.Data.ChatData;
import protocol.Data.FileData;
import protocol.Msg.SendChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/1/3.
 */

public class fileListTemp extends AppCompatActivity {
    public static Handler mHandler;
    private LinearLayout musicFile_wrap = null;
    private LinearLayout compressFile_wrap = null;
    private LinearLayout filmFile_wrap = null;
    private LinearLayout imgFile_wrap = null;
    private LinearLayout documentFile_wrap = null;
    private LinearLayout otherFile_wrap = null;
    private myFileAdapater musicAdapter = null;
    private myFileAdapater compressAdapter = null;
    private myFileAdapater filmAdapter = null;
    private myFileAdapater imgAdapter = null;
    private myFileAdapater documentAdapter = null;
    private myFileAdapater otherAdapter = null;
    private ImageButton fileList_backBtn;
    private ImageButton musicFile_btn;
    private ImageButton compressFile_btn;
    private ImageButton filmFile_btn;
    private ImageButton imgFile_btn;
    private ImageButton documentFile_btn;
    private ImageButton otherFile_btn;
    private ListView musicFile_listView;
    private ListView compressFile_listView;
    private ListView filmFile_listView;
    private ListView imgFile_listView;
    private ListView documentFile_listView;
    private ListView otherFile_listView;
    private myFileList fileItems;
    private List<HashMap<String, Object>> musicData;
    private List<HashMap<String, Object>> compressData;
    private List<HashMap<String, Object>> filmData;
    private List<HashMap<String, Object>> imgData;
    private List<HashMap<String, Object>> documentData;
    private List<HashMap<String, Object>> otherData;
    private String user_id;
    public static String file_id;
    public static String file_name;
    private fileObj agreeFileMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_filemaintemp);

        getData();
        initView();
        initEvent();

        //条目点击事件
        musicFile_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_id = (String) fileListTemp.this.musicData.get(info.position).get("fileId");
                file_name = ((Map<String, String>) fileListTemp.this.musicAdapter.getItem(info.position)).get("fileName");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
            }
        });
        compressFile_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_id = (String) fileListTemp.this.compressData.get(info.position).get("fileId");
                file_name = ((Map<String, String>) fileListTemp.this.compressAdapter.getItem(info.position)).get("fileName");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
            }
        });
        filmFile_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_id = (String) fileListTemp.this.filmData.get(info.position).get("fileId");
                file_name = ((Map<String, String>) fileListTemp.this.filmAdapter.getItem(info.position)).get("fileName");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
            }
        });
        imgFile_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_id = (String) fileListTemp.this.imgData.get(info.position).get("fileId");
                file_name = ((Map<String, String>) fileListTemp.this.imgAdapter.getItem(info.position)).get("fileName");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
            }
        });
        documentFile_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_id = (String) fileListTemp.this.documentData.get(info.position).get("fileId");
                file_name = ((Map<String, String>) fileListTemp.this.documentAdapter.getItem(info.position)).get("fileName");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
            }
        });
        otherFile_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                file_id = (String) fileListTemp.this.otherData.get(info.position).get("fileId");
                file_name = ((Map<String, String>) fileListTemp.this.otherAdapter.getItem(info.position)).get("fileName");
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "下载");
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        //agreeFileMsg = (fileObj) msg.obj;
                        //new Thread(agreeDownload).start();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initView(){
        musicFile_wrap = (LinearLayout) this.findViewById(R.id.musicFile_wrap);
        filmFile_wrap = (LinearLayout) this.findViewById(R.id.filmFile_wrap);
        compressFile_wrap = (LinearLayout) this.findViewById(R.id.compressFile_wrap);
        imgFile_wrap = (LinearLayout) this.findViewById(R.id.imgFile_wrap);
        documentFile_wrap = (LinearLayout) this.findViewById(R.id.documentFile_wrap);
        otherFile_wrap = (LinearLayout) this.findViewById(R.id.otherFile_wrap);
        fileList_backBtn = (ImageButton) this.findViewById(R.id.fileList_backBtn);
        musicFile_btn = (ImageButton) this.findViewById(R.id.musicFile_btn);
        compressFile_btn = (ImageButton) this.findViewById(R.id.compressFile_btn);
        filmFile_btn = (ImageButton) this.findViewById(R.id.filmFile_btn);
        imgFile_btn = (ImageButton) this.findViewById(R.id.imgFile_btn);
        documentFile_btn = (ImageButton) this.findViewById(R.id.documentFile_btn);
        otherFile_btn = (ImageButton) this.findViewById(R.id.otherFile_btn);
        musicFile_listView = (ListView) this.findViewById(R.id.musicFile_listView);
        compressFile_listView = (ListView) this.findViewById(R.id.compressFile_listView);
        filmFile_listView = (ListView) this.findViewById(R.id.filmFile_listView);
        imgFile_listView = (ListView) this.findViewById(R.id.imgFile_listView);
        documentFile_listView = (ListView) this.findViewById(R.id.documentFile_listView);
        otherFile_listView = (ListView) this.findViewById(R.id.otherFile_listView);

        //musicAdapter = new myFileAdapater(this,musicData);
        musicFile_listView.setAdapter(musicAdapter);
        //compressAdapter = new myFileAdapater(this, compressData);
        compressFile_listView.setAdapter(compressAdapter);
        //filmAdapter = new myFileAdapater(this, filmData);
        filmFile_listView.setAdapter(filmAdapter);
        //imgAdapter = new myFileAdapater(this, imgData);
        imgFile_listView.setAdapter(imgAdapter);
        //documentAdapter = new myFileAdapater(this, documentData);
        documentFile_listView.setAdapter(documentAdapter);
        //otherAdapter = new myFileAdapater(this, otherData);
        otherFile_listView.setAdapter(otherAdapter);
    }
    private void getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_id = bundle.getString("userId");
        fileItems = (myFileList) bundle.getSerializable("friendFile");
        musicData = new ArrayList<>();
        compressData = new ArrayList<>();
        filmData = new ArrayList<>();
        imgData = new ArrayList<>();
        documentData = new ArrayList<>();
        otherData = new ArrayList<>();
        for(FileData.FileItem file : fileItems.getList()){
            int style = fileStyle.getFileStyle(file);
            switch (style){
                case 0:
                    HashMap<String, Object> musicItem = new HashMap<>();
                    musicItem.put("fileId", file.getFileId());
                    musicItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    musicItem.put("fileName", file.getFileName());
                    musicItem.put("fileInfo", file.getFileInfo());
                    musicItem.put("fileSize", file.getFileSize());
                    musicData.add(musicItem);
                    break;
                case 1:
                    HashMap<String, Object> filmItem = new HashMap<>();
                    filmItem.put("fileId", file.getFileId());
                    filmItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    filmItem.put("fileName", file.getFileName());
                    filmItem.put("fileInfo", file.getFileInfo());
                    filmItem.put("fileSize", file.getFileSize());
                    filmData.add(filmItem);
                    break;
                case 2:
                    HashMap<String, Object> compressItem = new HashMap<>();
                    compressItem.put("fileId", file.getFileId());
                    compressItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    compressItem.put("fileName", file.getFileName());
                    compressItem.put("fileInfo", file.getFileInfo());
                    compressItem.put("fileSize", file.getFileSize());
                    compressData.add(compressItem);
                    break;
                case 3:
                    HashMap<String, Object> imgItem = new HashMap<>();
                    imgItem.put("fileId", file.getFileId());
                    imgItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    imgItem.put("fileName", file.getFileName());
                    imgItem.put("fileInfo", file.getFileInfo());
                    imgItem.put("fileSize", file.getFileSize());
                    imgData.add(imgItem);
                    break;
                case 4:
                    HashMap<String, Object> documentItem = new HashMap<>();
                    documentItem.put("fileId", file.getFileId());
                    documentItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    documentItem.put("fileName", file.getFileName());
                    documentItem.put("fileInfo", file.getFileInfo());
                    documentItem.put("fileSize", file.getFileSize());
                    documentData.add(documentItem);
                    break;
                default:
                    HashMap<String, Object> otherItem = new HashMap<>();
                    otherItem.put("fileId", file.getFileId());
                    otherItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    otherItem.put("fileName", file.getFileName());
                    otherItem.put("fileInfo", file.getFileInfo());
                    otherItem.put("fileSize", file.getFileSize());
                    otherData.add(otherItem);
                    break;
            }
        }
    }

    private void initEvent(){
        musicFile_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(musicData.get(position));
            }
        });
        compressFile_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(compressData.get(position));
            }
        });
        filmFile_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(filmData.get(position));
            }
        });
        imgFile_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(imgData.get(position));
            }
        });
        documentFile_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(documentData.get(position));
            }
        });
        otherFile_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(otherData.get(position));
            }
        });

        fileList_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileListTemp.this.finish();
            }
        });
        musicFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicFile_listView.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams layoutParams = musicFile_listView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    musicFile_listView.setLayoutParams(layoutParams);
                    musicFile_btn.setBackgroundResource(R.drawable.tab06_item_open);
                } else {
                    ViewGroup.LayoutParams layoutParams = musicFile_listView.getLayoutParams();
                    layoutParams.height = 0;
                    musicFile_listView.setLayoutParams(layoutParams);
                    musicFile_btn.setBackgroundResource(R.drawable.tab06_item_merge);
                }
            }
        });
        compressFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compressFile_listView.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams layoutParams = compressFile_listView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    compressFile_listView.setLayoutParams(layoutParams);
                    compressFile_btn.setBackgroundResource(R.drawable.tab06_item_open);
                } else {
                    ViewGroup.LayoutParams layoutParams = compressFile_listView.getLayoutParams();
                    layoutParams.height = 0;
                    compressFile_listView.setLayoutParams(layoutParams);
                    compressFile_btn.setBackgroundResource(R.drawable.tab06_item_merge);
                }
            }
        });
        filmFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filmFile_listView.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams layoutParams = filmFile_listView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    filmFile_listView.setLayoutParams(layoutParams);
                    filmFile_btn.setBackgroundResource(R.drawable.tab06_item_open);
                } else {
                    ViewGroup.LayoutParams layoutParams = filmFile_listView.getLayoutParams();
                    layoutParams.height = 0;
                    filmFile_listView.setLayoutParams(layoutParams);
                    filmFile_btn.setBackgroundResource(R.drawable.tab06_item_merge);
                }
            }
        });
        imgFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgFile_listView.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams layoutParams = imgFile_listView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    imgFile_listView.setLayoutParams(layoutParams);
                    imgFile_btn.setBackgroundResource(R.drawable.tab06_item_open);
                } else {
                    ViewGroup.LayoutParams layoutParams = imgFile_listView.getLayoutParams();
                    layoutParams.height = 0;
                    imgFile_listView.setLayoutParams(layoutParams);
                    imgFile_btn.setBackgroundResource(R.drawable.tab06_item_merge);
                }
            }
        });
        documentFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(documentFile_listView.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams layoutParams = documentFile_listView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    documentFile_listView.setLayoutParams(layoutParams);
                    documentFile_btn.setBackgroundResource(R.drawable.tab06_item_open);
                } else {
                    ViewGroup.LayoutParams layoutParams = documentFile_listView.getLayoutParams();
                    layoutParams.height = 0;
                    documentFile_listView.setLayoutParams(layoutParams);
                    documentFile_btn.setBackgroundResource(R.drawable.tab06_item_merge);
                }
            }
        });
        otherFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otherFile_listView.getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams layoutParams = otherFile_listView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    otherFile_listView.setLayoutParams(layoutParams);
                    otherFile_btn.setBackgroundResource(R.drawable.tab06_item_open);
                } else {
                    ViewGroup.LayoutParams layoutParams = otherFile_listView.getLayoutParams();
                    layoutParams.height = 0;
                    otherFile_listView.setLayoutParams(layoutParams);
                    otherFile_btn.setBackgroundResource(R.drawable.tab06_item_merge);
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        //info.position得到listview中选择的条目绑定的id
//        file_id = (String) fileList.this.musicData.get(info.position).get("fileId");
//        file_name = ((Map<String, String>) fileList.this.musicAdapter.getItem(info.position)).get("fileName");
        switch (item.getItemId()) {
            case 0:
                new Thread(download).start();//下载事件的方法
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    Runnable download = new Runnable() {
        @Override
        public void run() {
            try{
                SendChatMsg.SendChatReq.Builder builder = SendChatMsg.SendChatReq.newBuilder();
                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                chatItem.setTargetType(ChatData.ChatItem.TargetType.DOWNLOAD);
                chatItem.setSendUserId(Login.userId);
                chatItem.setReceiveUserId(user_id);
                chatItem.setFileName(file_name);
                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                chatItem.setChatBody("");
                builder.setChatData(chatItem);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };
    Runnable agreeDownload = new Runnable() {
        @Override
        public void run() {
            try{
                SendChatMsg.SendChatReq.Builder responseBuilder = SendChatMsg.SendChatReq.newBuilder();
                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                chatItem.setTargetType(ChatData.ChatItem.TargetType.AGREEDOWNLOAD);
                chatItem.setSendUserId(agreeFileMsg.getReceiverId());
                chatItem.setReceiveUserId(agreeFileMsg.getSenderId());
                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                chatItem.setChatBody("get");
                responseBuilder.setChatData(chatItem);
                byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), responseBuilder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, reByteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    public void showFileInfo(HashMap<String, Object> h){
        String[] items = new String[h.size()-2];
        items[0] = "文件名： " + h.get("fileName");
        items[1] = "文件大小： " + h.get("fileSize") + "KB";
        items[2] = "文件描述： " + h.get("fileInfo");
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("文件信息");
        listDialog.setItems(items, null);
        listDialog.show();
    }
}

