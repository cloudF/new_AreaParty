package com.dkzy.areaparty.phone;

import java.util.Arrays;
import java.util.List;

/**
 * Project Name： FamilyCentralControler
 * Description: 描述文件类型的常量
 * Author: boris
 * Time: 2017/2/16 21:58
 */

public class FileTypeConst {
    public static final int folder = 1;
    public static final int excel  = 2;
    public static final int music  = 3;
    public static final int pdf    = 4;
    public static final int ppt    = 5;
    public static final int video  = 6;
    public static final int word   = 7;
    public static final int zip    = 8;
    public static final int txt    = 9;
    public static final int pic    = 10;
    public static final int apk    = 11;
    public static final int none   = 12;

    private static String[] musicArray = {"m4a", "mp3", "mid", "xmf", "ogg", "wav", "flac", "ape"};
    private static String[] videoArray = {"3gp", "mp4", "mkv", "avi", "flv", "wmv", "rmvb", "mov"};
    private static String[] excelArray = {"xls", "xlsx", "xlsb", "xlsm", "xlst"};
    private static String[] pptArray   = {"pptx", "pptm", "ppt"};
    private static String[] wordArray  = {"docx", "docm", "doc", "dotx", "dot", "dotm"};
    private static String[] zipArray   = {"rar", "zip", "arj"};
    private static String[] picArray   = {"bmp", "jpg", "jpeg", "png", "gif"};

    public static List<String> musicTypeList = Arrays.asList(musicArray);
    public static List<String> videoTypeList = Arrays.asList(videoArray);
    public static List<String> pptTypeList   = Arrays.asList(pptArray);
    public static List<String> excelTypeList = Arrays.asList(excelArray);
    public static List<String> wordTypeList  = Arrays.asList(wordArray);
    public static List<String> zipTypeList   = Arrays.asList(zipArray);
    public static List<String> picTypeList   = Arrays.asList(picArray);

    public static int determineFileType(String fileName) {
        int type = 0;
        // 获取文件扩展名
        String end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        if(musicTypeList.contains(end)) {
            type = FileTypeConst.music;
        } else if(videoTypeList.contains(end)) {
            type = FileTypeConst.video;
        } else if(excelTypeList.contains(end)) {
            type = FileTypeConst.excel;
        } else if(pptTypeList.contains(end)) {
            type = FileTypeConst.ppt;
        } else if(wordTypeList.contains(end)) {
            type = FileTypeConst.word;
        } else if(zipTypeList.contains(end)) {
            type = FileTypeConst.zip;
        } else if(picTypeList.contains(end)) {
            type = FileTypeConst.pic;
        } else if(end.equals("pdf")) {
            type = FileTypeConst.pdf;
        } else if(end.equals("txt")) {
            type = FileTypeConst.txt;
        } else if(end.equals("apk")) {
            type = FileTypeConst.apk;
        } else {
            type = FileTypeConst.none;
        }

        return type;
    }
}
