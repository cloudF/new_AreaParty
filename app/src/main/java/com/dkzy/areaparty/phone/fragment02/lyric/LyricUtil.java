package com.dkzy.areaparty.phone.fragment02.lyric;

import android.text.TextUtils;
import android.util.Log;

import com.dkzy.areaparty.phone.fragment02.subtitle.HttpUtil;
import com.dkzy.areaparty.phone.fragment02.subtitle.SubTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LyricUtil {
    public static List<Lyric> lyricList = new ArrayList<>();
    private OnLoadListener listener;

    public void setListener(OnLoadListener listener) {
        this.listener = listener;
    }

    public void searchLyric(String songName,final String songLength) {
        lyricList.clear();
        listener.onStartLoad();

        if (songName.contains("/")) songName = songName.substring(songName.lastIndexOf("/"));
        if (songName.contains(".")) songName = songName.substring(0,songName.lastIndexOf("."));

        OkHttpClient client = new OkHttpClient();
        String url = "http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword="+songName+"&duration="+songLength;
        Log.w("LyricUtil",url);
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFinishLoad();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                response.close();
                Log.w("LyricUtil",responseDate);
                try {
                    JSONObject jsonObject = new JSONObject(responseDate);
                    JSONArray jsonArray = jsonObject.getJSONArray("candidates");
                    for (int i = 0 ; i<jsonArray.length() ; i++){
                        JSONObject lyricObject = jsonArray.getJSONObject(i);
                        String accesskey = lyricObject.getString("accesskey");
                        String id = lyricObject.getString("id");
                        String singer = lyricObject.getString("singer");
                        String song = lyricObject.getString("song");
                        lyricList.add(new Lyric(accesskey,id,singer,song));
                    }
                    if (lyricList.size()>0) lyricList.get(0).setChecked(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onFinishLoad();
            }
        });
    }
    public interface OnLoadListener {

        public void onStartLoad();

        public void onFinishLoad();

    }
}
