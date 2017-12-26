package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import protocol.Data.ChatData;
import protocol.Data.FileData;
import protocol.Msg.SendChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/1/3.
 */

public class fileList extends AppCompatActivity {
    public static Handler mHandler = null;
    private LinearLayout musicFile_wrap = null;
    private LinearLayout compressFile_wrap = null;
    private LinearLayout filmFile_wrap = null;
    private LinearLayout imgFile_wrap = null;
    private LinearLayout documentFile_wrap = null;
    private LinearLayout otherFile_wrap = null;
    private ImageButton fileList_backBtn;
    private Button chat_btn_send = null;
    private TextView friendSharedPicNumTV;
    private TextView friendSharedMusicNumTV;
    private TextView friendSharedMovieNumTV;
    private TextView friendSharedDocumentNumTV;
    private TextView friendSharedRarNumTV;
    private TextView friendSharedOtherNumTV;
    private TextView friendSharedTitle;
    private TextView historyMsg;
    private EditText et_sendmessage;
    private TextView chat_meChat;
    private TextView chat_friendChat;
    private ImageView chat_meHead;
    private ImageView chat_friendHead;
    private ListView chatList = null;
    private myFileList fileItems;
    private List<HashMap<String, Object>> musicData;
    private List<HashMap<String, Object>> compressData;
    private List<HashMap<String, Object>> filmData;
    private List<HashMap<String, Object>> imgData;
    private List<HashMap<String, Object>> documentData;
    private List<HashMap<String, Object>> otherData;
    private List<HashMap<String, Object>> chatData;
    private AVLoadingIndicatorView chat_tamp;
    public static String user_id = "";
    private String user_name;
    private int user_head;
    private int myUserHead;
    private int chatNum;
    private long chatId = 1;
    private HashMap<Long, Integer> sendChatIdList = new HashMap<>();
    private chatItemAdapater chatAdapater;
    public final static int FRIEND=1;
    public final static int ME=0;
    private ChatDBManager chatDB = MainActivity.getChatDBManager();
    int[] layout={R.layout.chatmeitem, R.layout.chatfrienditem};
    int[] to = {R.id.chat_meChat, R.id.chat_meHead, R.id.chat_friendChat, R.id.chat_friendHead};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_filemain);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getData();
        initView();
        initEvent();
    }

    private void initView(){
        musicFile_wrap = (LinearLayout) this.findViewById(R.id.musicFile_wrap);
        filmFile_wrap = (LinearLayout) this.findViewById(R.id.filmFile_wrap);
        compressFile_wrap = (LinearLayout) this.findViewById(R.id.compressFile_wrap);
        imgFile_wrap = (LinearLayout) this.findViewById(R.id.imgFile_wrap);
        documentFile_wrap = (LinearLayout) this.findViewById(R.id.documentFile_wrap);
        otherFile_wrap = (LinearLayout) this.findViewById(R.id.otherFile_wrap);
        fileList_backBtn = (ImageButton) this.findViewById(R.id.fileList_backBtn);
        chat_btn_send = (Button) this.findViewById(R.id.chat_btn_send);
        friendSharedPicNumTV = (TextView) this.findViewById(R.id.friendSharedPicNumTV);
        friendSharedMusicNumTV = (TextView) this.findViewById(R.id.friendSharedMusicNumTV);
        friendSharedMovieNumTV = (TextView) this.findViewById(R.id.friendSharedMovieNumTV);
        friendSharedDocumentNumTV = (TextView) this.findViewById(R.id.friendSharedDocumentNumTV);
        friendSharedRarNumTV = (TextView) this.findViewById(R.id.friendSharedRarNumTV);
        friendSharedOtherNumTV = (TextView) this.findViewById(R.id.friendSharedOtherNumTV);
        friendSharedTitle = (TextView) this.findViewById(R.id.friendSharedTitle);
        historyMsg = (TextView) this.findViewById(R.id.historyMsg);
        et_sendmessage = (EditText) this.findViewById(R.id.et_sendmessage);
        chat_tamp = (AVLoadingIndicatorView) this.findViewById(R.id.chat_temp);
        chatList = (ListView) this.findViewById(R.id.chatList);
        chatAdapater = new chatItemAdapater(this, chatData, layout, to);
        chatList.setAdapter(chatAdapater);
        friendSharedPicNumTV.setText("("+imgData.size()+")");
        friendSharedMusicNumTV.setText("("+musicData.size()+")");
        friendSharedMovieNumTV.setText("("+filmData.size()+")");
        friendSharedDocumentNumTV.setText("("+documentData.size()+")");
        friendSharedRarNumTV.setText("("+compressData.size()+")");
        friendSharedOtherNumTV.setText("("+otherData.size()+")");
        friendSharedTitle.setText(user_name + "的分享");
    }
    private void getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_id = bundle.getString("userId");
        user_name = bundle.getString("userName");
        user_head = bundle.getInt("userHead");
        chatNum = bundle.getInt("chatNum");
        fileItems = (myFileList) bundle.getSerializable("friendFile");
        myUserHead = bundle.getInt("myUserHead");
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
                size = Math.max(chatNum, 5);
                ArrayList<ChatObj> chats = chatDB.selectMyChatSQL(Login.userId, Login.userId, user_id, size);
                if(chats.size() > 5) size = chats.size();
                else size = Math.min(chats.size(),5);
                for(int i = size-1; i >=0; i--){
                    ChatObj chat = chats.get(i);
                    if(chat.sender_id.equals(Login.userId) && chat.receiver_id.equals(user_id)){
                        addTextToList(chat.msg, ME, chatId, true);
                        chatId++;
                    }else {
                        addTextToList(chat.msg, FRIEND, chatId, true);
                        chatId++;
                    }
                }
            }
        }).start();

        for(FileData.FileItem file : fileItems.getList()){
            int style = FileTypeConst.determineFileType(file.getFileName());//fileStyle.getFileStyle(file);
            switch (style){
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
                case 2: case 4: case 5:case 7: case 9:
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

    private void initEvent(){
        fileList_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileList.this.finish();
            }
        });
        chat_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Login.mainMobile) {
                    if (et_sendmessage.getText().toString().equals("")) {
                        Toast.makeText(fileList.this, "请输入内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String text = et_sendmessage.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!Login.socket.isConnected()) {
                                    Toast.makeText(fileList.this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SendChatMsg.SendChatReq.Builder builder = SendChatMsg.SendChatReq.newBuilder();
                                ChatData.ChatItem.Builder chatItem = ChatData.ChatItem.newBuilder();
                                chatItem.setTargetType(ChatData.ChatItem.TargetType.INDIVIDUAL);
                                chatItem.setSendUserId(Login.userId);
                                chatItem.setReceiveUserId(user_id);
                                chatItem.setChatType(ChatData.ChatItem.ChatType.TEXT);
                                chatItem.setChatBody(text);
                                chatItem.setChatId(chatId);
                                builder.setChatData(chatItem);
                                builder.setWhere("chat");
                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.SEND_CHAT_REQ.getNumber(), builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, byteArray);
                                Message msg = mHandler.obtainMessage();
                                msg.obj = text;
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(fileList.this, "当前设备不是主设备，无法使用此功能",Toast.LENGTH_SHORT).show();
                }
            }
        });
        historyMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(Login.mainMobile) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", user_id);
                    bundle.putInt("userHead", user_head);
                    bundle.putString("userName", user_name);
                    bundle.putInt("myUserHead", myUserHead);
                    intent.putExtras(bundle);
                    intent.setClass(fileList.this, HistoryMsg.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(fileList.this, "当前设备不是主设备，无法使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        musicFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(musicData);
                Intent intent = new Intent();
                intent.setClass(fileList.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",user_id);
                bundle.putInt("fileStyle", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        compressFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(compressData);
                Intent intent = new Intent();
                intent.setClass(fileList.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",user_id);
                bundle.putInt("fileStyle", 2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        filmFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(filmData);
                Intent intent = new Intent();
                intent.setClass(fileList.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",user_id);
                bundle.putInt("fileStyle", 1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        imgFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(imgData);
                Intent intent = new Intent();
                intent.setClass(fileList.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",user_id);
                bundle.putInt("fileStyle", 3);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        documentFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(documentData);
                Intent intent = new Intent();
                intent.setClass(fileList.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",user_id);
                bundle.putInt("fileStyle", 4);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        otherFile_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList list = new myList();
                list.setList(otherData);
                Intent intent = new Intent();
                intent.setClass(fileList.this, sortFIleList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileData", list);
                bundle.putString("userId",user_id);
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
                        if(chatList!=null)
                            chatAdapater.notifyDataSetChanged();
                        ChatObj chat = new ChatObj();
                        chat.date = ((ArrayList<Long>) msg.obj).get(1);
                        chat.msg = (String) chatData.get(position).get("text");
                        chat.receiver_id = user_id;
                        chat.sender_id = Login.userId;
                        chatDB.addChatSQL(chat, Login.userId);
                        break;
                    case 1:
                        String text = (String) msg.obj;
                        addTextToList(text, ME, fileList.this.chatId, false);
                        et_sendmessage.setText("");
                        fileList.this.chatId++;
                        break;
                    case 2:
                        ChatObj chatMsg = (ChatObj) msg.obj;
                        addTextToList(chatMsg.msg, FRIEND, fileList.this.chatId, true);
                        fileList.this.chatId++;
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
        if(chatList!=null)
            chatAdapater.notifyDataSetChanged();
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
            ViewHolderChat holder=null;
            int who=(Integer)chatData.get(position).get("person");
            boolean state = (boolean) chatData.get(position).get("state");
            if(who == ME){
                long chatId = (long) chatData.get(position).get("chatId");
                if(!sendChatIdList.containsKey(chatId)){
                    sendChatIdList.put(chatId,position);
                }
            }

            view= LayoutInflater.from(context).inflate(layout[who==ME?0:1], null);
            holder=new ViewHolderChat();
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

