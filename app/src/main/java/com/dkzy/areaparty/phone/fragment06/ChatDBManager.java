package com.dkzy.areaparty.phone.fragment06;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dkzy.areaparty.phone.DBConst;

import java.util.ArrayList;

/**
 * Created by SnowMonkey on 2017/5/3.
 */

public class ChatDBManager {
    ChatSQLiteHelper chatDBHelper;
    Context context;

    public ChatDBManager(Context context) {
        this.context = context;
        chatDBHelper = new ChatSQLiteHelper(context, "chatDB", 1);
    }
    /*
    * 离线发送消息，当接收的用户在相同手机登录时，会出现添加两次的现象，第一次为发送的时候添加，第二次为接收端登录时收到的消息
    * */
    public void addChatSQL(ChatObj chat, String table) {
        Log.i("DB","start add to database");
        SQLiteDatabase db = null;
        try{
            db = chatDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("sender_id", chat.sender_id);
            values.put("receiver_id", chat.receiver_id);
            values.put("msg", chat.msg);
            values.put("date", chat.date);
            long code = db.insert(table, null, values);
            System.out.println(code);
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally { db.close(); }
    }

    public ArrayList<ChatObj> selectMyChatSQL(String table, String myId, String peerId, int size) {
        ArrayList<ChatObj> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String limit = null;
        if(size != -1) limit = ""+size;
        try{
            db = chatDBHelper.getReadableDatabase();
            //sql = "select * from chatTB order by date desc limit 5";
            cursor = db.query(table, null ,"sender_id = ? and receiver_id = ? or sender_id = ? and receiver_id = ?", new String[]{myId,peerId, peerId, myId}, null, null, "date desc", limit);
            //cursor = db.query(table, null, null, null, null, null, null);
            ChatObj chat;
            while(cursor.moveToNext()) {
                chat = new ChatObj();
                chat.id = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_id));
                chat.sender_id = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_senderID));
                chat.receiver_id = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_receiverID));
                chat.msg = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_msg));
                chat.date = cursor.getLong(cursor.getColumnIndex(DBConst.tableItem_date));
                list.add(chat);
            }
        } catch (Exception e){} finally{
            cursor.close();
            db.close();
        }
        return list;
    }

    public ArrayList<ChatObj> selectMyChatSQL(String table, String myId, String peerId, long startTime, long endTime) {
        ArrayList<ChatObj> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = chatDBHelper.getReadableDatabase();
            //sql = "select * from chatTB order by date desc limit 5";
            cursor = db.query(table, null ,"(sender_id = ? and receiver_id = ? or sender_id = ? and receiver_id = ?) and date >= ? and date <= ?", new String[]{myId,peerId, peerId, myId, String.valueOf(startTime), String.valueOf(endTime)}, null, null, "date desc");
            //cursor = db.query(table, null, null, null, null, null, null);
            ChatObj chat;
            while(cursor.moveToNext()) {
                chat = new ChatObj();
                chat.id = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_id));
                chat.sender_id = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_senderID));
                chat.receiver_id = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_receiverID));
                chat.msg = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_msg));
                chat.date = cursor.getLong(cursor.getColumnIndex(DBConst.tableItem_date));
                list.add(chat);
            }
        } catch (Exception e){} finally{
            cursor.close();
            db.close();
        }
        return list;
    }

    public void deleteSharedFileSQL(int id, String table) {
        SQLiteDatabase db = null;
        try{
            db = chatDBHelper.getWritableDatabase();
            db.delete(table, DBConst.tableItem_id + "=" + id, null);
        } catch(Exception e){} finally{
            db.close();
        }
    }

    public void createTable(String table_name){
        System.out.println("开始建表");
        SQLiteDatabase db = null;
        try{
            db = chatDBHelper.getWritableDatabase();
            StringBuilder sBuffer = new StringBuilder();
            sBuffer.append("CREATE TABLE [").append(table_name).append("] (");
            sBuffer.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sBuffer.append("[sender_id] TEXT,");
            sBuffer.append("[receiver_id] TEXT,");
            sBuffer.append("[msg] TEXT,");
            sBuffer.append("[date] INTEGER)");
            db.execSQL(sBuffer.toString());
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            db.close();
        }
    }


    public boolean tabbleIsExist(String tableName){
        SQLiteDatabase db = null;
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            db = chatDBHelper.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master  where type ='table' and name = '" + tableName.trim() + "'";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return result;
    }
}
