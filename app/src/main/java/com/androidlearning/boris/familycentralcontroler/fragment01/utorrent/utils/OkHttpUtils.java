package com.androidlearning.boris.familycentralcontroler.fragment01.utorrent.utils;

import android.util.Log;


import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.hdhome.CookieJarImpl;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.hdhome.store.PersistentCookieStore;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhuyulin on 2017/10/18.
 */

public class OkHttpUtils{
    private static OkHttpUtils mInstance;
    private static OkHttpClient client;
    private String url;
    public static String authorization;
    private Request request;

    public static OkHttpUtils getInstance(){
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public static OkHttpClient getClient(){
        if (client == null){
            synchronized (OkHttpUtils.class)
            {
                if (client == null)
                {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    PersistentCookieStore persistentCookieStore = new PersistentCookieStore(MyApplication.getContext());
                    CookieJarImpl cookieJarImpl = new CookieJarImpl(persistentCookieStore);
                    builder.cookieJar(cookieJarImpl);
                    builder.connectTimeout(2, TimeUnit.SECONDS);
                    client = builder.build();
                }
            }
        }
        return client;
    }

    public OkHttpUtils setUrl(String url){
        this.url = url;

        return getInstance();
    }

    public OkHttpUtils setAuthorization(String authorization){
        this.authorization = authorization;
        return getInstance();
    }

    public OkHttpUtils build(){
        this.request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",authorization)
                .build();
        return getInstance();
    }

    public OkHttpUtils postBuild(String path){

        File file = new File(path);

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("torrent_file", file.getName(), fileBody).build();
        Log.w("OkHttpUtils",file.getName());
        this.request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","multipart/form-data")
                .addHeader("Authorization",authorization)
                .post(requestBody)
                .build();
        return getInstance();
    }

    public void execute(Callback callback){
        getClient().newCall(request).enqueue(callback);
    }


    public class FileNameSelector implements FilenameFilter {
        String extension = ".";
        public FileNameSelector(String fileExtensionNoDot) {
            extension += fileExtensionNoDot;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(extension);
        }
    }

}
