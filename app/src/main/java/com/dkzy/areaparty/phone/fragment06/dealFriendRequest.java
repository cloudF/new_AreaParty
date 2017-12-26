package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import protocol.Msg.AddFriendMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

/**
 * Created by SnowMonkey on 2017/3/10.
 */

public class dealFriendRequest extends AppCompatActivity {

    private ListView requestUserListView = null;
//    private myList requestUserList = null;
    private ImageButton dealFriendRequest_backBtn = null;
    private List<HashMap<String, Object>> requestUserData = null;
    private MyApater requestUserAdapter = null;
    private FriendRequestDBManager friendRequestDB = MainActivity.getFriendRequestDBManager();
    private String myUserId;
    private String myUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_dealfriendrequest);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getData();
        initView();
    }

    private void getData(){
        requestUserData = new ArrayList<>();
        ArrayList<RequestFriendObj> requests = friendRequestDB.selectRequestFriendSQL(Login.userId+"friend");
        System.out.println(requests.size());
        Iterator<RequestFriendObj> it = requests.iterator();
        while(it.hasNext()){
            RequestFriendObj request = it.next();
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("userId", request.friend_id);
            hm.put("userName", request.friend_name + "(" + request.friend_id + ")");
            hm.put("fileNum", "他共享了" + request.friend_filenum + "个文件");
            hm.put("userHead", headIndexToImgId.toImgId(request.friend_headindex));
            requestUserData.add(hm);
        }

        Intent intent = getIntent();
//        requestUserList = (myList) intent.getExtras().get("friendRequest");
        myUserId = intent.getExtras().getString("userId");
        myUserName = intent.getExtras().getString("userName");
//        int i = 0;
//        friendId = new String[requestUserList.getList().size()];
//        for(HashMap<String,Object> h : requestUserList.getList()){
//            friendId[i] = h.get("userId").toString();
//            HashMap<String, Object> hm = new HashMap<>();
//            hm.put("userName", h.get("userName") + "(" + h.get("userId") + ")");
//            hm.put("fileNum", "他共享了" + h.get("fileNum") + "个文件");
//            hm.put("userHead", h.get("userHead"));
//            requestUserData.add(hm);
//            i++;
//        }
    }
    private void initView(){
        requestUserListView = (ListView) findViewById(R.id.requestUserListView);
        dealFriendRequest_backBtn = (ImageButton) findViewById(R.id.dealFriendRequest_backBtn);
        requestUserAdapter = new MyApater(this);
        requestUserListView.setAdapter(requestUserAdapter);
        dealFriendRequest_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealFriendRequest.this.finish();
            }
        });
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
            return requestUserData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return requestUserData.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.tab06_userinfo, null);
            final TextView btn = (TextView) convertView.findViewById(R.id.agreeRequestBtn);
            TextView userName = (TextView) convertView.findViewById(R.id.requestUserName) ;
            TextView userFileNum = (TextView) convertView.findViewById(R.id.requestUserFileNum);
            ImageView friendHead = (ImageView) convertView.findViewById(R.id.addFriendHead);

            friendHead.setImageResource(Integer.parseInt(String.valueOf(requestUserData.get(requestUserData.size()-position-1).get("userHead"))));
            String userNameStr = String.valueOf(requestUserData.get(requestUserData.size()-position-1).get("userName"));
            String userFileNumStr = String.valueOf(requestUserData.get(requestUserData.size()-position-1).get("fileNum"));
            userName.setText(userNameStr);
            userFileNum.setText(userFileNumStr);
            btn.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    btn.setText("已同意");
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setTextColor(0xff888888);
                    btn.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AddFriendMsg.AddFriendReq.Builder aab = AddFriendMsg.AddFriendReq.newBuilder();
                                aab.setRequestType(AddFriendMsg.AddFriendReq.RequestType.AGREE);
                                aab.setFriendUserId(String.valueOf(requestUserData.get(requestUserData.size()-position-1).get("userId")));
                                System.out.println("friendUserId:" + requestUserData.get(requestUserData.size()-position-1).get("userId"));
                                byte[] AddbyteArray = new byte[0];
                                AddbyteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.ADD_FRIEND_REQ.getNumber(), aab.build().toByteArray());
                                Login.base.writeToServer(Login.outputStream,AddbyteArray);
                                //requestUserData.remove(requestUserData.size()-position-1);
                                Message msg = MainActivity.handlerTab06.obtainMessage();
                                msg.obj = requestUserData.size()-position-1;
                                msg.what = OrderConst.delFriend_order;
                                MainActivity.handlerTab06.sendMessage(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            return convertView;
        }
    }
}
