/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common;

import org.anoopam.ext.smart.weservice.SmartWebManager;
import org.json.JSONObject;

/**
 * Created by dadesai on 1/1/16.
 */
public abstract class AMServiceResponseListener implements SmartWebManager.OnResponseReceivedListener<JSONObject> {
    abstract public void onSuccess(JSONObject response);
    abstract public void onFailure(String failureMessage);
}
