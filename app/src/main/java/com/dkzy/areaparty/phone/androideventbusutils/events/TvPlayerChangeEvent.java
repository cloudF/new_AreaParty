package com.dkzy.areaparty.phone.androideventbusutils.events;

/**
 * Created by zhuyulin on 2017/9/26.
 */

public class TvPlayerChangeEvent {
    private boolean isPlayerRun;

    public TvPlayerChangeEvent(boolean isPlayerRun) {
        this.isPlayerRun = isPlayerRun;
    }

    public boolean isPlayerRun() {
        return isPlayerRun;
    }
}
