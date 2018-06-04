package com.dkzy.areaparty.phone.fragment06;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import protocol.Data.UserData;
import protocol.Msg.ChangeGroupMsg;
import protocol.Msg.CreateGroupChatMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

public class AddGroup extends AppCompatActivity {
    private List<HashMap<String, Object>> userGroupData = null;
    private List<String> userGroupMembers = null;
    private ListView lv_group;
    private ImageButton addGroupBack_btn;
    private ImageView addGroup_ok;
    private EditText groupName;
    private String str_groupName;
    private boolean isAdd = true;
    private String showGroupName = "";
    private String group_id="";
    private String group_name ="";
    private ArrayList<String> groupMems;
    private GroupMemsAdapter memsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initData();
        initView();
        initEvent();
    }

    private void initView(){
        lv_group =(ListView) findViewById(R.id.groupUserListView);
        //lv_group.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        memsAdapter = new GroupMemsAdapter();
        lv_group.setAdapter(memsAdapter);
        addGroupBack_btn = (ImageButton) findViewById(R.id.addGroupBack_btn);
        addGroup_ok = (ImageView) findViewById(R.id.addGroup_ok);
        groupName = (EditText)findViewById(R.id.groupName);

    }

    private void initEvent(){
        addGroupBack_btn.setOnClickListener(listener);
        addGroup_ok.setOnClickListener(listener);
        groupName.setText(group_name);
    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addGroupBack_btn:
                    //Toast.makeText(AddGroup.this, "xx1", Toast.LENGTH_SHORT).show();
                    AddGroup.this.finish();
                    break;
                case R.id.addGroup_ok:
                    //Toast.makeText(AddGroup.this, "xx2", Toast.LENGTH_SHORT).show();
//                    Log.i("lalalla","xxxxx = "+lv_group.getCheckedItemCount());
//                    Log.i("lalalla","xxxxx = "+lv_group.getCheckedItemPositions());
                    str_groupName = groupName.getText().toString().trim();
                    if(str_groupName.isEmpty()){
                        groupName.setError("输入为空，请重新输入");
                        str_groupName = "";
                    }
                    else if(str_groupName.length()>20){
                        groupName.setError("输入群组名称过长，请重新输入");
                        str_groupName = "";
                    }
                    else if(userGroupMembers.size()==0){
                        Toast.makeText(AddGroup.this, "请选择群组成员", Toast.LENGTH_SHORT).show();
                    }
                    else if(userGroupMembers.size() >= Login.userFriend.size()){
                        Toast.makeText(AddGroup.this, "已有全部成员分组", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        boolean isSame = false;
                        if(str_groupName.equals(group_name)) {
                            if (userGroupMembers.size() != groupMems.size()) {
                                isSame = false;
                            }
                            else{
                                if(groupMems.containsAll(userGroupMembers)){
                                    isSame=true;
                                }
                            }
                        }
                        if(isSame){
                            Toast.makeText(AddGroup.this, "群组信息未修改", Toast.LENGTH_SHORT).show();
                        }else{
                            new Thread(addGroupRunnable).start();
                            AddGroup.this.finish();
                        }
                    }
                    break;
            }
        }
    };

    Runnable addGroupRunnable = new Runnable() {
        @Override
        public void run() {
            try{
                //GetPersonalInfoMsg.GetPersonalInfoReq.Builder builder = GetPersonalInfoMsg.GetPersonalInfoReq.newBuilder();
                if(isAdd) {
                    CreateGroupChatMsg.CreateGroupChatReq.Builder builder = CreateGroupChatMsg.CreateGroupChatReq.newBuilder();
                    builder.setGroupName(str_groupName);
                    for (int i = 0; i < userGroupMembers.size(); i++) {
                        Log.i("lalalla", "yyy = " + userGroupMembers.get(i));
                        builder.addUserId(userGroupMembers.get(i));
                    }
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.CREATE_GROUP_CHAT_REQ.getNumber(), builder.build().toByteArray());
                    Login.base.writeToServer(Login.outputStream, byteArray);
                    userGroupMembers.clear();
                }
                else{
                    ChangeGroupMsg.ChangeGroupReq.Builder builder = ChangeGroupMsg.ChangeGroupReq.newBuilder();
                    builder.setChangeType(ChangeGroupMsg.ChangeGroupReq.ChangeType.UPDATE_INFO);
                    builder.setGroupName(str_groupName);
                    for (int i = 0; i < userGroupMembers.size(); i++) {
                        Log.i("lalalla", "yyy = " + userGroupMembers.get(i));
                        builder.addUserId(userGroupMembers.get(i));
                    }
                    builder.addUserId(Login.userId);
                    builder.setGroupId(group_id);
                    byte[] byteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.CHANGE_GROUP_REQ.getNumber(), builder.build().toByteArray());
                    Login.base.writeToServer(Login.outputStream, byteArray);
                    userGroupMembers.clear();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };
    private void initData(){
        userGroupData = (userGroupData==null)?new ArrayList<HashMap<String, Object>>():userGroupData;
        userGroupMembers=(userGroupMembers==null)?new ArrayList<String>():userGroupMembers;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isAdd = bundle.getBoolean("isAdd");
        if(!isAdd) {
            group_id = bundle.getString("GroupId");
            group_name = bundle.getString("GroupName");
            groupMems = bundle.getStringArrayList("GroupMems");
            groupMems.remove(Login.userId);
        }
        if(userGroupData.size() == 0){
            for(UserData.UserItem user : Login.userFriend){
                HashMap<String, Object> item = new HashMap<>();
                item.put("userId", user.getUserId());
                item.put("userName", user.getUserName());
                item.put("userHead", headIndexToImgId.toImgId(user.getHeadIndex()));
                userGroupData.add(item);
            }
        }
    }
    class GroupMemsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userGroupData.size();
        }

        @Override
        public Object getItem(int i) {
            return userGroupData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view;
            final ViewHolderGroupUser holder;
            if(convertView == null) {
                view = View.inflate(AddGroup.this, R.layout.tab06_addgroupitem, null);
                holder = new ViewHolderGroupUser();
                holder.headIndex  = (ImageView) view.findViewById(R.id.groupUserHead);
                holder.userId  = (TextView) view.findViewById(R.id.groupUserId);
                holder.userName = (TextView) view.findViewById(R.id.groupUserName);
                holder.checkBox= (CheckBox) view.findViewById(R.id.groupUser_check);
                view.setTag(holder);
            }
            else {
                view=convertView;
                holder=(ViewHolderGroupUser)view.getTag();
            }

            HashMap<String, Object> user = userGroupData.get(i);
            int headIndex = (int) user.get("userHead");
            String userId =  (String) user.get("userId");
            String userName = (String) user.get("userName");
            holder.headIndex.setImageResource(headIndex);
            holder.userId.setText(userId);
            holder.userName.setText(userName);
            holder.checkBox.setId(i);
            if(isAdd == false) {
                if(groupMems.contains(userId)){
                    holder.checkBox.setChecked(true);
                    if(!userGroupMembers.contains(userId))
                        userGroupMembers.add(userId);
                }else{
                    holder.checkBox.setChecked(false);
                }
            }
            holder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox box  = (CheckBox)buttonView;
                    holder.checkBox.setChecked(isChecked);
                    if(isChecked){
                        if(!userGroupMembers.contains((String)userGroupData.get(box.getId()).get("userId")))
                            userGroupMembers.add((String)userGroupData.get(box.getId()).get("userId"));
                    }
                    else{
                        userGroupMembers.remove((String)userGroupData.get(box.getId()).get("userId"));
                    }
                    //Toast.makeText(AddGroup.this, "box 位置"+box.getId()+userGroupData.get(box.getId()).get("userName"), 0).show();
                    Log.i("lalalla","xxxxxyyy = "+userGroupData.get(box.getId()).get("userId"));
                }
            });
//            final AppCompatCheckBox checkBox = (AppCompatCheckBox) view.findViewById(R.id.groupUser_check);
//            checkBox.setChecked(lv_group.isItemChecked(i));
//            checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("lalalla","onClick"+checkBox.isChecked());
//                    lv_group.setItemChecked(i,checkBox.isChecked());
//                    Log.i("lalalla","CheckedItemCount = "+lv_group.getCheckedItemCount());
//                    Log.i("lalalla","CheckedItemCount = "+lv_group.getCheckedItemPositions());
//                    SparseBooleanArray sarray = lv_group.getCheckedItemPositions();
//                    int size =sarray .size();
//                    for(int i=0;i<size;i++)
//                    {
//                        int key = sarray.keyAt(i);
//                        Log.i("lalalla","key = "+key);
//                        boolean valueat = sarray.valueAt(key);
//                        Log.i("lalalla","valueat = "+valueat);
//                        if(valueat)
//                        {
//                            Log.i("lalalla","xxxxxyyy = "+userGroupData.get(i).get("userId"));
//
//                        }
//                    }
//                }
//            });
            return view;
        }
        class ViewHolderGroupUser {
            ImageView headIndex;
            TextView userId;
            TextView userName;
            CheckBox checkBox;
        }
    }
}
