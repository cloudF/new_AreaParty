package com.dkzy.areaparty.phone.fragment01.utorrent.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuyulin on 2017/10/18.
 */

public class UrlUtils {
    public static String ip_port;
    private String action;
    private List<String> hashList;
    public static String token;
    private Integer p;
    private Integer f;
    private String list;
    private String cid;
    private String s;
    private String v;

    public UrlUtils setList(String list) {
        this.list = list;
        return this;
    }
    public UrlUtils setS(String s) {
        this.s = s;
        return this;
    }
    public UrlUtils setV(String v) {
        this.v = v;
        return this;
    }

    public UrlUtils setCid(String cid) {
        this.cid = cid;
        return this;
    }

//    public UrlUtils setToken(String token) {
//        this.token = token;
//        return this;
//    }

    public UrlUtils setHashList(List<String> hashList) {
        this.hashList = hashList;
        return this;
    }

    public UrlUtils setAction(String action) {

        this.action = action;
        return this;
    }

    public UrlUtils setP(Integer p) {
        this.p = p;return this;
    }

    public UrlUtils setf(Integer f) {
        this.f = f;return this;
    }

    //    public UrlUtils setIp_port(String ip_port) {
//
//        this.ip_port = ip_port;
//        return this;
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("http://");
        builder.append(ip_port).append("/gui/?");
        if (!TextUtils.isEmpty(token)) builder.append("token=").append(token).append("&");
        if (!TextUtils.isEmpty(action)) builder.append("action=").append(action).append("&");
        if (!TextUtils.isEmpty(s)) builder.append("s=").append(s).append("&");
        if (!TextUtils.isEmpty(v)) builder.append("v=").append(v).append("&");
        if (hashList!=null && hashList.size()!=0){
            for (String hash: hashList){
                builder.append("hash=").append(hash).append("&");
            }
        }
        if (p!=null) builder.append("p=").append(p).append("&");
        if (f!=null) builder.append("f=").append(f).append("&");
//        if (fList!=null && fList.size()!=0){
//            for (Integer f : fList){
//                builder.append("f=").append(f).append("&");
//            }
//        }
        if (!TextUtils.isEmpty(list)) builder.append("list=").append(list).append("&");
        if (!TextUtils.isEmpty(cid)) builder.append("cid=").append(cid).append("&");
        builder.append("t=").append(String.valueOf(new Date().getTime()));
        Log.w("url","url:"+builder.toString());
        return builder.toString();
    }
}
