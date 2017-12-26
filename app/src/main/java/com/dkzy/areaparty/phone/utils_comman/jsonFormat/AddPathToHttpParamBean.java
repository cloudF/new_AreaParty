package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import java.util.List;

/**
 * Created by borispaul on 17-5-11.
 */

public class AddPathToHttpParamBean {
//    private List<String> paths;
//
//    public List<String> getPaths() {
//        return paths;
//    }
//
//    public void setPaths(List<String> paths) {
//        this.paths = paths;
//    }
    private String paths;
    public void setPaths(List<String> path) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : path){
            stringBuilder.append(str);
            stringBuilder.append("?");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("?"));
        paths = stringBuilder.toString();
    }
}
