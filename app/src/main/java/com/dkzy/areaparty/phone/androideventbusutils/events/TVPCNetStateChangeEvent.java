package com.dkzy.areaparty.phone.androideventbusutils.events;

/**
 * Created by borispaul on 17-5-31.
 */

public class TVPCNetStateChangeEvent {
    private boolean isTVOnline;
    private boolean isPCOnline;

    public TVPCNetStateChangeEvent(boolean TVState, boolean PCState) {
        this.isPCOnline = PCState;
        this.isTVOnline = TVState;
    }

    public boolean isTVOnline() {
        return isTVOnline;
    }

    public boolean isPCOnline() {
        return isPCOnline;
    }
}
