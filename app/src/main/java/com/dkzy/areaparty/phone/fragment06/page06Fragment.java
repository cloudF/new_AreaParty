package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Base;
import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import protocol.Data.FileData;
import protocol.Data.UserData;
import protocol.Msg.DeleteFileMsg;
import protocol.Msg.GetPersonalInfoMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by boris on 2016/11/29.
 * TAB06---分享的Fragment
 */

public class page06Fragment extends Fragment {
    View rootView;
    private LinearLayout newFriend_wrap = null;
    private LinearLayout transform_wrap = null;
    private LinearLayout download_wrap = null;
    private LinearLayout id_tab06_friendWrap = null;
    private LinearLayout id_tab06_netWrap = null;
    private LinearLayout id_tab06_shareWrap = null;
    private LinearLayout id_tab06_fileWrap = null;
    private TextView newFriend_num = null;
    private TextView transform_num = null;
    private CustomListView id_tab06_userFriend = null;
    private CustomListView id_tab06_fileComputer = null;
    private CustomListView id_tab06_userNet = null;
    private CustomListView id_tab06_userShare = null;
    private ImageButton id_tab06_friend = null;
    private ImageButton id_tab06_file = null;
    private ImageButton id_tab06_net = null;
    private ImageButton id_tab06_share = null;
    private ImageView id_tab06_addFriend = null;
    private LinearLayout id_tab06_addFriendLL = null;
    private ImageView userHead = null;
    private static List<UserData.UserItem> userFirend_list = null;
    private static List<UserData.UserItem> userNet_list = null;
    private static List<UserData.UserItem> userShare_list = null;
    //private static List<FileData.FileItem> file_list = null;
    //private static List<SharedfileBean> sharedfileBeans = null;
    private MyFriendAdapater userFriendAdapter = null;
    private SimpleAdapter userNetAdapter = null;
    private SimpleAdapter userShareAdapter = null;
    private myFileAdapater userFileAdapter = null;
    private List<HashMap<String, Object>> userFriendData = null;
    private List<HashMap<String, Object>> userNetData = null;
    private List<HashMap<String, Object>> userShareData = null;
    private List<HashMap<String, Object>> filedata = null;
    private int[] imgId = null;
    private String myUserId;
    private String myUserName;
    private boolean mainMobile;
    private int myUserHead;
    private String showUserId;
    private String showUserName;
    private int showUserHead;
    private boolean outline;
    private userObj userFriendMsg;
    private userObj userNetMsg;
    private userObj userShareMsg;
    private SharedPreferences sp=null;
    private int friend_num = 0;
    private int transformNum = 0;
    public static myFileList showFriendFilesList;
    private long getFriendFilesTimer = 0;
    public static HashMap<String,Integer> friendChatNum = new HashMap<>();
    private FriendRequestDBManager friendRequestDB = MainActivity.getFriendRequestDBManager();

    private TextView helpInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        outline = bundle.getBoolean("outline");
        myUserId = intent.getExtras().getString("userId");
        myUserHead = intent.getExtras().getInt("userHeadIndex");
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getPcAreaPartyPath();
    }

    public void refreshFileData(){
        //file_list.clear();
        filedata.clear();
        //file_list.addAll(Login.files);
        /*for(FileData.FileItem file : file_list){
            HashMap<String, Object> item = new HashMap<>();
            int style = FileTypeConst.determineFileType(file.getFileName());//fileStyle.getFileStyle(file);
            item.put("fileName", file.getFileName());
            item.put("fileInfo", file.getFileInfo());
            item.put("fileSize", file.getFileSize());
            item.put("fileImg", fileIndexToImgId.toImgId(style));
            item.put("fileDate", file.getFileDate());
            filedata.add(item);
        }*/
        for (SharedfileBean file : MyApplication.getMySharedFiles()){
            HashMap<String, Object> item = new HashMap<>();
            int style = FileTypeConst.determineFileType(file.name);//fileStyle.getFileStyle(file);
            item.put("fileName", file.name);
            item.put("fileInfo", file.des);
            item.put("fileSize", file.size);
            item.put("fileImg", fileIndexToImgId.toImgId(style));
            item.put("fileDate", file.timeLong);
            item.put("id",file.id);
            filedata.add(item);
        }
        if(id_tab06_fileComputer!=null) userFileAdapter.notifyDataSetChanged();
    }

    public void getData() {
        initData();
        System.out.println("getData");
        userFirend_list = Login.userFriend;
        userNet_list = Login.userNet;
        userShare_list = Login.userShare;
        mainMobile = Login.mainMobile;
        //file_list = Login.files;
        // = MyApplication.getMySharedFiles();
        if(userFriendData.size() == 0){
            for(UserData.UserItem user : userFirend_list){
                HashMap<String, Object> item = new HashMap<>();
                item.put("userId", user.getUserId());
                item.put("userName", user.getUserName());
                item.put("fileNum", user.getFileNum());
                item.put("userOnline", user.getIsOnline());
                item.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
                item.put("chatNum", friendChatNum.containsKey(user.getUserId())?friendChatNum.get(user.getUserId()):0);
                if(user.getIsOnline())
                    userFriendData.add(0,item);
                else
                    userFriendData.add(item);
            }
        }
        for(UserData.UserItem user : userNet_list){
            HashMap<String, Object> item = new HashMap<>();
            item.put("userId", user.getUserId());
            item.put("userName", user.getUserName());
            item.put("fileNum", user.getFileNum());
            item.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
            userNetData.add(item);
        }
        for(UserData.UserItem user : userShare_list){
            HashMap<String, Object> item = new HashMap<>();
            item.put("userId", user.getUserId());
            item.put("userName", user.getUserName());
            item.put("fileNum", user.getFileNum());
            item.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
            userShareData.add(item);
        }
        for (SharedfileBean file : MyApplication.getMySharedFiles()){
            HashMap<String, Object> item = new HashMap<>();
            int style = FileTypeConst.determineFileType(file.name);//fileStyle.getFileStyle(file);
            item.put("fileName", file.name);
            item.put("fileInfo", file.des);
            item.put("fileSize", file.size);
            item.put("fileImg", fileIndexToImgId.toImgId(style));
            item.put("fileDate", file.timeLong);
            item.put("id",file.id);
            filedata.add(item);
        }
        /*for(FileData.FileItem file : file_list){
            HashMap<String, Object> item = new HashMap<>();
            int style = FileTypeConst.determineFileType(file.getFileName());//fileStyle.getFileStyle(file);
            item.put("fileName", file.getFileName());
            item.put("fileInfo", file.getFileInfo());
            item.put("fileSize", file.getFileSize());
            item.put("fileImg", fileIndexToImgId.toImgId(style));
            item.put("fileDate", file.getFileDate());
            filedata.add(item);
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(outline)
            return inflater.inflate(R.layout.tab06_outline, container, false);
        else{
            View view = inflater.inflate(R.layout.tab06, container, false);
            rootView = view;
            return view;
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!outline){
            try {
                getData();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(!outline){
            Log.i("addfriend","activityCreated");
            try {
                initViews();
                initEvents();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initEvents() {
        //好友列表的item点击后进入聊天界面
        id_tab06_userFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Map<String, String> map = (Map<String, String>) userFriendAdapter.getItem(position);
                showUserId = map.get("userId");
                showUserName = map.get("userName");
                long nowTime = new Date().getTime();
                if(nowTime - getFriendFilesTimer > 2000){
                    getFriendFilesTimer = nowTime;
                    new Thread(getFriendFile).start();
                }
            }
        });
        //其他用户列表的item点击后显示用户信息
        id_tab06_userNet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showListDialog(userNetData.get(position));
            }
        });
        id_tab06_userShare.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showListDialog(userShareData.get(position));
            }
        });

        id_tab06_fileComputer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showFileInfo(filedata.get(position));
            }
        });

        //ImageView点击显示列表
        id_tab06_friendWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_tab06_userFriend.getVisibility() == view.VISIBLE){
                    id_tab06_userFriend.setVisibility(view.GONE);
                    id_tab06_friend.setBackgroundResource(R.drawable.tab06_item_merge);
                }
                else{
                    id_tab06_userFriend.setVisibility(view.VISIBLE);
                    id_tab06_friend.setBackgroundResource(R.drawable.tab06_item_open);
                }
            }
        });

        id_tab06_shareWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "功能开发中", Toast.LENGTH_SHORT).show();
                /*if(id_tab06_userShare.getVisibility() == view.VISIBLE){
                    id_tab06_userShare.setVisibility(view.GONE);
                    id_tab06_share.setBackgroundResource(R.drawable.tab06_item_merge);
                }
                else{
                    id_tab06_userShare.setVisibility(view.VISIBLE);
                    id_tab06_share.setBackgroundResource(R.drawable.tab06_item_open);
                }*/
            }
        });

        id_tab06_netWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "功能开发中", Toast.LENGTH_SHORT).show();
                /*if(id_tab06_userNet.getVisibility() == view.VISIBLE){
                    id_tab06_userNet.setVisibility(view.GONE);
                    id_tab06_net.setBackgroundResource(R.drawable.tab06_item_merge);
                }
                else{
                    id_tab06_userNet.setVisibility(view.VISIBLE);
                    id_tab06_net.setBackgroundResource(R.drawable.tab06_item_open);
                }*/
            }
        });

        id_tab06_fileWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_tab06_fileComputer.getVisibility() == view.VISIBLE){
                    id_tab06_fileComputer.setVisibility(view.GONE);
                    id_tab06_file.setBackgroundResource(R.drawable.tab06_item_merge);
                }
                else{
                    //refreshFileData();
                    id_tab06_fileComputer.setVisibility(view.VISIBLE);
                    id_tab06_file.setBackgroundResource(R.drawable.tab06_item_open);
                }
            }
        });

        //搜索用户按钮（右上角的加号）点击后进入搜索用户界面
        id_tab06_addFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                if(mainMobile) {
                    Intent intentSearch = new Intent();
                    intentSearch.setClass(getActivity(), searchFriend.class);
                    intentSearch.putExtra("userId", myUserId);
                    intentSearch.putExtra("userName", myUserName);
                    startActivity(intentSearch);
                }else{
                    Toast.makeText(getActivity(), "当前设备不是主设备，无法使用此功能",Toast.LENGTH_SHORT).show();
                }
            }
        });
        id_tab06_addFriendLL.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                if(mainMobile) {
                    Intent intentSearch = new Intent();
                    intentSearch.setClass(getActivity(), searchFriend.class);
                    intentSearch.putExtra("userId", myUserId);
                    intentSearch.putExtra("userName", myUserName);
                    startActivity(intentSearch);
                }else{
                    Toast.makeText(getActivity(), "当前设备不是主设备，无法使用此功能",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //好友请求 点击后进入处理界面
        newFriend_wrap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mainMobile) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), dealFriendRequest.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", myUserId);
                    bundle.putString("userName", myUserName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    newFriend_num.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getActivity(), "当前设备不是主设备，无法使用此功能",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //文件下载请求 点击进入处理界面
        transform_wrap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mainMobile) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), dealFileRequest.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", myUserId);
                    bundle.putString("userName", myUserName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    transform_num.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getActivity(), "当前设备不是主设备，无法使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //下载管理界面 点击进入可查看下载状态
        download_wrap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (MyApplication.isSelectedPCOnline()){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), downloadManager.class);
                    startActivity(intent);
                }else {
                    Toasty.warning(getContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }

            }
        });

        helpInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showHelpInfoDialog(R.layout.dialog_page06);
            }
        });
    }

    private void initViews() {
        id_tab06_friendWrap = (LinearLayout) getActivity().findViewById(R.id.id_tab06_friendWrap);
        id_tab06_netWrap = (LinearLayout) getActivity().findViewById(R.id.id_tab06_netWrap);
        id_tab06_shareWrap = (LinearLayout) getActivity().findViewById(R.id.id_tab06_shareWrap);
        id_tab06_fileWrap = (LinearLayout) getActivity().findViewById(R.id.id_tab06_fileWrap);
        newFriend_wrap = (LinearLayout) getActivity().findViewById(R.id.newFriend_wrap);
        transform_wrap = (LinearLayout) getActivity().findViewById(R.id.transform_wrap);
        download_wrap = (LinearLayout) getActivity().findViewById(R.id.download_wrap);
        newFriend_num = (TextView) getActivity().findViewById(R.id.newFriend_num);
        transform_num = (TextView) getActivity().findViewById(R.id.transform_num);
        //requestText = (TextView) getActivity().findViewById(R.id.requestText);
        id_tab06_userFriend = (CustomListView) getActivity().findViewById(R.id.id_tab06_userFriend);
        id_tab06_fileComputer = (CustomListView) getActivity().findViewById(R.id.id_tab06_fileComputer);
        id_tab06_userNet = (CustomListView) getActivity().findViewById(R.id.id_tab06_userNet);
        id_tab06_userShare = (CustomListView) getActivity().findViewById(R.id.id_tab06_userShare);
//        showFiles = (ImageView) getActivity().findViewById(R.id.showFiles);
        id_tab06_friend = (ImageButton) getActivity().findViewById(R.id.id_tab06_friendButton);
        id_tab06_file = (ImageButton) getActivity().findViewById(R.id.id_tab06_fileButton);
        id_tab06_net = (ImageButton) getActivity().findViewById(R.id.id_tab06_netButton);
        id_tab06_share = (ImageButton) getActivity().findViewById(R.id.id_tab06_shareButton);
        id_tab06_addFriend = (ImageView) getActivity().findViewById(R.id.id_tab06_addFriend);
        id_tab06_addFriendLL = (LinearLayout) getActivity().findViewById(R.id.id_tab06_addFriendLL);
        userHead = (ImageView) getActivity().findViewById(R.id.userHead);
        helpInfo = (TextView)  rootView.findViewById(R.id.helpInfo);
        //userFriendAdapter = new SimpleAdapter(getActivity(), userFriendData, R.layout.tab06_useritem, new String[]{"userId", "userName", "userHead"}, new int[]{R.id.userId, R.id.userName, R.id.userHead});
        userFriendAdapter = new MyFriendAdapater(getActivity());
        userNetAdapter = new SimpleAdapter(getActivity(), userNetData, R.layout.tab06_useritem, new String[]{"userId", "userName", "userHead"}, new int[]{R.id.userId, R.id.userName, R.id.userHead});
        userShareAdapter = new SimpleAdapter(getActivity(), userShareData, R.layout.tab06_useritem, new String[]{"userId", "userName", "userHead"}, new int[]{R.id.userId, R.id.userName, R.id.userHead});
        //fileadapter = new SimpleAdapter(getActivity(), filedata, R.layout.tab06_fileitem, new String[]{"fileImg", "fileName"}, new int[]{R.id.fileImg, R.id.fileName});
        userFileAdapter = new myFileAdapater(getActivity(),filedata, false);
        id_tab06_fileComputer.setAdapter(userFileAdapter);
        id_tab06_userFriend.setAdapter(userFriendAdapter);
        id_tab06_userNet.setAdapter(userNetAdapter);
        id_tab06_userShare.setAdapter(userShareAdapter);

        ArrayList<RequestFriendObj> requestFriendList = friendRequestDB.selectRequestFriendSQL(Login.userId + "friend");
        friend_num = requestFriendList.size();
        if(friend_num > 0){
            newFriend_num.setVisibility(View.VISIBLE);
        }
        if(transformNum > 0){
            transform_num.setVisibility(View.VISIBLE);
        }
    }
    private  void initData(){
        //mContext = (mContext==null)?getActivity().getApplicationContext():mContext;
        //listDataSave = (listDataSave==null)?new listDataSave(mContext, "requestList"):listDataSave;
        //list = listDataSave.getDataList("userItem");
        //friend_num = list.size();
        //friendRequest.setList(list);
        //System.out.println(friendRequest.getList());
        if(sp==null){
            sp = MainActivity.getSp();
        }
        filedata = (filedata==null)?new ArrayList<HashMap<String, Object>>():filedata;
        userFriendData = (userFriendData==null)?new ArrayList<HashMap<String, Object>>():userFriendData;
        userNetData = (userNetData==null)?new ArrayList<HashMap<String, Object>>():userNetData;
        userShareData = (userShareData==null)?new ArrayList<HashMap<String, Object>>():userShareData;
        imgId = (imgId==null)?new int[]{R.drawable.tx1, R.drawable.tx2, R.drawable.tx3, R.drawable.tx4, R.drawable.tx5}:imgId;
//        list = (list==null)?new ArrayList<HashMap<String, Object>>():list;
    }
    public void addFriend(Message msg){
        initData();
        friend_num ++;
        if(newFriend_num!=null)
            newFriend_num.setVisibility(View.VISIBLE);
    }
    public void netUserLogIn(Message msg){
        initData();
        userNetMsg = (userObj) msg.obj;
        HashMap<String, Object> userNetItem = new HashMap<>();
        userNetItem.put("userId", userNetMsg.getUserId());
        userNetItem.put("userName", userNetMsg.getUserName());
        userNetItem.put("fileNum", userNetMsg.getFileNum());
        userNetItem.put("userHead", headIndexToImgId.toImgId(userNetMsg.getHeadIndex()));
        userNetData.add(userNetItem);
        if(id_tab06_userNet!=null) userNetAdapter.notifyDataSetChanged();
    }

    public void shareUserLogIn(Message msg){
        initData();
        userShareMsg = (userObj) msg.obj;
        HashMap<String, Object> userShareItem = new HashMap<>();
        userShareItem.put("userId", userShareMsg.getUserId());
        userShareItem.put("userName", userShareMsg.getUserName());
        userShareItem.put("fileNum", userShareMsg.getFileNum());
        userShareItem.put("userHead", headIndexToImgId.toImgId(userShareMsg.getHeadIndex()));
        userShareData.add(userShareItem);
        if(id_tab06_userShare!=null) userShareAdapter.notifyDataSetChanged();
    }

    public void friendUserLogIn(Message msg){
        initData();
        if(userFriendData.size() == 0){
            for(UserData.UserItem user : Login.userFriend){
                HashMap<String, Object> item = new HashMap<>();
                item.put("userId", user.getUserId());
                item.put("userName", user.getUserName());
                item.put("fileNum", user.getFileNum());
                item.put("userOnline", user.getIsOnline());
                item.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
                item.put("chatNum", friendChatNum.containsKey(user.getUserId())?friendChatNum.get(user.getUserId()):0);
                if(user.getIsOnline())
                    userFriendData.add(0,item);
                else
                    userFriendData.add(item);
            }
        }
        userFriendMsg = (userObj) msg.obj;
        HashMap<String, Object> userFriendItem = new HashMap<>();
        for(int i = 0; i < userFriendData.size(); i++){
            if(((String)userFriendData.get(i).get("userId")).equals(userFriendMsg.getUserId())){
                userFriendItem = userFriendData.get(i);
                userFriendItem.put("userOnline",true);
                userFriendData.remove(i);
                userFriendData.add(0,userFriendItem);
                break;
            }
        }
        if(id_tab06_userFriend!=null) userFriendAdapter.notifyDataSetChanged();
    }

    public void getUserMsgFail(){
        Toast.makeText(getActivity(), "获取用户信息失败",Toast.LENGTH_SHORT).show();
    }

    public void delFriend(Message msg){
        friend_num--;
    }

    public void friendUserAdd(Message msg){
        userObj user = (userObj) msg.obj;
        HashMap<String, Object> userFriendItem = new HashMap<>();
        userFriendItem.put("userId", user.getUserId());
        userFriendItem.put("userName", user.getUserName());
        userFriendItem.put("fileNum", user.getFileNum());
        userFriendItem.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
        userFriendItem.put("chatNum", friendChatNum.containsKey(user.getUserId())?friendChatNum.get(user.getUserId()):0);
        userFriendItem.put("userOnline", true);
        userFriendData.add(0,userFriendItem);
        if(id_tab06_userFriend!=null)
            userFriendAdapter.notifyDataSetChanged();
        if(user.getFileNum() > Base.FILENUM){
            Iterator<HashMap<String,Object>> it = userShareData.iterator();
            while(it.hasNext()){
                HashMap<String,Object> hm = it.next();
                if(hm.get("userId").equals(user.getUserId())){
                    it.remove();
                    if(id_tab06_userShare!=null)
                        userShareAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        else{
            Iterator<HashMap<String,Object>> it = userNetData.iterator();
            while(it.hasNext()){
                HashMap<String,Object> hm = it.next();
                if(hm.get("userId").equals(user.getUserId())){
                    it.remove();
                    if(id_tab06_userNet!=null)
                        userNetAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        friend_num--;
    }

    public void userLogOut(Message msg){
//        if(!outline && userFirend_list==null){
//            getData();
//        }else{
//            initData();
//        }
        String logOutId = ((userObj) msg.obj).getUserId();
        System.out.println(userNetData);
        if (userFriendData!=null && userNetData!=null && userShareData != null){
            Iterator<HashMap<String,Object>> friendIt = userFriendData.iterator();
            Iterator<HashMap<String,Object>> netIt = userNetData.iterator();
            Iterator<HashMap<String,Object>> shareIt = userShareData.iterator();
            while(friendIt.hasNext()){
                HashMap<String,Object> hm = friendIt.next();
                if(hm.get("userId").equals(logOutId)){
                    HashMap<String, Object> userFriendItem = hm;
                    userFriendItem.put("userOnline",false);
                    friendIt.remove();
                    userFriendData.add(userFriendItem);
                    if(id_tab06_userFriend!=null) userFriendAdapter.notifyDataSetChanged();
                    break;
                }
            }
            while(netIt.hasNext()){
                HashMap<String,Object> hm = netIt.next();
                if(hm.get("userId").equals(logOutId)){
                    netIt.remove();
                    if(id_tab06_userNet!=null) userNetAdapter.notifyDataSetChanged();
                    break;
                }
            }
            while(shareIt.hasNext()){
                HashMap<String,Object> hm = shareIt.next();
                if(hm.get("userId").equals(logOutId)){
                    shareIt.remove();
                    if(id_tab06_userShare!=null) userShareAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

    }

    private void showListDialog(HashMap<String, Object> h){
        String[] items = new String[3];
        showUserId = (String) h.get("userId");
        showUserName = (String) h.get("userName");
        showUserHead = (int) h.get("userHead");
        items[0] = "用户ID： " + showUserId;
        items[1] = "用户名： " + h.get("userName");
        items[2] = "该用户共享了" + h.get("fileNum") + "个文件";
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getActivity());
        listDialog.setTitle("个人信息");
        listDialog.setItems(items, null);
        listDialog.setPositiveButton("更多",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(getUnfriendFile).start();
            }
        });
        listDialog.show();
    }

    public void showFileList(Message msg){
        List<FileData.FileItem> l = (List<FileData.FileItem>) msg.obj;
        String[] items = new String[l.size()];
        for(int i = 0; i<l.size(); i++){
            items[i] = "文件" + (i+1) + "   " + l.get(i).getFileName();
        }
        AlertDialog.Builder listDialog = new AlertDialog.Builder(getActivity());
        listDialog.setTitle("文件信息");
        listDialog.setItems(items, null);
        listDialog.show();
    }

    public void showFileInfo(final HashMap<String, Object> h){
        String[] items = new String[3];
        items[0] = "文件名： " + h.get("fileName");
        items[1] = "文件大小： "+getSize((int)h.get("fileSize"));
        if(!h.get("fileInfo").equals(""))
            items[2] = "文件描述： " + h.get("fileInfo");
        else
            items[2] = "文件描述： 这家伙什么都没写";
        final AlertDialog.Builder listDialog = new AlertDialog.Builder(getActivity());
        listDialog.setTitle("文件信息");
        listDialog.setItems(items, null);
        listDialog.setPositiveButton("取消分享", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(){
                    @Override
                    public void run() {

                        DeleteFileMsg.DeleteFileReq.Builder builder = DeleteFileMsg.DeleteFileReq.newBuilder();
                        builder.setFileId((int)h.get("id"));
                        builder.setFileName((String) h.get("fileName"));
                        builder.setUserId(Login.userId);
                        builder.setFileInfo((String) h.get("fileInfo"));
                        builder.setFileSize((int)h.get("fileSize"));
                        //builder.setFileUrl(file.url);
                        //builder.setFilePwd(file.pwd);
                        try {
                            byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.DELETE_FILE_REQ.getNumber(),
                                    builder.build().toByteArray());
                            Login.base.writeToServer(Login.outputStream, byteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                listDialog.show().dismiss();
            }
        });
        listDialog.show();
    }

    public void showFriendFiles(Message msg){
        showFriendFilesList = new myFileList();
        showFriendFilesList.setList((List)msg.obj);
        Intent intent = new Intent();
        intent.setClass(getActivity(), fileList.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", showUserId);
        bundle.putString("userName", showUserName);
        bundle.putInt("userHead", showUserHead);
        if(friendChatNum.containsKey(showUserId))
            bundle.putInt("chatNum", friendChatNum.get(showUserId));
        bundle.putString("myUserId", myUserId);
        bundle.putString("myUserName", myUserName);
        bundle.putSerializable("friendFile", showFriendFilesList);
        bundle.putInt("myUserHead", myUserHead);
        intent.putExtras(bundle);
        startActivity(intent);
        friendChatNum.put(showUserId,0);
        delChatNum();
    }

    public void shareFileSuccess (Message msg){
        refreshFileData();
        /*initData();
        fileObj file = (fileObj) msg.obj;
        HashMap<String, Object> item = new HashMap<>();
        int style = FileTypeConst.determineFileType(file.getFileName());//fileStyle.getFileStyle(file);
        item.put("fileName", file.getFileName());
        item.put("fileInfo", file.getFileInfo());
        item.put("fileSize", file.getFileSize());
        item.put("fileImg", fileIndexToImgId.toImgId(style));
        item.put("fileDate", file.getFileDate());
        filedata.add(item);
        if(id_tab06_fileComputer!=null) userFileAdapter.notifyDataSetChanged();*/
    }
    public void deleteFileSuccess (Message msg){
        refreshFileData();
    }

    public void shareFileFail (){
        //boolean shareState = (boolean) msg.obj;
//        if(shareState == false){
//        }
        Toast.makeText(getActivity(), "好友已同意下载，请前往下载管理界面查看", Toast.LENGTH_SHORT).show();
    }

    public void addChatNum (Message msg){
        initData();
        Log.e("page06","1");
        String id = ((userObj) msg.obj).getUserId();
        Log.e("page06","1");
        for(int i = 0; i < userFriendData.size(); i++){
            if(id.equals(userFriendData.get(i).get("userId"))){
                Log.e("page06","start to add chatNum");
                userFriendData.get(i).put("chatNum", friendChatNum.get(id));
                System.out.println(1);
                SharedPreferences.Editor editor = sp.edit();
                System.out.println(1);
                editor.putInt(id, friendChatNum.get(id));
                Log.e("page06","start to add sp");
                editor.commit();
                break;
            }
        }
        if(userFriendAdapter!=null){
            userFriendAdapter.notifyDataSetChanged();
        }
    }

    public void delChatNum (){
        for(int i = 0; i < userFriendData.size(); i++){
            if(showUserId.equals(userFriendData.get(i).get("userId"))){
                userFriendData.get(i).put("chatNum", 0);
                break;
            }
        }
        if(userFriendAdapter!=null){
            userFriendAdapter.notifyDataSetChanged();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(showUserId, 0);
        editor.commit();
    }

    public void addFileRequest(Message msg){
        initData();
        transformNum++;
        if(transform_num != null)
            transform_num.setVisibility(View.VISIBLE);
    }
    public void delFileRequest(Message msg){
        transformNum--;
    }

    Runnable getUnfriendFile = new Runnable() {
        @Override
        public void run() {
            try{
                GetPersonalInfoMsg.GetPersonalInfoReq.Builder builder = GetPersonalInfoMsg.GetPersonalInfoReq.newBuilder();
                builder.setWhere("page06FragmentUnfriend");
                builder.setUserId(showUserId);
                builder.setFileInfo(true);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_PERSONALINFO_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    Runnable getFriendFile = new Runnable() {
        @Override
        public void run() {
            try{
                GetPersonalInfoMsg.GetPersonalInfoReq.Builder builder = GetPersonalInfoMsg.GetPersonalInfoReq.newBuilder();
                builder.setWhere("page06FragmentFriend");
                builder.setUserId(showUserId);
                builder.setFileInfo(true);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_PERSONALINFO_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    private class MyFriendAdapater extends BaseAdapter {
        LayoutInflater mInflater;
        public MyFriendAdapater(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return userFriendData.size();
        }

        @Override
        public Object getItem(int i) {
            return userFriendData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolderFriend holder;
            if(view == null) {
                view = mInflater.inflate(R.layout.tab06_useritem, null);
                holder = new ViewHolderFriend();
                holder.headIndex  = (ImageView) view.findViewById(R.id.userHead);
                holder.userId  = (TextView) view.findViewById(R.id.userId);
                holder.userName = (TextView) view.findViewById(R.id.userName);
                holder.chatNum = (TextView) view.findViewById(R.id.chatNum);
                view.setTag(holder);
            } else {
                holder = (ViewHolderFriend) view.getTag();
            }

            HashMap<String, Object> user = userFriendData.get(i);
            int headIndex = (int) user.get("userHead");
            String userId =  (String) user.get("userId");
            String userName = (String) user.get("userName");
            int chatNum = (int) user.get("chatNum");
            holder.headIndex.setImageResource(headIndex);
            holder.userId.setText(userId);
            holder.userName.setText(userName);
            if(chatNum > 0){
                if(chatNum > 99){
                    holder.chatNum.setText("99+");
                }else{
                    System.out.println(friendChatNum.get(userId));
                    holder.chatNum.setText(String.valueOf(friendChatNum.get(userId)));
                }
                holder.chatNum.setVisibility(View.VISIBLE);
            }else{
                holder.chatNum.setVisibility(View.GONE);
            }
            if(((boolean)user.get("userOnline")) == false){
                holder.headIndex.setImageAlpha(80);
            }
            else{
                holder.headIndex.setImageAlpha(255);
            }
            return view;
        }
    }
    class ViewHolderFriend {
        ImageView headIndex;
        TextView userId;
        TextView userName;
        TextView chatNum;
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


