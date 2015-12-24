package com.amapp.common.events;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Provides generic interface to obtain a singleton instance of Event Bus
 * Created by dadesai on 12/24/15.
 */
public final class EventBus {
    private static final Bus sBus = new Bus(ThreadEnforcer.ANY);

    private EventBus() {
    }

    public static Bus getInstance() {
        return sBus;
    }
}
