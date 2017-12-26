package com.dkzy.areaparty.phone.fragment06;

import protocol.Data.FileData;

/**
 * Created by SnowMonkey on 2017/3/22.
 */

public class fileStyle {
    private static final int MUSIC = 0;
    private static final int FILM = 1;
    private static final int COMPRESS = 2;
    private static final int IMG = 3;
    private static final int DOCUMENT = 4;
    private static final int OTHER = 5;
    public static int getFileStyle(FileData.FileItem file){
        String fileName = file.getFileName();
        String fileSuffix;
        if(file.getFileName().contains(".")){
            //fileSuffix = file.getFileName().split("\\.")[1].toLowerCase();
            fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        }else{
            fileSuffix = file.getFileName();
        }
        switch (fileSuffix){
            case "mp3":case "wma":
                return MUSIC;
            case "mp4":case "avi":case "wmv":case "rmvb":case "rm":case "mkv":case "mov":
                return FILM;
            case "zip":case "rar":case "7z":case "cab":case "iso":
                return COMPRESS;
            case "bmp":case "gif":case "jpeg":case "tiff":case "jpg":case "png":
                return IMG;
            case "txt":case "doc":case "pdf":case "caj":case "docx":
                return DOCUMENT;
            default:
                return OTHER;
        }
    }
    public static int getFileStyle(fileObj file){
        String fileSuffix;
        if(file.getFileName().contains(".")){
            fileSuffix = file.getFileName().split("\\.")[1].toLowerCase();
        }else{
            fileSuffix = file.getFileName();
        }
        switch (fileSuffix){
            case "mp3":case "wma":
                return MUSIC;
            case "mp4":case "avi":case "wmv":case "rmvb":case "rm":case "mkv":case "mov":
                return FILM;
            case "zip":case "rar":case "7z":case "cab":case "iso":
                return COMPRESS;
            case "bmp":case "gif":case "jpeg":case "tiff":case "jpg":case "png":
                return IMG;
            case "txt":case "doc":case "pdf":case "caj":case "docx":
                return DOCUMENT;
            default:
                return OTHER;
        }
    }
}
