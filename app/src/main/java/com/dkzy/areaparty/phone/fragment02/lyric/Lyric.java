package com.dkzy.areaparty.phone.fragment02.lyric;

/**
 * Created by zhuyulin on 2018/3/20.
 */

public class Lyric {
    private String accesskey;
    private String id;
    private String singer;
    private String song;
    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Lyric(String accesskey, String id, String singer, String song) {
        this.accesskey = accesskey;
        this.id = id;
        this.singer = singer;
        this.song = song;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
