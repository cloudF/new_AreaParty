package com.dkzy.areaparty.phone.fragment01.utorrent.utils;


import com.dkzy.areaparty.phone.fragment01.utorrent.model.TFile;
import com.dkzy.areaparty.phone.fragment01.utorrent.model.Torrent;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyulin on 2017/10/19.
 */

public class TorrentUtils {

    public static List<Torrent> paserTorrents(String torrents){
        List<Torrent> torrentList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(torrents);
            //Log.w("UTorrentActivity",jsonArray.length()+"");
            for (int i = 0; i<jsonArray.length(); i++){
                final JSONArray js = jsonArray.getJSONArray(i);
                //Log.w("UTorrentActivity",""+js.length());

                String hash = js.getString(0);
                String name = js.getString(2);
                String size = getSize(js.getLong(3));
                //Log.w("UTorrentActivity","size:"+size+"**"+hash+name);
                String downloadSpeed = getSize(js.getLong(9))+"/s";
                String ETA = secToTime(js.getLong(10));
                String status_progress = js.getString(21);
                int m = findNum(status_progress);
                String status = status_progress.substring(0,m-1);
                int pgress = js.getInt(4);
                torrentList.add(new Torrent(hash,name,size,status,downloadSpeed,ETA,pgress));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return torrentList;
    }

    public static List<TFile> parseFiles(String files){


        List<TFile> fileList = new ArrayList<>();
        try {
            JSONArray jsonArray1 = new JSONArray(files);

            JSONArray jsonArray = jsonArray1.getJSONArray(1);
            for (int i = 0;i < jsonArray.length(); i++){
                JSONArray js = jsonArray.getJSONArray(i);
                String name = js.getString(0);
                String size = getSize(js.getLong(1));
                String downloadSize = getSize(js.getLong(2));
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                String progress = nf.format(js.getDouble(2) * 100 / js.getDouble(1))+"%";
                int priority = js.getInt(3);
                //Log.w("TorrentUtils",name+"**"+size+"**"+progress+"**"+priority+"**");
                fileList.add(new TFile(name,size,downloadSize,progress,priority));
            }
        }catch (JSONException e){e.printStackTrace();}
        return fileList;
    }

    public static String getSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size*10 / 1024;
        }
        if (size < 10240) {
            //保留1位小数，
            return String.valueOf((size / 10)) + "."
                    + String.valueOf((size % 10)) + "MB";
        } else {
            //保留2位小数
            size = size * 10 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
    public static String secToTime(long time) {
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "∞";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                return minute + "分" + second + "秒";
            } else {
                hour = minute / 60;
                if (hour < 24){
                    minute = minute % 60;
                    return hour + "小时" + minute + "分";
                }else{
                    return "大于1天";
                }

            }
        }

    }
    private static int findNum(String str) {
        for(int i=0;i<str.length();i++)
        {
            char c=str.charAt(i);
            if(c>='0'&& c<='9')
            {
                return i;
            }
        }
        return 1;
    }

}
