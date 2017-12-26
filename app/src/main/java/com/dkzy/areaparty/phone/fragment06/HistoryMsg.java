package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MainActivity;
import com.dkzy.areaparty.phone.R;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SnowMonkey on 2017/5/23.
 */

public class HistoryMsg extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ImageButton historyMsgBackBtn;
    private TextView historyMsgTitle;
    private TextView historyMsg_tip;
    private ListView historyMsgList;
    private LinearLayout selectDate;
    private LinearLayout historyMsgListWrap;

    private String user_id;
    private String user_name;
    private int user_head;
    private int myUserHead;
    private List<HashMap<String, Object>> chatData;
    private chatItemAdapater chatAdapater;

    private ChatDBManager chatDB = MainActivity.getChatDBManager();
    public final static int FRIEND=1;
    public final static int ME=0;
    int[] layout={R.layout.tab06_filemain_historymeitem, R.layout.tab06_filemain_historyfrienditem};
    int[] to = {R.id.history_meChat, R.id.history_meHead, R.id.history_meMsgTime, R.id.history_friendChat, R.id.history_friendHead, R.id.history_friendMsgTime};

    final Calendar calendar = Calendar.getInstance();
    final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab06_filemain_historymsg);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getData();
        initView();
        initEvent();
    }
    private void getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_id = bundle.getString("userId");
        user_name = bundle.getString("userName");
        user_head = bundle.getInt("userHead");
        myUserHead = bundle.getInt("myUserHead");
        chatData = new ArrayList<>();
    }
    private void initView(){
        historyMsgBackBtn = (ImageButton) this.findViewById(R.id.historyMsgBackBtn);
        historyMsgTitle = (TextView) this.findViewById(R.id.historyMsgTitle);
        historyMsg_tip = (TextView) this.findViewById(R.id.historyMsg_tip);
        historyMsgList = (ListView) this.findViewById(R.id.historyMsgList);
        selectDate = (LinearLayout) this.findViewById(R.id.selectDate);
        historyMsgListWrap = (LinearLayout) this.findViewById(R.id.historyMsgListWrap);

        historyMsgTitle.setText("与"+user_id+"的聊天记录");
        historyMsg_tip.setText("请先选择要查询的日期");
        chatAdapater = new chatItemAdapater(this, chatData, layout, to);
        historyMsgList.setAdapter(chatAdapater);
    }
    private void initEvent(){
        historyMsgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HistoryMsg.this.finish();
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    datePickerDialog.setYearRange(2016, calendar.get(Calendar.YEAR));
                    datePickerDialog.setCloseOnSingleTapDay(false);
                    datePickerDialog.show(getSupportFragmentManager(), "datepicker");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        month = month + 1;
        String startYear = String.valueOf(year);
        String startMonth = String.valueOf(month);
        String startDay = String.valueOf(day);
        String endYear = String.valueOf(year);
        String endMonth = String.valueOf(month);
        String endDay = String.valueOf(day);
        String startDate = checkDate(startYear, startMonth, startDay) + "00时00分00秒";
        System.out.println(startDate);
        String endDate = checkDate(endYear, endMonth, endDay) + "23时59分59秒";
        System.out.println(endDate);
        if(endDate.equals("23时59分59秒") || startDate.equals("00时00分00秒")) return;
        final Long startTime = getTime(startDate);
        final Long endTime = getTime(endDate);
        chatData.clear();
        int size;
        ArrayList<ChatObj> chats = chatDB.selectMyChatSQL(Login.userId, Login.userId, user_id, startTime, endTime);
        size = chats.size();
        if(size > 0){
            historyMsgList.setVisibility(View.VISIBLE);
            historyMsg_tip.setVisibility(View.GONE);
            for(int i = size-1; i >=0; i--){
                ChatObj chat = chats.get(i);
                if(chat.sender_id.equals(Login.userId) && chat.receiver_id.equals(user_id)){
                    addTextToList(chat, ME);
                }else {
                    addTextToList(chat, FRIEND);
                }
            }
            if(historyMsgList!=null)
                chatAdapater.notifyDataSetChanged();
        }else {
            historyMsgList.setVisibility(View.GONE);
            historyMsg_tip.setVisibility(View.VISIBLE);
            historyMsg_tip.setText("没有找到当日的聊天记录");
        }
    }

    private String checkDate(String year, String month, String day){
        String date;

        Calendar c = Calendar.getInstance();
        int nowYear = c.get(Calendar.YEAR);
        int nowMonth = c.get(Calendar.MONTH) + 1;
        int nowDay = c.get(Calendar.DAY_OF_MONTH);

        if(year.equals("")) year = String.valueOf(nowYear);
        if(month.equals("")) month = String.valueOf(nowMonth);
        if(day.equals("")) day = String.valueOf(nowDay);

        if(month.length() == 1) month = "0" + month;
        if(day.length() == 1) day = "0" + day;

        date = year + "年" + month + "月" + day + "日";
        return date;
    }

    private long getTime(String user_time) {
        long l = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        }catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace();
            e.printStackTrace();
            Toast.makeText(HistoryMsg.this, "请正确输入日期",Toast.LENGTH_SHORT).show();
        }
        return l;
    }

    private void addTextToList(ChatObj chat, int who){
        String text = chat.msg;
        long time = chat.date;
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("person",who );
        map.put("userHead", who==ME? headIndexToImgId.toImgId(myUserHead): headIndexToImgId.toImgId(user_head));
        map.put("text", text);
        map.put("msgTime", time);
        chatData.add(map);
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
            ViewHolderChat holder;
            int who=(Integer)chatData.get(position).get("person");

            view= LayoutInflater.from(context).inflate(layout[who==ME?0:1], null);
            long chatDate = (Long) chatData.get(position).get("msgTime");
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd  HH:mm");
            Date dt = new Date(chatDate);
            String sDateTime = sdf.format(dt);
            holder=new ViewHolderChat();
            holder.msgTime = (TextView) view.findViewById(to[who*3+2]);
            holder.userHead=(ImageView)view.findViewById(to[who*3+1]);
            holder.userChat=(TextView)view.findViewById(to[who*3+0]);
            holder.msgTime.setText(sDateTime);
            holder.userHead.setBackgroundResource((Integer)chatData.get(position).get("userHead"));
            holder.userChat.setText(chatData.get(position).get("text").toString());
            return view;
        }

        class ViewHolderChat {
            TextView msgTime;
            ImageView userHead;
            TextView userChat;
        }
    }
}
