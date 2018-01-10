package com.dkzy.areaparty.phone.fragment02.subtitle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.dkzy.areaparty.phone.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubTitleUtil {
    public static List<SubTitle> subTitleList = new ArrayList<>();
    public static List<SubTitle> subTitleListDetail = new ArrayList<>();
    private OnLoadListener listener;
    private OnLoadListener listener1;
    private String seasonEpisode;

    public SubTitleUtil() {
    }

    public void setListener(OnLoadListener listener) {
        this.listener = listener;
    }
    public void setListener1(OnLoadListener listener1) {
        this.listener1 = listener1;
    }

    public void getInternetSub(final String mediaName) {
        listener.onStartLoad();

                String searchSubFileName = mediaName.substring(0, mediaName.lastIndexOf(".") > 0 ? mediaName.lastIndexOf(".") : mediaName.length());

                //去除电影名字中的Chi_Eng,Chi,Eng
                String pattern1="(?i)Chi_Eng";
                String pattern2="(?i)Chi";
                String pattern3="(?i)Eng";
                searchSubFileName=searchSubFileName.replaceAll(pattern1,"");
                searchSubFileName=searchSubFileName.replaceAll(pattern2,"");
                searchSubFileName=searchSubFileName.replaceAll(pattern3,"");
                Log.e("VideoPlayerActivity",searchSubFileName);

                //如果是S01E02这种命名的美剧，提取是多少集

                String regex = "(?i)S\\d+(?i)E\\d+";
                String INPUT = searchSubFileName;
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(INPUT); // 获取 matcher 对象
                int count = 0;
                seasonEpisode=null;

                while(m.find()) {
                    count++;
                    System.out.println("Match number "+count);
                    seasonEpisode=searchSubFileName.substring(m.start(),m.end());
                }

                downloadInternetSubtitles( searchSubFileName, mediaName,seasonEpisode);


    }
    public void getSubTitleUrl(final String videoID){
        listener1.onStartLoad();
        subTitleListDetail.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url1="http://api.assrt.net/v1/sub/detail?token=wOu37LmDZYHpMypmS3xaeWnsqbVv7vhw&id="+videoID;
                String result=HttpUtil.sendHttpRequest(url1);
                Log.e("videoPlayerActivity2",result);

                //获得filelist内容
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONObject jsonObjectSub=jsonObject.getJSONObject("sub");
                    JSONArray jsonArraySubs=jsonObjectSub.getJSONArray("subs");
                    try{
                        JSONArray jsonArrayFileList=jsonArraySubs.getJSONObject(0).getJSONArray("filelist");
                        int j = -1;
                        for(int i=0;i<jsonArrayFileList.length();i++){
                            JSONObject jsonObjectDetails=jsonArrayFileList.getJSONObject(i);
                            String fileUrl=jsonObjectDetails.getString("url");
                            String subFileName=jsonObjectDetails.getString("f");
                            SubTitle subTitle;
                            subTitle = new SubTitle(fileUrl,subFileName, false);
                            if(seasonEpisode != null && j == -1){
                                if(subFileName.contains(seasonEpisode)){
                                    j = i;
                                }
                            }
                            subTitleListDetail.add(subTitle);
                        }
                        if (subTitleListDetail.size() > 0 ){
                            if (j!=-1 && j<subTitleListDetail.size()){
                                subTitleListDetail.get(j).setChecked(true);
                            }else {
                                subTitleListDetail.get(0).setChecked(true);
                            }
                        }
                    }catch (Exception e){
                        try{
                            String fileUrl=jsonArraySubs.getJSONObject(0).getString("url");
                            String subFileName=jsonArraySubs.getJSONObject(0).getString("filename");
                            SubTitle subTitle = new SubTitle(fileUrl,subFileName, true);
                            subTitleListDetail.add(subTitle);
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                listener1.onFinishLoad();
            }
        }).start();

    }


    private  void downloadInternetSubtitles(final String searchSubFileName, final String mediaName,final String seasonEpisode){
        subTitleList.clear();
        //以下为调用射手网（伪）字幕api
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                //search 字幕文件
                //wOu37LmDZYHpMypmS3xaeWnsqbVv7vhw是射手网（伪）会员的识别码
                String url="http://api.assrt.net/v1/sub/search?token=wOu37LmDZYHpMypmS3xaeWnsqbVv7vhw&q="+searchSubFileName+"&cnt=20&pos=0"+"&no_muxer=1";
                String search= HttpUtil.sendHttpRequest(url);
                Log.e("videoPlayerActivity",search);//搜索结果
                try {
                    JSONObject jsonObject = new JSONObject(search);
                    JSONArray jsonArray = jsonObject.getJSONObject("sub").getJSONArray("subs");
                    if (jsonArray.length()<1){
                        return;
                    }
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        String id = object.getString("id");
                        String name = object.getString("native_name");
                        if (TextUtils.isEmpty(name)){
                            name = object.getString("videoname");
                        }
                        SubTitle subTitle;
                        if (i == 0){
                            subTitle = new SubTitle(id,name, true);
                        }else {
                            subTitle = new SubTitle(id,name, false);
                        }

                        subTitleList.add(subTitle);
                        Log.w("videoPlayerActivity",id+name);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                listener.onFinishLoad();



             //   String name="http://api.assrt.net/v1/sub/search?token=wOu37LmDZYHpMypmS3xaeWnsqbVv7vhw&q="+"Game.of.Thrones.S02E01"+"&cnt=1&pos=0";
                /*int low=search.indexOf("\"id\"");
                if(low==-1){
                    Log.e("videoPlayerActivity1","未找到字幕");
                    return;
                }
                int high=search.indexOf(",",low);
                String videoID=null;
                try{
                    videoID=search.substring(low+5,high);
                    Log.e("1",videoID);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(videoID==null){

                    Log.e("videoPlayerActivity","未找到字幕");
                }
                else {*/
                    //选择选中的字幕文件
            }
        });

        thread.start();


    }

    public interface OnLoadListener {

        public void onStartLoad();

        public void onFinishLoad();

    }
}
