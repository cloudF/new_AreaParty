package com.dkzy.areaparty.phone.fragment02.contentResolver;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Branch on 16/7/30.
 */
public class ContentDataControl {


  private static final ArrayMap<FileSystemType, ArrayList<FileItem>> mAllFileItem = new ArrayMap<>();
  public static final ArrayMap<String, ArrayList<FileItem>> mVideoFolder = new ArrayMap<>();
  public static final ArrayMap<String, ArrayList<FileItem>> mMusicFolder = new ArrayMap<>();
  public static final ArrayMap<String, ArrayList<FileItem>> mPhotoFolder = new ArrayMap<>();
  public static void clear(){
      mVideoFolder.clear();
      mMusicFolder.clear();
      mPhotoFolder.clear();
  }

  public static void addFileByType(FileSystemType type, FileItem fileItem) {

    if (type == null || fileItem == null) {
      return;
    }
      ArrayList<FileItem> fileItemList = mAllFileItem.get(type);
    if (fileItemList == null) {
      fileItemList = new ArrayList<>();
      mAllFileItem.put(type, fileItemList);
    }
    fileItemList.add(fileItem);

  }


  public static void addFileListByType(FileSystemType type, List<FileItem> fileItemList) {


    if (type == null || fileItemList == null) {
      return;
    }

//    ArrayList<FileItem> fileItems = mAllFileItem.get(type);
//
//    if (fileItems == null) {
//      fileItems = new ArrayList<>();
//      mAllFileItem.put(type, fileItems);
//    }
//
//    fileItems.addAll(fileItemList);
////////////////
    switch (type){
        case video:
            for (FileItem f : fileItemList){
              String path = f.getmFilePath();
              path = path.substring(0,path.lastIndexOf("/"));
              path = path.substring(path.lastIndexOf("/")+1);
              ArrayList<FileItem> fileItems1 = mVideoFolder.get(path);
              if (fileItems1 == null){
                fileItems1 = new ArrayList<>();
                mVideoFolder.put(path, fileItems1);
              }
              fileItems1.add(f);
            }
            break;
      case music:
            for (FileItem f : fileItemList){
              String path = f.getmFilePath();
              path = path.substring(0,path.lastIndexOf("/"));
              path = path.substring(path.lastIndexOf("/")+1);
              ArrayList<FileItem> fileItems1 = mMusicFolder.get(path);
              if (fileItems1 == null){
                fileItems1 = new ArrayList<>();
                mMusicFolder.put(path, fileItems1);
              }
              fileItems1.add(f);
            }
            break;
      case photo:
            for (FileItem f : fileItemList){
              String path = f.getmFilePath();
              path = path.substring(0,path.lastIndexOf("/"));
              path = path.substring(path.lastIndexOf("/")+1);
              ArrayList<FileItem> fileItems1 = mPhotoFolder.get(path);
              if (fileItems1 == null){
                fileItems1 = new ArrayList<>();
                mPhotoFolder.put(path, fileItems1);
              }
              fileItems1.add(f);
            }
            break;
    }




  }


  public static ArrayList<FileItem> getFileItemListByType(FileSystemType fileSystemType) {

    if (fileSystemType == null) {
      return null;
    }

    return mAllFileItem.get(fileSystemType);

  }

  public static ArrayList<FileItem> getFileItemListByFolder(FileSystemType type ,String folder) {

        if (type == null) {
          return null;
        }
        switch (type){
            case video:return mVideoFolder.get(folder);
            case music:return mMusicFolder.get(folder);
            case photo:return mPhotoFolder.get(folder);
            default: return null;
        }
  }


  public static int getTypeCount(FileSystemType fileSystemType) {

    List<FileItem> fileItemList = mAllFileItem.get(fileSystemType);


    return fileItemList == null ? 0 : fileItemList.size();

  }

  public static ArrayList<String> getFolder(FileSystemType fileSystemType){
      switch (fileSystemType){
          case video:return new ArrayList<>(mVideoFolder.keySet());
          case music:return new ArrayList<>(mMusicFolder.keySet());
          case photo:return new ArrayList<>(mPhotoFolder.keySet());
          default: return  new ArrayList<>();
      }

  }


  public static void destory() {
    mAllFileItem.clear();
  }

}
