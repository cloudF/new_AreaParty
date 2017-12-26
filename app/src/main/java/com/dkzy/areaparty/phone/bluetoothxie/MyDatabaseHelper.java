package com.dkzy.areaparty.phone.bluetoothxie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by XIE on 2017/5/15.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final int MOUSE = 0;
    public static final int AUDIO = 2;
    public static final int KEYBOARD = 1;
    public static final String CREATE_DEVICE = "create table Device ("
            + "name text primary key, "
            + "idname text, "
            + "checked integer, "
            + "type integer)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
