package com.dkzy.areaparty.phone.androideventbusutils.events;

/**
 * Created by borispaul on 17-5-31.
 */

public class changeSelectedDeviceNameEvent {
    private String name;

    public changeSelectedDeviceNameEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
