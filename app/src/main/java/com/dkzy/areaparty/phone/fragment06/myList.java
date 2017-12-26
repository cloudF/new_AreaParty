package com.dkzy.areaparty.phone.fragment06;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SnowMonkey on 2017/3/10.
 */

public class myList implements Serializable {

    private List<HashMap<String,Object>> list1;

    public List<HashMap<String, Object>> getList() {
        return list1;
    }

    public void setList(List<HashMap<String, Object>> list) {
        this.list1 = list;
    }

}
