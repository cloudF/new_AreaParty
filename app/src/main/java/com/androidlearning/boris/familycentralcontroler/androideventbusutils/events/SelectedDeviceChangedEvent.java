package com.androidlearning.boris.familycentralcontroler.androideventbusutils.events;

import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.IPInforBean;

/**
 * Created by borispaul on 17-5-31.
 */

public class SelectedDeviceChangedEvent {
    private boolean isDeviceOnline;
    private IPInforBean device;

    public SelectedDeviceChangedEvent(boolean deviceState, IPInforBean device) {
        this.isDeviceOnline = deviceState;
        this.device = device;
    }

    public boolean isDeviceOnline() {
        return isDeviceOnline;
    }

    public IPInforBean getDevice() {
        return device;
    }
}
