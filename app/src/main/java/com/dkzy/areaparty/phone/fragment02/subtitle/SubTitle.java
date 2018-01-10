package com.dkzy.areaparty.phone.fragment02.subtitle;

/**
 * Created by zhuyulin on 2018/1/4.
 */

public class SubTitle {
    private String id;
    private String name;
    private boolean checked;

    public SubTitle(String id, String name, boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
