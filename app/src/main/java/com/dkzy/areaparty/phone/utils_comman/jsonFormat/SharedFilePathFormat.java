package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/13 16:02
 */

public class SharedFilePathFormat {
    /// <summary>
    /// 相当于键(用于存储点击分享那一刻的毫秒数)
    /// </summary>
    public String creatTime;
    /// <summary>
    /// 相当于值(用于存储分享的文件的完整路径)
    /// </summary>
    public String wholePath;

    public String fileName;
    public int fileSize;


    public SharedFilePathFormat(String creatTime, String wholePath, String fileName, int fileSize) {
        this.creatTime = creatTime;
        this.wholePath = wholePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}
