package com.dkzy.areaparty.phone.fragment06;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dkzy.areaparty.phone.DBConst;

import java.util.ArrayList;

/**
 * Created by SnowMonkey on 2017/6/2.
 */

public class FileRequestDBManager {
    ChatSQLiteHelper fileRequestHelper;
    Context context;

    public FileRequestDBManager(Context context) {
        this.context = context;
        fileRequestHelper = new ChatSQLiteHelper(context, "chatDB", 1);
    }
    /*
    * 添加一条文件请求
    * */
    public void addFileRequestSQL(fileObj request, String table) {
        Log.i("DB","start add to database");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = fileRequestHelper.getWritableDatabase();
            cursor = db.query(table, null ,"peer_id = ? and file_date = ?", new String[]{request.getSenderId(),request.getFileDate()}, null, null, null);
            if(cursor.getCount() == 0){
                Log.i("DB","该文件请求不存在");
                ContentValues values = new ContentValues();
                values.put("peer_id", request.getSenderId());
                values.put("file_name", request.getFileName());
                values.put("file_date", request.getFileDate());
                values.put("file_size", request.getFileSize());
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

    public ArrayList<fileObj> selectFileRequestSQL(String table) {
        ArrayList<fileObj> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = fileRequestHelper.getReadableDatabase();
            cursor = db.query(table, null ,null, null, null, null, null);
            Log.i("DB",cursor.getCount()+"");
            fileObj request;
            while(cursor.moveToNext()) {
                request = new fileObj();
                request.setSenderId(cursor.getString(cursor.getColumnIndex(DBConst.tableItem_peer_id)));
                request.setFileName(cursor.getString(cursor.getColumnIndex(DBConst.tableItem_file_name)));
                request.setFileDate(cursor.getString(cursor.getColumnIndex(DBConst.tableItem_file_date)));
                request.setFileSize(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBConst.tableItem_file_size))));
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

    public void deleteFileRequestSQL(String peerId, String fileDate, String table) {
        SQLiteDatabase db = null;
        try{
            db = fileRequestHelper.getWritableDatabase();
            db.delete(table, "peer_id = ? and file_date = ?", new String[]{peerId, fileDate});
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            db.close();
        }
    }

    public void createTable(String table_name){
        SQLiteDatabase db = null;
        try{
            db = fileRequestHelper.getWritableDatabase();
            StringBuilder sBuffer = new StringBuilder();
            sBuffer.append("CREATE TABLE [").append(table_name).append("] (");
            sBuffer.append("[peer_id] TEXT,");
            sBuffer.append("[file_name] TEXT,");
            sBuffer.append("[file_date] INTEGER,");
            sBuffer.append("[file_size] TEXT)");
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
            db = fileRequestHelper.getReadableDatabase();
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
