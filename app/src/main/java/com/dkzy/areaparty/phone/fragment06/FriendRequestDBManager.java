package com.dkzy.areaparty.phone.fragment06;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dkzy.areaparty.phone.DBConst;

import java.util.ArrayList;

/**
 * Created by SnowMonkey on 2017/5/17.
 */

public class FriendRequestDBManager {
    ChatSQLiteHelper friendDBHelper;
    Context context;

    public FriendRequestDBManager(Context context) {
        this.context = context;
        friendDBHelper = new ChatSQLiteHelper(context, "chatDB", 1);
    }
    /*
    * 添加一条好友请求
    * */
    public void addRequestFriendSQL(RequestFriendObj request, String table) {
        Log.i("DB","start add to database");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = friendDBHelper.getWritableDatabase();
            cursor = db.query(table, null ,"friend_id = ?", new String[]{request.friend_id}, null, null, null);
            Log.i("DB","有"+cursor.getCount()+"个好友请求未处理");
            if(cursor.getCount() == 0){
                Log.i("DB","该好友请求不存在");
                ContentValues values = new ContentValues();
                values.put("friend_id", request.friend_id);
                values.put("friend_name", request.friend_name);
                values.put("friend_headindex", request.friend_headindex);
                values.put("friend_filenum", request.friend_filenum);
                values.put("isagree", request.isagree);
                long code = db.insert(table, null, values);
                System.out.println(code);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
    }

    public ArrayList<RequestFriendObj> selectRequestFriendSQL(String table) {
        ArrayList<RequestFriendObj> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = friendDBHelper.getReadableDatabase();
            cursor = db.query(table, null ,"isagree = ?", new String[]{"0"}, null, null, null);
            Log.i("DB",cursor.getCount()+"");
            RequestFriendObj request;
            while(cursor.moveToNext()) {
                request = new RequestFriendObj();
                request.friend_id = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_friend_id));
                request.friend_name = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_friend_name));
                request.friend_headindex = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_friend_headindex));
                request.friend_filenum = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_friend_filenum));
                request.isagree = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_isagree));
                list.add(request);
            }
        } catch (Exception e){} finally{
            cursor.close();
            db.close();
        }
        return list;
    }

    public ArrayList<RequestFriendObj> selectAgreeFriendSQL(String table) {
        ArrayList<RequestFriendObj> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = friendDBHelper.getReadableDatabase();
            cursor = db.query(table, null ,"isagree = ?", new String[]{"1"}, null, null, null);
            RequestFriendObj request;
            while(cursor.moveToNext()) {
                request = new RequestFriendObj();
                request.friend_id = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_friend_id));
                request.friend_name = cursor.getString(cursor.getColumnIndex(DBConst.tableItem_friend_name));
                request.friend_headindex = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_friend_headindex));
                request.friend_filenum = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_friend_filenum));
                request.isagree = cursor.getInt(cursor.getColumnIndex(DBConst.tableItem_isagree));
                list.add(request);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            cursor.close();
            db.close();
        }
        return list;
    }

    public void changeRequestStateSQL(RequestFriendObj request, String table){
        SQLiteDatabase db = null;
        Log.i("DB","开始更改好友请求");
        try{
            db = friendDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("friend_id", request.friend_id);
            values.put("friend_name", request.friend_name);
            values.put("friend_headindex", request.friend_headindex);
            values.put("friend_filenum", request.friend_filenum);
            values.put("isagree", request.isagree);

            Log.i("DB","update start");
            String whereClause = "friend_id=?";
            String[] whereArgs = new String[] { request.friend_id };
            db.update(table, values, whereClause, whereArgs);
            Log.i("DB","update finish");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteSharedFileSQL(int id, String table) {
        SQLiteDatabase db = null;
        try{
            db = friendDBHelper.getWritableDatabase();
            db.delete(table, DBConst.tableItem_id + "=" + id, null);
        } catch(Exception e){} finally{
            db.close();
        }
    }

    public void createTable(String table_name){
        SQLiteDatabase db = null;
        try{
            db = friendDBHelper.getWritableDatabase();
            StringBuilder sBuffer = new StringBuilder();
            sBuffer.append("CREATE TABLE [").append(table_name).append("] (");
            sBuffer.append("[friend_id] TEXT,");
            sBuffer.append("[friend_name] TEXT,");
            sBuffer.append("[friend_headindex] INTEGER,");
            sBuffer.append("[friend_filenum] INTEGER,");
            sBuffer.append("[isagree] INTEGER)");
            db.execSQL(sBuffer.toString());
        } catch(Exception e){} finally{
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
            db = friendDBHelper.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }
}
