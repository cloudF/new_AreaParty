package com.dkzy.areaparty.phone.fragment01.model;

import android.util.Log;

import java.text.SimpleDateFormat;

/**
 * Created by borispaul on 17-5-4.
 */

public class downloadedFileBean {
    private String name;
    private String sizeInfor;
    private long createTimeLong;
    private String createTimeStr;
    private int timeLength;
    private String timeLenStr;
    private int fileType;
    private String path;

    public downloadedFileBean(String name, String sizeInfor, long createTimeLong, int timeLength,
                              int fileType, String path) {
        this.name = name;
        this.sizeInfor = sizeInfor;
        this.createTimeLong = createTimeLong;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        this.createTimeStr = format.format(createTimeLong);
        this.timeLength = timeLength;
        int h = timeLength / 3600;
        int m = (timeLength - h * 3600) / 60;
        int s = (timeLength - h * 3600) % 60;
        if(h == 0) {
            if(m == 0) {
                this.timeLenStr = s + "";
            } else this.timeLenStr = m + ":" + s;
        } else {
            this.timeLenStr = h + ":" + m + ":" + s;
        }
        this.fileType = fileType;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSizeInfor() {
        return sizeInfor;
    }

    public void setSizeInfor(String sizeInfor) {
        this.sizeInfor = sizeInfor;
    }

    public long getCreateTimeLong() {
        return createTimeLong;
    }

    public void setCreateTimeLong(long createTimeLong) {
        this.createTimeLong = createTimeLong;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
        int h = timeLength / 3600;
        int m = (timeLength - h * 3600) / 60;
        int s = (timeLength - h * 3600) % 60;
        if(h == 0) {
            if(m == 0) {
                String temp = (s < 10)? ("0" + s) : ("" + s);
                this.timeLenStr = "00:00:" + temp;
            } else  {
                String tempm = (m < 10)? ("0" + m) : ("" + m);
                String temps = (s < 10)? ("0" + s) : ("" + s);
                this.timeLenStr = "00:" + tempm + ":" + temps;
            }
        } else {
            String temph = (h < 10)? ("0" + h) : ("" + h);
            String tempm = (m < 10)? ("0" + m) : ("" + m);
            String temps = (s < 10)? ("0" + s) : ("" + s);
            this.timeLenStr = temph + ":" + tempm + ":" + temps;
        }
        Log.e("test", timeLenStr + "  " + timeLength);
    }

    public String getTimeLenStr() {
        return timeLenStr;
    }

    public void setTimeLenStr(String timeLenStr) {
        this.timeLenStr = timeLenStr;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

}
