/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common.events;

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
