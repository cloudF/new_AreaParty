package com.dkzy.areaparty.phone.fragment06;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dkzy.areaparty.phone.DBConst;

/**
 * Created by SnowMonkey on 2017/5/3.
 */

public class ChatSQLiteHelper extends SQLiteOpenHelper {
    private void createChatTables(String table_name, SQLiteDatabase db) {
        StringBuilder sBuffer = new StringBuilder();
        sBuffer.append("CREATE TABLE [").append(table_name).append("] (");  //创建表和定义表明
        sBuffer.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");        //设置_id为主键，不能为空与自增性质
        sBuffer.append("[sender_id] TEXT,");                                //发送者id
        sBuffer.append("[receiver_id] TEXT,");                              //接收者id
        sBuffer.append("[msg] TEXT,");                                      //信息
        sBuffer.append("[date] INTEGER)");                                  //发送或接收时间
        db.execSQL(sBuffer.toString());
    }

    private void createFriendTables(String table_name, SQLiteDatabase db) {
        StringBuilder sBuffer = new StringBuilder();
        sBuffer.append("CREATE TABLE [").append(table_name).append("] (");  //创建表和定义表明
        sBuffer.append("[friend_id] TEXT,");                                //请求者id
        sBuffer.append("[friend_name] TEXT,");                              //请求者name
        sBuffer.append("[friend_headindex] INTEGER,");                      //请求者头像
        sBuffer.append("[friend_filenum] INTEGER,");                        //请求者文件数
        sBuffer.append("[isagree] INTEGER)");                               //是否已接受请求
        db.execSQL(sBuffer.toString());
    }

    private void createFileRequestTables(String table_name, SQLiteDatabase db){
        StringBuilder sBuffer = new StringBuilder();
        sBuffer.append("CREATE TABLE [").append(table_name).append("] (");  //创建表和定义表明
        sBuffer.append("[peer_id] TEXT,");                                //请求者id
        sBuffer.append("[file_name] TEXT,");                              //请求文件的name
        sBuffer.append("[file_date] INTEGER,");                           //请求文件的date
        sBuffer.append("[file_size] TEXT)");                              //请求文件的size
        db.execSQL(sBuffer.toString());
    }

    public ChatSQLiteHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createChatTables(DBConst.chatTB, sqLiteDatabase);
        createFriendTables(DBConst.friendTB, sqLiteDatabase);
        createFileRequestTables(DBConst.fileRequestTB, sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        System.out.println("upgrade a Database");
    }
}
