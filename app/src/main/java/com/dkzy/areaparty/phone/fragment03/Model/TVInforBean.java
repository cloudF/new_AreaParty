package com.dkzy.areaparty.phone.fragment03.Model;

/**
 * Created by borispaul on 17-6-28.
 */

public class TVInforBean {
    public String brand = "";  // 品牌
    public String model = "";  // 型号
    public String totalStorage = "";   // 总容量
    public String freeStorage  = "";   // 可用容量
    public String totalMemory  = "";   // 总内存
    public String resolution   = "";   // 分辨率
    public String androidVersion = ""; // anroid版本
    public String isRoot = "";         // 是否root（“是, 否”）

    public boolean isEmpty() {
        return brand.equals("") && model.equals("") && totalStorage.equals("") && freeStorage.equals("") &&
               totalMemory.equals("") && resolution.equals("") && androidVersion.equals("") && isRoot.equals("");
    }
}
