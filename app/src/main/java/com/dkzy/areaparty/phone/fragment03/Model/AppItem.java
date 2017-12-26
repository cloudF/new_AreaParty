package com.dkzy.areaparty.phone.fragment03.Model;

import android.support.annotation.NonNull;

/**
 * Created by poxiaoge on 2016/12/21.
 */

public class AppItem implements Comparable {
    private String appName;
    private String packageName;
    private String iconURL;    // ...保冲兵
    private boolean isRunning = false; // ...保冲兵

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final AppItem other = (AppItem) obj;
        if(packageName != other.packageName){
            return false;
        }
        if(appName != other.appName){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + appName.hashCode();
        result = prime * result + packageName.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.appName.compareTo(((AppItem) o).appName);
    }
}