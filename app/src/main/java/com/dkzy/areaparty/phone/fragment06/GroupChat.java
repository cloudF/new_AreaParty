package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import protocol.Data.ChatData;
import protocol.Data.FileData;
import protocol.Msg.SendChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

public class GroupChat extends AppCompatActivity {
    public static Handler mHandler = null;
    private ImageButton groupChat_backBtn;
    private Button group_chat_btn_send;
    private EditText group_et_sendmessage;
    private ImageButton groupChat_setting;
    private LinearLayout group_imgFile_wrap;
    private LinearLayout group_musicFile_wrap;
    private LinearLayout group_filmFile_wrap;
    private LinearLayout group_documentFile_wrap;
    private LinearLayout group_compressFile_wrap;
    private LinearLayout group_otherFile_wrap;
    private myFileList fileItems;
    private ListView group_chatList = null;
    private List<HashMap<String, Object>> musicData;
    private List<HashMap<String, Object>> compressData;
    private List<HashMap<String, Object>> filmData;
    private List<HashMap<String, Object>> imgData;
    private List<HashMap<String, Object>> documentData;
    private List<HashMap<String, Object>> otherData;
    private List<HashMap<String, Object>> chatData;
    private TextView groupSharedPicNumTV;
    private TextView groupSharedMusicNumTV;
    private TextView groupSharedMovieNumTV;
    private TextView groupSharedDocumentNumTV;
    private TextView groupSharedRarNumTV;
    private TextView groupSharedOtherNumTV;
    private TextView group_historyMsg;
    //private int chatNum;
    private long chatId = 1;
    public static String group_id = "";
    public static String group_name = "";
    public static ArrayList<String> groupMems = new ArrayList<>();
    private int user_head;
    private int myUserHead;

    private HashMap<Long, Integer> sendChatIdList = new HashMap<>();
    private GroupChat.chatItemAdapater chatAdapater;
    public final static int FRIEND=1;
    public final static int ME=0;
    private GroupChatDBManager groupChatDB = MainActivity.getGroupChatDBManager();
    int[] layout={R.layout.chatmeitem, R.layout.chatfrienditem};
    int[] to = {R.id.chat_meChat, R.id.chat_meHead, R.id.chat_friendChat, R.id.chat_friendHead};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_groupchatmain);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        group_id = bundle.getString("GroupId");
//        group_name = bundle.getString("GroupName");
        fileItems = (myFileList) bundle.getSerializable("GroupFile");
//        groupMems = bundle.getStringArrayList("GroupMems");
        //chatNum = bundle.getInt("chatNum");
        musicData = new ArrayList<>();
        compressData = new ArrayList<>();
        filmData = new ArrayList<>();
        imgData = new ArrayList<>();
        documentData = new ArrayList<>();
        otherData = new ArrayList<>();
        chatData = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                size = 5;
                ArrayList<GroupChatObj> chats = groupChatDB.selectMyGroupChatSQL(Login.userId+"group", Login.userId, group_id, size);
                if(chats.size() > 5) size = chats.size();
                else size = Math.min(chats.size(),5);
                for(int i = size-1; i >=0; i--){
                    GroupChatObj chat = chats.get(i);
                    if(chat.sender_id.equals(Login.userId) && chat.receiver_id.equals(group_id)){
                        addTextToList(chat.msg, ME, chatId, true);
                        chatId++;
                    }else {
                        addTextToList(chat.msg, FRIEND, chatId, true);
                        chatId++;
                    }
                }
            }
        }).start();
        for(FileData.FileItem file : fileItems.getList()) {
            int style = FileTypeConst.determineFileType(file.getFileName());//fileStyle.getFileStyle(file);
            switch (style) {
                case 3:
                    HashMap<String, Object> musicItem = new HashMap<>();
                    musicItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    musicItem.put("fileName", file.getFileName());
                    musicItem.put("fileInfo", file.getFileInfo());
                    musicItem.put("fileSize", file.getFileSize());
                    musicItem.put("fileDate", file.getFileDate());
                    musicData.add(musicItem);
                    break;
                case 6:
                    HashMap<String, Object> filmItem = new HashMap<>();
                    filmItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    filmItem.put("fileName", file.getFileName());
                    filmItem.put("fileInfo", file.getFileInfo());
                    filmItem.put("fileSize", file.getFileSize());
                    filmItem.put("fileDate", file.getFileDate());
                    filmData.add(filmItem);
                    break;
                case 8:
                    HashMap<String, Object> compressItem = new HashMap<>();
                    compressItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    compressItem.put("fileName", file.getFileName());
                    compressItem.put("fileInfo", file.getFileInfo());
                    compressItem.put("fileSize", file.getFileSize());
                    compressItem.put("fileDate", file.getFileDate());
                    compressData.add(compressItem);
                    break;
                case 10:
                    HashMap<String, Object> imgItem = new HashMap<>();
                    imgItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    imgItem.put("fileName", file.getFileName());
                    imgItem.put("fileInfo", file.getFileInfo());
                    imgItem.put("fileSize", file.getFileSize());
                    imgItem.put("fileDate", file.getFileDate());
                    imgData.add(imgItem);
                    break;
                case 2:
                case 4:
                case 5:
                case 7:
                case 9:
                    HashMap<String, Object> documentItem = new HashMap<>();
                    documentItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    documentItem.put("fileName", file.getFileName());
                    documentItem.put("fileInfo", file.getFileInfo());
                    documentItem.put("fileSize", file.getFileSize());
                    documentItem.put("fileDate", file.getFileDate());
                    documentData.add(documentItem);
                    break;
                default:
                    HashMap<String, Object> otherItem = new HashMap<>();
                    otherItem.put("fileImg", fileIndexToImgId.toImgId(style));
                    otherItem.put("fileName", file.getFileName());
                    otherItem.put("fileInfo", file.getFileInfo());
                    otherItem.put("fileSize", file.getFileSize());
                    otherItem.put("fileDate", file.getFileDate());
                    otherData.add(otherItem);
                    break;
            }
        }
    }
    private void initView() {
        groupChat_setting = (ImageButton)findViewById(R.id.groupChat_setting);

        groupChat_backBtn = (ImageButton)findViewById(R.id.groupChat_backBtn);
        group_chat_btn_send = (Button)findViewById(R.id.group_chat_btn_send);
        group_et_sendmessage = (EditText)findViewById(R.id.group_et_sendmessage);
        group_imgFile_wrap = (LinearLayout)findViewById(R.id.group_imgFile_wrap);
        group_musicFile_wrap = (LinearLayout)findViewById(R.id.group_musicFile_wrap);
        group_filmFile_wrap = (LinearLayout)findViewById(R.id.group_filmFile_wrap);
        group_documentFile_wrap = (LinearLayout)findViewById(R.id.group_documentFile_wrap);
        group_compressFile_wrap = (LinearLayout)findViewById(R.id.group_compressFile_wrap);
        group_otherFile_wrap = (LinearLayout)findViewById(R.id.group_otherFile_wrap);
        groupSharedPicNumTV = (TextView)findViewById(R.id.group_friendSharedPicNumTV);
        groupSharedMusicNumTV = (TextView)findViewById(R.id.group_friendSharedMusicNumTV);
        groupSharedMovieNumTV = (TextView)findViewById(R.id.group_friendSharedMovieNumTV);
        groupSharedDocumentNumTV = (TextView)findViewById(R.id.group_friendSharedDocumentNumTV);
        groupSharedRarNumTV = (TextView)findViewById(R.id.group_friendSharedRarNumTV);
        groupSharedOtherNumTV = (TextView)findViewById(R.id.group_friendSharedOtherNumTV);
        group_historyMsg = (TextView)findViewById(R.id.group_historyMsg);
        //groupSharedTitle = (TextView)findViewById(R.id.shareFileTitleTV);
        group_chatList = (ListView) this.findViewById(R.id.group_chatList);
        chatAdapater = new GroupChat.chatItemAdapater(this, chatData, layout, to);
        group_chatList.setAdapter(chatAdapater);
        groupSharedPicNumTV.setText("("+imgData.size()+")");
        groupSharedMusicNumTV.setText("("+musicData.size()+")");
        groupSharedMovieNumTV.setText("("+filmData.size()+")");
        groupSharedDocumentNumTV.setText("("+documentData.size()+")");
        groupSharedRarNumTV.setText("("+compressData.size()+")");
        groupSharedOtherNumTV.setText("("+otherData.size()+")");
        //groupSharedTitle.setText("的分享");

    }

    private void initEvent() {
        groupChat_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if("0".equals(group_id)){
                Toast.makeText(GroupChat.this, "该群组不可修改", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, AddGroup.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isAdd",false);
                bundle.putString("GroupName",group_name);
                bundle.putString("GroupId",group_id);
                bundle.putStringArrayList("GroupMems",groupMems);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            }
        });
        //退出按钮
        groupChat_backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                GroupChat.this.finish();
            }
        });
        //发送消息按钮
        group_chat_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Login.mainMobile) {
                    if (group_et_sendmessage.getText().toString().equals("")) {
                        Toast.makeText(GroupChat.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String text = group_et_sendmessage.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!Login.socket.isConnected()) {
                                    Toast.makeText(GroupChat.this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SendChatMsg.SendChatReq.Builder builder = SendChatMsg.SendChatReq.newBuilder();
                                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                                chatItem.setTargetType(ChatData.ChatItem.TargetType.GROUP);
                                chatItem.setSendUserId(Login.userId);
                                chatItem.setReceiveUserId(group_id);
                                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                                chatItem.setChatBody(text);
                                chatItem.setChatId(chatId);
                                builder.setChatData(chatItem);
                                builder.setWhere("group");
                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, byteArray);
                                Message msg = mHandler.obtainMessage();
                                msg.obj = text;
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        group_historyMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(Login.mainMobile) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isUser",false);
                    bundle.putString("groupId", group_id);
                    bundle.putInt("groupHead", user_head);
                    bundle.putString("userName", group_name);
                    bundle.putInt("myUserHead", myUserHead);
                    intent.putExtras(bundle);
                    intent.setClass(GroupChat.this, HistoryMsg.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(GroupChat.this, "当前设备不是主设备，无法使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        group_musicFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(musicData);
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",Login.userId);
                bundle.putInt("fileStyle", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        group_compressFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(compressData);
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",Login.userId);
                bundle.putInt("fileStyle", 2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        group_filmFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(filmData);
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",Login.userId);
                bundle.putInt("fileStyle", 1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        group_imgFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(imgData);
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",Login.userId);
                bundle.putInt("fileStyle", 3);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        group_documentFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(documentData);
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",Login.userId);
                bundle.putInt("fileStyle", 4);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        group_otherFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(otherData);
                Intent intent = new Intent();
                intent.setClass(GroupChat.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",Login.userId);
                bundle.putInt("fileStyle", 5);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        ArrayList<Long> arrayList = (ArrayList<Long>) msg.obj;
                        long chatId = arrayList.get(0);
                        int position = sendChatIdList.get(chatId);
                        HashMap<String,Object> map=new HashMap<String,Object>();
                        map.put("person",ME );
                        map.put("userHead", R.drawable.tx1);
                        map.put("text", chatData.get(position).get("text"));
                        map.put("state", true);
                        map.put("chatId", chatData.get(position).get("chatId"));
                        chatData.set(position, map);
                        if(group_chatList!=null)
                            chatAdapater.notifyDataSetChanged();
                        GroupChatObj chat = new GroupChatObj();
                        chat.date = ((ArrayList<Long>) msg.obj).get(1);
                        chat.msg = (String) chatData.get(position).get("text");
                        chat.receiver_id = group_id;
                        chat.sender_id = Login.userId;
                        chat.group_id=group_id;
                        groupChatDB.addGroupChatSQL(chat, Login.userId+"group");
                        break;
                    case 1:
                        String text = (String) msg.obj;
                        addTextToList(text, ME, GroupChat.this.chatId, false);
                        group_et_sendmessage.setText("");
                        GroupChat.this.chatId++;
                        break;
                    case 2:
                        ChatObj chatMsg = (ChatObj) msg.obj;
                        addTextToList(chatMsg.msg, FRIEND, GroupChat.this.chatId, true);
                        GroupChat.this.chatId++;
                        break;
                }
            }
        };
    }
    private void addTextToList(String text, int who, long chatId, boolean state){
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("person",who );
        map.put("userHead", who==ME? headIndexToImgId.toImgId(myUserHead): headIndexToImgId.toImgId(user_head));
        map.put("text", text);
        map.put("state", state);
        map.put("chatId", chatId);
        chatData.add(map);
        if(group_chatList!=null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatAdapater.notifyDataSetChanged();
                }
            });

    }
    private class chatItemAdapater extends BaseAdapter {
        Context context;
        List<HashMap<String, Object>> chatData;
        int[] layout;
        int[] to;
        chatItemAdapater(Context context, List<HashMap<String, Object>> chatData, int[] layout, int[] to){
            super();
            this.context = context;
            this.chatData = chatData;
            this.layout = layout;
            this.to = to;
        }

        @Override
        public int getCount() {
            return chatData.size();
        }

        @Override
        public Object getItem(int i) {
            return chatData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            GroupChat.chatItemAdapater.ViewHolderChat holder=null;
            int who=(Integer)chatData.get(position).get("person");
            boolean state = (boolean) chatData.get(position).get("state");
            if(who == ME){
                long chatId = (long) chatData.get(position).get("chatId");
                if(!sendChatIdList.containsKey(chatId)){
                    sendChatIdList.put(chatId,position);
                }
            }

            view= LayoutInflater.from(context).inflate(layout[0], null);
            holder=new GroupChat.chatItemAdapater.ViewHolderChat();
            if(who == ME){
                if(state == false){
                    holder.chatTemp = (AVLoadingIndicatorView) view.findViewById(R.id.chat_temp);
                    holder.chatTemp.setVisibility(View.VISIBLE);
                }else if(state == true){
                    holder.chatTemp = (AVLoadingIndicatorView) view.findViewById(R.id.chat_temp);
                    holder.chatTemp.setVisibility(View.GONE);
                }
            }
            holder.userHead=(ImageView)view.findViewById(to[who*2+1]);
            holder.userChat=(TextView)view.findViewById(to[who*2+0]);
            holder.userHead.setBackgroundResource((Integer)chatData.get(position).get("userHead"));
            holder.userChat.setText(chatData.get(position).get("text").toString());
            return view;
        }

        class ViewHolderChat {
            AVLoadingIndicatorView chatTemp;
            ImageView userHead;
            TextView userChat;
        }
    }
}
