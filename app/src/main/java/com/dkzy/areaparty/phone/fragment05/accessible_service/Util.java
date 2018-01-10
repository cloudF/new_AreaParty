package com.dkzy.areaparty.phone.fragment05.accessible_service;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.AREAPARTY_NET;

/**
 * Created by zhuyulin on 2017/7/20.
 */

public class Util {
    private static final String addressLogin = "http://"+AREAPARTY_NET+":8080/Auto_login.php";
    public static final String addressLogout = "http://"+AREAPARTY_NET+":8080/Auto_logout.php";
    public static void sendRequestLogin(final String requestLogin , final String websit, final String serialNumber , Callback callback){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("requestlogin", requestLogin)
                .add("websit", websit)
                .add("user_mac", serialNumber)
                .build();

        Request request = new Request.Builder()
                .url(addressLogin)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequestLogout(final String requestLogout , final String websit, final String accound_id , Callback callback){
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("requestlogout", requestLogout)
                .add("websit", websit)
                .add("account_id", accound_id)
                .build();

        Request request = new Request.Builder()
                .url(addressLogout)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void setRecord(Context context , String websit , String account_id){
        SharedPreferences.Editor editor = context.getSharedPreferences("login_record",MODE_PRIVATE).edit();
        editor.putString("websit",websit);
        editor.putString("account_id", account_id);
        editor.apply();
    }
    public static void setRecord(Context context , String websit){
        SharedPreferences.Editor editor = context.getSharedPreferences("login_record",MODE_PRIVATE).edit();
        editor.putString("websit",websit);
        editor.apply();
    }

    public static String getRecordWebsit(Context context){
        SharedPreferences pref = context.getSharedPreferences("login_record",MODE_PRIVATE);
        return pref.getString("websit","");
    }
    public static String getRecordId(Context context){
        SharedPreferences pref = context.getSharedPreferences("login_record",MODE_PRIVATE);
        return pref.getString("account_id","");
    }
    public static void clearRecordId(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("login_record",MODE_PRIVATE).edit();
        editor.putString("account_id","");
        editor.apply();
    }
    public static void clearRecord(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("login_record",MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
