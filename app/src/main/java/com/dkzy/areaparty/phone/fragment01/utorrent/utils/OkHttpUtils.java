package com.dkzy.areaparty.phone.fragment01.utorrent.utils;

import android.text.TextUtils;
import android.util.Log;


import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.CookieJarImpl;
import com.dkzy.areaparty.phone.fragment01.websitemanager.hdhome.store.PersistentCookieStore;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhuyulin on 2017/10/18.
 */

public class OkHttpUtils {
    private static OkHttpUtils mInstance;
    private static OkHttpClient client;
    private String url;
    public static String authorization;
    private Request request;

    public static int account;

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
    public OkHttpUtils buildNormal(){
        this.request = new Request.Builder()
                .url(url)
                .build();
        return getInstance();
    }

    public OkHttpUtils postBuild(List<String> paths){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (String path : paths){
            if (path.endsWith(".torrent")){
                File file = new File(path);
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
                builder.addFormDataPart("torrent_file",file.getName(),fileBody);
            }
        }
        RequestBody requestBody = builder.build();



        //RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("torrent_file", file.getName(), fileBody).build();
        //Log.w("OkHttpUtils",file.getName());
        this.request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","multipart/form-data")
                .addHeader("Authorization",authorization)
                .post(requestBody)
                .build();
        return getInstance();
    }
    public void upFile(List<String> paths){
        account = 0;
        for (String path : paths){
            if (path.endsWith(".torrent")){
                File file = new File(path);
                if (!file.exists()) return;
                Log.w("OKHTTPU",path);
                MultipartBody.Builder builder1 = new MultipartBody.Builder().setType(MultipartBody.FORM);
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
                builder1.addFormDataPart("torrent_file",file.getName(),fileBody);
                final RequestBody requestBody = builder1.build();
                this.request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","multipart/form-data")
                        .addHeader("Authorization",authorization)
                        .post(requestBody)
                        .build();
                getClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.w("OKHTTPU",responseData);
                        if (!TextUtils.isEmpty(responseData) && responseData.contains("build") && responseData.length()<20)
                           account++;
                    }
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }



        //RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("torrent_file", file.getName(), fileBody).build();
        //Log.w("OkHttpUtils",file.getName());

    }

    public void execute(Callback callback){
        getClient().newCall(request).enqueue(callback);
    }


//    public class FileNameSelector implements FilenameFilter {
//        String extension = ".";
//        public FileNameSelector(String fileExtensionNoDot) {
//            extension += fileExtensionNoDot;
//        }
//
//        public boolean accept(File dir, String name) {
//            return name.endsWith(extension);
//        }
//    }

}
