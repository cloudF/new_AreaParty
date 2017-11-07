package com.androidlearning.boris.familycentralcontroler.fragment02.contentResolver;

/**
 * Created by Branch on 16/7/30.
 */
public enum FileSystemType {

  photo,
  music,
  video,
  text,
  zip;


  public static FileSystemType getFileTypeByOrdinal(int ordinal) {

    for (FileSystemType type : values()) {

      if (type.ordinal() == ordinal) {

        return type;
      }

    }


    return photo;

  }

}
