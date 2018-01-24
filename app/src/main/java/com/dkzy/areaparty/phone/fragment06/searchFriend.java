package com.dkzy.areaparty.phone.fragment06;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;

import java.io.IOException;

import protocol.Msg.AddFriendMsg;
import protocol.Msg.GetPersonalInfoMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/3/8.
 */

public class searchFriend extends AppCompatActivity {

    private LinearLayout searchFriendWrap;
    private EditText searchUserId;
    private ImageButton searchUserIdBtn;
    private TextView userSearchName;
    private TextView userSearchIsFriend;
    private TextView userSearchFileNum;
    private TextView myIdNum;
    private Button userSearchBtn;
    private ImageButton searchFriendBack_btn;
    private ImageView searchUserHead;
    private String userSearchId;
    private String myUserId;
    private String myUserName;
    private userObj userItem;
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfriend);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        myUserId = intent.getExtras().getString("userId");
        myUserName = intent.getExtras().getString("userName");
        initview();
        initEvent();
    }

    private void initview(){
        myIdNum = (TextView) findViewById(R.id.myIdNum);
        myIdNum.setText("我的Id号：" + myUserId);
        searchFriendWrap = (LinearLayout) findViewById(R.id.searchFriendWrap);
        searchUserId = (EditText) findViewById(R.id.searchUserId);
        searchUserIdBtn = (ImageButton) findViewById(R.id.searchUserIdBtn);
        searchFriendBack_btn = (ImageButton) findViewById(R.id.searchFriendBack_btn);
        searchUserHead = (ImageView) findViewById(R.id.searchUserHead);
        userSearchName = (TextView) findViewById(R.id.userSearchName);
        userSearchIsFriend = (TextView) findViewById(R.id.userSearchIsFriend);
        userSearchFileNum = (TextView) findViewById(R.id.userSearchFileNum);
        userSearchBtn = (Button) findViewById(R.id.userSearchBtn);
        Drawable searchImg = getResources().getDrawable(R.drawable.search,null);
        searchImg.setBounds(7,7,25,25);
        searchUserId.setCompoundDrawables(searchImg,null,null,null);
    }

    private void initEvent(){

        searchUserIdBtn.setOnClickListener(listener);
        searchFriendBack_btn.setOnClickListener(listener);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if(msg.obj != null){
                            userItem = (userObj) msg.obj;
                            searchFriendWrap.setBackgroundColor(Color.WHITE);
                            userSearchFileNum.setText("他共享了 " + String.valueOf(userItem.getFileNum()) + " 个文件");
                            searchUserHead.setImageResource(headIndexToImgId.toImgId(userItem.getHeadIndex()));
                            if(userItem.getIsFriend() || userItem.getUserId().equals(myUserId)){
                                if(userItem.getIsOnline()){
                                    userSearchName.setText(userItem.getUserName() + "(在线)");
                                }
                                else{
                                    userSearchName.setText(userItem.getUserName() + "(不在线)");
                                }
                                userSearchIsFriend.setText("该用户与您已是好友，无法添加");
                                userSearchBtn.setBackgroundColor(0xff34adfd);
                                userSearchBtn.setText("查看文件");
                            }
                            else{
                                if(userItem.getIsOnline()){
                                    userSearchName.setText(userItem.getUserName() + "(在线)");
                                    userSearchIsFriend.setText("该用户与您还不是好友");
                                    userSearchBtn.setBackgroundColor(0xff34adfd);
                                }
                                else{
                                    userSearchName.setText(userItem.getUserName() + "(不在线)");
                                    userSearchIsFriend.setText("该用户当前不在线，无法添加");
                                    userSearchBtn.setBackgroundColor(0xffaaaaaa);
                                }
                                userSearchBtn.setText("添加好友");
                            }
                            userSearchBtn.setVisibility(View.VISIBLE);
                            userSearchBtn.setOnClickListener(listener);
                        }
                        break;
                    case 1:
                        Toast.makeText(searchFriend.this, "未查询到相关用户", Toast.LENGTH_SHORT).show();
                        break;
                        default:break;
                }
            }
        };
    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.searchUserIdBtn:
                    userSearchId = searchUserId.getText().toString();
                    new Thread(searchUser).start();
                    break;
                case R.id.searchFriendBack_btn:
                    searchFriend.this.finish();
                    break;
                case R.id.userSearchBtn:
                    if (userSearchBtn.getText().toString().equals("添加好友")&&((ColorDrawable)userSearchBtn.getBackground()).getColor()==0xff34adfd){
                        new Thread(addFriend).start();
                        Toast.makeText(searchFriend.this, "请求已发送",Toast.LENGTH_SHORT).show();
                    }
                    else if (((ColorDrawable)userSearchBtn.getBackground()).getColor()==0xffaaaaaa){
                        Toast.makeText(searchFriend.this, "该用户不在线，无法添加",Toast.LENGTH_SHORT).show();
                    }
                    else if (userSearchBtn.getText().toString().equals("查看文件")) {
                        myFileList mf = new myFileList();
                        mf.setList(userItem.getShareFiles());
                        Intent intent = new Intent();
                        intent.setClass(searchFriend.this, fileList.class);
                        Bundle bundle = new Bundle();
//                        bundle.putString("userName", userItem.getUserName());
                        bundle.putString("userId", userItem.getUserId());
                        bundle.putSerializable("friendFile", mf);
//                        bundle.putString("myUserId", myUserId);
//                        bundle.putString("myUserName", myUserName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    Runnable searchUser = new Runnable() {
        @Override
        public void run() {
            try{
                GetPersonalInfoMsg.GetPersonalInfoReq.Builder builder = GetPersonalInfoMsg.GetPersonalInfoReq.newBuilder();
                builder.setWhere("searchFriend");
                builder.setUserId(userSearchId);
                builder.setUserInfo(true);
                builder.setFileInfo(true);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.GET_PERSONALINFO_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    Runnable addFriend = new Runnable() {
        @Override
        public void run() {
            try{
                AddFriendMsg.AddFriendReq.Builder builder = AddFriendMsg.AddFriendReq.newBuilder();
                builder.setFriendUserId(userItem.getUserId());
                builder.setRequestType(AddFriendMsg.AddFriendReq.RequestType.REQUEST);
                byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ADD_FRIEND_REQ.getNumber(), builder.build().toByteArray());
                Login.base.writeToServer(Login.outputStream, byteArray);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };
}
