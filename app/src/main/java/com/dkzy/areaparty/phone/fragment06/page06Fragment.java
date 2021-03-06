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
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import protocol.Data.FileData;
import protocol.Data.GroupData;
import protocol.Data.UserData;
import protocol.Msg.ChangeGroupMsg;
import protocol.Msg.DeleteFileMsg;
import protocol.Msg.GetGroupInfoMsg;
import protocol.Msg.GetPersonalInfoMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static com.dkzy.areaparty.phone.Login.mainMobile;

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
    private LinearLayout id_tab06_groupWrap = null;
    private LinearLayout id_tab06_netWrap = null;
    private LinearLayout id_tab06_shareWrap = null;
    private LinearLayout id_tab06_fileWrap = null;
    private TextView newFriend_num = null;
    private TextView transform_num = null;
    private CustomListView id_tab06_userFriend = null;
    private CustomListView id_tab06_userGroup = null;
    private CustomListView id_tab06_fileComputer = null;
    private CustomListView id_tab06_userNet = null;
    private CustomListView id_tab06_userShare = null;
    private ImageButton id_tab06_friend = null;
    private ImageButton id_tab06_group = null;
    private ImageButton id_tab06_addGroup = null;
    private ImageButton id_tab06_file = null;
    private ImageButton id_tab06_net = null;
    private ImageButton id_tab06_share = null;
    //private ImageButton groupChat_setting = null;
    private ImageView id_tab06_addFriend = null;
    private LinearLayout id_tab06_addFriendLL = null;
    private ImageView userHead = null;
    private static List<UserData.UserItem> userFirend_list = null;
    private static List<GroupData.GroupItem> userGroup_list = null;
    private static List<UserData.UserItem> userNet_list = null;
    private static List<UserData.UserItem> userShare_list = null;
    //private static List<FileData.FileItem> file_list = null;
    //private static List<SharedfileBean> sharedfileBeans = null;
    private MyFriendAdapater userFriendAdapter = null;
    private GroupAdapater userGroupAdapter = null;
    private NetUserAdapater userNetAdapter = null;
    private ShareUserAdapater userShareAdapter = null;
    private myFileAdapater userFileAdapter = null;
    private List<HashMap<String, Object>> userFriendData = null;
    private List<HashMap<String, Object>> userGroupData = null;
    //private HashMap<String,List<Object>> userGroupData1 = null;
    private List<HashMap<String, Object>> userNetData = null;
    private List<HashMap<String, Object>> userShareData = null;
    private List<HashMap<String, Object>> filedata = null;
    private int[] imgId = null;
    private String myUserId;
    private String myUserName;

    private int myUserHead;
    private String showUserId;
    private String showUserName;
    private String showGroupId;
    private String showGroupName;
    private int showUserHead;
    private boolean outline;
    private userObj userFriendMsg;
    private userObj userGroupMsg;
    private userObj userNetMsg;
    private userObj userShareMsg;
    private SharedPreferences sp=null;
    private int friend_num = 0;
    private int transformNum = 0;
    public static myFileList showFriendFilesList;
    public static myFileList showGroupFilesList;
    private long getFriendFilesTimer = 0;
    private long getGroupFilesTimer = 0;
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
        if(userFileAdapter!=null) userFileAdapter.notifyDataSetChanged();
    }

    public void getData() {
        initData();
        System.out.println("getData");
        userFirend_list = Login.userFriend;
        userNet_list = Login.userNet;
        userShare_list = Login.userShare;
        mainMobile = mainMobile;
        userGroup_list = Login.userGroups;
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
            item.put("userOnline", user.getIsOnline());
            item.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
            userNetData.add(item);
        }
        for(UserData.UserItem user : userShare_list){
            HashMap<String, Object> item = new HashMap<>();
            item.put("userId", user.getUserId());
            item.put("userName", user.getUserName());
            item.put("fileNum", user.getFileNum());
            item.put("userOnline", user.getIsOnline());
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
        for(GroupData.GroupItem group : userGroup_list){
            HashMap<String, Object> item = new HashMap<>();
            item.put("createrId",group.getCreaterUserId());
            item.put("groupName",group.getGroupName());
            item.put("groupId",group.getGroupId());
            item.put("groupHead", headIndexToImgId.toImgId(1));
            userGroupData.add(item);
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
        //添加分组点击事件
        id_tab06_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainMobile) {
                    if (Login.userFriend.size() > 0) {
                        Intent intentGroup = new Intent();
                        intentGroup.setClass(getActivity(), AddGroup.class);
                        intentGroup.putExtra("isAdd", true);
                        startActivity(intentGroup);
                    } else {
                        Toast.makeText(getContext(), "请先添加好友", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "当前设备不是主设备,无法使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        id_tab06_userGroup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String groupId = ((HashMap<String, Object>)(userGroupAdapter.getItem(position))).get("groupId").toString();
                if("0".equals(groupId)){
                    return true;
                }
                if(mainMobile) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("删除：");
                    builder.setMessage("请问确定要删除该群组?");
                    //点击对话框以外的区域是否让对话框消失
                    builder.setCancelable(true);
                    //设置正面按钮
                    builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "你点击了是的"+((HashMap<String, Object>)(userGroupAdapter.getItem(position))).get("groupId").toString(), Toast.LENGTH_SHORT).show();
                            try {
                                ChangeGroupMsg.ChangeGroupReq.Builder builder = ChangeGroupMsg.ChangeGroupReq.newBuilder();
                                builder.setChangeType(ChangeGroupMsg.ChangeGroupReq.ChangeType.DELETE);
                                builder.setGroupId(groupId);
                                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.CHANGE_GROUP_REQ.getNumber(), builder.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream, byteArray);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });
                    //设置反面按钮
                    builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "你点击了不是", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    //对话框显示的监听事件
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {

                        }
                    });
                    //对话框消失的监听事件
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                        }
                    });
                    //显示对话框
                    dialog.show();
                }else{
                    Toast.makeText(getContext(), "当前设备不是主设备,无法使用此功能", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        //点击群进入聊天界面
        id_tab06_userGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Map<String, String> map = (Map<String, String>) userGroupAdapter.getItem(position);
                //Toast.makeText(getActivity(), "xxx",Toast.LENGTH_SHORT).show();
//                Intent intentGroupChat = new Intent();
//                intentGroupChat.setClass(getActivity(), GroupChat.class);
//                startActivity(intentGroupChat);
                if(mainMobile) {
                    Map<String, String> map = (Map<String, String>) userGroupAdapter.getItem(position);
                    showGroupId = map.get("groupId");
                    showGroupName = map.get("groupName");
                    long nowTime = new Date().getTime();
                    if (nowTime - getGroupFilesTimer > 2000) {
                        getGroupFilesTimer = nowTime;
                        new Thread(getGroupFile).start();
                    }
                }else{
                    Toast.makeText(MyApplication.getContext(), "当前设备不是主设备,无法使用此功能", Toast.LENGTH_LONG).show();
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
                if(mainMobile) {
                    showFileInfo(filedata.get(position));
                }else{
                    Toast.makeText(MyApplication.getContext(), "当前设备不是主设备,无法使用此功能", Toast.LENGTH_LONG).show();
                }
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

        id_tab06_groupWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id_tab06_userGroup.getVisibility() == view.VISIBLE){
                    id_tab06_userGroup.setVisibility(view.GONE);
                    id_tab06_group.setBackgroundResource(R.drawable.tab06_item_merge);
                    id_tab06_addGroup.setVisibility(view.GONE);
                }
                else{
                    id_tab06_userGroup.setVisibility(view.VISIBLE);
                    id_tab06_group.setBackgroundResource(R.drawable.tab06_item_open);
                    id_tab06_addGroup.setVisibility(view.VISIBLE);
                }
            }
        });

        id_tab06_shareWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "功能开发中", Toast.LENGTH_SHORT).show();
                if(id_tab06_userShare.getVisibility() == view.VISIBLE){
                    id_tab06_userShare.setVisibility(view.GONE);
                    id_tab06_share.setBackgroundResource(R.drawable.tab06_item_merge);
                }
                else{
                    id_tab06_userShare.setVisibility(view.VISIBLE);
                    id_tab06_share.setBackgroundResource(R.drawable.tab06_item_open);
                }
            }
        });

        id_tab06_netWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "功能开发中", Toast.LENGTH_SHORT).show();
                if(id_tab06_userNet.getVisibility() == view.VISIBLE){
                    id_tab06_userNet.setVisibility(view.GONE);
                    id_tab06_net.setBackgroundResource(R.drawable.tab06_item_merge);
                }
                else{
                    id_tab06_userNet.setVisibility(view.VISIBLE);
                    id_tab06_net.setBackgroundResource(R.drawable.tab06_item_open);
                }
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
                    Toast.makeText(getActivity(), "当前设备不是主设备,无法使用此功能",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "当前设备不是主设备,无法使用此功能",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "当前设备不是主设备,无法使用此功能",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "当前设备不是主设备,无法使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //下载管理界面 点击进入可查看下载状态
        download_wrap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mainMobile) {
                    if (MyApplication.isSelectedPCOnline()) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), downloadManager.class);
                        startActivity(intent);
                    } else {
                        Toasty.warning(getContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "当前设备不是主设备,无法使用此功能", Toast.LENGTH_SHORT).show();
                }
            }
        });

        helpInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showHelpInfoDialog(R.layout.dialog_page06);
            }
        });

//        groupChat_setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), downloadManager.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initViews() {
        id_tab06_friendWrap = (LinearLayout) getActivity().findViewById(R.id.id_tab06_friendWrap);
        id_tab06_groupWrap = (LinearLayout) getActivity().findViewById(R.id.id_tab06_groupWrap);
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
        id_tab06_userGroup = (CustomListView) getActivity().findViewById(R.id.id_tab06_userGroup);
        id_tab06_fileComputer = (CustomListView) getActivity().findViewById(R.id.id_tab06_fileComputer);
        id_tab06_userNet = (CustomListView) getActivity().findViewById(R.id.id_tab06_userNet);
        id_tab06_userShare = (CustomListView) getActivity().findViewById(R.id.id_tab06_userShare);
//        showFiles = (ImageView) getActivity().findViewById(R.id.showFiles);
        id_tab06_addGroup = (ImageButton) getActivity().findViewById(R.id.id_tab06_addGroupButton);
        id_tab06_friend = (ImageButton) getActivity().findViewById(R.id.id_tab06_friendButton);
        id_tab06_group = (ImageButton) getActivity().findViewById(R.id.id_tab06_groupButton);
        id_tab06_file = (ImageButton) getActivity().findViewById(R.id.id_tab06_fileButton);
        id_tab06_net = (ImageButton) getActivity().findViewById(R.id.id_tab06_netButton);
        id_tab06_share = (ImageButton) getActivity().findViewById(R.id.id_tab06_shareButton);
        id_tab06_addFriend = (ImageView) getActivity().findViewById(R.id.id_tab06_addFriend);
        id_tab06_addFriendLL = (LinearLayout) getActivity().findViewById(R.id.id_tab06_addFriendLL);
        //groupChat_setting = (ImageButton)getActivity().findViewById(R.id.groupChat_setting);
        userHead = (ImageView) getActivity().findViewById(R.id.userHead);
        helpInfo = (TextView)  rootView.findViewById(R.id.helpInfo);
        //userFriendAdapter = new SimpleAdapter(getActivity(), userFriendData, R.layout.tab06_useritem, new String[]{"userId", "userName", "userHead"}, new int[]{R.id.userId, R.id.userName, R.id.userHead});
        userFriendAdapter = new MyFriendAdapater(getActivity());
        userGroupAdapter = new GroupAdapater(getActivity());
        userNetAdapter = new NetUserAdapater(getActivity());
        userShareAdapter = new ShareUserAdapater(getActivity());
        //fileadapter = new SimpleAdapter(getActivity(), filedata, R.layout.tab06_fileitem, new String[]{"fileImg", "fileName"}, new int[]{R.id.fileImg, R.id.fileName});
        userFileAdapter = new myFileAdapater(getActivity(),filedata, false);
        id_tab06_fileComputer.setAdapter(userFileAdapter);
        id_tab06_userGroup.setAdapter(userGroupAdapter);
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
        userGroupData = (userGroupData==null)?new ArrayList<HashMap<String, Object>>():userGroupData;
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
//        initData();
//        userNetMsg = (userObj) msg.obj;
//        HashMap<String, Object> userNetItem = new HashMap<>();
//        userNetItem.put("userId", userNetMsg.getUserId());
//        userNetItem.put("userName", userNetMsg.getUserName());
//        userNetItem.put("fileNum", userNetMsg.getFileNum());
//        userNetItem.put("userHead", headIndexToImgId.toImgId(userNetMsg.getHeadIndex()));
//        userNetData.add(userNetItem);
//        if(id_tab06_userNet!=null) userNetAdapter.notifyDataSetChanged();
    }

    public void shareUserLogIn(Message msg){
        initData();
        if(userShareData.size()!=0){
            userShareMsg = (userObj) msg.obj;
            HashMap<String, Object> userShareItem = new HashMap<>();
            for(int i = 0; i < userShareData.size(); i++){
                if(((String)userShareData.get(i).get("userId")).equals(userShareMsg.getUserId())){
                    userShareItem = userShareData.get(i);
                    userShareItem.put("userOnline",true);
                    userShareData.remove(i);
                    userShareData.add(0,userShareItem);
                }
            }
            if(userShareAdapter!=null) userShareAdapter.notifyDataSetChanged();
        }else{
            userShareMsg = (userObj) msg.obj;
            HashMap<String, Object> userShareItem = new HashMap<>();
            userShareItem.put("userId", userShareMsg.getUserId());
            userShareItem.put("userName", userShareMsg.getUserName());
            userShareItem.put("fileNum", userShareMsg.getFileNum());
            userShareItem.put("userHead", headIndexToImgId.toImgId(userShareMsg.getHeadIndex()));
            userShareData.add(userShareItem);
            if(userShareAdapter!=null) userShareAdapter.notifyDataSetChanged();
        }
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
        for(int i = 0; i < userNetData.size(); i++){
            if(((String)userNetData.get(i).get("userId")).equals(userFriendMsg.getUserId())){
                userFriendItem = userNetData.get(i);
                userFriendItem.put("userOnline",true);
                userNetData.remove(i);
                userNetData.add(0,userFriendItem);
                break;
            }
        }
        if(userShareData.size()!=0){
            userShareMsg = (userObj) msg.obj;
            HashMap<String, Object> userShareItem = new HashMap<>();
            for(int i = 0; i < userShareData.size(); i++){
                if(((String)userShareData.get(i).get("userId")).equals(userShareMsg.getUserId())){
                    userShareItem = userShareData.get(i);
                    userShareItem.put("userOnline",true);
                    userShareData.remove(i);
                    userShareData.add(0,userShareItem);
                }
            }
            if(userShareAdapter!=null) userShareAdapter.notifyDataSetChanged();
        }
        if(userFriendAdapter!=null) userFriendAdapter.notifyDataSetChanged();
        if(userNetAdapter!=null) userNetAdapter.notifyDataSetChanged();
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
//        for(int i = 0; i < userGroup_list.size(); i++){
//            GroupData.GroupItem gg = userGroup_list.get(i);
//            if("0".equals(gg.getGroupId())){
//                GroupData.GroupItem.Builder ggb = gg.toBuilder();
//                ggb.addMemberUserId(user.getUserId());
//
//            }
//        }
        if(userFriendAdapter!=null)
            userFriendAdapter.notifyDataSetChanged();
        if(user.getFileNum() > Base.FILENUM){
            Iterator<HashMap<String,Object>> it = userShareData.iterator();
            while(it.hasNext()){
                HashMap<String,Object> hm = it.next();
                if(hm.get("userId").equals(user.getUserId())){
                    it.remove();
                    if(userShareAdapter!=null)
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
                    if(userNetAdapter!=null)
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
                    if(userFriendAdapter!=null) userFriendAdapter.notifyDataSetChanged();
                    break;
                }
            }
            while(netIt.hasNext()){
                HashMap<String,Object> hm = netIt.next();
                if(hm.get("userId").equals(logOutId)){
                    HashMap<String, Object> userNetItem = hm;
                    userNetItem.put("userOnline",false);

                    //有点问题 netIt.remove();
                    if(userNetAdapter!=null) userNetAdapter.notifyDataSetChanged();
                    break;
                }
            }
            while(shareIt.hasNext()){
                HashMap<String,Object> hm = shareIt.next();
                if(hm.get("userId").equals(logOutId)){
                    HashMap<String, Object> userShareItem = hm;
                    userShareItem.put("userOnline",false);
                    if(userShareAdapter!=null) userShareAdapter.notifyDataSetChanged();
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
    //不是好友只显示一部分信息
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

                new Thread() {
                    @Override
                    public void run() {

                        DeleteFileMsg.DeleteFileReq.Builder builder = DeleteFileMsg.DeleteFileReq.newBuilder();
                        builder.setFileId((int) h.get("id"));
                        builder.setFileName((String) h.get("fileName"));
                        builder.setUserId(Login.userId);
                        builder.setFileInfo((String) h.get("fileInfo"));
                        builder.setFileSize((int) h.get("fileSize"));
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
    public void showGroupFiles(Message msg) {
        List<String> showMembersList = ((GetGroupInfoMsg.GetGroupInfoRsp)(msg.obj)).getGroupItem().getMemberUserIdList();
        List<FileData.FileItem> showFilesList = ((GetGroupInfoMsg.GetGroupInfoRsp)(msg.obj)).getFilesList();

        showGroupFilesList = new myFileList();
        showGroupFilesList.setList(showFilesList);
        ArrayList<String> groupMems = new ArrayList<>();
        groupMems.addAll(showMembersList);
        Intent intent = new Intent();
        intent.setClass(getActivity(), GroupChat.class);
        Bundle bundle = new Bundle();
//        bundle.putString("GroupId", showGroupId);
//        bundle.putString("GroupName", showGroupName);
        //bundle.putInt("GroupHead", showGHead);
        //if(friendChatNum.containsKey(showUserId))
       // bundle.putInt("chatNum", friendChatNum.get(showUserId));
        bundle.putString("myUserId", myUserId);
        bundle.putString("myUserName", myUserName);
        bundle.putSerializable("GroupFile", showGroupFilesList);
//        bundle.putSerializable("GroupMembers",showMembersList);
//        bundle.putStringArrayList("GroupMems",groupMems);
        //bundle.putInt("myUserHead", myUserHead);
        GroupChat.group_id="";
        GroupChat.group_name="";
        GroupChat.groupMems.clear();

        GroupChat.group_id=showGroupId;
        GroupChat.group_name=showGroupName;
        GroupChat.groupMems=groupMems;
        intent.putExtras(bundle);
        startActivity(intent);
        //friendChatNum.put(showUserId,0);
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
    //getUnfriendFile getfriendFile有啥区别
    //getUnfriendFile最终只显示文件个数等
    //getfriendFile 显示完全文件
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

    Runnable getGroupFile = new Runnable() {
        @Override
        public void run() {
            try{
//                GetPersonalInfoMsg.GetPersonalInfoReq.Builder builder = GetPersonalInfoMsg.GetPersonalInfoReq.newBuilder();
//                builder.setWhere("page06FragmentGroup");
//                builder.setUserId(showGroupId);
//                builder.setFileInfo(true);
                GetGroupInfoMsg.GetGroupInfoReq.Builder builder = GetGroupInfoMsg.GetGroupInfoReq.newBuilder();
                builder.setGroupId(showGroupId);
                builder.setWhere("page06FragmentGroup");
                builder.setFileInfo(true);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_GROUP_INFO_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };
    public void addGroup(Message msg) {
        HashMap<String, Object> item = new HashMap<>();
        item.put("groupId", ((groupObj) msg.obj).groupId);
        item.put("groupName", ((groupObj) msg.obj).groupName);
        item.put("groupHead", headIndexToImgId.toImgId(1));
        userGroupData.add(item);
        if (id_tab06_userGroup != null)
            userGroupAdapter.notifyDataSetChanged();
    }

    public void updateGroup(Message msg) {
        for(int i = 0; i< userGroupData.size();i++){
            HashMap<String, Object> g = userGroupData.get(i);
            if(g.get("groupId").equals(((groupObj) msg.obj).groupId)){
                g.put("groupName",((groupObj) msg.obj).groupName);
                break;
            }
        }
        GroupChat.group_id="";
        GroupChat.group_name="";
        GroupChat.groupMems.clear();

        GroupChat.group_id=((groupObj) msg.obj).groupId;
        GroupChat.group_name=((groupObj) msg.obj).groupName;
        GroupChat.groupMems.addAll(((groupObj) msg.obj).getMemberUserId());
        if (id_tab06_userGroup != null)
            userGroupAdapter.notifyDataSetChanged();
    }

    public void deleteGroup(Message msg) {
        for(int i = 0; i< userGroupData.size();i++){
            HashMap<String, Object> g = userGroupData.get(i);
            if(g.get("groupId").equals(((groupObj) msg.obj).groupId)){
                userGroupData.remove(g);
                break;
            }
        }
        if (id_tab06_userGroup != null)
            userGroupAdapter.notifyDataSetChanged();
    }
    //分组
    private class GroupAdapater extends BaseAdapter {
        LayoutInflater mInflater;
        public GroupAdapater(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return userGroupData.size();
        }

        @Override
        public Object getItem(int i) {
            return userGroupData.get(i);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {

            ViewHolderGroup holder;
            if(view == null) {
                view = mInflater.inflate(R.layout.tab06_groupitem, null);
                holder = new ViewHolderGroup();
                holder.headIndex  = (ImageView) view.findViewById(R.id.groupHead);
                holder.groupName  = (TextView) view.findViewById(R.id.groupName);
                //holder.chatNum = (TextView) view.findViewById(R.id.chatNum);
                view.setTag(holder);
            } else {
                holder = (ViewHolderGroup) view.getTag();
            }

            HashMap<String, Object> group = userGroupData.get(i);
            int headIndex = (int) group.get("groupHead");
            String groupName = (String) group.get("groupName");
            holder.headIndex.setImageResource(headIndex);
            holder.groupName.setText(groupName);
            return view;
        }
    }
    class ViewHolderGroup {
        ImageView headIndex;
        TextView groupName;
    }
    //好友
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

    private class ShareUserAdapater extends BaseAdapter {
        LayoutInflater mInflater;
        public ShareUserAdapater(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return userShareData.size();
        }

        @Override
        public Object getItem(int i) {
            return userShareData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolderShareUser holder;
            if(view == null) {
                view = mInflater.inflate(R.layout.tab06_useritem, null);
                holder = new ViewHolderShareUser();
                holder.headIndex  = (ImageView) view.findViewById(R.id.userHead);
                holder.userId  = (TextView) view.findViewById(R.id.userId);
                holder.userName = (TextView) view.findViewById(R.id.userName);
                view.setTag(holder);
            } else {
                holder = (ViewHolderShareUser) view.getTag();
            }

            HashMap<String, Object> user = userShareData.get(i);
            int headIndex = (int) user.get("userHead");
            String userId =  (String) user.get("userId");
            String userName = (String) user.get("userName");
            holder.headIndex.setImageResource(headIndex);
            holder.userId.setText(userId);
            holder.userName.setText(userName);
            if((user.containsKey("userOnline"))&&(!(boolean)user.get("userOnline"))){
                holder.headIndex.setImageAlpha(80);
            }
            else{
                holder.headIndex.setImageAlpha(255);
            }
            return view;
        }
    }
    class ViewHolderShareUser {
        ImageView headIndex;
        TextView userId;
        TextView userName;
    }
    //速传用户
    private class NetUserAdapater extends BaseAdapter {
        LayoutInflater mInflater;
        public NetUserAdapater(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return userNetData.size();
        }

        @Override
        public Object getItem(int i) {
            return userNetData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolderNetUser holder;
            if(view == null) {
                view = mInflater.inflate(R.layout.tab06_useritem, null);
                holder = new ViewHolderNetUser();
                holder.headIndex  = (ImageView) view.findViewById(R.id.userHead);
                holder.userId  = (TextView) view.findViewById(R.id.userId);
                holder.userName = (TextView) view.findViewById(R.id.userName);
                view.setTag(holder);
            } else {
                holder = (ViewHolderNetUser) view.getTag();
            }

            HashMap<String, Object> user = userNetData.get(i);
            int headIndex = (int) user.get("userHead");
            String userId =  (String) user.get("userId");
            String userName = (String) user.get("userName");
            holder.headIndex.setImageResource(headIndex);
            holder.userId.setText(userId);
            holder.userName.setText(userName);
            if(((boolean)user.get("userOnline")) == false){
                holder.headIndex.setImageAlpha(80);
            }
            else{
                holder.headIndex.setImageAlpha(255);
            }
            return view;
        }
    }
    class ViewHolderNetUser {
        ImageView headIndex;
        TextView userId;
        TextView userName;
    }
}


